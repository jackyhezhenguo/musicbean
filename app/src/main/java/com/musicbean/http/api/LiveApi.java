package com.musicbean.http.api;


import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.musicbean.http.HostManager;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.ResponseHandler;

/**
 */
public class LiveApi {

    public static void getHotList(Context context, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/gethotlist";
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getHotBanner(Context context, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/gethotlistbanner";
        HttpUtil.getInstance().get(context, url, null, responseHandler);
    }

    public static void getNewList(Context context, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/getnewlist";
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getMyList(Context context, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/getmylist";
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void init(Context context, String title, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/init";
        RequestParams params = new RequestParams();
        params.put("title", title);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void joinRoom(Context context, String roomid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/in";
        RequestParams params = new RequestParams();
        params.put("roomid", roomid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void quitRoom(Context context, String roomid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/out";
        RequestParams params = new RequestParams();
        params.put("roomid", roomid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getOnlineList(Context context, String roomid, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/getonlinelist";
        RequestParams params = new RequestParams();
        params.put("roomid", roomid);
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }


    public static void liveEnd(Context context, String roomid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/end";
        RequestParams params = new RequestParams();
        params.put("roomid", roomid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void reportRecord(Context context, String id, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/live/recordin";
        RequestParams params = new RequestParams();
        params.put("recordid", id);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }
}

