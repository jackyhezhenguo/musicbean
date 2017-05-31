package com.musicbean.http.api;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.musicbean.http.HostManager;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.ResponseHandler;

/**
 * Created by boyo on 16/11/26.
 * 点播接口
 */
public class VideoApi {

    public static void getVideoList(Context context, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/video/getlist";
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void search(Context context, String key, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/video/search";
        RequestParams params = new RequestParams();
        params.put("content", key);
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void report(Context context, String id, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/video/in";
        RequestParams params = new RequestParams();
        params.put("videoid", id);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getVideoListById(Context context, String id, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/video/getListByUid";
        RequestParams params = new RequestParams();
        if (id != null) {
            params.put("uid", id);
        }
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }
}
