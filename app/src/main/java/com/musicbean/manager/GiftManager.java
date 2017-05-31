package com.musicbean.manager;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.App;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.ChatApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.GiftBean;
import com.musicbean.preference.MySettings;
import com.musicbean.preference.PreferencesManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by boyo on 16/7/3.
 */
public class GiftManager {
    private static volatile GiftManager mInstance;

    private ArrayList<GiftBean> mGiftList;

    private GiftManager() {
        // TODO
        MySettings set = (MySettings) PreferencesManager.getSettings();
        if (!TextUtils.isEmpty(set.giftJson)) {
            parseData(set.giftJson);
        } else {
            String json = "{\"errorno\":0,\"errmsg\":\"ok\",\"data\":[{\"id\":\"4\",\"name\":\"\\u6d4b\\u8bd54\",\"image\":\"http:\\/\\/p1.qhimg.com\\/t016b4288d0c7653e17.png\",\"diamond\":\"10\",\"experence\":\"100\",\"cartoon_type\":\"1\",\"sort\":\"0\",\"is_doublehit\":\"1\",\"is_del\":\"0\",\"createline\":\"1467031822\",\"updateline\":\"1467031822\"},{\"id\":\"5\",\"name\":\"\\u6d4b\\u8bd55\",\"image\":\"http:\\/\\/p9.qhimg.com\\/t012be0aa5f532666ef.png\",\"diamond\":\"100\",\"experence\":\"1000\",\"cartoon_type\":\"1\",\"sort\":\"0\",\"is_doublehit\":\"1\",\"is_del\":\"0\",\"createline\":\"1467031822\",\"updateline\":\"1467031822\"},{\"id\":\"6\",\"name\":\"\\u6d4b\\u8bd56\",\"image\":\"http:\\/\\/p0.qhimg.com\\/t01bbf75ec7d4e307be.png\",\"diamond\":\"500\",\"experence\":\"5000\",\"cartoon_type\":\"1\",\"sort\":\"0\",\"is_doublehit\":\"0\",\"is_del\":\"0\",\"createline\":\"1467031822\",\"updateline\":\"1467031822\"},{\"id\":\"7\",\"name\":\"\\u6d4b\\u8bd57\",\"image\":\"http:\\/\\/p5.qhimg.com\\/t011001f0fc06c10817.png\",\"diamond\":\"1000\",\"experence\":\"10000\",\"cartoon_type\":\"1\",\"sort\":\"0\",\"is_doublehit\":\"0\",\"is_del\":\"0\",\"createline\":\"1467031822\",\"updateline\":\"1467031822\"},{\"id\":\"8\",\"name\":\"\\u6d4b\\u8bd58\",\"image\":\"http:\\/\\/p6.qhimg.com\\/t01806341bf7db3f0f3.png\",\"diamond\":\"10000\",\"experence\":\"100000\",\"cartoon_type\":\"1\",\"sort\":\"0\",\"is_doublehit\":\"0\",\"is_del\":\"0\",\"createline\":\"1467031822\",\"updateline\":\"1467031822\"},{\"id\":\"9\",\"name\":\"\\u6d4b\\u8bd59\",\"image\":\"http:\\/\\/p5.qhimg.com\\/t0103074224f98c9ccf.png\",\"diamond\":\"100000\",\"experence\":\"1000000\",\"cartoon_type\":\"1\",\"sort\":\"0\",\"is_doublehit\":\"0\",\"is_del\":\"0\",\"createline\":\"1467031822\",\"updateline\":\"1467031822\"},{\"id\":\"3\",\"name\":\"\\u6d4b\\u8bd53\",\"image\":\"http:\\/\\/p6.qhimg.com\\/t01090cc5ec573a0af0.png\",\"diamond\":\"5\",\"experence\":\"50\",\"cartoon_type\":\"1\",\"sort\":\"1\",\"is_doublehit\":\"1\",\"is_del\":\"0\",\"createline\":\"1467031822\",\"updateline\":\"1467031822\"},{\"id\":\"2\",\"name\":\"\\u6d4b\\u8bd51\",\"image\":\"http:\\/\\/p2.qhimg.com\\/t018d15f4635a27161c.png\",\"diamond\":\"1\",\"experence\":\"10\",\"cartoon_type\":\"1\",\"sort\":\"2\",\"is_doublehit\":\"1\",\"is_del\":\"0\",\"createline\":\"1467031822\",\"updateline\":\"1467031822\"}]}";
            parseData(json);
        }
    }

    public static GiftManager getInstance() {
        if (mInstance == null) {
            synchronized (LoginManager.class) {
                if (mInstance == null) {
                    mInstance = new GiftManager();
                }
            }
        }
        return mInstance;
    }

    public void refreshGift() {
        ChatApi.getGift(App.getContext(), new ResponseHandler() {
            @Override
            public void onSuccess(String data) {

                MySettings set = (MySettings) PreferencesManager.getSettings();
                set.giftJson = data;
                set.sync();

                parseData(data);

                for (GiftBean bean : mGiftList) {
                    onDownLoad(bean.image);
                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    public ArrayList<GiftBean> getGiftList() {
        return mGiftList;
    }

    public GiftBean findGift(int id) {
        for (GiftBean bean : mGiftList) {
            if (bean.id == id) {
                return bean;
            }
        }
        return null;
    }

    private void parseData(String json) {
        Type objectType = new TypeToken<BaseResponse<ArrayList<GiftBean>>>() {
        }.getType();
        BaseResponse<ArrayList<GiftBean>> res = new Gson().fromJson(json, objectType);
        mGiftList = res.data;
    }

    /**
     * 单线程列队执行
     */
    private static ExecutorService singleExecutor = null;


    /**
     * 执行单线程列队执行
     */
    public void runOnQueue(Runnable runnable) {
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

    /**
     * 启动图片下载线程
     */
    private void onDownLoad(final String url) {
        Runnable service = new Runnable() {
            @Override
            public void run() {
                try {
                    Glide.with(App.getContext())
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //启动图片下载线程
        runOnQueue(service);
    }
}
