package com.xmu.supertractor.connection.bluetooth.service;

import java.io.Serializable;


import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.xmu.supertractor.connection.bluetooth.BluetoothComThread;
import com.xmu.supertractor.connection.bluetooth.BluetoothServerConnThread;
import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.logic.Connectionlogic;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Player_Info;
import com.xmu.supertractor.desk.Desk;
import com.xmu.supertractor.desk.Member;
import com.xmu.supertractor.player.Me;
import com.xmu.supertractor.player.PlayerList;

@SuppressLint("HandlerLeak")
public class BlueServerConnectService extends Service {
    // 蓝牙通讯线程
    private BluetoothComThread bluetoothhread;
    private LocalBinder mLocBin = new LocalBinder();
    private Desk desk = Desk.dk_getInstance();
    public static int num_temp = 1;
    private PlayerList playerlist = null;


    // 接收其他线程消息的Handler
    private Handler serviceHandler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BluetoothAdmin.MESSAGE_CONNECT_SUCCESS:
                    // 连接成功
                    Log.d("my", "BlueServerConnectService Connect_Success:" + num_temp);
                    bluetoothhread = new BluetoothComThread(serviceHandler, (BluetoothSocket) msg.obj);
                    Log.d("my", "BlueServerConnectService Create ComThead:" + num_temp + ":" + bluetoothhread.toString());
                    BluetoothAdmin.CommunThread_map.put(num_temp, bluetoothhread);
                    bluetoothhread.start();
                    // 开启通讯线程
                    // 发送连接成功消息
                    Intent connSuccIntent = new Intent(BluetoothAdmin.ACTION_CONNECT_SUCCESS);
                    sendBroadcast(connSuccIntent);
                    break;

                case BluetoothAdmin.MESSAGE_CONNECT_ERROR:
                    // 连接错误
                    // 发送连接错误广播
                    Intent errorIntent = new Intent(BluetoothAdmin.ACTION_CONNECT_ERROR);
                    sendBroadcast(errorIntent);
                    break;

                case BluetoothAdmin.MESSAGE_READ_OBJECT:
                    // 读取到数据
                    // 发送数据广播（包含数据对象）
                    // 接收数据
                    TransmitUnit recdata = (TransmitUnit) ((Serializable) msg.obj);
                    int rectype = recdata.type;
                    Log.d("my", "SererService Receive_Data:" + "type:" + rectype);
                    switch (rectype) {
                        // 接受连接玩家信息
                        case 1:
                            Unit_Player_Info u1=(Unit_Player_Info) recdata.obj;
                            String name = u1.name;
                            int seq=u1.seq;
                            desk.add_player(seq, name,  BluetoothAdmin.CommunThread_map.get(seq));
                            playerlist.add_player(seq, name);
                            for (int member = 2; member <= num_temp; ++member) {
                                for (int i = 1; i <= num_temp; ++i) {
                                    Member m = desk.getMember(i);
                                    Unit_Player_Info ui= new Unit_Player_Info(m.seq,m.name);
                                    TransmitUnit senddata = new TransmitUnit(4,0 ,member,ui);
                                    Connectionlogic.send_message_to_client(senddata);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    Intent dataIntent = new Intent(BluetoothAdmin.ACTION_DATA_TO_ACTIVITY);
                    dataIntent.putExtra(BluetoothAdmin.DATA, (Serializable) msg.obj);
                    sendBroadcast(dataIntent);
            }

            super.handleMessage(msg);
        }
    };



    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d("my", "ServerService onCreate");
        Me me = Me.get_me();
        Connectionlogic.client_init();
        Connectionlogic.server_init();
        playerlist = PlayerList.getPlayerList();
        new BluetoothServerConnThread(serviceHandler).start();
        desk.add_player(1, me.name);
        playerlist.add_player(me);
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d("my", "ServerService onStartCommand");
        if (desk.getMember(2) != null) {
            desk.getMember(2).bluetooththread.SetHandler(serviceHandler);
            Log.d("my", "sethandler");
        }
        if (desk.getMember(3) != null) {
            desk.getMember(3).bluetooththread.SetHandler(serviceHandler);
            Log.d("my", "sethandler");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.d("my", "ServerService onDestory");
        bluetoothhread = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d("my", "ServerService onBind");

        return mLocBin;
    }

    public class LocalBinder extends Binder {
        public BlueServerConnectService getService() {
            Log.d("BlueServerService", "return BlueClientConnectService");
            return BlueServerConnectService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("BlueServerService", "BindService-->onUnbind()");
        return super.onUnbind(intent);
    }
}
