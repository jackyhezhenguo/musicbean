package com.musicbean.http.api;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.musicbean.http.HostManager;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.bean.GiftBean;
import com.musicbean.util.MD5Utils;

/**
 * Created by boyo on 16/7/10.
 */
public class ChatApi {

    public static void getGift(Context context, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/chat/getgift";
        HttpUtil.getInstance().get(context, url, null, responseHandler);
    }

    /**
     * 禁言
     */
    public static void gagUser(Context context, String uid, String roomid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/chat/gaguser";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("roomid", roomid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void setManager(Context context, String uid, String roomid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/chat/setmanager";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("roomid", roomid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }


    public static void sendGift(Context context, GiftBean gift, int hit, String roomid, String anchorid, ResponseHandler responseHandler) {
        if (gift == null) {
            return;
        }
        String url = HostManager.getHost() + "/chat/consumegift";
        RequestParams params = new RequestParams();
        params.put("roomid", roomid);
        params.put("touid", anchorid);
        params.put("giftid", gift.id);
        params.put("diamond", gift.diamond);
        params.put("experence", gift.experence);
        params.put("cartoon_type", gift.cartoon_type);
        params.put("hit", hit);
        long ts = System.currentTimeMillis();
        params.put("ts", ts);

        String str = roomid + '_' + anchorid + '_' + gift.id
                + '_' + gift.diamond + '_' + gift.experence + '_' + hit + '_' + gift.cartoon_type + '_' + ts + '_' + "musicbean5688";

        params.put("sign", MD5Utils.getMD5(str));
        params.setUseJsonStreamer(true);

        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void sendDanmu(Context context, String roomid, String anchorid, String content, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/chat/sendmsg";
        RequestParams params = new RequestParams();
        params.put("roomid", roomid);
        params.put("touid", anchorid);
        params.put("content", content);
        long ts = System.currentTimeMillis();
        params.put("ts", ts);

        String str = roomid + '_' + anchorid + '_' + content + '_' + ts + '_' + "musicbean5688";

        params.put("sign", MD5Utils.getMD5(str));
        params.setUseJsonStreamer(true);

        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }
}
