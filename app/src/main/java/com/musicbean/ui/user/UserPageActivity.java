package com.musicbean.ui.user;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.api.VideoApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.ListData;
import com.musicbean.http.bean.RecordInfo;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.http.bean.UserPageInfo;
import com.musicbean.http.bean.VideoInfo;
import com.musicbean.manager.LevelManager;
import com.musicbean.ui.BaseActivity;
import com.musicbean.ui.live.PlaybackActivity;
import com.musicbean.widget.DividerItemDecoration;
import com.musicbean.widget.EndlessRecyclerViewAdapter;

import net.tsz.afinal.FinalBitmap;

import java.lang.reflect.Type;

/**
 * Created by boyo on 16/7/8.
 */
public class UserPageActivity extends BaseActivity implements View.OnClickListener,
        EndlessRecyclerViewAdapter.RequestToLoadMoreListener {
    private TextView mAttends;
    private TextView mFans;
    private TextView mBeans;

    private ImageView mHeadImg;
    private ImageView mCoverImg;
    private ImageView mCertLevelImg;
    private TextView mCertLevelTxt;
    private TextView mStudio;
    private TextView mStudio2;
    private TextView mName;
    private TextView mSign;
    private TextView mHonor;
    private TextView mOpus;
    private TextView mProfession;

    private ImageView[] mFansImg = new ImageView[3];

    private UserInfoBean mUinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_page);

        mUinfo = (UserInfoBean) getIntent().getSerializableExtra("uinfo");
        if (mUinfo == null) {
            finish();
            return;
        }

        initView();
        initVideoView();

        setData(mUinfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }

    private void requestData() {
        UserApi.getUserpage(this, mUinfo.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<UserPageInfo>>() {
                }.getType();
                BaseResponse<UserPageInfo> res = new Gson().fromJson(data, objectType);

                setData(res.data);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private View recordSplit;
    private View recordNewest;
    private ImageView recordImg;
    private TextView recordName;
    private TextView recordCount;
    private TextView recordDate;

    private void setData(final UserPageInfo pageInfo) {
        // setData(pageInfo.userinfo);
        mAttends.setText(pageInfo.conceninfo.total + "\n关注");
        mFans.setText(pageInfo.followinfo.total + "\n粉丝");

        if (pageInfo.followinfo.list != null && pageInfo.followinfo.list.size() > 0) {
            int count = Math.min(pageInfo.followinfo.list.size(), 3);
            for (int i = 0; i < count; i++) {
                UserInfoBean info = pageInfo.followinfo.list.get(i);
                FinalBitmap.getInstance().display(mFansImg[i], info.image);
                mFansImg[i].setTag(info);
            }
        }

        if (pageInfo.recordinfo != null && pageInfo.recordinfo.getAnchorinfo() != null) {
            final RecordInfo recordInfo = pageInfo.recordinfo;
            FinalBitmap.getInstance().display(recordImg, recordInfo.getAnchorinfo().image);
            recordName.setText(recordInfo.getAnchorinfo().nickname);
            recordCount.setText(String.valueOf(recordInfo.getPlaynum()));
            recordDate.setText(recordInfo.getPubdate_desc());

            recordNewest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserPageActivity.this, PlaybackActivity.class);
                    intent.putExtra(PlaybackActivity.EXTRA_ANCHOR_DATA, pageInfo.userinfo);
                    intent.putExtra(PlaybackActivity.EXTRA_PLAY_URL, recordInfo.getPlayaddr());
                    intent.putExtra(PlaybackActivity.EXTRA_VIDEO_ID, recordInfo.getId());
                    intent.putExtra(PlaybackActivity.EXTRA_TYPE, HotItemInfo.record);
                    startActivity(intent);
                }
            });
            recordSplit.setVisibility(View.VISIBLE);
            recordNewest.setVisibility(View.VISIBLE);
        } else {
            recordSplit.setVisibility(View.GONE);
            recordNewest.setVisibility(View.GONE);
        }
    }

    public void setData(UserInfoBean info) {
        FinalBitmap.getInstance().display(mHeadImg, info.image);
        if (!TextUtils.isEmpty(info.h_cover)) {
            FinalBitmap.getInstance().display(mCoverImg, info.h_cover);
        }
        if (info.is_certified == 1) {
            mCertLevelImg.setVisibility(View.VISIBLE);
            mCertLevelImg.setImageResource(LevelManager.getInstance().getCertLevelResId(info.certify_level));
            mCertLevelTxt.setVisibility(View.VISIBLE);
            mCertLevelTxt.setText(info.certify_level_desc);
        }
        mName.setText(LevelManager.getInstance().getNameSexLevelSS(info));
        mStudio.setText(info.studio);
        mStudio2.setText(info.studio);
        mSign.setText(info.spec_sign);
        mHonor.setText(info.honor);
        mOpus.setText(info.opus);
        mProfession.setText(info.occupation);

        mBeans.setText(info.bean + "\n音悦豆");
        mAttends.setText("0\n关注");
        mFans.setText("0\n粉丝");
    }

    private void initView() {
        mAttends = (TextView) findViewById(R.id.txt_attends);
        mAttends.setOnClickListener(this);
        mFans = (TextView) findViewById(R.id.txt_fans);
        mFans.setOnClickListener(this);
        mBeans = (TextView) findViewById(R.id.txt_beans);

        mHeadImg = (ImageView) findViewById(R.id.head_img);
        mCoverImg = (ImageView) findViewById(R.id.cover_img);
        mStudio = (TextView) findViewById(R.id.studio);
        mStudio2 = (TextView) findViewById(R.id.studio2);
        mName = (TextView) findViewById(R.id.name);
        mSign = (TextView) findViewById(R.id.sign);

        mCertLevelImg = (ImageView) findViewById(R.id.cert_level);
        mCertLevelTxt = (TextView) findViewById(R.id.cert_desc);

        mHonor = (TextView) findViewById(R.id.honor);
        mOpus = (TextView) findViewById(R.id.opus);
        mProfession = (TextView) findViewById(R.id.profession);

        mFansImg[0] = (ImageView) findViewById(R.id.img1);
        mFansImg[1] = (ImageView) findViewById(R.id.img2);
        mFansImg[2] = (ImageView) findViewById(R.id.img3);
        mFansImg[0].setOnClickListener(this);
        mFansImg[1].setOnClickListener(this);
        mFansImg[2].setOnClickListener(this);

        recordSplit = findViewById(R.id.record_split);
        recordNewest = findViewById(R.id.record_newest);
        recordImg = (ImageView) findViewById(R.id.record_head_img);
        recordName = (TextView) findViewById(R.id.record_nickname);
        recordCount = (TextView) findViewById(R.id.record_count);
        recordDate = (TextView) findViewById(R.id.record_time);


        findViewById(R.id.fans_layout).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private View videoSplite;
    private RecyclerView videoList;
    private UserVideoAdapter videoAdapter;
    private EndlessRecyclerViewAdapter loadMoreVideoAdapter;
    private int start = 0;

    private void initVideoView() {
        videoSplite = findViewById(R.id.video_split);
        videoList = (RecyclerView) findViewById(R.id.video_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false);
        videoList.setLayoutManager(layoutManager);
        videoList.setHasFixedSize(true);
        Drawable divider = getResources().getDrawable(R.drawable.user_video_list_divider);
        videoList.addItemDecoration(new DividerItemDecoration(this, divider, DividerItemDecoration.HORIZONTAL_LIST));
        videoAdapter = new UserVideoAdapter(this);
        loadMoreVideoAdapter = new EndlessRecyclerViewAdapter(this, videoAdapter, this, R.layout.loading_view, true);
        videoList.setAdapter(loadMoreVideoAdapter);
        requestVideoData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fans_layout:
                Intent intent = new Intent(this, FansRankActivity.class);
                intent.putExtra("uid", mUinfo.id);
                startActivity(intent);
                break;
            case R.id.img1:
            case R.id.img2:
            case R.id.img3:
                UserInfoBean info = (UserInfoBean) v.getTag();
                if (info != null) {
                    intent = new Intent(this, UserPageActivity.class);
                    intent.putExtra("uinfo", info);
                    startActivity(intent);
                }
                break;
            case R.id.txt_attends:
                intent = new Intent(this, ConcernListActivity.class);
                intent.putExtra("uid", mUinfo.id);
                startActivity(intent);
                break;
            case R.id.txt_fans:
                intent = new Intent(this, FansListActivity.class);
                intent.putExtra("uid", mUinfo.id);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onLoadMoreRequested() {
        requestVideoData();
    }

    private void requestVideoData() {
        VideoApi.getVideoListById(this, mUinfo.id, start, 10, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ListData<VideoInfo>>>() {
                }.getType();
                BaseResponse<ListData<VideoInfo>> ret = new Gson().fromJson(data, objectType);
                if (ret.data.total <= 0) return;

                if (start == 0) {
                    videoAdapter.setData(ret.data.list, mUinfo);
                    videoList.setVisibility(View.VISIBLE);
                    videoSplite.setVisibility(View.VISIBLE);
                } else {
                    videoAdapter.addData(ret.data.list);
                }

                start++;

                if (videoAdapter.getItemCount() < ret.data.total) {
                    loadMoreVideoAdapter.onDataReady(true);
                } else {
                    loadMoreVideoAdapter.onDataReady(false);
                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                loadMoreVideoAdapter.onAppendingFailure();
            }

            @Override
            public void onFinish() {
            }
        });
    }
}
