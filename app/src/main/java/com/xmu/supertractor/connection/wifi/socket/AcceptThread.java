package com.xmu.supertractor.connection.wifi.socket;

import android.os.Handler;


import com.xmu.supertractor.Tools.PrintLog;
import com.xmu.supertractor.connection.WifiTools;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.parameter.Status;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class AcceptThread extends Thread {
    private volatile ServerSocket mServerSocket;
    private String tag = "AcceptThread";
    private volatile boolean flag = true;
    public static boolean connected_statu = false;

    private Handler handler;

    public void stopthread() throws IOException {
        if (flag) {
            this.flag = false;
            mServerSocket.close();
        }
    }

    public AcceptThread(Handler handler) {
        PrintLog.log(tag, "into SocketServer(final int port, ServerMsgListener serverListener) ...................................");
        this.handler = handler;
    }

    public void run() {
        try { // init server
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            InetSocketAddress address = new InetSocketAddress(WifiTools.port);
            mServerSocket.bind(address);
            PrintLog.log(tag, "server  =" + mServerSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mServerSocket != null) {
            while (Status.connected_num < 3 && flag) {
                try {
                    PrintLog.log(tag, "Listening......");
                    Socket socket = mServerSocket.accept();
                    Status.connected_num++;
                    if (socket != null) {
                        PrintLog.log(tag, "Accept:" + Status.connected_num + "--" + socket.getRemoteSocketAddress().toString());
                        WifiComThread comthread = new WifiComThread(handler, socket);
                        WifiAdmin.CommunThread_map.put(Status.connected_num + 1, comthread);
                        comthread.start();
                        //noinspection StatementWithEmptyBody
                        while (!connected_statu)
                            ;
                        connected_statu = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (Status.connected_num == 3) {
            PrintLog.log(tag, "3 player connected!!!!");
            handler.obtainMessage(2).sendToTarget();
        }
        PrintLog.log(tag, "accept thread out!");
    }
}
