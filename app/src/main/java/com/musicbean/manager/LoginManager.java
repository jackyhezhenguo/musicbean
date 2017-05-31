package com.musicbean.manager;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.bean.UserCookie;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.preference.MySettings;
import com.musicbean.preference.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class LoginManager {

    private static volatile LoginManager mInstance;
    private boolean isLogin = false;
    private UserCookie mUserCookie;
    private List<OnLogListener> mListeners;

    private LoginManager() {
        // TODO
        mListeners = new ArrayList<>();

        MySettings set = (MySettings) PreferencesManager.getSettings();
        if (!TextUtils.isEmpty(set.userBean)) {
            isLogin = true;
            Gson gson = new Gson();
            mUserCookie = gson.fromJson(set.userBean, UserCookie.class);

            HttpUtil.getInstance().setCookie(mUserCookie.mbuc);
        } else {
            isLogin = false;
        }
    }

    public static LoginManager getInstance() {
        if (mInstance == null) {
            synchronized (LoginManager.class) {
                if (mInstance == null) {
                    mInstance = new LoginManager();
                }
            }
        }
        return mInstance;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void onLogin(UserCookie info) {
        if (isLogin) {
            return;
        }
        isLogin = true;
        mUserCookie = info;
        // TODO
        MySettings set = (MySettings) PreferencesManager.getSettings();
        Gson gson = new Gson();
        set.userBean = gson.toJson(info);
        set.sync();

        HttpUtil.getInstance().setCookie(mUserCookie.mbuc);

        // TODO
        for (OnLogListener l : mListeners) {
            l.onLogin();
        }
    }

    public void onLogout() {
        if (!isLogin) {
            return;
        }

        isLogin = false;
        mUserCookie = null;
        // TODO
        MySettings set = (MySettings) PreferencesManager.getSettings();
        set.userBean = "";
        set.sync();

        HttpUtil.getInstance().setCookie("");

        for (OnLogListener l : mListeners) {
            l.onLogout();
        }
    }

    public UserCookie getUserCookie() {
        return mUserCookie;
    }

    public UserInfoBean getUserInfo() {
        return mUserCookie.userinfo;
    }

    public void setUserInfo(UserInfoBean info) {
        // TODO
        if (info != null) {

            mUserCookie.userinfo = info;

            saveUserInfo();
        }
    }

    public void saveUserInfo() {
        MySettings set = (MySettings) PreferencesManager.getSettings();
        Gson gson = new Gson();
        set.userBean = gson.toJson(mUserCookie);
        set.sync();
    }

    public interface OnLogListener {
        public void onLogin();

        public void onLogout();
    }

    public void addOnLogListener(OnLogListener l) {
        if (l != null) {
            synchronized (mListeners) {
                if (!mListeners.contains(l)) {
                    mListeners.add(l);
                }
            }
        }
    }

    public void removeOnLogListener(OnLogListener l) {
        if (l != null) {
            synchronized (mListeners) {
                int index = mListeners.indexOf(l);
                if (index != -1) {
                    mListeners.remove(index);
                }
            }
        }
    }
}
