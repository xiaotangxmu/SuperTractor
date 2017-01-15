package com.xmu.supertractor.application;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.xmu.supertractor.card.Hand_Card;
import com.xmu.supertractor.parameter.Setting;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.pokegame.AnalyzeHandPokes;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;


public class GlobalApplication extends Application {
    public void onCreate() {

        Context context = getApplicationContext();
// 获取当前包名
        String packageName = context.getPackageName();
// 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
// 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        strategy.setAppChannel("Inner Channel");
        strategy.setAppPackageName("com.xmu.supertractor");
        strategy.setAppReportDelay(8000);
        if (Setting.debug_mode)
            Bugly.init(getApplicationContext(), "277294b907", false, strategy);
        super.onCreate();
//
//        {
//            Status.main_color = 0;
//            Status.main_level = 8;
//            PokeGameTools.computeval();
//            PokeGameTools.print_value_sort();
//        }


    }

    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}