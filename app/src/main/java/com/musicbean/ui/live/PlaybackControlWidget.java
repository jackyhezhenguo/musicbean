package com.musicbean.ui.live;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.RelationInfo;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.util.DateUtils;
import com.musicbean.widget.play.MyMediaController;
import com.pili.pldroid.player.IMediaController;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.qihoo.share.framework.ShareParam;

import java.lang.reflect.Type;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by boyo on 16/11/20.
 */
public class PlaybackControlWidget extends LiveControlWidget implements RadioButton.OnCheckedChangeListener
        , IMediaController, View.OnClickListener {
    SeekBar mSeekbar;
    LinearLayout bottomLayout;
    CheckBox mBtnScale;
    CheckBox mCbxPlay;

    private MyReceiver mBroadcastReceiver;
    private int count = 0;

    private TextView mCurTime;
    private TextView mTotalTime;
    private TextView mRelationTxt;
    private View mRelationLine;

    public PlaybackControlWidget(Context context) {
        super(context);
    }

    public PlaybackControlWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews() {
        super.initViews();

        mOnlineNum.setVisibility(View.GONE);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);

        mSeekbar = (SeekBar) findViewById(R.id.progress_bar);
        mSeekbar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("11","touch");
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    bottomLayout.setVisibility(View.VISIBLE);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 2000);
                }

                return false;
            }
        });
        mSeekbar.setMax(1000);
        mBtnScale = (CheckBox) findViewById(R.id.btn_scale);
        mCbxPlay = (CheckBox) findViewById(R.id.cbx_play);
        mCbxPlay.setVisibility(View.VISIBLE);
        mCurTime = (TextView) findViewById(R.id.current_time);
        mTotalTime = (TextView) findViewById(R.id.total_time);
        findViewById(R.id.play_progress_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_chat).setVisibility(View.GONE);
        findViewById(R.id.btn_gift).setVisibility(View.GONE);

        mCbxPlay.setOnCheckedChangeListener(this);
        mBtnScale.setOnCheckedChangeListener(this);
        mCbxPlay.setEnabled(false);
        mSeekbar.setEnabled(false);

        setOnTouchListener(null);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRelationTxt = (TextView) findViewById(R.id.btn_follow);
        mRelationTxt.setOnClickListener(this);
        mRelationLine = findViewById(R.id.line_follow);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mMsgList.setVisibility(View.GONE);


        registerBroadcast();


    }
    private void registerBroadcast(){
        mBroadcastReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ON_TOUCH");
        intentFilter.addAction("ON_LOOSEN");
        getContext().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    public class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if("ON_TOUCH".equals(intent.getAction())){
                Log.d("11","ON_TOUCH1");
                bottomLayout.setVisibility(View.VISIBLE);

                //开始一个定时任务

                mHandler.sendEmptyMessageDelayed(HIDE_CONTROL,10000);
            } else if ("ON_LOOSEN".equals(intent.getAction())) {
                mHandler.sendEmptyMessage(HIDE_CONTROL);
            }
        }
    }


    @Override
    public void setData(UserInfoBean info) {
        super.setData(info);

        getRelation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_follow:
                follow();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void getRelation() {
        UserApi.getRalation(getContext(), mAnchorData.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<RelationInfo>>() {
                }.getType();
                BaseResponse<RelationInfo> res = new Gson().fromJson(data, objectType);

                if (res.data.relation == 0) {
                    mRelationLine.setVisibility(View.VISIBLE);
                    mRelationTxt.setVisibility(View.VISIBLE);
                    mRelationTxt.setText("关注");
                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void follow() {
        UserApi.follow(getContext(), mAnchorData.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                mRelationTxt.setVisibility(View.GONE);
                mRelationLine.setVisibility(View.GONE);
                Toast.makeText(getContext(), "关注成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void disFollow() {
        UserApi.disFollow(getContext(), mAnchorData.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                mRelationTxt.setText("关注");
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    @Override
    public void initChat(String roomid) {
//        mRoomId = roomid;
//
//        IMManager.getInstance().addMessageListener(this);
//        IMManager.getInstance().getHistoryMessage(mRoomId, 0);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.btn_scale) {
            Log.d("11","btn_scale");

            if (isChecked) {
                Intent intent = new Intent();
                intent.setAction("BIG_SCALE");
                getContext().sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction("SMALL_SCALE");
                getContext().sendBroadcast(intent);
            }
        } else {
            if (isChecked) {
                if (mPlayer != null) {
                    mPlayer.start();
                }
            } else {
                if (mPlayer != null) {
                    mPlayer.pause();
                }
            }
        }
    }

    private PLVideoView mVideoView;
    private MediaPlayerControl mPlayer;

    public void setVideoView(PLVideoView videoView) {
        mVideoView = videoView;
        mVideoView.setMediaController(this);
    }

    private static final int SHOW_PROGRESS = 2;
    private static final int SHOW_CONTROL = 22;
    private static final int HIDE_CONTROL = 33;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case SHOW_PROGRESS:
                    pos = setProgress();
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    updatePausePlay();
                    break;
                case HIDE_CONTROL:
                    bottomLayout.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    private void updatePausePlay() {
        if (mPlayer.isPlaying())
            mCbxPlay.setChecked(true);
        else
            mCbxPlay.setChecked(false);
    }

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mSeekbar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mSeekbar.setProgress((int) pos);
            }
            //int percent = mPlayer.getBufferPercentage();
            //mSeekbar.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mTotalTime != null)
            mTotalTime.setText(DateUtils.formatDuration(mDuration));
        if (mCurTime != null)
            mCurTime.setText(DateUtils.formatDuration(position));

        return position;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void quitRoom() {

    }

    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayerControl) {
        mPlayer = mediaPlayerControl;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        mCbxPlay.setEnabled(true);
        mSeekbar.setEnabled(true);
        mSeekbar.setOnSeekBarChangeListener(mSeekListener);
    }

    @Override
    public void show() {

    }

    @Override
    public void show(int i) {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void setAnchorView(View view) {

    }

    private boolean mDragging;
    private long mDuration;
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mPlayer.seekTo((int) (mDuration / 1000f * bar.getProgress()));

            mHandler.removeMessages(SHOW_PROGRESS);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    @Override
    protected ShareParam getShareParam() {
        return ShareWindow.buildParam(getContext(), mAnchorData.nickname, mAnchorData.id, mType, mId);
    }

    private String mType, mId;

    public void setVideoInfo(String type, String id) {
        mType = type;
        mId = id;
    }

    private MyMediaController.OnControlViewChangeListener mViewChangeListener;

    public void setOnControlViewChangeListener(MyMediaController.OnControlViewChangeListener l) {
        mViewChangeListener = l;
    }

    @Override
    protected void onCloseClick() {
        if (mViewChangeListener != null) {
            mViewChangeListener.onChangeToSmallscreen();
        } else {
            super.onCloseClick();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mViewChangeListener != null) {
            mViewChangeListener.onChangeToSmallscreen();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void showUserWindow(UserInfoBean info) {
        if (mUserWindow == null) {
            mUserWindow = new UserPageWindow((Activity) getContext(), mRoomId);
            mUserWindow.setOnUserPageListener(this);
            mUserWindow.hideOpLayout(mType);
        }

        if (!mUserWindow.isShowing()) {
            mUserWindow.show(info, mAnchorData.id);
        }
    }

}
