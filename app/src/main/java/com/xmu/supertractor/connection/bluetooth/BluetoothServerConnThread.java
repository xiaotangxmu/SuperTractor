package com.xmu.supertractor.connection.bluetooth;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.bluetooth.service.BlueServerConnectService;
import com.xmu.supertractor.parameter.Setting;


public class BluetoothServerConnThread extends Thread {

	private Handler serviceHandler; // 用于同Service通信的Handler
	private BluetoothAdapter adapter;
	private BluetoothSocket socket; // 用于通信的Socket
	private BluetoothServerSocket serverSocket;
	private BluetoothDevice dev_connected = null;

	public BluetoothServerConnThread(Handler handler) {
		this.serviceHandler = handler;
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void run() {
		while (BlueServerConnectService.num_temp < Setting.num_player) {
			Log.d("my", "BluetoothServerConnThread ConnThread Started.");
			try {
				serverSocket = adapter.listenUsingRfcommWithServiceRecord(
						"Server", BluetoothAdmin.PRIVATE_UUID);
				socket = serverSocket.accept();
			} catch (Exception e) {
				// 发送连接失败消息
				serviceHandler.obtainMessage(
						BluetoothAdmin.MESSAGE_CONNECT_ERROR).sendToTarget();
				e.printStackTrace();
				return;
			} finally {
				try {
					serverSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (socket != null) {
				// 发送连接成功消息，消息的obj字段为连接的socket
				++BlueServerConnectService.num_temp;
				dev_connected = socket.getRemoteDevice();
				if (!BluetoothAdmin.contectedDevices.contains(dev_connected))
					BluetoothAdmin.contectedDevices.add(dev_connected);
				Message msg = serviceHandler.obtainMessage();
				msg.what = BluetoothAdmin.MESSAGE_CONNECT_SUCCESS;
				msg.obj = socket;
				msg.sendToTarget();
			} else {
				// 发送连接失败消息
				serviceHandler.obtainMessage(
						BluetoothAdmin.MESSAGE_CONNECT_ERROR).sendToTarget();
			}
			
		}
		Log.d("my", "BluetoothServerConnThread Quit.");
	}
}
