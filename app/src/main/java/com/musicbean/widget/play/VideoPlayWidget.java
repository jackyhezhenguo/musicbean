package com.musicbean.widget.play;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.VideoApi;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.VideoListItem;
import com.musicbean.ui.live.PlaybackControlWidget;
import com.musicbean.ui.main.MainActivity;
import com.musicbean.util.LogUtils;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;

/**
 * Created by boyo on 16/11/26.
 */
public class VideoPlayWidget extends FrameLayout implements MyMediaController.OnControlViewChangeListener {

    private PLVideoView mVideoView;

    private MyMediaController mMediaController;
    private PlaybackControlWidget mFullControlView;

    private ViewGroup mContainer;

    private ViewGroup.LayoutParams mLayoutParams;

    private boolean isFullScreen = false;

    private int mVideoWidth, mVideoHeight;

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public VideoPlayWidget(Context context) {
        super(context);
        init();
    }

    public VideoPlayWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setBackgroundColor(Color.BLACK);

        initVideoView();

        setFocusable(true);
        setFocusableInTouchMode(true);

        setFitsSystemWindows(true);
    }

    private void initVideoView() {
        mVideoView = new PLVideoView(getContext());
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, 0);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);

        mVideoView.setAVOptions(options);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

        // You can also use a custom `MediaController` widget
        mMediaController = new MyMediaController(getContext());
        //mVideoView.setMediaController(mMediaController);

        mFullControlView = new PlaybackControlWidget(getContext());

        mVideoView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer) {
                LogUtils.e("player", "onPrepared");
                mMediaController.hidePreparing();
                mMediaController.show();
                mVideoHeight = plMediaPlayer.getVideoHeight();
                mVideoWidth = plMediaPlayer.getVideoWidth();
                LogUtils.e("player", "onPrepared:width:" + mVideoWidth + ",height:" + mVideoHeight);
            }
        });
        mVideoView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
                switch (what) {
                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mMediaController.showBuffering(0);
                        break;
                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mMediaController.showBuffering(100);
                        break;
                }
                return false;
            }
        });

        mMediaController.setOnControlViewChangeListener(this);
        mFullControlView.setOnControlViewChangeListener(this);

        addView(mVideoView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //addView(mMediaController, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    @Override
    public void onChangeToFullscreen() {

        isFullScreen = true;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (mContainer != null) {
            mContainer.removeView(this);
        }

        ViewGroup vg = getActivityRootView();
        if (vg instanceof LinearLayout) {
            vg.addView(this, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            vg.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        addControlView(mFullControlView);

        mVideoView.setMediaController(mFullControlView);
        ((MainActivity) getActivity()).hideTabLayout();

        mFullControlView.requestFocus();

        //mMediaController.showFullView();

        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

        if (mVideoWidth > mVideoHeight) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

//        Intent intent = new Intent(getContext(), PlaybackActivity.class);
//        intent.putExtra(PlaybackActivity.EXTRA_ANCHOR_DATA, mPlayItem.userinfo);
//        intent.putExtra(PlaybackActivity.EXTRA_PLAY_URL, mPlayItem.playaddr.flv);
//        intent.putExtra(PlaybackActivity.EXTRA_VIDEO_ID, mPlayItem.id);
//        intent.putExtra(PlaybackActivity.EXTRA_TYPE, HotItemInfo.video);
//
//        Log.e("wcb", "isPlay:" + mVideoView.isPlaying());
//        Log.e("wcb", "position:" + mVideoView.getCurrentPosition());
//        if (mVideoView.isPlaying() && mVideoView.getCurrentPosition() > 0) {
//            intent.putExtra(PlaybackActivity.EXTRA_START_TIME, mVideoView.getCurrentPosition());
//        }
//        getContext().startActivity(intent);
    }

    @Override
    public void onChangeToSmallscreen() {
        isFullScreen = false;
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getActivityRootView().removeView(this);
        if (mContainer != null) {
            mContainer.removeView(this);
            mContainer.addView(this, mLayoutParams);
        }

        addControlView(mMediaController);
        mVideoView.setMediaController(mMediaController);
        ((MainActivity) getActivity()).showTabLayout();

        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
        //mMediaController.showSmallView();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private Activity getActivity() {
        return (Activity) getContext();
    }

    protected ViewGroup getActivityRootView() {
        return (ViewGroup) ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
        //return (ViewGroup) getActivity().getWindow().getDecorView();
    }

    private VideoListItem mPlayItem;

    public void playInsideView(ViewGroup vg, ViewGroup.LayoutParams lp, VideoListItem item) {
        if (mContainer != null) {
            mVideoView.stopPlayback();
            mContainer.removeView(this);
        }

        mContainer = vg;
        mLayoutParams = lp;
        mPlayItem = item;

        onChangeToSmallscreen();
        mMediaController.showPreparing();
        mMediaController.setTitle(item.title);
        mFullControlView.setData(item.userinfo);
        mFullControlView.setVideoInfo(HotItemInfo.video, item.id + "");
        mVideoView.setVideoPath(item.playaddr.flv);

        VideoApi.report(getContext(), item.id + "", new ResponseHandler() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    public void clearContainer() {
        if (mContainer != null) {
            mVideoView.stopPlayback();
            mContainer.removeView(this);

            mContainer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isFullScreen) {
            onChangeToSmallscreen();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void pause() {
        mVideoView.pause();
    }

    public void resume() {
        if (mContainer != null) {
            mVideoView.start();
        }
    }

    private View mControllView;

    protected void addControlView(View view) {
        if (mControllView != null) {
            removeView(mControllView);
        }

        mControllView = view;
        addView(mControllView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
