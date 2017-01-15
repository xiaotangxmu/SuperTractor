package com.xmu.supertractor.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;
import com.xmu.supertractor.R;
import com.xmu.supertractor.Tools.Tools;
import com.xmu.supertractor.connection.wifi.service.WifiClientConnectService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.xmu.supertractor.Tools.PrintLog.log;


public class WifiClientActivity extends Activity {
    private TextView tv_wificonstate;
    public Context mcontext;
    private MyHandler uihandler;
    private String tag = "WifiClientActivity";
    private WifiClientActivity mactivity;
    private TextView tv_ip;
    private WifiClientConnectService clientconnectservice;
    private ArrayAdapter<String> arrayadapter = null;
    private List<String> l;
    private boolean destoryed = false;

    private ServiceConnection connectionclient = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            log(tag, "onServiceDisconnected.");
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            clientconnectservice = ((WifiClientConnectService.LocalBinder) service).getService();
            clientconnectservice.wa = mactivity;
            clientconnectservice.setuiHandler(uihandler);
            log(tag, "onServiceConnected.");
            clientconnectservice.onStart();
        }

    };
    private TextView tv_server;


    static class MyHandler extends Handler {
        WeakReference<WifiClientActivity> wifiClientActivityWeakReference = null;
        WifiClientActivity wifiClientActivity = null;

        MyHandler(WifiClientActivity wa) {
            wifiClientActivityWeakReference = new WeakReference<>(wa);
            wifiClientActivity = wifiClientActivityWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    wifiClientActivity.l.clear();
                    ArrayList<String> temp = Tools.cast(msg.obj);
                    wifiClientActivity.l.addAll(temp);
                    log(wifiClientActivity.tag, "flush listview");
                    wifiClientActivity.arrayadapter.notifyDataSetChanged();
                    break;
                case 2:
                    wifiClientActivity.stop_connect_service();
                    Intent startgame = new Intent();
                    startgame.setClass(wifiClientActivity.mcontext, GameActivity.class);
                    wifiClientActivity.startActivity(startgame);
                    break;
            }
        }
    }


    public void setTv_wificonstate(String s) {
        tv_wificonstate.setText(s);
    }

    public void setTv_ip(String s) {
        tv_ip.setText(s);
    }

    public void setTv_server(String s) {
        tv_server.setText(s);
    }

    private void init_view() {
        tv_wificonstate = (TextView) findViewById(R.id.tv_wificonstatec);
        Button bt_wifi_con_state = (Button) findViewById(R.id.bt_wifi_con_statec);
        tv_ip = (TextView) findViewById(R.id.tv_ipc);
        ListView lv_player = (ListView) findViewById(R.id.lv_playerc);
        l = new ArrayList<>();
        arrayadapter = new ArrayAdapter<>(mcontext,
                android.R.layout.simple_expandable_list_item_1, l);
        lv_player.setAdapter(arrayadapter);
        tv_server = (TextView) findViewById(R.id.tv_server);
        Onclickst st = new Onclickst();
        bt_wifi_con_state.setOnClickListener(st);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log(tag, "onCreate.");
        setContentView(R.layout.activity_wificlient);
        mcontext = this;
        mactivity = this;
        init_view();
        uihandler = new MyHandler(WifiClientActivity.this);
        start_connect_service();
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        log(tag, "onStart.");
        super.onStart();
    }

    private void start_connect_service() {
        Intent startClientService = new Intent(mactivity,
                WifiClientConnectService.class);
        startService(startClientService);
        bindService(startClientService, connectionclient, BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        log(tag, "onRestart.");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        log(tag, "onResume");
        CrashReport.setUserSceneTag(mcontext, 32874);
        super.onResume();
    }

    @Override
    protected void onPause() {
        log(tag, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        log(tag, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log(tag, "onDestroy");
        stop_connect_service();
        if (uihandler != null) {
            uihandler.removeCallbacksAndMessages(null);
            uihandler = null;
        }
        setContentView(R.layout.acticity_null);
        super.onDestroy();
    }

    private void stop_connect_service() {
        if (!destoryed) {
            Intent stopIntent = new Intent(this, WifiClientConnectService.class);
            stopService(stopIntent);
            unbindService(connectionclient);
        }
        destoryed = true;
    }


    class Onclickst implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.bt_wifi_con_statec:
                    clientconnectservice.get_wifi_info();
                    break;
                default:
                    break;
            }
        }
    }
}
