package com.xmu.supertractor.connection.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;

import java.io.IOException;


public class BluetoothClientConnThread extends Thread {

	private Handler serviceHandler;		//用于向客户端Service回传消息的handler
	private BluetoothDevice serverDevice;	//服务器设备
	private BluetoothSocket socket;		//通信Socket
	

	public BluetoothClientConnThread(Handler handler, BluetoothDevice serverDevice) {
		this.serviceHandler = handler;
		this.serverDevice = serverDevice;
	}
	
	@Override
	public void run() {
		Log.d("my", "BluetoothClientConnThread Try_to_connect_device:"+serverDevice.getName());
		if(BluetoothAdmin.adapter.isDiscovering())
			BluetoothAdmin.stopDiscovery();
		try {
			socket = serverDevice.createRfcommSocketToServiceRecord(BluetoothAdmin.PRIVATE_UUID);
			socket.connect();
			
		} catch (Exception ex) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//发送连接失败消息
			serviceHandler.obtainMessage(BluetoothAdmin.MESSAGE_CONNECT_ERROR).sendToTarget();
			return;
		}
		if (!BluetoothAdmin.contectedDevices.contains(serverDevice))
			BluetoothAdmin.contectedDevices.add(serverDevice);
		//发送连接成功消息，消息的obj参数为连接的socket
		Message msg = serviceHandler.obtainMessage();
		msg.what = BluetoothAdmin.MESSAGE_CONNECT_SUCCESS;
		msg.obj = socket;
		msg.sendToTarget();
		Log.d("my", "BluetoothClientConnThread Quit.");
	}
}
