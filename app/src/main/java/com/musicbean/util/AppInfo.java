package com.musicbean.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created by boyo on 16/6/20.
 */
public class AppInfo {
    private static String uuid = null;

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getUuid(Context context) {
        if (TextUtils.isEmpty(uuid)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceid = tm.getDeviceId();
            String androidid = android.provider.Settings.System.getString(context.getApplicationContext().getContentResolver(), "android_id");
            String serialno = getDeviceSerial();
            uuid = MD5Utils.getMD5(deviceid + androidid + serialno);
        }
        return uuid;
    }

    private static String getDeviceSerial() {
        String serial = "";
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class);
            serial = (String) get.invoke(clazz, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }
}
