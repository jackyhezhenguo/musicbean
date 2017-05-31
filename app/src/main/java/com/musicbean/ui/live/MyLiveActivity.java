//package com.musicbean.ui.live;
//
//import android.content.Intent;
//import android.hardware.Camera;
//import android.media.MediaCodec;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.example.xinhuang.test1.AudioRecordThread;
//import com.example.xinhuang.test1.UploadThread;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.musicbean.R;
//import com.musicbean.http.HttpUtil;
//import com.musicbean.http.ResponseHandler;
//import com.musicbean.http.api.LiveApi;
//import com.musicbean.http.bean.BaseResponse;
//import com.musicbean.http.bean.LiveFinishBean;
//import com.musicbean.http.bean.LiveInitInfo;
//import com.musicbean.http.bean.UserInfoBean;
//import com.musicbean.manager.LoginManager;
//import com.musicbean.ui.BaseActivity;
//import com.musicbean.util.LogUtils;
//import com.musicbean.util.ScreenUtils;
//
//import java.io.File;
//import java.lang.reflect.Type;
//import java.nio.ByteBuffer;
//
//import me.relex.camerafilter.FileUtil;
//import me.relex.camerafilter.camera.CameraController;
//import me.relex.camerafilter.camera.CameraRecordRenderer;
//import me.relex.camerafilter.camera.FrameBufferCallBack;
//import me.relex.camerafilter.video.EncoderConfig;
//import me.relex.camerafilter.widget.CameraSurfaceView;
//
//
///**
// * Created by boyo on 16/7/1.
// */
//public class MyLiveActivity extends BaseActivity implements FrameBufferCallBack {
//
//    static {
//        System.loadLibrary("DeluxPushVideo");
//    }
//
//    CameraSurfaceView mSurface;
//
//    UploadThread mUploadTread;
//    AudioRecordThread mAudioThread;
//
//    private LiveControlWidget mControlWidet;
//
//    private String mRoomid;
//
//    private int mSurfaceHeight;
//    private int mSurfaceWidth;
//
//    private String mRtmpUrl;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mylive);
//
//        if (!LoginManager.getInstance().isLogin()) {
//            finish();
//            return;
//        }
//
//        UserInfoBean info = LoginManager.getInstance().getUserCookie().userinfo;
//
//        mControlWidet = (LiveControlWidget) findViewById(R.id.control_widget);
//        mControlWidet.setData(info);
//
//        ImageView iv = (ImageView) mControlWidet.findViewById(R.id.btn_gift);
//        iv.setImageResource(R.drawable.icon_switch_camera);
//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeCamera();
//            }
//        });
//
//        initSurface();
//
//        requestLive();
//    }
//
//    private void initSurface() {
//        mSurface = (CameraSurfaceView) findViewById(R.id.surface);
//        mSurface.setFrameBufferCallBack(MyLiveActivity.this);
//        mSurfaceHeight = ScreenUtils.getScreenHeight(this);
//        mSurfaceWidth = ScreenUtils.getScreenWidth(this);
//        mSurface.getLayoutParams().height = mSurfaceHeight;
//        mSurface.getLayoutParams().width = mSurfaceWidth;
//
//        CameraController.getInstance().setCameraIndex(Camera.CameraInfo.CAMERA_FACING_FRONT);
//    }
//
//    private void changeCamera() {
//        mSurface.getRenderer().changeCamera();
//    }
//
//    private void requestLive() {
//        //String title = getIntent().getStringExtra("title");
//        LiveInitInfo liveInfo = (LiveInitInfo) getIntent().getSerializableExtra("info");
//        mRoomid = liveInfo.roomid;
//        mControlWidet.initChat(liveInfo.roomid);
//        mRtmpUrl = liveInfo.push_addr.rtmp;
//        initUpload(mRtmpUrl);
//
//        mSurface.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                CameraRecordRenderer renderer = mSurface.getRenderer();
//                renderer.setEncoderConfig(new EncoderConfig(new File(
//                        FileUtil.getCacheDirectory(MyLiveActivity.this, true),
//                        "easy.h264"), 480, 640,
//                        400000 /* 1 Mb/s */));
//
//                mSurface.queueEvent(new Runnable() {
//                    @Override
//                    public void run() {
//                        mSurface.getRenderer().setRecordingEnabled(true);
//                    }
//                });
//            }
//        }, 2000);
//    }
//
//    private void initUpload(String url) {
//        LogUtils.e("MyLiveActivity", "live url:" + url);
//        mUploadTread = new UploadThread(480, 640);
//        //初始化native
//        //String url = "rtmp://vedio.obaymax.com/live/1234";
//        String savePath = null;
//
//        mUploadTread.framerate = 20;
//        mUploadTread.bitrate = 400000;
//
//        mUploadTread.useHWEncode = 1;
//
//        mUploadTread.nativeDsInitHandle(url,
//                mUploadTread.iWidth,
//                mUploadTread.iHeight,
//                mUploadTread.framerate,
//                mUploadTread.bitrate,
//                mUploadTread.useHWEncode,
//                mUploadTread.useTcpAdapter,
//                savePath);
//
//        //硬解参数
////        if (mPublisher.useHWEncode == 1) {
////            mPublisher.AvcEncoder(mPublisher.iWidth, mPublisher.iHeight, mPublisher.framerate, mPublisher.bitrate);
////        }
//
//        mAudioThread = new AudioRecordThread(mUploadTread);
//        mAudioThread.start();
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mSurface.onPause();
//
////        if (mPublisher != null) {
////            //mPublisher.nativeDestroyHandle();
////            final UploadThread uThread = mPublisher;
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    uThread.nativeDestroyHandle();
////                }
////            }).start();
////            mPublisher = null;
////            mAudioThread.setStop();
////            mAudioThread = null;
////        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mSurface.onResume();
//
////        if (!TextUtils.isEmpty(mRtmpUrl) && mPublisher == null) {
////            initUpload(mRtmpUrl);
////        }
//    }
//
//
//    @Override
//    public void onBufferCallback(byte[] data, int isCfg) {
//        //Log.e("upload", "data.length:" + data.length + "; isCfg:" + isCfg);
//        if (mUploadTread != null) {
//            mUploadTread.addData(data, isCfg);
//        }
//    }
//
//    @Override
//    public void onBufferCallbackRaw(ByteBuffer es, MediaCodec.BufferInfo bi) {
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mSurface.onDestroy();
//
//
//        if (mUploadTread != null) {
//            //mPublisher.nativeDestroyHandle();
//            final UploadThread uThread = mUploadTread;
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    uThread.nativeDestroyHandle();
////                }
////            }).start();
//            mUploadTread = null;
//            mAudioThread.setStop();
//            mAudioThread = null;
//        }
//
//        try {
//            mControlWidet.onDestroy();
//
//            HttpUtil.getInstance().cancelRequest(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void finish() {
//        mSurface.queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                mSurface.getRenderer().setRecordingEnabled(false);
//            }
//        });
//
//        LiveApi.liveEnd(this, mRoomid, new ResponseHandler() {
//            @Override
//            public void onSuccess(String data) {
//                Type objectType = new TypeToken<BaseResponse<LiveFinishBean>>() {
//                }.getType();
//                BaseResponse<LiveFinishBean> res = new Gson().fromJson(data, objectType);
//
//                if (!MyLiveActivity.this.isFinishing()) {
//                    Intent intent = new Intent(MyLiveActivity.this, MyLiveFinishActivity.class);
//                    intent.putExtra("onlines", res.data.income_users);
//                    intent.putExtra("beans", res.data.income_beans);
//                    startActivity(intent);
//
//                    MyLiveActivity.super.finish();
//                }
//            }
//
//            @Override
//            public void onFailure(int responseCode, String errorMsg) {
//                if (!isFinishing()) {
//                    MyLiveActivity.super.finish();
//                }
//            }
//        });
//
//    }
//
//
//}
