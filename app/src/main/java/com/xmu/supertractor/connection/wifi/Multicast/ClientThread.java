package com.xmu.supertractor.connection.wifi.Multicast;

import android.os.Handler;


import com.xmu.supertractor.connection.WifiTools;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.connection.wifi.socket.WifiComThread;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import static com.xmu.supertractor.Tools.PrintLog.log;



public class ClientThread extends Thread {

    private volatile MulticastSocket cs;
    private volatile boolean flag;
    private boolean wait;
    private Handler handler;
    private String ip;
    private volatile Socket client;
    @SuppressWarnings("FieldCanBeLocal")
    private String tag = "ClientThread";


    private void init() {
        try {
            flag = true;
            cs = new MulticastSocket(8086);
            String multicastHost = "224.0.0.1";
            InetAddress receiveAddress = InetAddress.getByName(multicastHost);
            cs.joinGroup(receiveAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ClientThread(Handler handler) {
        this.handler = handler;
        this.wait=true;
        init();
    }

    public void stopthread() throws IOException {
        this.flag = false;
        this.cs.close();
    }

    @Override
    public void run() {
        if (flag) {
// TODO Auto-generated method stub
            byte buf[] = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, 1024);
            log(tag, "client thread run...");
            try {
                cs.receive(dp);
                ip = new String(buf, 0, dp.getLength());
                log(tag, "server ip:" + ip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (flag) {
            try {
                log(tag,"Try to connect "+ip+":"+ WifiTools.port+"......");
                client = new Socket(ip, WifiTools.port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (client == null) {
                if(wait){
                    wait=false;
                    log(tag,"wait for socket......");
                }
            }
            WifiComThread comthread = new WifiComThread(handler, client);
            WifiAdmin.clientrhread = comthread;
            comthread.start();
            cs.close();
        }
        log(tag, "client thread out...");
    }
}
