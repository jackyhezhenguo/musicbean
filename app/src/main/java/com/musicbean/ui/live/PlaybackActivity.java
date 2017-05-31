package com.musicbean.ui.live;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.LiveApi;
import com.musicbean.http.api.VideoApi;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.ui.BaseActivity;
import com.musicbean.util.ScreenUtils;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

public class PlaybackActivity extends BaseActivity implements View.OnClickListener{

    private static final int HIDE_CONTROL = 1;
    private static final int NO_SHOWING = 2;

    private boolean isShowing = false;
    private UserInfoBean mInfo;
    private PlaybackControlWidget mControlWidet;
    private TextView tvReply;
    private FrameLayout rootView;
    private LinearLayout llBlank;
    private LinearLayout llSay;
    private ListView lvChat;
    private ImageView ivAll;
    private FrameLayout flCover;
    private PLVideoTextureView mSurface;
    private MyReceiver mBroadcastReceiver;

    private boolean isFullScreen = true;


    public static final String EXTRA_ANCHOR_DATA = "data";
    public static final String EXTRA_PLAY_URL = "playurl";
    public static final String EXTRA_VIDEO_ID = "id";
    public static final String EXTRA_START_TIME = "stime";
    public static final String EXTRA_TYPE = "type";

    private String mPlayUrl;
    private int mId;
    private long mStartTime;
    private String mType;
    private int oldWidth;
    private int oldHeight;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_CONTROL:
                    Intent intent = new Intent();
                    intent.setAction("ON_LOOSEN");
                    sendBroadcast(intent);
                    Log.d("11","hide1");
                    break;
                case NO_SHOWING:
                    isShowing = false;
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        mInfo = (UserInfoBean) getIntent().getSerializableExtra(EXTRA_ANCHOR_DATA);
        mPlayUrl = getIntent().getStringExtra(EXTRA_PLAY_URL);
        mId = getIntent().getIntExtra(EXTRA_VIDEO_ID, -1);
        mStartTime = getIntent().getLongExtra(EXTRA_START_TIME, 0);
        Log.e("wcb", "stime:" + mStartTime);
        mType = getIntent().getStringExtra(EXTRA_TYPE);
        if (TextUtils.isEmpty(mType)) {
            mType = HotItemInfo.video;
        }

        if (mInfo == null || TextUtils.isEmpty(mPlayUrl) || mId < 0) {
            finish();
            return;
        }
        tvReply = (TextView) findViewById(R.id.tv_reply);
        tvReply.setOnClickListener(this);
        rootView = (FrameLayout) findViewById(R.id.root);
        llBlank = (LinearLayout) findViewById(R.id.ll_blank);
        llSay = (LinearLayout) findViewById(R.id.ll_say);
        lvChat = (ListView) findViewById(R.id.lv_chat);
//        ivAll = (ImageView) findViewById(R.id.iv_all);
//        ivAll.setOnClickListener(this);
        mControlWidet = (PlaybackControlWidget) findViewById(R.id.control_widget);
        mControlWidet.setData(mInfo);
        mControlWidet.setVideoInfo(mType, mId + "");
        //mControlWidet.initChat(mInfo.roomid);


        mControlWidet.showPreparing();
        initSurface();
        registerBroadcast();
    }

    private void registerBroadcast(){
        mBroadcastReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("BIG_SCALE");
        intentFilter.addAction("SMALL_SCALE");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private int mVideoHeight;
    private int mVideoWidth;

    private void initSurface() {
        flCover = (FrameLayout) findViewById(R.id.fl_cover);
        flCover.setOnClickListener(this);
        mSurface = (PLVideoTextureView) findViewById(R.id.surface);
//        mSurface.setDisplayOrientation()

        mSurface.getLayoutParams().height = ScreenUtils.getScreenHeight(this)/3;
        mSurface.getLayoutParams().width = ScreenUtils.getScreenWidth(this)/3;
//        mSurface.getLayoutParams().height = ScreenUtils.getScreenHeight(this);
//        mSurface.getLayoutParams().width = ScreenUtils.getScreenWidth(this);


//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);



        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = getIntent().getIntExtra("mediaCodec", 0);
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);

        mSurface.setAVOptions(options);

        mSurface.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

        mSurface.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer) {
                mControlWidet.hidePreparing();
                mVideoHeight = plMediaPlayer.getVideoHeight();
                mVideoWidth = plMediaPlayer.getVideoWidth();

                Log.d("11",mVideoHeight+"bbbbbbbbbb"+mVideoWidth);
                if (mVideoWidth > mVideoHeight) {
                    Log.d("11","bbbbbbbbbb");
                    mSurface.setDisplayOrientation(90);
                    mSurface.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
                }
            }
        });



        mSurface.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
                Log.e("LivePlayActivity", "onInfo:" + what);
                switch (what) {
                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mControlWidet.showLoading();
                        break;
                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mControlWidet.hideLoading();
                        break;
                }
                return false;
            }
        });

        mSurface.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
                Log.e("LivePlayActivity", "onError:" + i);
                return false;
            }
        });

        mSurface.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer plMediaPlayer) {
                //mSurface.setVideoPath(mPlayUrl);
                //mSurface.start();
            }
        });
        mSurface.setMediaController(mControlWidet);
