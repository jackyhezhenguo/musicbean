package com.musicbean.widget.play;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.util.DateUtils;
import com.musicbean.util.LogUtils;
import com.pili.pldroid.player.IMediaController;

/**
 * Created by boyo on 16/8/8.
 */
public class MyMediaController extends RelativeLayout implements IMediaController, CompoundButton.OnCheckedChangeListener
        , View.OnTouchListener, GestureDetectorController.IGestureListener, View.OnClickListener {
    private static final String TAG = "MyMediaController";
    private CheckBox mBtnPlay;
    private CheckBox mBtnScale;
    private SeekBar mProgressBar;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    private TextView mTitleText;
    private ImageView mBackImg;

    private View mLoadingView;

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = false;
    private static int sDefaultTimeout = 3000;
    private static final int SEEK_TO_POST_DELAY_MILLIS = 200;

    private AudioManager mAM;
    private Runnable mLastSeekBarRunnable;

    private IMediaController.MediaPlayerControl mPlayer;

    private View mBottomLayout;

    private GestureDetectorController mGestureController;

    public MyMediaController(Context context) {
        this(context, null);
    }

    public MyMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAM = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initViews();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        //updatePausePlay();
                    }
                    break;
            }
        }
    };

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mProgressBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgressBar.setProgress((int) pos);
            }
            //int percent = mPlayer.getBufferPercentage();
            //mProgressBar.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mTotalTime != null)
            mTotalTime.setText(DateUtils.formatDuration(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(DateUtils.formatDuration(position));

        return position;
    }


    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.player_controller_layout, this);
        mBtnPlay = (CheckBox) findViewById(R.id.btn_play);
        mBtnScale = (CheckBox) findViewById(R.id.btn_scale);
        mProgressBar = (SeekBar) findViewById(R.id.progress_bar);
        mCurrentTime = (TextView) findViewById(R.id.current_time);
        mTotalTime = (TextView) findViewById(R.id.total_time);
        mLoadingView = findViewById(R.id.loading_view);
        mBottomLayout = findViewById(R.id.bottom_layout);
        mBackImg = (ImageView) findViewById(R.id.btn_back);
        mBackImg.setOnClickListener(this);

        mBtnPlay.setOnCheckedChangeListener(this);
        mBtnScale.setOnCheckedChangeListener(this);
        mProgressBar.setOnSeekBarChangeListener(mSeekListener);
        mProgressBar.setMax(1000);

        mTitleText = (TextView) findViewById(R.id.title);

        setOnTouchListener(this);

        mGestureController = new GestureDetectorController(getContext());
        mGestureController.setGestureListener(this);

        setEnabled(false);

        mBackImg.setVisibility(View.GONE);
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayerControl) {
        mPlayer = mediaPlayerControl;
        setEnabled(true);
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    public void show(int timeout) {
        if (!mShowing) {
            mBottomLayout.setVisibility(View.VISIBLE);
            mBtnPlay.setVisibility(View.VISIBLE);
            mShowing = true;
        }
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout);
        }

        if (mPlayer != null) {
            mBtnPlay.setChecked(mPlayer.isPlaying());
        }
    }

    @Override
    public void hide() {
        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                mBottomLayout.setVisibility(View.GONE);
                mBtnPlay.setVisibility(View.GONE);
            } catch (IllegalArgumentException ex) {
                LogUtils.d(TAG, "MediaController already removed");
            }
            mShowing = false;
        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public void setAnchorView(View view) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.btn_play:
                if (isChecked) {
                    mPlayer.start();
                } else {
                    mPlayer.pause();
                }

                break;
            case R.id.btn_scale:
//                if (isChecked) {
//                    toFullScreen();
//                } else {
//                    toSmallScreen();
//                }

                toFullScreen();

                break;
        }
    }

    private void toFullScreen() {
        if (mViewChangeListener != null) {
            mViewChangeListener.onChangeToFullscreen();
        }
    }

    private void toSmallScreen() {
        if (mViewChangeListener != null) {
            mViewChangeListener.onChangeToSmallscreen();
        }
    }

    private Activity getActivity() {
        return (Activity) getContext();
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;

            final int newposition = (int) (mDuration * progress) / 1000;
            String time = DateUtils.formatDuration((newposition));
            if (mInstantSeeking) {
                mHandler.removeCallbacks(mLastSeekBarRunnable);
                mLastSeekBarRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mPlayer.seekTo(newposition);
                    }
                };
                mHandler.postDelayed(mLastSeekBarRunnable, SEEK_TO_POST_DELAY_MILLIS);
            }
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking) {
                mPlayer.seekTo((int) (mDuration * bar.getProgress()) / 1000);
            }

            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.btn_back) {
            if (mBtnScale.isChecked()) {
                mBtnScale.setChecked(false);
            } else {
                getActivity().finish();
            }
            return true;
        } else {
            return mGestureController.onTouchEvent(event);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            if (mBtnScale.isChecked()) {
                mBtnScale.setChecked(false);
            } else {
                getActivity().finish();
            }
        }
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0
                && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            if (mPlayer.isPlaying()) {
                mBtnPlay.setChecked(false);
            } else {
                mBtnPlay.setChecked(true);
            }
            show(sDefaultTimeout);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            mBtnPlay.setChecked(false);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mBtnPlay.setEnabled(enabled);
        mProgressBar.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public void showPreparing() {
        mLoadingView.setVisibility(View.VISIBLE);
        setEnabled(false);
    }

    public void hidePreparing() {
        mLoadingView.setVisibility(View.GONE);
        setEnabled(true);
    }

    public void showBuffering(int percent) {
        if (percent == 100) {
            mLoadingView.setVisibility(View.GONE);
        } else {
            mLoadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScrollBegin(GestureDetectorController.ScrollViewType type) {

    }

    @Override
    public void onScrollEnd(GestureDetectorController.ScrollViewType type, MotionEvent e) {

    }

    @Override
    public void onScrollLeft(float distanceY, float moveY) {

    }

    @Override
    public void onScrollRight(float distanceY, float moveY) {

    }

    @Override
    public void onScrollHorizontal(float distanceX, float moveX) {

    }

    @Override
    public void onSingleClick(MotionEvent e) {
        if (mShowing) {
            hide();
        } else {
            show(sDefaultTimeout);
        }
    }

    @Override
    public void onDoubleClick(MotionEvent e) {
        //mBtnPlay.setChecked(!mBtnPlay.isChecked());
    }

    public interface OnControlViewChangeListener {
        void onChangeToFullscreen();

        void onChangeToSmallscreen();
    }

    private OnControlViewChangeListener mViewChangeListener;

    public void setOnControlViewChangeListener(OnControlViewChangeListener l) {
        mViewChangeListener = l;
    }

    public void setTitle(String str) {
        mTitleText.setText(str);
    }

    public void showSmallView() {
        mBackImg.setVisibility(View.GONE);
    }

    public void showFullView() {
        mBackImg.setVisibility(View.VISIBLE);
    }
}
