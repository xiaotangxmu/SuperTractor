package com.xmu.supertractor.connection.wifi.Multicast;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


import com.xmu.supertractor.Tools.PrintLog;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.parameter.Status;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class BroadcastThread extends Thread {
    @SuppressWarnings("FieldCanBeLocal")
    private String tag = "BroadcastThread";
    private String ip;
    private volatile Boolean flag = true;
    private volatile MulticastSocket ms;
    private Context context;
    private boolean firstsend;

    public BroadcastThread(Context context) {
        this.context = context;
        this.firstsend = true;
    }

    public void stopthread() {
        this.flag = false;
        this.interrupt();
    }

    private void init() {
        this.flag = true;
        try {
            /*创建socket实例*/
            ms = new MulticastSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close_broad() {
        this.ms.close();
    }

    private InetAddress getBroadcastAddress() throws UnknownHostException {
        if (WifiAdmin.isAp)
            return InetAddress.getByName("192.168.43.255");
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


    @Override
    public void run() {
        init();
        Log.d("xiaotang", "server thread run...");
        while (Status.connected_num < 3 && flag) {
            //这个地方可以输出判断该地址是不是广播类型的地址
            try {
                ms.setTimeToLive(4);
                //将本机的IP（这里可以写动态获取的IP）地址放到数据包里，其实server端接收到数据包后也能获取到发包方的IP的
                byte[] data = ip.getBytes();
                InetAddress address = getBroadcastAddress();
                DatagramPacket dataPacket = new DatagramPacket(data, data.length, address,
                        8086);
                ms.send(dataPacket);
                if (firstsend) {
                    firstsend=false;
                    PrintLog.log(tag, "sending:" + address.toString() + "......");
                }
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        close_broad();
        PrintLog.log(tag, "Connectnum:" + Status.connected_num + ",stop send broadcast.");

    }

    public void setmyip(String ip_str) {
        this.ip = ip_str;
    }
}
