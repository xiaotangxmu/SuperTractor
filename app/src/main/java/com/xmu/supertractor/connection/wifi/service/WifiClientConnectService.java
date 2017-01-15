package com.xmu.supertractor.connection.wifi.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.xmu.supertractor.Tools.Tools;
import com.xmu.supertractor.activity.WifiClientActivity;
import com.xmu.supertractor.connection.logic.Connectionlogic;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.wifi.Multicast.ClientThread;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.player.PlayerList;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.xmu.supertractor.Tools.PrintLog.log;


public class WifiClientConnectService extends Service {
    private Context mcontext;
    private String tag = "WifiClientConnectService";
    private LocalBinder mBinder = new LocalBinder();
    private ClientThread ct;
    private WifiAdmin wifiadmin;
    public WifiClientActivity wa;
    private Handler uihandler;
    private ClientHandler clienthandler;

    private void receive_messag_from_server(TransmitUnit tu) {
        log(tag, "receive_messag_from_server,type:" + tu.type);
        switch (tu.type) {
            case 1:
                Connectionlogic.receive_my_seq_client(tu);
                wa.setTv_server(PlayerList.getPlayerList().getPlayer(1).name);
                break;
            case 3:
                int num = Connectionlogic.receive_broadcast_client(tu);
                List<String> l = Tools.cast(new ArrayList());
                l.clear();
                for (int i = 1; i <= num; ++i) {
                    l.add(i + "." + PlayerList.getPlayerList().getPlayer(i).name);
                }
                Message message = uihandler.obtainMessage(1);
                message.obj = l;
                message.sendToTarget();
                break;
            case Status.START_GAME_ACTIVITY:
                Connectionlogic.receive_pos_info(tu);
                Message message2 = uihandler.obtainMessage(2);
                message2.sendToTarget();
                break;
        }
    }


    static class ClientHandler extends Handler {

        WeakReference<WifiClientConnectService> wifiClientConnectServiceWeakReference = null;
        WifiClientConnectService wifiClientConnectService = null;

        ClientHandler(WifiClientConnectService wc) {
            wifiClientConnectServiceWeakReference = new WeakReference<>(wc);
            wifiClientConnectService = wifiClientConnectServiceWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    log(wifiClientConnectService.tag, "comthread setup.");
                    Connectionlogic.connect_successs_client();
                    break;
                case 1:
                    TransmitUnit tu = (TransmitUnit) msg.obj;
                    wifiClientConnectService.receive_messag_from_server(tu);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public void setuiHandler(Handler hd) {
        this.uihandler = hd;
    }

    public class LocalBinder extends Binder {
        public WifiClientConnectService getService() {
            log(tag, "return WifiClientConnectService");
            return WifiClientConnectService.this;
        }
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
            //PokeGameTools.MyToast(wa.mcontext, wifiadmin.getWifiApConfiguration().toString() + "    ip:" + wifiadmin.ip);
            wa.setTv_wificonstate(wifiadmin.getWifiApConfiguration().SSID + " 热点开启");
        } else {
            //PokeGameTools.MyToast(wa.mcontext, wifiadmin.getWifiInfo() + "    ip:" + wifiadmin.ip);
            switch (wifiadmin.isWifiContected(mcontext)) {
                case 3:
                    wa.setTv_wificonstate("正在连接");
                    break;
                case 1:
                    wa.setTv_wificonstate(wifiadmin.getSSID() + "已连接");
                    break;
                case 2:
                    wa.setTv_wificonstate("未连接");
                    break;
                default:
                    break;
            }
        }
    }

    private void init_wifi() {
        log(tag, "init_wifi");
        wifiadmin = new WifiAdmin(this);
        wifiadmin.creatWifiLock();
        wifiadmin.acquireWifiLock();
        log(tag, "wifilook");
    }

    public void onStart() {
        Connectionlogic.client_init();
        get_wifi_info();
        ct = new ClientThread(clienthandler);
        ct.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        log(tag, "onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        log(tag, "onCreate");
        clienthandler = new ClientHandler(WifiClientConnectService.this);
        PlayerList.init_playerlist();
        mcontext = this;
        init_wifi();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("xiaotang", "WifiClientConnectService---" + "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("xiaotang", "WifiClientConnectService---" + "onDestory");
        try {
            if (ct != null)
                ct.stopthread();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (clienthandler != null) {
            clienthandler.removeCallbacksAndMessages(null);
            clienthandler = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("xiaotang", "WifiClientConnectService---" + "OnUnbind");
        return super.onUnbind(intent);
    }
}
