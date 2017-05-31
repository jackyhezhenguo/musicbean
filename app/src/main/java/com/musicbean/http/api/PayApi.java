package com.musicbean.http.api;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.musicbean.http.HostManager;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.ResponseHandler;
import com.musicbean.util.MD5Utils;

/**
 * Created by boyo on 16/7/25.
 */
public class PayApi {

    public static void getCash(Context context, String beans, String money, String name, String account, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/pay/drawcash";
        RequestParams params = new RequestParams();
        params.put("bean", beans);
        params.put("money", money);
        params.put("real_name", name);
        params.put("alipay_account", account);

        long ts = System.currentTimeMillis();
        params.put("ts", ts);

        String str = beans + '_' + money + '_' + name
                + '_' + account + '_' + ts + '_' + "musicbean5688";

        params.put("sign", MD5Utils.getMD5(str));

        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void checkCaptcha(Context context, String phone, String code, ResponseHandler responseHandler){
        String url = HostManager.getHost() + "/user/checkcaptcha";
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("code", code);

        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void exchangeDiamond(Context context, long beans, long diamonds, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/pay/drawdiamond";
        RequestParams params = new RequestParams();
        params.put("bean", beans);
        params.put("diamond", diamonds);

        long ts = System.currentTimeMillis();
        params.put("ts", ts);

        String str = (beans + "") + '_' + (diamonds + "") + '_' + ts + '_' + "musicbean5688";

        params.put("sign", MD5Utils.getMD5(str));

        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }


    // 提现记录
    public static void getCashRecord(Context context, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/pay/getdrawcashlist";
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);

        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getAccountInfo(Context context, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/pay/getaccountinfo";
        HttpUtil.getInstance().get(context, url, null, responseHandler);
    }

    // 充值记录
    public static void getRechargeRecord(Context context, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/pay/getrechargelist";
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);

        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    // 充值记录
    public static void getExchangeRecord(Context context, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/pay/getdrawdiamondlist";
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);

        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void recharge(Context context, int money, int diamonds, String type, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/pay/recharge";
        RequestParams params = new RequestParams();
        params.put("money", money);
        params.put("diamond", diamonds);
        params.put("pay_method", type);

        long ts = System.currentTimeMillis();
        params.put("ts", ts);

        String str = (money + "") + '_' + (diamonds + "") + '_' + type + '_' + ts + '_' + "musicbean5688";

        params.put("sign", MD5Utils.getMD5(str));

        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getRechargeStatus(Context context, int id, ResponseHandler responseHandler){
        String url = HostManager.getHost() + "/pay/getrechargestatus";
        RequestParams params = new RequestParams();
        params.put("recharge_orderid", id);

        long ts = System.currentTimeMillis();
        params.put("ts", ts);

        String str = (id + "") + '_' + ts + '_' + "musicbean5688";

        params.put("sign", MD5Utils.getMD5(str));

        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

}
