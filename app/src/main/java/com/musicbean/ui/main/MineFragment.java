package com.musicbean.ui.main;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.HttpUtil;
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
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.live.PlaybackActivity;
import com.musicbean.ui.pay.BuyActivity;
import com.musicbean.ui.pay.NewGetCashActivity;
import com.musicbean.ui.settings.SettingsActivity;
import com.musicbean.ui.storeVideos.StoreActivity;
import com.musicbean.ui.user.ConcernListActivity;
import com.musicbean.ui.user.FansListActivity;
import com.musicbean.ui.user.FansRankActivity;
import com.musicbean.ui.user.MyLevelActivity;
import com.musicbean.ui.user.UserInfoActivity;
import com.musicbean.ui.user.UserPageActivity;
import com.musicbean.ui.user.UserVideoAdapter;
import com.musicbean.widget.DividerItemDecoration;
import com.musicbean.widget.EndlessRecyclerViewAdapter;

import net.tsz.afinal.FinalBitmap;

import java.lang.reflect.Type;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment implements View.OnClickListener,
        EndlessRecyclerViewAdapter.RequestToLoadMoreListener {

    private View mRootView;

    private TextView mAttends;
    private TextView mFans;
    private TextView mBeans;

    private ImageView mHeadImg;
    private ImageView mCoverImg;
    private TextView mStudio;
    private TextView mName;
    private TextView mSign;
    private TextView mBean;
    private TextView mDiamond;

    private TextView mLevel;
    private ImageView mCertLevelImg;
    private TextView mCertLevelTxt;

    private ImageView[] mFansImg = new ImageView[3];

    public MineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_mine, container, false);

//            mAttends = (TextView) mRootView.findViewById(R.id.txt_attends);
//            mFans = (TextView) mRootView.findViewById(R.id.txt_fans);
//            mBeans = (TextView) mRootView.findViewById(R.id.txt_beans);

//            mAttends.setOnClickListener(this);
//            mFans.setOnClickListener(this);

            mHeadImg = (ImageView) mRootView.findViewById(R.id.head_img);
//            mCoverImg = (ImageView) mRootView.findViewById(R.id.cover_img);
//            mStudio = (TextView) mRootView.findViewById(R.id.studio);
//            mName = (TextView) mRootView.findViewById(R.id.name);
//            mSign = (TextView) mRootView.findViewById(R.id.sign);
//            mBean = (TextView) mRootView.findViewById(R.id.bean);
//            mDiamond = (TextView) mRootView.findViewById(R.id.diamond);

            mLevel = (TextView) mRootView.findViewById(R.id.level);
//            mCertLevelImg = (ImageView) mRootView.findViewById(R.id.cert_level);
            mCertLevelTxt = (TextView) mRootView.findViewById(R.id.cert_desc);

//            mFansImg[0] = (ImageView) mRootView.findViewById(R.id.img1);
//            mFansImg[1] = (ImageView) mRootView.findViewById(R.id.img2);
//            mFansImg[2] = (ImageView) mRootView.findViewById(R.id.img3);
//            mFansImg[0].setOnClickListener(this);
//            mFansImg[1].setOnClickListener(this);
//            mFansImg[2].setOnClickListener(this);

//            recordSplit = mRootView.findViewById(R.id.my_record_split);
//            recordNewest = mRootView.findViewById(R.id.my_record_newest);
            recordImg = (ImageView) mRootView.findViewById(R.id.my_record_head_img);
//            recordName = (TextView) mRootView.findViewById(R.id.my_record_nickname);
//            recordCount = (TextView) mRootView.findViewById(R.id.my_record_count);
//            recordDate = (TextView) mRootView.findViewById(R.id.my_record_time);

//            mRootView.findViewById(R.id.btn_setting).setOnClickListener(this);
            mRootView.findViewById(R.id.store_video_layout).setOnClickListener(this);
            mRootView.findViewById(R.id.game_layout).setOnClickListener(this);
            mRootView.findViewById(R.id.message_layout).setOnClickListener(this);
            mRootView.findViewById(R.id.child_zone_layout).setOnClickListener(this);
//            mRootView.findViewById(R.id.level_layout).setOnClickListener(this);

