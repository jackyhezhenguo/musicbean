package com.musicbean.http;

import com.musicbean.App;
import com.musicbean.Constant;


/**
 * 服务器主机地址管理
 * <p/>
 */
public class HostManager {

    // TODO 设置服务器主机地址
    //private static final String HOST_DEV = "http://114.215.117.46";
    private static final String HOST_DEV = "http://musicbean-app-server-dev.obaymax.com";
    private static final String HOST_TEST = "http://musicbean-app-server-test.obaymax.com";
    private static final String HOST_ONLINE = "http://app.musicbean.cn";

    /**
     * 获取服务器主机地址
     *
     * @return 服务端地址
     */
    public static String getHost() {
        switch (App.getEnvironment()) {
            case Constant.Environment.DEVELOPMENT:
                return HOST_DEV;
            case Constant.Environment.TEST:
                return HOST_TEST;
            default:
                return HOST_ONLINE;
        }
    }

    public static String getShareHost() {
        switch (App.getEnvironment()) {
            case Constant.Environment.DEVELOPMENT:
                return HOST_DEV + "/play2.html";
            case Constant.Environment.TEST:
                return Constant.Environment.TEST +"/play2.html";
            default:
                return HOST_ONLINE + "/play2.html";
        }
    }
}