//        mControlWidet.setAnchorView(mSurface);
//                /** 设置旋转动画 */
//        final RotateAnimation animation =new RotateAnimation(90f,180f, Animation.RELATIVE_TO_SELF,
//                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//        animation.setDuration(5000);//设置动画持续时间
//        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
//        mControlWidet.startAnimation(animation);

        mSurface.setVideoPath(mPlayUrl);
        mSurface.setDisplayOrientation(90);

        if (mType.equals(HotItemInfo.video)) {
            VideoApi.report(this, mId + "", new ResponseHandler() {
                @Override
                public void onSuccess(String data) {

                }

                @Override
                public void onFailure(int responseCode, String errorMsg) {

                }
            });
        } else {
            LiveApi.reportRecord(this, mId + "", new ResponseHandler() {
                @Override
                public void onSuccess(String data) {

                }

                @Override
                public void onFailure(int responseCode, String errorMsg) {

                }
            });
        }
    }

    public void replay() {
        mSurface.stopPlayback();
        mSurface.setVideoPath(mPlayUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStartTime > 0) {
            mSurface.seekTo(mStartTime);
        } else {
            mSurface.start();
        }

        mHandler.sendEmptyMessageDelayed(HIDE_CONTROL,10000);

        isShowing = true;//正在显示控制栏

        mHandler.sendEmptyMessageDelayed(NO_SHOWING,10000);//10秒后把isShowing变成false,10秒后才可以点击
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurface.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mControlWidet != null) {
            mControlWidet.onDestroy();
        }
        if (mSurface != null) {
            mSurface.stopPlayback();
        }

        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_cover:
                if (!isShowing) {
                    isShowing = true;
                    Intent intent = new Intent();
                    intent.setAction("ON_TOUCH");
                    sendBroadcast(intent);
                    mHandler.sendEmptyMessageDelayed(NO_SHOWING,10000);
                }
                break;
            case R.id.tv_reply:

                break;
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if("BIG_SCALE".equals(intent.getAction())){
                OnLandscape();
            } else if ("SMALL_SCALE".equals(intent.getAction())) {
                OnPortrait();
            }
        }
    }

    private void OnLandscape() {
        isFullScreen = false;

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mSurface.getLayoutParams().height = ScreenUtils.getScreenWidth(PlaybackActivity.this);
        mSurface.getLayoutParams().width = ScreenUtils.getScreenHeight(PlaybackActivity.this);
        flCover.getLayoutParams().height = ScreenUtils.getScreenWidth(PlaybackActivity.this)-110;
        flCover.getLayoutParams().width = ScreenUtils.getScreenHeight(PlaybackActivity.this);
        llSay.setVisibility(View.GONE);
        llBlank.setVisibility(View.GONE);
        lvChat.setVisibility(View.GONE);
        oldWidth = mControlWidet.getWidth();
        oldHeight = mControlWidet.getHeight();
        mControlWidet.getLayoutParams().height = ScreenUtils.getScreenWidth(PlaybackActivity.this);
        mControlWidet.getLayoutParams().width = ScreenUtils.getScreenHeight(PlaybackActivity.this);
    }
    private void OnPortrait() {
        isFullScreen = true;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSurface.getLayoutParams().height = ScreenUtils.getScreenWidth(PlaybackActivity.this)/3;
        mSurface.getLayoutParams().width = ScreenUtils.getScreenHeight(PlaybackActivity.this)/3;
        flCover.getLayoutParams().width = ScreenUtils.getScreenWidth(PlaybackActivity.this);//避免碰到进度条，碰到进度条就会触发并影响控制栏的显示时间
        flCover.getLayoutParams().height = ScreenUtils.getScreenHeight(PlaybackActivity.this)/2 - 20;
        llSay.setVisibility(View.VISIBLE);
        llBlank.setVisibility(View.VISIBLE);
        lvChat.setVisibility(View.VISIBLE);
        mControlWidet.getLayoutParams().height = oldHeight;
        mControlWidet.getLayoutParams().width = oldWidth;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isFullScreen) {
                OnPortrait();
                return true;
            } else {
                PlaybackActivity.this.finish();
                return false;
            }
        }
        return false;
    }

    private void addView(final LinearLayout lineLayout)
    {
        final TextView showText = new TextView(this);
        showText.setTextColor(Color.GREEN);
        showText.setTextSize(30);
        showText.setId(10001);//设置 id
        showText.setText("我是在程序中添加的第一个文本");
        showText.setBackgroundColor(Color.GRAY);
        // set 文本大小
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //set 四周距离
        params.setMargins(10, 10, 10, 10);

        showText.setLayoutParams(params);

        //添加文本到主布局
        lineLayout.addView(showText );

        //创建按钮
        Button btn = new Button(this);
        btn.setText("点击删除文本");
        btn.setBackgroundColor(Color.GRAY) ;
        LinearLayout.LayoutParams btn_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_params.setMargins(0, 60, 60, 0);
        btn_params.gravity = Gravity.CENTER_HORIZONTAL;
        btn.setLayoutParams(btn_params);
        // 动态添加按钮到主布局
        lineLayout.addView(btn);

        //点击按钮 动态删除文本
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(null != lineLayout.findViewById(10001))
                {
                    lineLayout.removeView(showText);
                }
                else
                {
                    Toast.makeText(PlaybackActivity.this, "文本已被删除", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //动态创建一个相对布局
        RelativeLayout relaLayout = new RelativeLayout(this);
        relaLayout.setBackgroundColor(Color.BLUE);
        RelativeLayout.LayoutParams lp11 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 120);


        relaLayout.setLayoutParams(lp11);
        //动态创建一个文本
        final TextView RelaText = new TextView(this);
        RelaText.setTextColor(Color.GREEN);
        RelaText.setTextSize(20);
        RelaText.setText("我是在程序中添加的第二个文本，在相对布局中");
        RelaText.setBackgroundColor(Color.GRAY);

        //设置文本的布局
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        lp2.setMargins(10, 10, 10, 10);
        //将文本添加到相对布局中
        relaLayout.addView(RelaText,lp2);
        //将这个布局添加到主布局中
        lineLayout.addView(relaLayout);

    }
}
