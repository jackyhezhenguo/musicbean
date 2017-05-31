package com.musicbean.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 应用崩溃异常处理，将异常信息保存到指定目录
 * <p/>
 * Created by jiang on 2015/10/16.
 */
public class CrashHandler {

    private static Context mContext;

    private static String mSaveDir; // 错误信息保存目录

    private static Thread.UncaughtExceptionHandler mDefaultHandler; //系统默认的UncaughtException处理类

    private static boolean isInit = false;

    private CrashHandler() {

    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param saveDir 异常信息保存目录，如果为空将默认保存到应用数据目录下的crash文件夹中，
     *                需要使用android.permission.WRITE_EXTERNAL_STORAGE权限
     */
    public static void init(Context context, String saveDir) {
        if (isInit) {
            return;
        }
        mContext = context.getApplicationContext();
        mSaveDir = saveDir;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        isInit = true;
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            handlerException(ex);
            mDefaultHandler.uncaughtException(thread, ex);
        }

        // 处理Crash异常
        private void handlerException(Throwable throwable) {
            if (throwable == null) {
                return;
            }
            Map<String, String> deviceInfo = collectDeviceInfo();
            saveToFile(throwable, deviceInfo);
        }

        // 手机手机信息
        private Map<String, String> collectDeviceInfo() {
            Map<String, String> infoMap = new HashMap<>();
            try {
                PackageManager pm = mContext.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
                if (pi != null) {
                    String versionName = pi.versionName == null ? "null" : pi.versionName;
                    String versionCode = pi.versionCode + "";
                    infoMap.put("versionName", versionName);
                    infoMap.put("versionCode", versionCode);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    infoMap.put(field.getName(), field.get(null).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return infoMap;
        }

        // 将Crash异常保存到文件中
        private void saveToFile(Throwable throwable, Map<String, String> extraInfo) {
            StringBuilder sb = new StringBuilder();
            // 拼接额外信息
            for (Map.Entry<String, String> entry : extraInfo.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key).append("=").append(value).append("\n");
            }
            // 拼接异常信息
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            Throwable cause = throwable.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            // 将信息保存到文件
            if (TextUtils.isEmpty(mSaveDir)) {
                File filesDir = mContext.getExternalFilesDir(null);
                if (filesDir == null) {
                    // 如果没有设置保存目录，应用的默认缓存目录又获取不到，就不保存崩溃日志
                    return;
                }
                mSaveDir = filesDir.getAbsolutePath() + File.separator + "crash";
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            String fileName = dateFormat.format(new Date()) + ".log";
            File crashFile = new File(mSaveDir, fileName);
            if (!crashFile.getParentFile().exists()) {
                crashFile.getParentFile().mkdirs();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(crashFile);
                fos.write(sb.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}