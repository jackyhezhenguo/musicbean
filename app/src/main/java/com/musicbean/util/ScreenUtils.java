package com.musicbean.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;

/**
 * 手机屏幕工具类
 * <p/>
 * Created by jiang on 2015/10/19.
 */
public class ScreenUtils {

    /**
     * 获取手机屏幕高度
     *
     * @param context 上下文
     * @return 屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取手机屏幕宽度
     *
     * @param context 上下文
     * @return 屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取手机屏幕像素密度
     *
     * @param context 上下文
     * @return 像素密度
     */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取状态栏高度
     *
     * @param context 上下文
     * @return 状态栏高度，如果界面没有呈现将返回0
     */
    public static int getStatusBarHeight(Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dp      相对像素密度值
     * @return 像素值
     */
    public static int dp2px(Context context, float dp) {
        return (int) (dp * getDensity(context) + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