//            initVideoView();

            if (LoginManager.getInstance().isLogin()) {
//                setData(LoginManager.getInstance().getUserCookie().userinfo);
            }

        }
        return mRootView;
    }

    private View videoSplite;
    private RecyclerView videoList;
    private UserVideoAdapter videoAdapter;
    private EndlessRecyclerViewAdapter loadMoreVideoAdapter;
    private int start = 0;

//    private void initVideoView() {
//        videoSplite = mRootView.findViewById(R.id.my_video_split);
//        videoList = (RecyclerView) mRootView.findViewById(R.id.my_video_list);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.HORIZONTAL, false);
//        videoList.setLayoutManager(layoutManager);
//        videoList.setHasFixedSize(true);
//        Drawable divider = getResources().getDrawable(R.drawable.user_video_list_divider);
//        videoList.addItemDecoration(new DividerItemDecoration(getActivity(), divider, DividerItemDecoration.HORIZONTAL_LIST));
//        videoAdapter = new UserVideoAdapter(getActivity());
//        loadMoreVideoAdapter = new EndlessRecyclerViewAdapter(getActivity(), videoAdapter, this, R.layout.loading_view, true);
//        videoList.setAdapter(loadMoreVideoAdapter);
//    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
        start = 0;
        requestVideoData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_setting:
//                startActivityForResult(new Intent(getActivity(), SettingsActivity.class), 111);
//                break;
//            case R.id.name_layout:
//                startActivity(new Intent(getActivity(), UserInfoActivity.class));
//                break;
//            case R.id.fans_layout:
//                Intent intent = new Intent(getActivity(), FansRankActivity.class);
//                intent.putExtra("uid", LoginManager.getInstance().getUserCookie().userinfo.id);
//                startActivity(intent);
//                break;
//            case R.id.level_layout:
//                startActivity(new Intent(getActivity(), MyLevelActivity.class));
//                break;
            case R.id.store_video_layout:
                startActivity(new Intent(getActivity(), StoreActivity.class));
                break;
            case R.id.game_layout:
                startActivity(new Intent(getActivity(), MyLevelActivity.class));
                break;
            case R.id.message_layout:
                startActivity(new Intent(getActivity(), MyLevelActivity.class));
                break;
            case R.id.child_zone_layout:
                startActivity(new Intent(getActivity(), MyLevelActivity.class));
                break;
