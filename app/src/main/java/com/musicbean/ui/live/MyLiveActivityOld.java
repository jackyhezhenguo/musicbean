//package com.musicbean.ui.live;
//
//import android.content.Intent;
//import android.hardware.Camera;
//import android.opengl.GLSurfaceView;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.example.xinhuang.test1.AudioRecordThread;
//import com.example.xinhuang.test1.UploadThread;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.musicbean.App;
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
//import java.lang.reflect.Type;
//import java.util.List;
//
//import jp.co.cyberagent.android.gpuimage.GPUImage;
//import jp.co.cyberagent.android.gpuimage.beauty.SimpleBeautyFilter2;
//import jp.co.cyberagent.android.gpuimage.util.FrameBufferCallBack;
//
///**
// * Created by boyo on 16/7/1.
// */
//public class MyLiveActivityOld extends BaseActivity implements FrameBufferCallBack, App.AppStateChangeListener {
//
//    static {
//        System.loadLibrary("DeluxPushVideo");
//    }
//
//    GLSurfaceView mSurface;
//    Camera mCamera;
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
//    private GPUImage mGPUImage;
//
//    private String mRtmpUrl;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_myliveold);
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
//        initCamera();
//
//        initSurface();
//
//        requestLive();
//
//        openCamera();
//
//        App.getInstance().addStateChangeListener(this);
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
//        mControlWidet.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mGPUImage.setFilter(new SimpleBeautyFilter2());
//            }
//        }, 500);
//
//
////        LiveApi.init(this, title, new ResponseHandler() {
////            @Override
////            public void onSuccess(String data) {
////                Type objectType = new TypeToken<BaseResponse<LiveInitInfo>>() {
////                }.getType();
////                BaseResponse<LiveInitInfo> ret = new Gson().fromJson(data, objectType);
////
////                mRoomid = ret.data.roomid;
////                mControlWidet.initChat(ret.data.roomid);
////                mRtmpUrl = ret.data.push_addr.rtmp;
////                initUpload(mRtmpUrl);
////
////                mGPUImage.setFilter(new SimpleBeautyFilter2());
////            }
////
////            @Override
////            public void onFailure(int responseCode, String errorMsg) {
////                //Toast.makeText(MyLiveActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
////                finish();
////            }
////        });
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
//        mUploadTread.useHWEncode = 0;
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
//        if (mUploadTread.useHWEncode == 1) {
//            mUploadTread.AvcEncoder(mUploadTread.iWidth, mUploadTread.iHeight, mUploadTread.framerate, mUploadTread.bitrate);
//        }
//
//        if (mAudioThread == null) {
//            mAudioThread = new AudioRecordThread(mUploadTread);
//            mAudioThread.start();
//        }
//    }
//
//    private void initSurface() {
//        mSurface = (GLSurfaceView) findViewById(R.id.surface);
//        mSurfaceHeight = ScreenUtils.getScreenHeight(this);
//        mSurfaceWidth = ScreenUtils.getScreenWidth(this);
//        mSurface.getLayoutParams().height = mSurfaceHeight;
//        mSurface.getLayoutParams().width = mSurfaceWidth;
//
//        mGPUImage = new GPUImage(this);
//        mGPUImage.setGLSurfaceView(mSurface);
//        mGPUImage.setFrameBufferCallBack(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//
//    private void releaseCamera() {
//        if (mCamera != null) {
//            mCamera.setPreviewCallback(null);
//            mCamera.release();//释放资源
//            mCamera = null;//取消原来摄像头
//        }
//    }
//
//    private void openCamera() {
//        if (mCamera != null) {
//            mCamera.setPreviewCallback(null);
//            mCamera.release();//释放资源
//            mCamera = null;//取消原来摄像头
//
//        }
//
//        try {
//            mCamera = Camera.open(mCurCameraType);
//            Camera.Parameters para = mCamera.getParameters();
//            List<int[]> listfps = para.getSupportedPreviewFpsRange();
//            //para.setPreviewFpsRange(20000, 25000);
//            //Camera.Size size = findBestPreviewSize(para);
//            //para.setPreviewSize(size.width, size.height);
//            para.setPreviewSize(640, 480);
//            List<String> listFocusMode = para.getSupportedFocusModes();
//            if (para.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
//                para.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//            }
//            mCamera.setParameters(para);
//
//            Camera.CameraInfo cinfo = new Camera.CameraInfo();
//            Camera.getCameraInfo(mCurCameraType, cinfo);
//            mGPUImage.setUpCamera(mCamera, cinfo.orientation, cinfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT, false);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            if (null != mCamera) {
//                mCamera.release();
//                mCamera = null;
//            }
//            return;
//        }
//    }
//
//    @Override
//    public void onBufferCallback(int[] data) {
//        if (mUploadTread != null) {
//            mUploadTread.addData(data, 0);
//        }
//    }
//
//    @Override
//    public void onPreviewCallback(byte[] data) {
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            mControlWidet.onDestroy();
//
//            HttpUtil.getInstance().cancelRequest(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        App.getInstance().removeStateChangeListener(this);
//    }
//
//    private int mCurCameraType;
//    private int FRONT_CAMERA;
//    private int BACK_CAMERA;
//    private Camera.Size mBestSize;
//
//    private void initCamera() {
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
//
//        for (int i = 0; i < cameraCount; i++) {
//            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
//            Log.e("live", "camera oritation:" + cameraInfo.orientation);
//            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                FRONT_CAMERA = i;
//            } else {
//                BACK_CAMERA = i;
//            }
//        }
//
//        mCurCameraType = FRONT_CAMERA;
//
//    }
//
//    public void changeCamera() {
//        if (mCurCameraType == FRONT_CAMERA) {
//            mCurCameraType = BACK_CAMERA;
//        } else {
//            mCurCameraType = FRONT_CAMERA;
//        }
//
//        openCamera();
//
//    }
//
//    @Override
//    public void finish() {
//        stopUpload();
//
//        LiveApi.liveEnd(this, mRoomid, new ResponseHandler() {
//            @Override
//            public void onSuccess(String data) {
//                Type objectType = new TypeToken<BaseResponse<LiveFinishBean>>() {
//                }.getType();
//                BaseResponse<LiveFinishBean> res = new Gson().fromJson(data, objectType);
//
//                if (!MyLiveActivityOld.this.isFinishing()) {
//                    Intent intent = new Intent(MyLiveActivityOld.this, MyLiveFinishActivity.class);
//                    intent.putExtra("onlines", res.data.income_users);
//                    intent.putExtra("beans", res.data.income_beans);
//                    startActivity(intent);
//
//                    MyLiveActivityOld.super.finish();
//                }
//            }
//
//            @Override
//            public void onFailure(int responseCode, String errorMsg) {
//                if (!isFinishing()) {
//                    MyLiveActivityOld.super.finish();
//                }
//            }
//        });
//
//    }
//
//    private Camera.Size findBestPreviewSize(Camera.Parameters parameters) {
//
//        List<Camera.Size> preSizeList = parameters.getSupportedPreviewSizes();
//        int ReqTmpWidth = mSurfaceHeight;
//        int ReqTmpHeight = mSurfaceWidth;
//        //先查找preview中是否存在与surfaceview相同宽高的尺寸
//        for (Camera.Size size : preSizeList) {
//            if ((size.width == ReqTmpWidth) && (size.height == ReqTmpHeight)) {
//                return size;
//            }
//        }
//
//        // 得到与传入的宽高比最接近的size
//        float reqRatio = ((float) ReqTmpWidth) / ReqTmpHeight;
//        float curRatio, deltaRatio;
//        float deltaRatioMin = Float.MAX_VALUE;
//        Camera.Size retSize = null;
//        for (Camera.Size size : preSizeList) {
//            curRatio = ((float) size.width) / size.height;
//            deltaRatio = Math.abs(reqRatio - curRatio);
//            if (deltaRatio < deltaRatioMin) {
//                deltaRatioMin = deltaRatio;
//                retSize = size;
//            }
//        }
//
//        return retSize;
//    }
//
//    @Override
//    public void onChangeToBack() {
//        //stopUpload();
//    }
//
//    @Override
//    public void onChangeToFront() {
//        //startUpload();
//    }
//
//    private void stopUpload() {
//        releaseCamera();
//        if (mUploadTread != null) {
//            //mPublisher.nativeDestroyHandle();
//            final UploadThread uThread = mUploadTread;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    uThread.nativeDestroyHandle();
//                }
//            }).start();
//            mUploadTread = null;
//
//            if (mAudioThread != null) {
//                mAudioThread.setStop();
//                mAudioThread = null;
//            }
//        }
//    }
//
//    private void startUpload() {
//        openCamera();
//
//        if (!TextUtils.isEmpty(mRtmpUrl) && mUploadTread == null) {
//            initUpload(mRtmpUrl);
//        }
//    }
//}
