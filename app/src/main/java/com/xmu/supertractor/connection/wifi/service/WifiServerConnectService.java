package com.xmu.supertractor.connection.wifi.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.xmu.supertractor.activity.WifiServerActivity;
import com.xmu.supertractor.connection.logic.Connectionlogic;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Player_Broadcast_Info;
import com.xmu.supertractor.connection.wifi.Multicast.BroadcastThread;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.connection.wifi.socket.AcceptThread;
import com.xmu.supertractor.desk.Desk;
import com.xmu.supertractor.player.PlayerList;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.xmu.supertractor.Tools.PrintLog.log;
import static com.xmu.supertractor.connection.logic.Connectionlogic.send_message_to_client;


public class WifiServerConnectService extends Service {
    private Context mcontext;
    private WifiAdmin wifiadmin;
    private String tag = "WifiServerConnectService";
    public WifiServerActivity wa;
    private LocalBinder mBinder = new LocalBinder();
    private AcceptThread at;
    private BroadcastThread st;
    private Handler uihandler;
    private ServiceHandler serviceHandler;

    private void receive_messag_from_client(final TransmitUnit tu) {
        log(tag, "receive_messag_from_client,type:" + tu.type);
        switch (tu.type) {
            case 0:
                Connectionlogic.receive_client_name_server(tu);
                break;
            case 2:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = false;
                        int num = (int) tu.obj;
                        Unit_Player_Broadcast_Info upbi = new Unit_Player_Broadcast_Info();
                        upbi.num = num;
                        synchronized (WifiServerConnectService.this) {
                            while (!flag) {
                                log(tag, "thread:" + num + " get lock and run...");
                                for (int i = 1; i <= num; ++i)
                                    log(tag, "thread:" + num + " desk member:" + i + (!Desk.dk_getInstance().exist_or_not(i) ? " null" : Desk.dk_getInstance().getMember(i).name));
                                flag = true;
                                for (int i = 1; i <= num; ++i) {
                                    if (!Desk.dk_getInstance().exist_or_not(i)) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    for (int i = 1; i <= num; ++i) {
                                        upbi.player_map.put(i, Desk.dk_getInstance().getMember(i).name);
                                    }
                                    for (int i = 2; i <= num; ++i) {
                                        TransmitUnit t = new TransmitUnit(3, 0, i, upbi);
                                        send_message_to_client(t);
                                    }
                                    List<String> ll = new ArrayList<>();
                                    for (int i = 1; i <= num; ++i) {
                                        ll.add(i + " " + Desk.dk_getInstance().getMember(i).name);
                                    }
                                    Message message = uihandler.obtainMessage(1);
                                    message.obj = ll;
                                    message.sendToTarget();
                                } else {
                                    log(tag, "thread:" + num + "notifyall()");
                                    WifiServerConnectService.this.notifyAll();
                                    try {
                                        log(tag, "thread:" + num + "wait()");
                                        WifiServerConnectService.this.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                ).start();

                break;
            default:
                break;
        }
    }

    public void setuiHandler(Handler hd) {
        this.uihandler = hd;
    }


    static class ServiceHandler extends Handler {
        WeakReference<WifiServerConnectService> wifiServerConnectServiceWeakReference = null;
        WifiServerConnectService wifiServerConnectService = null;

        ServiceHandler(WifiServerConnectService wc) {
            wifiServerConnectServiceWeakReference = new WeakReference<>(wc);
            wifiServerConnectService = wifiServerConnectServiceWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String s = (String) msg.obj;
                    log(wifiServerConnectService.tag, "comthread setup,ip:" + s);
                    break;
                case 1:
                    TransmitUnit tu = (TransmitUnit) msg.obj;
                    wifiServerConnectService.receive_messag_from_client(tu);
                    break;
                case 2:
                    wifiServerConnectService.uihandler.obtainMessage(2).sendToTarget();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public class LocalBinder extends Binder {
        public WifiServerConnectService getService() {
            log(tag, "return WifiServerConnectService");
            return WifiServerConnectService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void init_wifi() {
        log(tag, "init_wifi");
        wifiadmin = new WifiAdmin(this);
        wifiadmin.creatWifiLock();
        wifiadmin.acquireWifiLock();
        log(tag, "wifilook");
    }

    @Override
    public void onCreate() {
        log(tag, "onCreate");
        mcontext = this;
        serviceHandler = new ServiceHandler(WifiServerConnectService.this);
        Desk.init_desk();
        PlayerList.init_playerlist();
        init_wifi();
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        log(tag, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        log(tag, "onDestroy");
        try {
            if (at != null)
                at.stopthread();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serviceHandler.removeCallbacksAndMessages(null);
        serviceHandler = null;
        st.stopthread();
        wifiadmin.releaseWifiLock();
        System.gc();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void onStart() {
        log(tag, "onStart.");
        Connectionlogic.client_init();
        Connectionlogic.server_init();
        Connectionlogic.add_myself();
        get_wifi_info();
        at = new AcceptThread(serviceHandler);
        at.start();
        st = new BroadcastThread(mcontext);
        st.setmyip(wifiadmin.ip);
        st.start();
    }

    public void get_wifi_info() {
        if (wifiadmin.isWifiApEnabled()) {
            WifiAdmin.isAp = true;
            log(tag, wifiadmin.getDhcpInfo().toString());
            wifiadmin.ip = "192.168.43.1";
        } else {
            WifiAdmin.isAp = false;
            log(tag, wifiadmin.getWifiInfo());
            int ip;
            ip = wifiadmin.getIPAddress();
            int ip4 = ip & 255;
            int ip3 = ip >>> 8 & 255;
            int ip2 = ip >>> 16 & 255;
            int ip1 = ip >>> 24;
            wifiadmin.ip = "" + ip4 + "." + ip3 + "." + ip2 + "." + ip1;
        }
        log(tag, wifiadmin.ip);
        wa.setTv_ip(wifiadmin.ip);
        if (WifiAdmin.isAp) {
            PokeGameTools.MyToast(wa.mcontext, wifiadmin.getDhcpInfo().toString() + "    ip:" + wifiadmin.ip);
            wa.setTv_wificonstate("热点开启");
        } else {
            PokeGameTools.MyToast(wa.mcontext, wifiadmin.getWifiInfo() + "    ip:" + wifiadmin.ip);
            switch (wifiadmin.isWifiContected(mcontext)) {
                case 3:
                    wa.setTv_wificonstate("正在连接");
                    break;
                case 1:
                    wa.setTv_wificonstate("已连接");
                    break;
                case 2:
                    wa.setTv_wificonstate("未连接");
                    break;
                default:
                    break;
            }
        }
    }
}
