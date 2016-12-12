package com.xmu.supertractor.connection.wifi.admin;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.SparseArray;

import com.xmu.supertractor.connection.wifi.socket.WifiComThread;

import java.lang.reflect.Method;



public class WifiAdmin {

    public static boolean isAp;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    public String ip;
    public static WifiComThread clientrhread;
    public static SparseArray<WifiComThread> CommunThread_map=new SparseArray<>();

    private WifiLock mWifiLock;

    public WifiAdmin(Context context) {

        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        clientrhread = null;
        CommunThread_map.clear();
    }


    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public DhcpInfo getDhcpInfo() {
        return mWifiManager.getDhcpInfo();
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }


    private static final int WIFI_CONNECTED = 0x01;
    private static final int WIFI_CONNECT_FAILED = 0x02;
    private static final int WIFI_CONNECTING = 0x03;

    public int isWifiContected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation") NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetworkInfo.getDetailedState() == DetailedState.OBTAINING_IPADDR
                || wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTING) {
            return WIFI_CONNECTING;
        } else if (wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTED) {
            return WIFI_CONNECTED;
        } else {
            return WIFI_CONNECT_FAILED;
        }
    }

//    // 检查当前WIFI状态
//    public int checkState() {
//        return mWifiManager.getWifiState();
//    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }


    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString() + ",ssid:" + mWifiInfo.getSSID();
    }

    public String getSSID() {
        return mWifiInfo.getSSID();
    }

    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}