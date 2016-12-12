package com.xmu.supertractor.desk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


import com.xmu.supertractor.connection.TransmitUnit.bluetooth.BluetoothTools;


import com.xmu.supertractor.connection.TransmitUnit.bluetooth.TransmitUnit;
import com.xmu.supertractor.card.Out_Card;
import com.xmu.supertractor.parameter.Status;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressLint("HandlerLeak")
public class DeskService extends Service {

    public static Context dContext = null;
    private LocalBinder mLocBin = new LocalBinder();
    public Handler playerHandler = null;
    public Handler deskHandler = null;
    private int prepared_num = 1;
    private Desk desk = null;
    private Lock lock;
    private int passtime = 0;
    private Condition condition_time;
    private Intent sendDataIntent;

    private BroadcastReceiver deskReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothTools.ACTION_DATA_TO_PLAYER.equals(action)) {
                data_to_player(intent);
            }
        }

        public void data_to_player(Intent intent) {
            // 发送数据
            Object data = intent.getSerializableExtra(BluetoothTools.DATA);
            TransmitUnit senddata = (TransmitUnit) data;
            int sour = senddata.sour;
            int dest = senddata.dest;
            Log.d("desk", "DeskService Send from:" + sour + "  to:" + dest + ".   type:" + senddata.type );
            switch (dest) {
                case 0:
                    BluetoothTools.CommunThread_map.get(2).writeObject(data);
                    BluetoothTools.CommunThread_map.get(3).writeObject(data);
                    BluetoothTools.CommunThread_map.get(4).writeObject(data);
                case 1:
                    Message msg = playerHandler.obtainMessage();
                    msg.what = BluetoothTools.MESSAGE_READ_OBJECT;
                    msg.obj = senddata;
                    msg.sendToTarget();
                    break;
                case 2:
                    BluetoothTools.CommunThread_map.get(2).writeObject(data);
                    break;
                case 3:
                    BluetoothTools.CommunThread_map.get(3).writeObject(data);
                    break;
                case 4:
                    BluetoothTools.CommunThread_map.get(4).writeObject(data);
                    break;
                default:
                    break;
            }
        }

    };




    private void initdeskHandler() {
        playerHandler = null;
        deskHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BluetoothTools.MESSAGE_READ_OBJECT:
                        TransmitUnit recdata = (TransmitUnit) ((Serializable) msg.obj);
                        int recsour = recdata.sour;
                        int rectype = recdata.type;
                        Log.d("desk", "Desk Receive_Data from " + recsour + ":" + " type:" + recdata.type );
                        switch (msg.what) {
                            case BluetoothTools.MESSAGE_READ_OBJECT:
                                switch (rectype) {

                                    case Status.GAME_ACTIVITY_PREPARED:
                                        Make_Allo_25_card();
                                        break;

                                    case Status.Ack_25_Hand_Card:
                                        if(prepared_num ==4) {
                                            Show_25_Card();
                                        }else{
                                            prepared_num = prepared_num+1;
                                        }
                                        break;

                                    case Status.Done_Show_One_Card:
                                        Show_25_Card();
                                        break;

                                    case Status.Call_Lord_First:
                                        Out_Card Call_Lord_Card = (Out_Card)recdata.obj;
                                        Status.main_color = Call_Lord_Card.color;
                                        Status.main_num = Call_Lord_Card.pokes.get(0);
                                    default:
                                        break;
                                }
                                break;

                        }
                        super.handleMessage(msg);
                }
            }
        };
    }

    private void Make_Allo_25_card(){
        desk.initdesk();

        ArrayList<Integer> First_player_card ;
        ArrayList<Integer> Second_player_card ;
        ArrayList<Integer> Third_player_card ;
        ArrayList<Integer> Fourth_player_card;

        First_player_card = desk.member_card_map.get(1).pokes;
        Second_player_card = desk.member_card_map.get(2).pokes;
        Third_player_card = desk.member_card_map.get(3).pokes;
        Fourth_player_card = desk.member_card_map.get(4).pokes;

        TransmitUnit cardInfo = TransmitUnit.getTransmitUnit(Status.Deliver_Hand_Card,1,1,First_player_card);
        send(cardInfo);

        cardInfo = TransmitUnit.getTransmitUnit(Status.Deliver_Hand_Card,1,2,Second_player_card);
        send(cardInfo);

        cardInfo = TransmitUnit.getTransmitUnit(Status.Deliver_Hand_Card,1,3,Third_player_card);
        send(cardInfo);

        cardInfo = TransmitUnit.getTransmitUnit(Status.Deliver_Hand_Card,1,4,Fourth_player_card);
        send(cardInfo);
    }

    private void Show_25_Card(){ //通知用户地显示25张手牌里的一张
            TransmitUnit ShowOne = TransmitUnit.getTransmitUnit(Status.Show_One_Card, 1, 1, null);
            send(ShowOne);

            ShowOne = TransmitUnit.getTransmitUnit(Status.Show_One_Card, 1, 2, null);
            send(ShowOne);

            ShowOne = TransmitUnit.getTransmitUnit(Status.Show_One_Card, 1, 3, null);
            send(ShowOne);

            ShowOne = TransmitUnit.getTransmitUnit(Status.Show_One_Card, 1, 4, null);
            send(ShowOne);
    }

    @Override
    public void onCreate() {
        Log.d("desk", "DeskService onCreate.");
        initdeskHandler();
        IntentFilter deskFilter = new IntentFilter();
        deskFilter.addAction(BluetoothTools.ACTION_DATA_TO_PLAYER);
        registerReceiver(deskReceiver, deskFilter);
        dContext = this;
        desk = Desk.dk_getInstance();
        lock = new ReentrantLock();
        condition_time = lock.newCondition();
        BluetoothTools.CommunThread_map.get(2).SetHandler(deskHandler);
        BluetoothTools.CommunThread_map.get(3).SetHandler(deskHandler);
        BluetoothTools.CommunThread_map.get(4).SetHandler(deskHandler);
        super.onCreate();
    }

    public void onDestroy() {
        Log.d("desk", "DeskService onDestory.");
        unregisterReceiver(deskReceiver);
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public DeskService getService() {
            Log.d("desk", "return DeskService.this;");
            return DeskService.this;
        }
    }

    public void init() {
        desk.compute_pos(Status.partner);
        TransmitUnit tu=TransmitUnit.getTransmitUnit(Status.START_GAME,0,0,desk.pos_list);
        send(tu);
    }

    public void send(TransmitUnit Unit) {
        sendDataIntent=  new Intent(BluetoothTools.ACTION_DATA_TO_PLAYER);
        sendDataIntent.putExtra(BluetoothTools.DATA, Unit);
        sendBroadcast(sendDataIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("desk", "Desk OnBind");
        return mLocBin;
    }


}
