package com.xmu.supertractor.connection.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BluetoothComThread extends Thread {

	private Handler serviceHandler;		//��Serviceͨ�ŵ�Handler
	private BluetoothSocket socket;
	private ObjectInputStream inStream;		//����������
	private ObjectOutputStream outStream;	//���������
	public volatile boolean isRun = true;	//���б�־λ
	

	public BluetoothComThread(Handler handler, BluetoothSocket socket) {
		this.serviceHandler = handler;
		this.socket = socket;
		try {
			this.outStream = new ObjectOutputStream(socket.getOutputStream());
			this.inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//发送连接失败消息
			serviceHandler.obtainMessage(BluetoothAdmin.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
		}
	}
	public void SetHandler(Handler handler){
		this.serviceHandler=handler;
	}
	
	@Override
	public void run() {
		Log.d("my", "BluetoothComThread CommunThread_started");
		while (true) {
			if (!isRun) {
				break;
			}
			try {
				Object obj = inStream.readObject();
				//发送成功读取到对象的消息，消息的obj参数为读取到的对象
				Message msg = serviceHandler.obtainMessage();
				msg.what = BluetoothAdmin.MESSAGE_READ_OBJECT;
				msg.obj = obj;
				msg.sendToTarget();
			} catch (Exception ex) {
				//发送连接失败消息
				serviceHandler.obtainMessage(BluetoothAdmin.MESSAGE_CONNECT_ERROR).sendToTarget();
				ex.printStackTrace();
				return;
			}
		}

		//关闭流
		if (inStream != null) {
			try {
				inStream.close();
				Log.d("my", "BluetoothComThread inStream.close");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outStream != null) {
			try {
				outStream.close();
				Log.d("my", "BluetoothComThread outStream.close");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
				Log.d("my", "BluetoothComThread socket.close");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	public void writeObject(Object obj) {
		try {
			//Log.d("com","---------thread:"+this.toString()+ "deat:"+((TransmitBean)obj).dest+" type:"+((TransmitBean)obj).type+" "+((TransmitBean)obj).msg );
			outStream.flush();
			outStream.writeObject(obj);
			outStream.flush();
		} catch (IOException e) {
			Log.d("my", "BluetoothComThread send_error!");
			e.printStackTrace();
		}
	}
}
