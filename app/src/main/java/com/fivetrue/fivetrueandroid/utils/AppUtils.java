package com.fivetrue.fivetrueandroid.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class AppUtils {

    public static int getApplicationVersionCode(Context context){
        int code = 0;
        if(context != null){
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            code = pInfo.versionCode;
        }
        return code;
    }

    public static String getApplicationVersionName(Context context){
        String name = null;
        if(context != null){
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            name = pInfo.versionName;
        }
        return name;
    }


    public static void goAppStore(Context context, String marketUrl){
        if(context != null){
            String appPackageName = context.getPackageName();
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
        }
    }
}
