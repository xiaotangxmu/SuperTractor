package com.xmu.supertractor.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.xmu.supertractor.R;
import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.bluetooth.service.BlueClientConnectService;
import com.xmu.supertractor.connection.logic.Connectionlogic;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Player_Info;
import com.xmu.supertractor.player.Me;
import com.xmu.supertractor.pokegame.PokeGameTools;


import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends Activity {
	private Button bt_roomprepare = null;
	public Context rContext=null;
	public Activity rActivity=null;
	private ListView lv_roomshow=null;
	private Me me=null;
	private ArrayAdapter<String> arrayadapter = null;
	private MyOnClickListener l=null;
	private List<String> data = new ArrayList<>();
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdmin.ACTION_DATA_TO_ACTIVITY.equals(action)) {
				TransmitUnit recdata = (TransmitUnit) (intent
						.getSerializableExtra(BluetoothAdmin.DATA));
				int rectype = recdata.type;
				Log.d("my", "RoomActivity Receive_Data:" + "type:" + rectype);
				switch (rectype) {
				case 4:
					String str=intent.getStringExtra("str");
					if(!data.contains(str)){
						data.add(str);
						arrayadapter.notifyDataSetChanged();
					}
					break;
				case 10:
					Log.d("game","RoomActivity Start_Game");
					Intent stopServerService = new Intent(rContext,
							BlueClientConnectService.class);
					stopService(stopServerService);
					Intent startgame=new Intent();
					startgame.setClass(rContext, GameActivity.class);
					startActivity(startgame);
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);
		rContext=this;
		rActivity=this;
		l=new MyOnClickListener();
		Log.d("my", "RoomActivity onCreate");
		bt_roomprepare = (Button) findViewById(R.id.bt_roomprepare);
		bt_roomprepare.setOnClickListener(l);	
		lv_roomshow=(ListView) findViewById(R.id.lv_roomshow);
		arrayadapter = new ArrayAdapter<>(rContext,
				android.R.layout.simple_expandable_list_item_1,data);
		lv_roomshow.setAdapter(arrayadapter);
		PokeGameTools.MyToast(rContext, "连接"
				+ BluetoothAdmin.contectedDevices.get(0).getName()
				+ "成功");
		Intent it=this.getIntent();
		int i=it.getIntExtra("i", 0);
		me=Me.get_me();
		Unit_Player_Info u1= new Unit_Player_Info(i,me.name);
		TransmitUnit senddata = new TransmitUnit(1,me.seq,0,u1);
		Connectionlogic.send_message_to_server(senddata);
		l.prepare();
	}

	class MyOnClickListener implements OnClickListener {
		boolean flag_prepare = false;

		public void onClick(View v) {
			if (v.getId() == R.id.bt_roomprepare) {
				if (false == flag_prepare) {
					prepare();
				} else {
					cancel_prepare();
				}
			}
		}

		private void cancel_prepare() {
			Log.d("my", "RoomActivity Cancel Prepared.");
			Unit_Player_Info ui= new Unit_Player_Info(me.seq,me.name);
			TransmitUnit senddata = new TransmitUnit(3,me.seq,0,ui);
			Connectionlogic.send_message_to_server(senddata);
			bt_roomprepare.setText("准备游戏");
			flag_prepare = false;
		}

		private void prepare() {
			Log.d("my", "RoomActivity Prepared.");
			Unit_Player_Info ui= new Unit_Player_Info(me.seq,me.name);
			TransmitUnit senddata = new TransmitUnit(2,me.seq,0,ui);
			Connectionlogic.send_message_to_server(senddata);
			bt_roomprepare.setText("取消准备");
			flag_prepare = true;
		}
	}

	@Override
	protected void onStart() {
		Log.d("my", "RoomActivity onStart");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdmin.ACTION_DATA_TO_ACTIVITY);
		registerReceiver(broadcastReceiver, intentFilter);
		super.onStart();
	}

	protected void onStop() {
		// 关闭后台Service
		Log.d("my", "RoomActivity onStop");
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.d("my", "RoomActivity onResume");
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("my", "RoomActivity onDestory");
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.d("my", "RoomActivity onRestart!");
		Intent startClientService = new Intent(rContext,
				BlueClientConnectService.class);
		startService(startClientService);
		super.onRestart();
	}

}
