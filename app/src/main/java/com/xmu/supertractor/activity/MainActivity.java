package com.xmu.supertractor.activity;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;
import com.xmu.supertractor.R;
import com.xmu.supertractor.Tools.Tools;
import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.player.Me;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.util.ArrayList;

import static com.xmu.supertractor.Tools.PrintLog.log;

public class MainActivity extends Activity {
    private EditText ed_name = null;
    public  Activity mActivity = null;
    public  Context mContext = null;
    private String tag="MainActivity";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mContext = this;
        TextView tv_version= (TextView) findViewById(R.id.tv_version);
        tv_version.setText(""+Tools.getVersionName(mContext));
        MyOnClickListener l = new MyOnClickListener();
        Button bt_toclient = (Button) findViewById(R.id.bt_toclient);
        Button bt_toserver = (Button) findViewById(R.id.bt_toserver);
        RadioGroup rd = (RadioGroup) findViewById(R.id.radioGroup);
        Button bt_help = (Button) findViewById(R.id.bt_help);
        bt_help.setOnClickListener(l);
        ed_name = (EditText) findViewById(R.id.ed_name);
        Button bt_exit = (Button) findViewById(R.id.bt_exit);
        bt_toclient.setOnClickListener(l);
        bt_toserver.setOnClickListener(l);
        bt_exit.setOnClickListener(l);
        rd.setOnCheckedChangeListener(new RadioListener());
        ed_name.setText(BluetoothAdmin.adapter.getName());
    }

    private boolean checkinput(String name) {
        boolean flag = true;
        if (name.isEmpty()) {
            PokeGameTools.MyToast(mContext, "游戏昵称不能为空！");
            flag = false;
        }
        if (name.length()>15) {
            PokeGameTools.MyToast(mContext, "游戏昵称长度超过15个字符");
            flag = false;
        }
        return flag;
    }

    class MyOnClickListener implements OnClickListener {
        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            String name = ed_name.getText().toString();
            CrashReport.setUserId(name);
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.bt_toserver:
                    if (checkinput(name)) {
                        Me.create_me(name);
                        Me.get_me().server_flag = true;
                        if (Status.wifi_or_bluetooth) {
                            if (checkwifi()) return;
                            intent.setClass(mContext, WifiServerActivity.class);
                        } else {
                            intent.setClass(mContext, BluetoothServerActivity.class);
                        }
                        startActivity(intent);
                    }
                    break;
                case R.id.bt_toclient:
                    if (checkinput(name)) {
                        Me.create_me(name);
                        Me.get_me().server_flag = false;
                        if (Status.wifi_or_bluetooth) {
                            if (checkwifi()) return;
                            intent.setClass(mContext, WifiClientActivity.class);
                        } else {
                            intent.setClass(mContext, BluetoothClientActivity.class);
                        }
                        startActivity(intent);
                    }
                    break;
                case R.id.bt_exit:
                    log(tag, "Exit");
                    finish();
//                    ArrayList aa=null;
//                    aa.add(0,1);
                    break;
                case R.id.bt_help:
                    AboutDialog a=new AboutDialog(mContext);
                    a.show();
                    WindowManager m = getWindowManager();
                    Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
                    //noinspection ConstantConditions
                    android.view.WindowManager.LayoutParams p = a.getWindow().getAttributes();  //获取对话框当前的参数值
                    p.height = (int) (d.getHeight() * 0.9);   //高度设置为屏幕的0.9
                    p.width = (int) (d.getWidth() * 0.9);    //宽度设置为屏幕的0.9
                    a.getWindow().setAttributes(p);
                    break;
                default:
                    break;
            }
        }

        private boolean checkwifi() {
            WifiAdmin wifiAdmin = new WifiAdmin(mContext);
            if (!wifiAdmin.isWifiApEnabled()) {
                if (!wifiAdmin.isWifiEnabled()) {
                    PokeGameTools.MyToast(mContext, "Wifi未开启.");
                    return true;
                }
                if (0x01 != wifiAdmin.isWifiContected(mContext)) {
                    PokeGameTools.MyToast(mContext, "Wifi未连接");
                    return true;
                }
            }
            return false;
        }

    }

    class RadioListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            Status.wifi_or_bluetooth = radioButtonId == R.id.rb_wifi;
            PokeGameTools.MyToast(mContext, Status.wifi_or_bluetooth + "");
        }
    }

//    public void openBluetooth() {
//        if (!BluetoothAdmin.adapter.isEnabled()) {
//            Log.d("my", "蓝牙未打开");
//            Log.d("my", "打开蓝牙面板");
//            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
//        } else {
//            Log.d("my", "蓝牙已打开");
//        }
//    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        log(tag,"onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        log(tag,"onResume");
        CrashReport.setUserSceneTag(mContext,32871);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        log(tag,"onPausee");
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        log(tag,"onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        log(tag,"onDestroy");
        setContentView(R.layout.acticity_null);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent); //这一句必须的，否则Intent无法获得最新的数据
    }

}

