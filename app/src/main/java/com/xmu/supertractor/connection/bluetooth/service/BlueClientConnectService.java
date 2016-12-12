package com.xmu.supertractor.connection.bluetooth.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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


import com.xmu.supertractor.connection.bluetooth.BluetoothClientConnThread;
import com.xmu.supertractor.connection.bluetooth.BluetoothComThread;
import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.logic.Connectionlogic;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Player_Info;
import com.xmu.supertractor.player.Me;
import com.xmu.supertractor.player.PlayerList;

import java.io.Serializable;
import java.util.List;

@SuppressLint("HandlerLeak")
public class BlueClientConnectService extends Service {

    private LocalBinder mLocBin = new LocalBinder();
    private Me me = Me.get_me();
    private PlayerList playerlist = null;
    // 搜索到的远程设备集合
    private List<BluetoothDevice> discoveredDevices = null;

    private BluetoothComThread communThread;
    // 蓝牙适配器
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();

    private BroadcastReceiver controlReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdmin.ACTION_START_DISCOVERY.equals(action)) {
                action_start_connect();
            } else if (BluetoothAdmin.ACTION_SELECTED_DEVICE.equals(action)) {
                action_start_connect(intent);
            }

        }


        private void action_start_connect(Intent intent) {
            // 选择了连接的服务器设备
            BluetoothDevice device = (BluetoothDevice) intent.getExtras()
                    .get(BluetoothAdmin.DEVICE);
            // 开启设备连接线程
            new BluetoothClientConnThread(clienthandler, device).start();
        }

        private void action_start_connect() {
            // 开始搜索
            if (bluetoothAdapter.isDiscovering())
                BluetoothAdmin.stopDiscovery();
            discoveredDevices.clear(); // o0c 清空存放设备的集合
            bluetoothAdapter.startDiscovery(); // 开始搜索
        }
    };

    // 蓝牙搜索广播的接收器
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取广播的Action
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                action_discovery_started();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                action_found(intent);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                action_discovery_finished();
            }
        }

        private void action_discovery_finished() {
            Log.d("my", "ClientServer Discovery Finished.");
            // 搜索结束
            Intent discovery_finished = new Intent("discovery_finished");
            sendBroadcast(discovery_finished);
            if (discoveredDevices.isEmpty()) {
                // 若未找到设备，则发动未发现设备广播
                Intent foundIntent = new Intent(
                        BluetoothAdmin.ACTION_NOT_FOUND_SERVER);
                sendBroadcast(foundIntent);
            }
        }

        private void action_found(Intent intent) {
            // 发现远程蓝牙设备
            // 获取设备
            Log.d("my", "ClientServer Device Founded");
            BluetoothDevice bluetoothDevice = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            discoveredDevices.add(bluetoothDevice);

            // 发送发现设备广播
            Intent deviceListIntent = new Intent(
                    BluetoothAdmin.ACTION_FOUND_DEVICE);
            deviceListIntent.putExtra(BluetoothAdmin.DEVICE,
                    bluetoothDevice);
            sendBroadcast(deviceListIntent);
        }

        private void action_discovery_started() {
            Log.d("my", "ClientServer Discovering...");
            // 开始搜索
        }
    };

    Handler clienthandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // 处理消息
            switch (msg.what) {
                case BluetoothAdmin.MESSAGE_CONNECT_ERROR:
                    // 连接错误
                    // 发送连接错误广播
                    Log.d("my", "ClientService Sent Connect_Error_Broatcast.");

                    Intent errorIntent = new Intent(
                            BluetoothAdmin.ACTION_CONNECT_ERROR);
                    sendBroadcast(errorIntent);
                    break;
                case BluetoothAdmin.MESSAGE_CONNECT_SUCCESS:
                    // 连接成功
                    Log.d("my", "ClientService Connect_Success");
                    // 开启通讯线程
                    communThread = new BluetoothComThread(clienthandler,
                            (BluetoothSocket) msg.obj);
                    BluetoothAdmin.CommunThread_map.put(0, communThread);
                    communThread.start();
                    // 发送连接成功广播
                    Intent succIntent = new Intent(
                            BluetoothAdmin.ACTION_CONNECT_SUCCESS);
                    sendBroadcast(succIntent);
                    break;
                case BluetoothAdmin.MESSAGE_READ_OBJECT:
                    // 读取到对象
                    TransmitUnit recdata = (TransmitUnit) ((Serializable) msg.obj);
                    int rectype = recdata.type;
                    Log.d("my", "BlueClientConnectService Receive_Data:" + "type:"
                            + recdata.type);
                    Intent data_to_ClientActivity = new Intent(
                            BluetoothAdmin.ACTION_DATA_TO_ACTIVITY);
                    data_to_ClientActivity.putExtra(BluetoothAdmin.DATA,
                            (Serializable) msg.obj);
                    switch (rectype) {
                        case 0:
                            Unit_Player_Info u1=(Unit_Player_Info) recdata.obj;
                            String recname = u1.name;
                            me.seq = u1.seq;
                            me.blueComThread = BluetoothAdmin.CommunThread_map.get(0);
                            playerlist.add_player(1, recname);
                            playerlist.add_player(me);
                            sendBroadcast(data_to_ClientActivity);
                            break;
                        case 4:
                            Unit_Player_Info u2=(Unit_Player_Info) recdata.obj;
                            String name = u2.name;
                            int i = u2.seq;
                            Log.d("my","---------------------------------------------"+i+"-------"+name);
                            playerlist.add_player(i, name);
                            data_to_ClientActivity.putExtra("str", i + ".   " + name);
                            sendBroadcast(data_to_ClientActivity);
                            break;
                        case 10:
                            Connectionlogic.receive_pos_info(recdata);
                            sendBroadcast(data_to_ClientActivity);
                            break;
                        default:
                            Log.d("my", "BlueClientConnectService Receive Error msg.");
                            break;
                    }
                    break;

            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d("my", "ClientService onCreate");
        Connectionlogic.client_init();
        playerlist = PlayerList.getPlayerList();
        discoveredDevices = BluetoothAdmin.discoveredDevices;
        // discoveryReceiver的IntentFilter
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);

        // controlReceiver的IntentFilter
        IntentFilter controlFilter = new IntentFilter();
        controlFilter.addAction(BluetoothAdmin.ACTION_START_DISCOVERY);
        controlFilter.addAction(BluetoothAdmin.ACTION_SELECTED_DEVICE);

        // 注册BroadcastReceiver
        registerReceiver(discoveryReceiver, discoveryFilter);
        registerReceiver(controlReceiver, controlFilter);
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d("my", "ClientService onStartCommand");
        if (!BluetoothAdmin.CommunThread_map.isEmpty()) {
            BluetoothAdmin.CommunThread_map.get(0).SetHandler(clienthandler);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.d("my", "ClientService onDestory");
        unregisterReceiver(discoveryReceiver);
        unregisterReceiver(controlReceiver);
        communThread = null;
        Log.d("my", "ClientService unregisterReceiver(discoveryReceiver);");
        Log.d("my", "ClientService unregisterReceiver(controlReceiver);");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d("my", "ClientService onBind");

        return mLocBin;
    }

    public class LocalBinder extends Binder {
        public BlueClientConnectService getService() {
            Log.d("BlueClientService", "return BlueClientConnectService");
            return BlueClientConnectService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("BlueClientService", "BindService-->onUnbind()");
        return super.onUnbind(intent);
    }
}