package com.musicbean.http;

import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.musicbean.App;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.manager.LoginManager;
import com.musicbean.util.LogUtils;

import cz.msebera.android.httpclient.Header;

/**
 * http响应处理类
 * <p/>
 * Created by jiangyujiang on 16/1/24.
 */
public abstract class ResponseHandler extends TextHttpResponseHandler {

    @Override
    public void onStart() {

    }

    /**
     * http请求成功回调，不要做耗时操作
     *
     * @param data 返回数据，如果没有将返回null
     */
    public abstract void onSuccess(String data);

    /**
     * http请求失败回调，不要做耗时操作
     *
     * @param responseCode http响应状态码
     * @param errorMsg     错误信息
     */
    public abstract void onFailure(int responseCode, String errorMsg);

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        if (TextUtils.isEmpty(responseString)) {
            printResponse(statusCode, headers, responseString, null);
            onFailure(statusCode, headers, "服务端返回数据异常", null);
            return;
        } else {
            try {
                BaseResponse res = new Gson().fromJson(responseString, BaseResponse.class);
                if (res.errorno != 0) {
                    printResponse(statusCode, headers, responseString, null);
                    onFailure(res.errorno, headers, res.errmsg, null);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // TODO 统一处理返回的业务状态码，业务失败需要回调onFailure(statusCode, errorMsg)，否则回调成功方法
        printResponse(statusCode, headers, responseString, null);
        try {
            onSuccess(responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        if (statusCode == 2002 || statusCode == 2024) {
            LoginManager.getInstance().onLogout();
        } else if (statusCode == 2013) {
            Toast.makeText(App.getContext(), responseString, Toast.LENGTH_SHORT).show();
            return;
        }

        printResponse(statusCode, headers, responseString, throwable);
        onFailure(statusCode, responseString);
        if (!TextUtils.isEmpty(responseString)) {
            //Toast.makeText(App.getContext(), responseString, Toast.LENGTH_SHORT).show();
        }
    }

    // 打印http响应数据
    protected void printResponse(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        String tag = "ResponseHandler";
        LogUtils.v(tag, "------http请求响应------");
        LogUtils.v(tag, "请求地址：" + getRequestURI().toString());
        LogUtils.v(tag, "响应状态码：" + statusCode);
        StringBuilder headerSb = new StringBuilder();
        if (headers != null) {
            for (Header header : headers) {
                headerSb.append(header.getName()).append("=").append(header.getValue())
                        .append(";");
            }
        }
        LogUtils.v(tag, "响应头：" + headerSb.toString());
        LogUtils.v(tag, "响应数据：" + responseString);
        if (throwable != null) {
            LogUtils.v(tag, "异常描述：" + throwable.getMessage());
        }
    }
}
