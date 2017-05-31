package com.musicbean.ui.live;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.RelationInfo;
import com.musicbean.ui.BaseActivity;
import com.musicbean.util.ScreenUtils;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;

import java.lang.reflect.Type;

public class LivePlayActivity extends BaseActivity implements View.OnClickListener {

    private HotItemInfo mInfo;
    private LiveControlWidget mControlWidet;
    private PLVideoView mSurface;

    private TextView mRelationTxt;
    private View mRelationLine;

    private int mRelation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_play);
        Log.d("11","ooo");

        mInfo = (HotItemInfo) getIntent().getSerializableExtra("data");
        if (mInfo == null) {
            finish();
            return;
        }

        mControlWidet = (LiveControlWidget) findViewById(R.id.control_widget);
        mControlWidet.setData(mInfo.anchorinfo);
        mControlWidet.initChat(mInfo.roomid);
        mRelationTxt = (TextView) mControlWidet.findViewById(R.id.btn_follow);
        mRelationTxt.setOnClickListener(this);
        mRelationLine = mControlWidet.findViewById(R.id.line_follow);

        mControlWidet.showPreparing();
        initSurface();

        getRelation();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_follow:
                //if (mRelation == 1) {
                //    disFollow();
                //} else {
                follow();
                //}
                break;
        }
    }

    private void follow() {
        UserApi.follow(this, mInfo.anchorinfo.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                mRelation = 1;
                mRelationTxt.setVisibility(View.GONE);
                mRelationLine.setVisibility(View.GONE);
                Toast.makeText(LivePlayActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void disFollow() {
        UserApi.disFollow(this, mInfo.anchorinfo.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                mRelation = 0;
                mRelationTxt.setText("关注");
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void getRelation() {
        UserApi.getRalation(this, mInfo.anchorinfo.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<RelationInfo>>() {
                }.getType();
                BaseResponse<RelationInfo> res = new Gson().fromJson(data, objectType);
                mRelation = res.data.relation;

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

    private void initSurface() {
        mSurface = (PLVideoView) findViewById(R.id.surface);

        mSurface.getLayoutParams().height = ScreenUtils.getScreenHeight(this);
        mSurface.getLayoutParams().width = ScreenUtils.getScreenWidth(this);

        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = getIntent().getIntExtra("mediaCodec", 0);
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);

        mSurface.setAVOptions(options);

        mSurface.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
//        mSurface.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

        mSurface.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer) {
                mControlWidet.hidePreparing();
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
                mSurface.setVideoPath(mInfo.play_addr.rtmp);
                mSurface.start();
            }
        });

        mSurface.setVideoPath(mInfo.play_addr.rtmp);
    }

    public void replay() {
        mSurface.stopPlayback();
        mSurface.setVideoPath(mInfo.play_addr.rtmp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurface.start();
        Log.d("11","ooo");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurface.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mControlWidet.onDestroy();
        mSurface.stopPlayback();
    }

}
