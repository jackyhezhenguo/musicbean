package com.musicbean.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * <p/>
 * Created by jiang on 2016/1/23.
 */
public class NetworkUtil {

    /**
     * 检查网络连接
     *
     * @param context 用于获取ConnectivityManager
     * @return 网络可用返回true，否则返回false
     */
    public static boolean checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * 获取网络连接类型
     *
     * @param context 用于获取ConnectivityManager
     * @return 网络不可用返回-1，手机网络连接返回0，WiFi网络连接返回1
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return -1;
        } else {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
