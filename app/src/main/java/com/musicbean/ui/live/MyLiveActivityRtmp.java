package com.musicbean.ui.live;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.boyo.camerafilterfbo.FileUtil;
import com.boyo.camerafilterfbo.camera.CameraController;
import com.boyo.camerafilterfbo.encoder.EncoderConfig;
import com.boyo.camerafilterfbo.encoder.FrameBufferCallBack;
import com.boyo.camerafilterfbo.renderer.GLSurfaceViewRender;
import com.boyo.camerafilterfbo.widget.CameraGLSurfaceView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.LiveApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.LiveFinishBean;
import com.musicbean.http.bean.LiveInitInfo;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.rtmp.RtmpPublisher;
import com.musicbean.ui.BaseActivity;
import com.musicbean.util.LogUtils;
import com.musicbean.util.ScreenUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;


/**
 * Created by boyo on 16/7/1.
 */
public class MyLiveActivityRtmp extends BaseActivity implements FrameBufferCallBack {

    CameraGLSurfaceView mSurface;

    RtmpPublisher mPublisher;

    private LiveControlWidget mControlWidet;

    private String mRoomid;

    private int mSurfaceHeight;
    private int mSurfaceWidth;

    private String mRtmpUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylive);

        if (!LoginManager.getInstance().isLogin()) {
            finish();
            return;
        }

        CameraController.getInstance().setCameraIndex(Camera.CameraInfo.CAMERA_FACING_FRONT);
        UserInfoBean info = LoginManager.getInstance().getUserCookie().userinfo;

        mControlWidet = (LiveControlWidget) findViewById(R.id.control_widget);
        mControlWidet.setData(info);

        ImageView iv = (ImageView) mControlWidet.findViewById(R.id.btn_gift);
        iv.setImageResource(R.drawable.icon_switch_camera);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCamera();
            }
        });

        initSurface();

        requestLive();
    }

    private void initSurface() {
        mSurface = (CameraGLSurfaceView) findViewById(R.id.surface);
        mSurface.getRender().setPreviewSize(480, 640);
        mSurface.getRender().setFrameBufferCallBack(MyLiveActivityRtmp.this);
        mSurfaceHeight = ScreenUtils.getScreenHeight(this);
        mSurfaceWidth = ScreenUtils.getScreenWidth(this);
        mSurface.getLayoutParams().height = mSurfaceHeight;
        mSurface.getLayoutParams().width = mSurfaceWidth;
    }

    private void changeCamera() {
        mSurface.getRender().changeCamera();
    }

    private void requestLive() {
        //String title = getIntent().getStringExtra("title");
        LiveInitInfo liveInfo = (LiveInitInfo) getIntent().getSerializableExtra("info");
        mRoomid = liveInfo.roomid;
        mControlWidet.initChat(liveInfo.roomid);
        mRtmpUrl = liveInfo.push_addr.rtmp;
        initUpload(mRtmpUrl);

        GLSurfaceViewRender renderer = mSurface.getRender();
        renderer.setEncoderConfig(new EncoderConfig(new File(
                FileUtil.getCacheDirectory(MyLiveActivityRtmp.this, true),
                "easy.h264"), 480, 640,
                400000 /* 1 Mb/s */));

    }

    private void initUpload(String url) {
        LogUtils.e("MyLiveActivity", "live url:" + url);
        mPublisher = new RtmpPublisher(480, 640, url);
        //初始化native
        //String url = "rtmp://vedio.obaymax.com/live/1234";
        mPublisher.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurface.onPause();

        mControlWidet.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurface.onResume();

        mControlWidet.onResume();

        if(mPublisher!=null){
            mPublisher.checkAudio();
        }
    }


    @Override
    public void onBufferCallback(ByteBuffer es, MediaCodec.BufferInfo bi) {
        //Log.e("wcb", "onBufferCallback");
        if (mPublisher != null) {
            mPublisher.writeVideoData(es, bi);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mSurface.onDestroy();

        try {
            mControlWidet.onDestroy();

            HttpUtil.getInstance().cancelRequest(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        if (isFinishing()) {
            return;
        }

        mSurface.getRender().stopRecording();
        mSurface.getRender().setFrameBufferCallBack(null);

        if (mPublisher != null) {
            // TODO
            mPublisher.Stop();
            mPublisher = null;
        }

        LiveApi.liveEnd(this, mRoomid, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<LiveFinishBean>>() {
                }.getType();
                BaseResponse<LiveFinishBean> res = new Gson().fromJson(data, objectType);

                if (!MyLiveActivityRtmp.this.isFinishing()) {
                    Intent intent = new Intent(MyLiveActivityRtmp.this, MyLiveFinishActivity.class);
                    intent.putExtra("onlines", res.data.income_users);
                    intent.putExtra("beans", res.data.income_beans);
                    startActivity(intent);

                    MyLiveActivityRtmp.super.finish();
                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                if (!isFinishing()) {
                    MyLiveActivityRtmp.super.finish();
                }
            }
        });

    }


}
