package com.xmu.supertractor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xmu.supertractor.Tools.PrintLog;
import com.xmu.supertractor.R;
import com.xmu.supertractor.Tools.Tools;
import com.xmu.supertractor.connection.wifi.service.WifiServerConnectService;
import com.xmu.supertractor.desk.Desk;
import com.xmu.supertractor.parameter.Setting;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.player.PlayerList;
import com.xmu.supertractor.pokegame.Logic;
import com.xmu.supertractor.pokegame.PokeGameTools;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.xmu.supertractor.Tools.PrintLog.log;


public class WifiServerActivity extends Activity {
    private String tag = "WifiServerActivity";
    private TextView tv_wificonstate;
    public Context mcontext;
    private MyHandler uihandler;
    private WifiServerActivity mactivity;
    private TextView tv_ip;
    private WifiServerConnectService connectservice;
    private ArrayAdapter<String> arrayadapter = null;
    private List<String> l;
    private boolean destoryed = false;

    private ServiceConnection connectionserver = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            PrintLog.log(tag, "onServiceDisconnected.");
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            connectservice = ((WifiServerConnectService.LocalBinder) service).getService();
            connectservice.wa = mactivity;
            connectservice.setuiHandler(uihandler);
            PrintLog.log(tag, "onServiceConnected.");
            connectservice.onStart();
        }

    };
    private TextView tv_partnerwifi;
    private TextView tv_zhuangwifi;
    private TextView tv_levela_wifi;
    private TextView tv_levelb_wifi;
    private TextView tv_mainlevel_wifi;
    private TextView tv_printzhuang;

    public void setTv_wificonstate(String s) {
        tv_wificonstate.setText(s);
    }

    public void setTv_ip(String s) {
        tv_ip.setText(s);
    }

    private void init_view() {
        uihandler = new MyHandler(WifiServerActivity.this);
        tv_partnerwifi = (TextView) findViewById(R.id.tv_partnerwifi);
        tv_zhuangwifi = (TextView) findViewById(R.id.tv_zhuangwifi);
        tv_partnerwifi.setHint("请点击左侧列表选择庄家");
        tv_zhuangwifi.setText("----");
        tv_wificonstate = (TextView) findViewById(R.id.tv_wificonstate);
        tv_levela_wifi = (TextView) findViewById(R.id.tv_levela_wifi);
        tv_levelb_wifi = (TextView) findViewById(R.id.tv_levelb_wifi);
        tv_printzhuang = (TextView) findViewById(R.id.tv_printzhuang);
        tv_levela_wifi.setHint("2");
        tv_levelb_wifi.setHint("2");
        Button bt_start_wifi = (Button) findViewById(R.id.bt_start_wifi);
        tv_mainlevel_wifi = (TextView) findViewById(R.id.tv_mainlevel_wifi);
        tv_mainlevel_wifi.setHint("2");
        Button bt_wifi_con_state = (Button) findViewById(R.id.bt_wifi_con_state);
        tv_ip = (TextView) findViewById(R.id.tv_ip);
        ListView lv_player = (ListView) findViewById(R.id.lv_player);
        l = new ArrayList<>();
        arrayadapter = new ArrayAdapter<>(mcontext,
                android.R.layout.simple_list_item_1, l);
        lv_player.setAdapter(arrayadapter);
        lv_player.setOnItemClickListener(new ListviewOnClickListener());
        lv_player.setSelector(android.R.color.holo_orange_light);
        Onclickst st = new Onclickst();
        bt_wifi_con_state.setOnClickListener(st);
        bt_start_wifi.setOnClickListener(st);
        tv_zhuangwifi.setOnClickListener(st);
        tv_levela_wifi.setOnClickListener(st);
        tv_levelb_wifi.setOnClickListener(st);
        tv_mainlevel_wifi.setOnClickListener(st);
        RadioGroup rd = (RadioGroup) findViewById(R.id.rd_new_or_not);
        rd.setOnCheckedChangeListener(new RadioListener());
        if (rd.getCheckedRadioButtonId() == R.id.rd_newgame) {
            Logic.init_new_game_status();
            init_new_game_view();
        } else {
            Logic.init_custom_status();
            init_custom_view();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiserver);
        Log.d("xiaotang", "WifiServerActivity---" + "onCreate");
        mcontext = this;
        mactivity = this;
        start_connect_service();
    }

    static class MyHandler extends Handler {

        WeakReference<WifiServerActivity> wifiServerActivityWeakReference=null;
        WifiServerActivity wifiServerActivity=null;

        MyHandler(WifiServerActivity wa){
            wifiServerActivityWeakReference=new WeakReference<>(wa);
            wifiServerActivity=wifiServerActivityWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    wifiServerActivity.l.clear();
                    ArrayList<String> temp=Tools.cast(msg.obj);
                    wifiServerActivity.l.addAll(temp);
                    wifiServerActivity.l.remove(0);
                    log(wifiServerActivity.tag, "flush listview");
                    wifiServerActivity.arrayadapter.notifyDataSetChanged();
                    break;
                case 2:

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        init_view();
        Log.d("xiaotang", "WifiServerActivity---" + "onStart");
        super.onStart();
    }

    private void start_connect_service() {
        Intent startClientService = new Intent(mactivity,
                WifiServerConnectService.class);
        startService(startClientService);
        bindService(startClientService, connectionserver, BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        Log.d("xiaotang", "WifiServerActivity---" + "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d("xiaotang", "WifiServerActivity---" + "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("xiaotang", "WifiServerActivity---" + "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("xiaotang", "WifiServerActivity---" + "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("xiaotang", "WifiServerActivity---" + "onDestory");
        close_connect_service();
        setContentView(R.layout.acticity_null);
        super.onDestroy();
    }

    private void close_connect_service() {
        if (!destoryed) {
            Intent stopIntent = new Intent(this, WifiServerConnectService.class);
            stopService(stopIntent);
            unbindService(connectionserver);
        }
        destoryed=true;
    }


    class Onclickst implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_wifi_con_state:
                    connectservice.get_wifi_info();
                    break;
                case R.id.bt_start_wifi:
                    if (Status.connected_num < 3) {
                        PokeGameTools.MyToast(mcontext, "玩家人数不足");
                        return;
                    }
                    if (0 == Status.server_partner) {
                        PokeGameTools.MyToast(mcontext, "请点击左侧列表选择对家");
                        return;
                    }
                    if (Setting.user_level) {
                        if (0 == Status.lord_number) {
                            PokeGameTools.MyToast(mcontext, "请选择庄家");
                            return;
                        }
                        if (Status.level_a == 0 || Status.level_b == 0) {
                            PokeGameTools.MyToast(mcontext, "请选择双方等级");
                            return;
                        }
                        if (0 == Status.main_level) {
                            PokeGameTools.MyToast(mcontext, "请选择主打等级");
                            return;
                        }
                    }
                    close_connect_service();
                    start_game_activity();
                    break;
                case R.id.tv_zhuangwifi:
                    if (l.size() < 1) {
                        PokeGameTools.MyToast(mcontext, "没有连接的玩家");
                        return;
                    }
                    int num = PlayerList.getPlayerList().map.size();
                    String[] a = new String[num];
                    for (int i = 1; i <= num; ++i)
                        a[i - 1] = Desk.dk_getInstance().getMember(i).name;
                    AlertDialog.Builder builder_zhuang = new AlertDialog.Builder(mcontext);
                    builder_zhuang.setTitle("选择庄家");
                    builder_zhuang.setItems(a, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Logic.setzhuang(i + 1);
                            tv_zhuangwifi.setText(Desk.dk_getInstance().getMember(i + 1).name);
                        }
                    });
                    builder_zhuang.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder_zhuang.show();
                    break;
                case R.id.tv_levela_wifi:
                    String[] b = new String[12];
                    for (int i = 0; i <= 11; ++i)
                        b[i] = (i + 2) + "";
                    AlertDialog.Builder builder_levela = new AlertDialog.Builder(mcontext);
                    builder_levela.setTitle("选择我方等级");
                    builder_levela.setItems(b, new DialogInterface.OnClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Logic.setlevala(i + 2);
                            tv_levela_wifi.setText((i + 2) + "");
                        }
                    });
                    builder_levela.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder_levela.show();
                    break;
                case R.id.tv_levelb_wifi:
                    String[] c = new String[12];
                    for (int i = 0; i <= 11; ++i)
                        c[i] = (i + 2) + "";
                    AlertDialog.Builder builder_levelb = new AlertDialog.Builder(mcontext);
                    builder_levelb.setTitle("选择对方等级");
                    builder_levelb.setItems(c, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Logic.setlevalb(i + 2);
                            tv_levelb_wifi.setText(" " + (i + 2) + " ");
                        }
                    });
                    builder_levelb.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder_levelb.show();
                    break;
                case R.id.tv_mainlevel_wifi:
                    if ((0 == Status.level_a) || (0 == Status.level_b)) {
                        PokeGameTools.MyToast(mcontext, "请选择双方等级");
                        return;
                    }
                    String[] d = new String[2];
                    d[0] = Status.level_a + "";
                    d[1] = Status.level_b + "";
                    AlertDialog.Builder builder_mainlevel = new AlertDialog.Builder(mcontext);
                    builder_mainlevel.setTitle("选择主打等级");
                    builder_mainlevel.setItems(d, new DialogInterface.OnClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (0 == i)
                                Logic.setmainlevel(Status.level_a);
                            else
                                Logic.setmainlevel(Status.level_b);
                            tv_mainlevel_wifi.setText(Status.main_level + "");
                        }
                    });
                    builder_mainlevel.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder_mainlevel.show();
                    break;
                default:
                    break;
            }
        }
    }

    private void start_game_activity() {
        Intent intent = new Intent();
        intent.setClass(mcontext, GameActivity.class);
        startActivity(intent);
    }


    class ListviewOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Logic.setpartner(i + 2);
            Logic.seta_or_b(i+2);
            tv_partnerwifi.setText(Desk.dk_getInstance().getMember(i + 2).name);

        }
    }

    class RadioListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            if (radioButtonId == R.id.rd_newgame) {
                Logic.init_new_game_status();
                init_new_game_view();
            } else if (radioButtonId == R.id.rd_custom) {
                Logic.init_custom_status();
                init_custom_view();
            }
        }
    }

    private void init_new_game_view() {
        tv_printzhuang.setVisibility(View.INVISIBLE);
        tv_zhuangwifi.setEnabled(false);
        tv_zhuangwifi.setVisibility(View.INVISIBLE);
        tv_levela_wifi.setText("2");
        tv_levelb_wifi.setText("2");
        tv_mainlevel_wifi.setText("2");
        tv_levela_wifi.setEnabled(false);
        tv_levelb_wifi.setEnabled(false);
        tv_mainlevel_wifi.setEnabled(false);
    }

    @SuppressLint("SetTextI18n")
    private void init_custom_view() {
        tv_printzhuang.setVisibility(View.VISIBLE);
        tv_zhuangwifi.setVisibility(View.VISIBLE);
        tv_zhuangwifi.setText("______");
        tv_levela_wifi.setText("_____");
        tv_levelb_wifi.setText("_____");
        tv_mainlevel_wifi.setText("_____");
        tv_zhuangwifi.setEnabled(true);
        tv_levela_wifi.setEnabled(true);
        tv_levelb_wifi.setEnabled(true);
        tv_mainlevel_wifi.setEnabled(true);
    }
}

