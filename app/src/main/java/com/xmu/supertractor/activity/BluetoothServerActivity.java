package com.xmu.supertractor.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.xmu.supertractor.R;
import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.bluetooth.service.BlueServerConnectService;
import com.xmu.supertractor.connection.logic.Connectionlogic;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Player_Info;
import com.xmu.supertractor.parameter.Setting;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.player.Me;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.util.ArrayList;
import java.util.List;

public class BluetoothServerActivity extends Activity {
    public  Activity sActivity = null;
    public  Context sContext = null;
    private TextView tv_num = null;
    private Button bt_start = null;
    private ArrayAdapter<String> arrayadapter = null;
    private List<String> data = new ArrayList<>();
    private Me me = Me.get_me();
    public EditText et_partner = null;


    private ServiceConnection connectionserver = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            BlueServerConnectService serverconnectservice = ((BlueServerConnectService.LocalBinder) service).getService();
        }

    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        int num_prepared = 0;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothAdmin.ACTION_DATA_TO_ACTIVITY.equals(action)) {
                TransmitUnit recdata = (TransmitUnit) (intent
                        .getSerializableExtra(BluetoothAdmin.DATA));
                int rectype = recdata.type;
                Log.d("my", "BluetoothServerActivity Receive_Data:" + "type:" + rectype);
                switch (rectype) {
                    // 接受连接玩家信息
                    case 1:
                        Unit_Player_Info ui = (Unit_Player_Info) recdata.obj;
                        data.add(ui.seq + ".      " + ui.name);
                        arrayadapter.notifyDataSetChanged();
                        break;
                    // 玩家准备
                    case 2:
                        Unit_Player_Info ui2 = (Unit_Player_Info) recdata.obj;
                        data.set(ui2.seq - 2, ui2.seq + ".      " + ui2.name + "    Ready.");
                        arrayadapter.notifyDataSetChanged();
                        if (Setting.num_player - 1 == ++num_prepared) {
                            bt_start.setEnabled(true);
                            PokeGameTools.MyToast(sContext, "请开始游戏");
                        }
                        break;
                    // 玩家取消准备
                    case 3:
                        Unit_Player_Info ui3 = (Unit_Player_Info) recdata.obj;
                        data.set(ui3.seq - 2, ui3.seq + ".      " + ui3.name);
                        arrayadapter.notifyDataSetChanged();
                        if (Setting.num_player - 1 == (num_prepared--)) {
                            bt_start.setEnabled(false);
                        }
                        break;
                    default:
                        break;
                }

            } else if (BluetoothAdmin.ACTION_CONNECT_SUCCESS.equals(action)) {
                Log.d("my", "BluetoothServerActivity Connect_Success.");
                tv_num.setText(" " + BlueServerConnectService.num_temp);
                Unit_Player_Info u1 = new Unit_Player_Info(BlueServerConnectService.num_temp, me.name);
                TransmitUnit msg_data = new TransmitUnit(0, 0, BlueServerConnectService.num_temp, u1);
                Connectionlogic.send_message_to_client(msg_data);
            }
        }
    };
    private EditText et_levela;
    private EditText et_levelb;
    private EditText ed_zhuang;
    private EditText et_main_level;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothserver);
        Log.d("my", "BluetoothServerActivity onCreate");
        sActivity = this;
        sContext = this;
        init_Ui();
    }

    private void init_Ui() {
        tv_num = (TextView) findViewById(R.id.tv_num);
        TextView tv_showname = (TextView) findViewById(R.id.tv_showname);
        bt_start = (Button) findViewById(R.id.bt_start);
        et_partner = (EditText) findViewById(R.id.et_partner);
        et_levela = (EditText) findViewById(R.id.ed_levela);
        et_levela.setText("2");
        et_levelb = (EditText) findViewById(R.id.ed_levelb);
        et_levelb.setText("2");
        ed_zhuang = (EditText) findViewById(R.id.ed_zhuang);
        ed_zhuang.setText("0");
        et_main_level = (EditText) findViewById(R.id.et_main_level);
        bt_start.setOnClickListener(new myOnClickListener());
        Button bt_opensearch = (Button) findViewById(R.id.bt_opensearch);
        bt_opensearch.setOnClickListener(new myOnClickListener());
        bt_start.setEnabled(false);
        ListView lv_servershow = (ListView) findViewById(R.id.lv_servershow);
        arrayadapter = new ArrayAdapter<>(sContext,
                android.R.layout.simple_expandable_list_item_1, data);
        lv_servershow.setAdapter(arrayadapter);
        me.seq = 1;
        String name = me.name;
        tv_showname.setText(name);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.d("my", "BluetoothServerActivity onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {

        // 开启后台service
        Log.d("my", "BluetoothServerActivity onStart");
        // 注册BoradcasrReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdmin.ACTION_DATA_TO_ACTIVITY);
        intentFilter.addAction(BluetoothAdmin.ACTION_CONNECT_SUCCESS);
        registerReceiver(broadcastReceiver, intentFilter);
        Intent startServerService = new Intent(BluetoothServerActivity.this,
                BlueServerConnectService.class);
        startService(startServerService);
        bindService(startServerService, connectionserver, BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onStop() {

        Log.d("my", "BluetoothServerActivity onStop().");
        unregisterReceiver(broadcastReceiver);
        unbindService(connectionserver);
        super.onStop();
    }

    class myOnClickListener implements OnClickListener {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_start:
                    int partner;
                    int lord;
                    int main_level;
                    int la, lb;
                    if (et_partner.getText().toString().isEmpty()) {
                        PokeGameTools.MyToast(sContext, "请输入正确的玩家序号.");
                        break;
                    } else {
                        partner = Integer.parseInt(et_partner.getText().toString());
                        if (partner < 2 || partner > 4) {
                            PokeGameTools.MyToast(sContext, "请输入正确的玩家序号.");
                            et_partner.setText("");
                            break;
                        }
                    }
                    if (et_levela.getText().toString().isEmpty() || et_levelb.getText().toString().isEmpty()) {
                        PokeGameTools.MyToast(sContext, "请输入正确的级别.");
                        break;
                    } else {
                        la = Integer.parseInt(et_levela.getText().toString());
                        lb = Integer.parseInt(et_levelb.getText().toString());
                        if (la < 1 || la > 13 || lb < 1 || lb > 13) {
                            PokeGameTools.MyToast(sContext, "请输入正确的级别.");
                            et_levela.setText("2");
                            et_levelb.setText("2");
                            break;
                        }
                    }
                    if (la != 2 || lb != 2) {
                        Setting.user_level = true;
                        Status.first_round = false;
                        Status.level_a = la;
                        Status.level_b = lb;
                    } else {
                        Setting.user_level = false;
                        Status.first_round = true;
                    }
                    if (Setting.user_level) {
                        if (ed_zhuang.getText().toString().isEmpty()) {
                            PokeGameTools.MyToast(sContext, "请输入庄家编号.");
                            break;
                        }
                        lord = Integer.parseInt(ed_zhuang.getText().toString());
                        if (lord < 1 || lord > 4) {
                            PokeGameTools.MyToast(sContext, "庄家编号不正确");
                            break;
                        } else {
                            Status.lord_number = lord;
                            Log.d("my", "set lord:" + Status.lord_number);
                        }
                        if (et_main_level.getText().toString().isEmpty()) {
                            PokeGameTools.MyToast(sContext, "请输入主级别.");
                            break;
                        }
                        main_level = Integer.parseInt(et_main_level.getText().toString());
                        if (main_level >= 1 && main_level <= 13 && (main_level == Status.level_a || main_level == Status.level_b)) {
                            Status.main_level = main_level;
                        } else {
                            PokeGameTools.MyToast(sContext, "主级别不正确.");
                            break;
                        }
                    }
                    Status.server_partner = partner;
                    Intent stopServerService = new Intent(sContext,
                            BlueServerConnectService.class);
                    stopService(stopServerService);
                    Intent intent = new Intent();
                    intent.setClass(sContext, GameActivity.class);
                    startActivity(intent);
                    break;
                case R.id.bt_opensearch:
                    openDiscovery(300);
                    break;
                default:
                    break;
            }
        }
    }

    public void openDiscovery(int duration) {
        if (duration <= 0 || duration > 300) {
            duration = 200;
        }
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.d("my", "蓝牙可见" + duration + "s");
    }

    @Override
    protected void onRestart() {
        Log.d("my", "BluetoothServerActivity onRestart().");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub\
        Log.d("my", "BluetoothServerActivity onPause().");
        super.onPause();
    }

}
