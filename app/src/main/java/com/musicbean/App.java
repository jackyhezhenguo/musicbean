package com.musicbean;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.boyo.camerafilterfbo.encoder.TextureMovieEncoder;
import com.musicbean.manager.GiftManager;
import com.musicbean.manager.IMManager;
import com.musicbean.manager.LoginManager;
import com.musicbean.preference.MySettings;
import com.musicbean.preference.PreferencesManager;
import com.musicbean.util.CrashHandler;
import com.musicbean.util.LogUtils;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalBitmapConfig;
import net.tsz.afinal.bitmap.core.BitmapCache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;
import io.rong.imlib.RongIMClient;


/**
 * Created by boyo on 16/6/12.
 */
public class App extends Application implements LoginManager.OnLogListener, Application.ActivityLifecycleCallbacks {
    // AndroidManifest清单文件中配置的环境变量，应用所有和环境相关的配置都必须依赖该变量
    private static String environment;

    private static App instance;

    private int activityCount = 0;

    @Override
    public void onCreate() {

        super.onCreate();

        instance = this;

        // 全局崩溃异常日志输出
        CrashHandler.init(this, null);
        readEnvironmentVariable();
        logConfig();

        BitmapCache.BitmapCacheConfig cacheConfig = new BitmapCache.BitmapCacheConfig(this);
        cacheConfig.setDiskCacheDir(getCachePath());
        cacheConfig.setMaxDiskCacheSize(50 * 1024 * 1024);
        FinalBitmap.getInstance().init(new FinalBitmapConfig.Builder(this).cacheConfig(cacheConfig).build());
        PreferencesManager.getInstance().loadSettings(this, MySettings.class, null);

        LoginManager.getInstance().addOnLogListener(this);

        fixBugs();

        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            IMManager.init(this);

            if (LoginManager.getInstance().isLogin()) {
                doConnect();
            }
        }

        JPushInterface.setDebugMode(!Constant.Environment.ONLINE.equals(getEnvironment()));
        JPushInterface.init(this);

        if (LoginManager.getInstance().isLogin()) {
            JPushInterface.setAlias(this, LoginManager.getInstance().getUserInfo().id, null);
        }

        TextureMovieEncoder.initialize(getApplicationContext());

        registerActivityLifecycleCallbacks(this);
    }

    private void doConnect() {
        IMManager.getInstance().connect(LoginManager.getInstance().getUserCookie().chatoken, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                LogUtils.d("wcb", "connect onTokenIncorrect()");
            }

            @Override
            public void onSuccess(String s) {
                LogUtils.d("wcb", "connect onSuccess(): " + s);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.d("wcb", "connect onError(): " + errorCode);
            }
        });
    }

    public String getCachePath() {
        return getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 获取运行环境，应用所有和环境相关的配置都必须依赖该值
     *
     * @return 运行环境
     */
    public static String getEnvironment() {
        return environment == null ? Constant.Environment.ONLINE : environment;
    }

    // 读取AndroidManifest运行环境变量
    private void readEnvironmentVariable() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            environment = appInfo.metaData.getString("Environment", Constant.Environment.ONLINE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 配置是否输出日志
    private void logConfig() {
        if (Constant.Environment.ONLINE.equalsIgnoreCase(environment)) {
            LogUtils.setIsWriteLog(false);
        } else {
            LogUtils.setIsWriteLog(true);
        }
//        LogUtils.setIsWriteLog(true);
    }

    public static Context getContext() {
        return instance;
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onLogout() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity(intent);
    }

    @Override
    public void onLogin() {
        doConnect();

        GiftManager.getInstance().refreshGift();

        JPushInterface.setAlias(this, LoginManager.getInstance().getUserInfo().id, null);


    }

    private void fixBugs() {
        // 三星的bug
        try {
            Class cls = Class.forName("android.sec.clipboard.ClipboardUIManager");
            Method m = cls.getDeclaredMethod("getInstance", Context.class);
            m.setAccessible(true);
            m.invoke(null, this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityCount == 0) {
            Log.v("App", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
            if (mListeners != null) {
                for (AppStateChangeListener l : mListeners) {
                    l.onChangeToFront();
                }
            }

        }
        activityCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            Log.v("App", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
            if (mListeners != null) {
                for (AppStateChangeListener l : mListeners) {
                    l.onChangeToBack();
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public ArrayList<AppStateChangeListener> mListeners;

    public void addStateChangeListener(AppStateChangeListener l) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }

        mListeners.add(l);
    }

    public void removeStateChangeListener(AppStateChangeListener l) {
        if (mListeners == null) {
            return;
        }

        mListeners.remove(l);
    }

    public interface AppStateChangeListener {
        void onChangeToFront();

        void onChangeToBack();
    }
}
