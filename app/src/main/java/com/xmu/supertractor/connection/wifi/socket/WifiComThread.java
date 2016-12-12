package com.xmu.supertractor.connection.wifi.socket;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class WifiComThread extends Thread {

    private Handler handler;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private Socket socket;
    private boolean isRun;
    //    private String tag="WifiComThread";

    public void setHandler(Handler h){
        this.handler=h;
//        log(tag,this.toString()+"set handler:"+h.toString());
    }

    public WifiComThread(Handler handler, Socket socket) {
//        log(tag,"comthread create:"+socket.toString());
        this.handler= handler;
        this.socket = socket;
        isRun = true;
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
            Log.d("xiaotang","fail");
            e.printStackTrace();
            handler.obtainMessage(2).sendToTarget();
        }
    }

    public void run() {
        Message message=handler.obtainMessage(0);
        message.obj=socket.getRemoteSocketAddress()+"";
        message.sendToTarget();
        while (true) {
            if (!isRun)
                break;
            try {
                Object obj = inStream.readObject();
                //发送成功读取到对象的消息，消息的obj参数为读取到的对象
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = obj;
                msg.sendToTarget();
            } catch (Exception ex) {
                //发送连接失败消息
                handler.obtainMessage(2).sendToTarget();
                ex.printStackTrace();
                return;
            }
        }

        //关闭流
        if (inStream != null) {
            try {
                inStream.close();
                Log.d("xiaotang", "BluetoothComThread inStream.close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outStream != null) {
            try {
                outStream.close();
                Log.d("xiaotang", "BluetoothComThread outStream.close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
                Log.d("xiaotang", "BluetoothComThread socket.close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeObject(Object obj) {
        try {
//            log(tag,"---------thread:"+this.toString()+ "deat:"+((TransmitUnit)obj).dest+" type:"+((TransmitUnit)obj).type);
            outStream.flush();
            outStream.writeObject(obj);
            outStream.flush();
        } catch (IOException e) {
            Log.d("xiaotang", "BluetoothComThread send_error!");
            e.printStackTrace();
        }
    }
}