//            case R.id.img1:
//            case R.id.img2:
//            case R.id.img3:
//                UserInfoBean info = (UserInfoBean) v.getTag();
//                if (info != null) {
//                    // TODO
//                    intent = new Intent(getActivity(), UserPageActivity.class);
//                    intent.putExtra("uinfo", info);
//                    startActivity(intent);
//                }
//                break;
//            case R.id.txt_attends:
//                intent = new Intent(getActivity(), ConcernListActivity.class);
//                intent.putExtra("uid", LoginManager.getInstance().getUserCookie().userinfo.id);
//                startActivity(intent);
//                break;
//            case R.id.txt_fans:
//                intent = new Intent(getActivity(), FansListActivity.class);
//                intent.putExtra("uid", LoginManager.getInstance().getUserCookie().userinfo.id);
//                startActivity(intent);
//                break;
//            case R.id.getcash_layout:
//                startActivity(new Intent(getActivity(), NewGetCashActivity.class));
//                break;
//            case R.id.buy_layout:
//                startActivity(new Intent(getActivity(), BuyActivity.class));
//                break;
        }
    }

    private void refresh() {
        UserApi.getUserpage(getActivity(), null, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<UserPageInfo>>() {
                }.getType();
                BaseResponse<UserPageInfo> res = new Gson().fromJson(data, objectType);

//                setData(res.data);

                LoginManager.getInstance().setUserInfo(res.data.userinfo);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestVideoData() {
        VideoApi.getVideoListById(getActivity(), null, start, 10, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ListData<VideoInfo>>>() {
                }.getType();
                BaseResponse<ListData<VideoInfo>> ret = new Gson().fromJson(data, objectType);
                if (ret.data.total <= 0) return;

                if (start == 0) {
                    videoAdapter.setData(ret.data.list, LoginManager.getInstance().getUserCookie().userinfo);
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

    private View recordSplit;
    private View recordNewest;
    private ImageView recordImg;
    private TextView recordName;
    private TextView recordCount;
    private TextView recordDate;

//    private void setData(final UserPageInfo pageInfo) {
//        setData(pageInfo.userinfo);
//        mAttends.setText(Html.fromHtml(getResources().getString(R.string.attend, String.valueOf(pageInfo.conceninfo.total))));
//        mFans.setText(Html.fromHtml(getResources().getString(R.string.fans, String.valueOf(pageInfo.followinfo.total))));
//
//        if (pageInfo.followinfo.list != null && pageInfo.followinfo.list.size() > 0) {
//            int count = Math.min(pageInfo.followinfo.list.size(), 3);
//            for (int i = 0; i < count; i++) {
//                UserInfoBean info = pageInfo.followinfo.list.get(i);
//                FinalBitmap.getInstance().display(mFansImg[i], info.image);
//                mFansImg[i].setTag(info);
//            }
//        }
//
//        if (pageInfo.recordinfo != null && pageInfo.recordinfo.getAnchorinfo() != null) {
//            final RecordInfo recordInfo = pageInfo.recordinfo;
//            FinalBitmap.getInstance().display(recordImg, recordInfo.getAnchorinfo().image);
//            recordName.setText(recordInfo.getAnchorinfo().nickname);
//            recordCount.setText(String.valueOf(recordInfo.getPlaynum()));
//            recordDate.setText(recordInfo.getPubdate_desc());
//
//            recordNewest.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), PlaybackActivity.class);
//                    intent.putExtra(PlaybackActivity.EXTRA_ANCHOR_DATA, pageInfo.userinfo);
//                    intent.putExtra(PlaybackActivity.EXTRA_PLAY_URL, recordInfo.getPlayaddr());
//                    intent.putExtra(PlaybackActivity.EXTRA_VIDEO_ID, recordInfo.getId());
//                    intent.putExtra(PlaybackActivity.EXTRA_TYPE, HotItemInfo.record);
//                    startActivity(intent);
//                }
//            });
//            recordSplit.setVisibility(View.VISIBLE);
//            recordNewest.setVisibility(View.VISIBLE);
//        } else {
//            recordSplit.setVisibility(View.GONE);
//            recordNewest.setVisibility(View.GONE);
//        }
//    }

//    public void setData(UserInfoBean info) {
//        FinalBitmap.getInstance().display(mHeadImg, info.image, null, R.drawable.icon_default_new_header, mHeadImg.getWidth(), mHeadImg.getHeight());
//        if (!TextUtils.isEmpty(info.h_cover)) {
//            FinalBitmap.getInstance().display(mCoverImg, info.h_cover);
//        }
//        mName.setText(LevelManager.getInstance().getNameSexLevelSS(info));
//        mStudio.setText(info.studio);
//        mSign.setText("个性签名：" + info.spec_sign);
//        mBean.setText(info.bean + "豆");
//        mDiamond.setText(info.diamond + "钻");
//
//        mLevel.setText(info.user_level + "");
//        if (info.is_certified == 1) {
//            mCertLevelImg.setVisibility(View.VISIBLE);
//            mCertLevelImg.setImageResource(LevelManager.getInstance().getCertLevelResId(info.certify_level));
//            mCertLevelTxt.setVisibility(View.VISIBLE);
//            mCertLevelTxt.setText(info.certify_level_desc);
//        } else {
//            mCertLevelImg.setVisibility(View.GONE);
//            mCertLevelTxt.setVisibility(View.GONE);
//        }
//
//        mBeans.setText(Html.fromHtml(getResources().getString(R.string.beans, info.bean)));
//        mAttends.setText(Html.fromHtml(getResources().getString(R.string.attend, "0")));
//        mFans.setText(Html.fromHtml(getResources().getString(R.string.fans, "0")));
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
            LoginManager.getInstance().onLogout();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HttpUtil.getInstance().cancelRequest(getActivity());
    }

    @Override
    public void onLoadMoreRequested() {
        requestVideoData();
    }
}
