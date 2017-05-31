package com.musicbean.ui.live.danmu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.musicbean.App;
import com.musicbean.R;
import com.musicbean.http.bean.MessageInfo;
import com.musicbean.util.BitmapUtils;
import com.musicbean.util.ScreenUtils;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.ImageLoadingListener;

import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Danmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.android.QihooDanmmukuParser;
import master.flame.danmaku.ui.widget.DanmakuSurfaceView;

public class DanmuView extends DanmakuSurfaceView {

    protected BaseDanmakuParser mParser;

    // private Bitmap starBitmap;
    public DanmuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DanmuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanmuView(Context context) {
        super(context);
        init();
    }

    public void init() {
        DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(DanmakuGlobalConfig.DANMAKU_STYLE_STROKEN,
                DanmakuGlobalConfig.DANMAKU_TYPE_LIVE, 3);
        DanmakuGlobalConfig.DEFAULT.setIsHasStar(false);
        //DanmakuGlobalConfig.DEFAULT.setMaximumVisibleSizeInScreen(20);

        setDefaultParser();

    }

    private void setDefaultParser() {
        mParser = new QihooDanmmukuParser();
        prepare(mParser);

        setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                start();
                liveShow();
            }

            @Override
            public void updateTimer(DanmakuTimer danmakuTimer) {

            }
        });
    }

    public void addDanmu(final MessageInfo mInfo) {

        FinalBitmap.getInstance().loadImage(mInfo.image, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLoadingFailed(String imageUri, View view) {
                // TODO Auto-generated method stub
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_new_header);
                Bitmap headImg = BitmapUtils.getCircleBitmapWithBound(bmp, 2,
                        ScreenUtils.dp2px(App.getContext(), 30));
                doAddDanmu(mInfo, headImg);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {

                Bitmap headImg = BitmapUtils.getCircleBitmapWithBound(bitmap, 2,
                        ScreenUtils.dp2px(App.getContext(), 30));
                doAddDanmu(mInfo, headImg);
            }

        });
    }

    private void doAddDanmu(MessageInfo info, Bitmap bmp) {
        try {
            BaseDanmaku danmaku;
            danmaku = DanmakuFactory.createDanmaku(Danmaku.TYPE_SCROLL_RL);
            danmaku.text = info.msg;
            danmaku.textSize = ScreenUtils.dp2px(getContext(), 16);
            danmaku.name = info.name;
            danmaku.headBitmap = bmp;
            danmaku.isBottom = false;
            danmaku.padding = ScreenUtils.dp2px(getContext(), 10);
            danmaku.priority = 1;
            danmaku.time = getCurrentTime() + new Random().nextInt(6) * 100;
            danmaku.textColor = Color.WHITE;
            danmaku.isLive = true;
            danmaku.duration = new Duration(8000);
            danmaku.backgroundColor = 0x66000000;
            addDanmaku(danmaku);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }
}
