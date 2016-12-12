package com.xmu.supertractor.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.xmu.supertractor.R;
import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.bluetooth.service.BlueClientConnectService;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Player_Info;
import com.xmu.supertractor.player.Me;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.util.ArrayList;
import java.util.List;

public class BluetoothClientActivity extends Activity {

    private List<BluetoothDevice> discoveredDevices = null;
    @SuppressWarnings("unused")
    private BlueClientConnectService clientconnectservice = null;
    private Button bt_search = null;
    private Button bt_connect = null;
    private Button bt_bind = null;
    private TextView tv_shownameclient = null;
    private ListView lv_show = null;
    private ArrayAdapter<String> arrayadapter = null;
    public static Activity cActivity = null;
    public static Context cContext = null;
    private MyOnClickListener l;
    private Me me = Me.get_me();
    private List<String> data = new ArrayList<>();

    private ServiceConnection connectionclient = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            clientconnectservice = ((BlueClientConnectService.LocalBinder) service).getService();
        }

    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.d("my", "BluetoothClientActivity onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        Log.d("my", "BluetoothClientActivity onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d("my", "BluetoothClientActivity onDestory");
        super.onDestroy();
    }

    // 广播接收器
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdmin.ACTION_NOT_FOUND_SERVER.equals(action)) {
                Log.d("my", "BluetoothClientActivity Device_Not_Found.");
                PokeGameTools.MyToast(cContext, "未发现设备!");
                // 未发现设备

            } else if (BluetoothAdmin.ACTION_CONNECT_ERROR.equals(action)) {
                PokeGameTools.MyToast(cContext, "服务器连接失败！");
            } else if (BluetoothAdmin.ACTION_FOUND_DEVICE.equals(action)) {
                Log.d("my", "BluetoothClientActivity Device_Found.");
                getData();

            } else if (BluetoothAdmin.ACTION_CONNECT_SUCCESS.equals(action)) {
                Log.d("my", "BluetoothClientActivity Connect_Success");
                // 连接成功
            } else if (BluetoothAdmin.ACTION_DATA_TO_ACTIVITY.equals(action)) {
                // 接收数据
                TransmitUnit recunit = (TransmitUnit) (intent
                        .getSerializableExtra(BluetoothAdmin.DATA));
                int rectype = recunit.type;
                Log.d("my", "BluetoothClientActivity Receive_Data:" + "type:" +rectype);
                switch (rectype) {
                    case 0:
                        Unit_Player_Info u1=(Unit_Player_Info)  recunit.obj;
                        Intent intent_to_room = new Intent();
                        intent_to_room.setClass(cContext, RoomActivity.class);
                        intent_to_room.putExtra("i", u1.seq);
                        startActivity(intent_to_room);
                        break;
                }
            } else if ("discovery_finished".equals(action)) {
                bt_search.setEnabled(true);
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothclient);
        Log.d("my", "BluetoothClientActivity onCreate");
        cActivity = this;
        cContext = this;
        init_UI();
    }

    private void init_UI() {
        lv_show = (ListView) findViewById(R.id.lv_show);
        arrayadapter = new ArrayAdapter<String>(BluetoothClientActivity.this,
                android.R.layout.simple_expandable_list_item_1, data);
        lv_show.setAdapter(arrayadapter);
        lv_show.setOnItemClickListener(new MyOnItemClickListener());
        discoveredDevices = BluetoothAdmin.discoveredDevices;
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_connect = (Button) findViewById(R.id.bt_connected);
        bt_bind = (Button) findViewById(R.id.bt_bind);
        tv_shownameclient = (TextView) findViewById(R.id.tv_shownameclient);
        l = new MyOnClickListener();
        bt_search.setOnClickListener(l);
        bt_connect.setOnClickListener(l);
        bt_bind.setOnClickListener(l);
        tv_shownameclient.setText(me.name);
    }

    @Override
    protected void onStart() {
        // 开启后台service
        Intent startClientService = new Intent(BluetoothClientActivity.this,
                BlueClientConnectService.class);
        startService(startClientService);
        bindService(startClientService, connectionclient, BIND_AUTO_CREATE);
        // 注册BoradcasrReceiver
        init_Broadcast();
        l.bind();
        super.onStart();
    }

    private void init_Broadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdmin.ACTION_NOT_FOUND_SERVER);
        intentFilter.addAction(BluetoothAdmin.ACTION_FOUND_DEVICE);
        intentFilter.addAction(BluetoothAdmin.ACTION_DATA_TO_ACTIVITY);
        intentFilter.addAction(BluetoothAdmin.ACTION_CONNECT_SUCCESS);
        intentFilter.addAction(BluetoothAdmin.ACTION_CONNECT_ERROR);
        intentFilter.addAction("discovery_finished");
        registerReceiver(broadcastReceiver, intentFilter);
    }


    class MyOnClickListener implements OnClickListener {

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bt_search:
                    search();
                    break;
                case R.id.bt_bind:
                    bind();
                    break;
                case R.id.bt_connected:
                    connected();
                    break;
                default:
                    break;
            }
        }

        private void search() {
            Intent startSearchIntent = new Intent(
                    BluetoothAdmin.ACTION_START_DISCOVERY);
            sendBroadcast(startSearchIntent);
            bt_search.setEnabled(false);
            data.clear();
            arrayadapter.notifyDataSetChanged();
            Log.d("my", "BluetoothClientActivity Send startdiscovery Broadcast");
        }

        private void connected() {
            data.clear();
            for (BluetoothDevice i : BluetoothAdmin.contectedDevices)
                data.add(i.getName() + "," + i.getAddress());
            arrayadapter.notifyDataSetChanged();
        }

        private void bind() {
            data.clear();
            for (BluetoothDevice i : BluetoothAdmin.adapter.getBondedDevices())
                data.add(i.getName() + "," + i.getAddress());
            arrayadapter.notifyDataSetChanged();
        }
    }

    class MyOnItemClickListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Log.d("my", "BluetoothClientActivity ItemClicked " + position);
            String dev = data.get(position);
            int num = dev.lastIndexOf(",");
            String dev_name = dev.substring(0, num);
            String dev_addr = dev.substring(num + 1, dev.length());
            Log.d("my", dev_name + "," + dev_addr);
            BluetoothDevice dev_intent = BluetoothAdmin.adapter
                    .getRemoteDevice(dev_addr);
            if (BluetoothAdmin.adapter.getBondedDevices().contains(dev_intent)) {
                Log.d("my", dev_name + " Bonded");
            } else {
                Log.d("my", dev_name + " no Bonded");
            }
            if (!BluetoothAdmin.contectedDevices.isEmpty()) {
                PokeGameTools.MyToast(cContext, "已连接服务器");
            } else {
                Intent selectDeviceIntent = new Intent(
                        BluetoothAdmin.ACTION_SELECTED_DEVICE);
                selectDeviceIntent.putExtra(BluetoothAdmin.DEVICE, dev_intent);
                sendBroadcast(selectDeviceIntent);
            }
        }

    }

    @Override
    protected void onStop() {
        // 关闭后台Service
        Log.d("my", "BluetoothClientActivity onStop");
        unregisterReceiver(broadcastReceiver);
        unbindService(connectionclient);
        super.onStop();
    }

    private void getData() {
        for (BluetoothDevice i : discoveredDevices) {
            if (!data.contains(i.getName() + "," + i.getAddress())) {
                data.add(i.getName() + "," + i.getAddress());
                arrayadapter.notifyDataSetChanged();
            }
        }
    }
}
