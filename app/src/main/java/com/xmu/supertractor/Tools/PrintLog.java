package com.xmu.supertractor.Tools;

import android.util.Log;

import com.tencent.bugly.crashreport.BuglyLog;


public class PrintLog {
    public static void log(String TAG,String s){
        Log.d("xiaotang",TAG+"---"+s);
        BuglyLog.d(TAG, s);
    }
}
