package com.musicbean.http;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.musicbean.App;
import com.musicbean.util.AppInfo;
import com.musicbean.util.LogUtils;

import cz.msebera.android.httpclient.HttpEntity;

/**
 * http请求工具
 * <p/>
 */
public class HttpUtil {

    private AsyncHttpClient httpClient;

    private static HttpUtil instance;

    private String mCookie;

    private String version = "3.1.0";

    /**
     * 获取对象实例
     *
     * @return HttpUtil实例
     */
    public synchronized static HttpUtil getInstance() {
        if (instance == null) {
            instance = new HttpUtil();
        }
        return instance;
    }

    private HttpUtil() {
        if (httpClient == null) {
            httpClient = new AsyncHttpClient();
            // 在此可以设置网络请求超时、cookie自动缓存、UA、请求头等全局网络请求属性
            version = AppInfo.getAppVersionName(App.getContext());
        }
    }

    public void setCookie(String c) {
        mCookie = c;
    }

    /**
     * http get请求
     *
     * @param context         发起请求的context，用于记录该context发起的所有请求
     * @param url             请求地址
     * @param params          请求参数
     * @param responseHandler 响应处理
     */
    public void get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        LogUtils.v(HttpUtil.class, "GET请求：" + url);
        if (params == null) {
            params = new RequestParams();
        }

        params.put("client", "android");
        params.put("version", version);
        if (!TextUtils.isEmpty(mCookie)) {
            params.put("mbuc", mCookie);
        }

        LogUtils.v(HttpUtil.class, "请求参数：" + params.toString());
        httpClient.addHeader("Content-Type", "application/json");
        httpClient.get(context, url, params, responseHandler);
    }

    /**
     * http post请求
     *
     * @param context         发起请求的context，用于记录该context发起的所有请求
     * @param url             请求地址
     * @param params          请求参数
     * @param responseHandler 响应处理
     */
    public void post(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        LogUtils.v(HttpUtil.class, "POST请求：" + url);
        if (params == null) {
            LogUtils.v(HttpUtil.class, "请求参数：" + null);
        } else {
            LogUtils.v(HttpUtil.class, "请求参数：" + params.toString());
        }
        httpClient.addHeader("Content-Type", "application/json");
        // TODO
        httpClient.post(context, url, params, responseHandler);
    }

    public void post(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        LogUtils.v(HttpUtil.class, "POST请求：" + url);
        if (entity == null) {
            LogUtils.v(HttpUtil.class, "请求参数：" + null);
        } else {
            LogUtils.v(HttpUtil.class, "请求参数：entity");
        }

        url += "?client=android&version=" + version;
        if (!TextUtils.isEmpty(mCookie)) {
            url += ("&mbuc=" + mCookie);
        }

        httpClient.addHeader("Content-Type", contentType);
        httpClient.post(context, url, entity, contentType, responseHandler);
    }

    /**
     * 取消对应context的请求
     *
     * @param context 需要取消请求的context
     */
    public void cancelRequest(Context context) {
        httpClient.cancelRequests(context, true);
    }

    /**
     * 取消所有请求
     */
    public void cancelAllRequest() {
        httpClient.cancelAllRequests(true);
    }

    /**
     * 添加请求头
     *
     * @param header 头名称
     * @param value  头内容
     */
    public void setHeader(String header, String value) {
        httpClient.addHeader(header, value);
    }

}
