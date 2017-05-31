package com.musicbean.http.api;

import android.content.Context;
import android.util.Base64;

import com.loopj.android.http.RequestParams;
import com.musicbean.http.HostManager;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.util.AppInfo;
import com.musicbean.util.MD5Utils;

import java.io.File;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;

/**
 * Created by boyo on 16/6/25.
 */
public class UserApi {

    public static void getCaptcha(Context context, String phone, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/uc/getcaptcha";
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void loginByPhone(Context context, String phone, String code, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/uc/loginbyphone";
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("code", code);
        params.put("uuid", AppInfo.getUuid(context));
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

//    $salt = musicbean5688;
//    $token = base64_decode($code);
//    $str  = $ site.'_'.$token.'_'.$ts.'_'.$salt;
//    return md5($str);

    public static void loginByOauth(Context context, String site, String token, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/uc/loginbyoauth";
        RequestParams params = new RequestParams();
        params.put("site", site);
        String code = Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
        params.put("code", code);
        long ts = System.currentTimeMillis();
        params.put("ts", ts);
        params.put("uuid", AppInfo.getUuid(context));

        String str = site + "_" + token + "_" + AppInfo.getUuid(context) + "_" + ts + "_" + "musicbean5688";

        params.put("sign", MD5Utils.getMD5(str));
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void bindPhone(Context context, String phone, String code, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/savephone";
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("code", code);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void logout(Context context, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/uc/logout";
        HttpUtil.getInstance().get(context, url, null, responseHandler);
    }

    /**
     * 查询别人的基本信息
     */
    public static void getUserInfo(Context context, String uid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getuserinfo";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    /**
     * 个人主页接口，获取页面的相关信息
     *
     * @param context
     * @param responseHandler
     */
    public static void getMypage(Context context, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getmypage";
        HttpUtil.getInstance().get(context, url, null, responseHandler);
    }

    /**
     * 查询别人的主页
     *
     * @param context
     * @param responseHandler
     */
    public static void getUserpage(Context context, String uid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getuserpage";
        RequestParams params = new RequestParams();
        if (uid != null) {
            params.put("uid", uid);
        }
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    /**
     * 查询别人的概述窗口
     *
     * @param context
     * @param responseHandler
     */
    public static void getUserpageLive(Context context, String uid, String anchorId, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getuserlivepage";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("anchorid", anchorId);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getRalation(Context context, String uid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getrelation";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void follow(Context context, String uid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/concernone";
        RequestParams params = new RequestParams();
        params.put("concernid", uid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void disFollow(Context context, String uid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/disconcernone";
        RequestParams params = new RequestParams();
        params.put("concernid", uid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    /**
     * 举报
     *
     * @param context
     * @param uid
     * @param responseHandler
     */
    public static void report(Context context, String uid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/report";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    /**
     * 粉丝列表
     *
     * @param context
     * @param uid
     * @param start
     * @param count
     * @param sort            0 时间倒序  1时间正序  2土豪粉丝榜
     * @param responseHandler
     */
    public static void getFollowList(Context context, String uid, int start, int count, int sort, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getfollowlist";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("start", start);
        params.put("count", count);
        params.put("sort", sort);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    /**
     * 关注列表
     *
     * @param context
     * @param uid
     * @param start
     * @param count
     * @param responseHandler
     */
    public static void getConcernList(Context context, String uid, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getconcernlist";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void search(Context context, int type, String key, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/search";
        RequestParams params = new RequestParams();
        if (type == 0) {
            params.put("nickname", key);
        } else {
            params.put("studio", key);
        }
        params.put("start", start);
        params.put("count", count);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void feedback(Context context, String content, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/advise";
        RequestParams params = new RequestParams();
        params.put("content", content);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void saveInfo(Context context, UserInfoBean info, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/saveinfo";
        RequestParams params = new RequestParams();
        params.put("nickname", info.nickname);
        params.put("studio", info.studio);
        params.put("gender", info.gender);
        params.put("honor", info.honor);
        params.put("opus", info.opus);
        params.put("spec_sign", info.spec_sign);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void uploadUserImage(Context context, String imgPath, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/saveimage";

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        File file = new File(imgPath);
        builder.addPart("image", new FileBody(file, ContentType.create("image/jpeg"), file.getName()));
        HttpEntity entity = builder.build();

        HttpUtil.getInstance().post(context, url, entity, entity.getContentType().getValue(), responseHandler);
    }

    public static void uploadCoverImage(Context context, String imgPath, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/savehcover";

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        File file = new File(imgPath);
        builder.addPart("h_cover", new FileBody(file, ContentType.create("image/jpeg"), file.getName()));
        HttpEntity entity = builder.build();

        HttpUtil.getInstance().post(context, url, entity, entity.getContentType().getValue(), responseHandler);
    }

    public static void delManager(Context context, String uid, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/delmanager";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getManagerList(Context context, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getmanagerlist";
        HttpUtil.getInstance().get(context, url, null, responseHandler);
    }

    public static void remind(Context context, String uid, int remind, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/remindone";
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("remind", remind);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getMyRemindList(Context context, int start, int count, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getmyremindlist";
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("count", count);
        params.put("sort", 0);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void saveRemind(Context context, int remind, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/saveremind";
        RequestParams params = new RequestParams();
        params.put("is_remind", remind);
        params.setUseJsonStreamer(true);
        HttpUtil.getInstance().get(context, url, params, responseHandler);
    }

    public static void getLevelInfo(Context context, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/getlevelinfo";
        HttpUtil.getInstance().get(context, url, null, responseHandler);
    }

    public static void userRecommend(Context context, ResponseHandler responseHandler) {
        String url = HostManager.getHost() + "/user/recommend";
        HttpUtil.getInstance().get(context, url, null, responseHandler);
    }
}
