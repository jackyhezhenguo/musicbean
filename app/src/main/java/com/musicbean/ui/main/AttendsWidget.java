package com.musicbean.ui.main;


import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.LiveApi;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.HotResult;
import com.musicbean.http.bean.UserInfoBean;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendsWidget extends HotWidget {
    private RecommendWidget recommendWidget;

    public AttendsWidget(Context context) {
        super(context);
        mListView.removeHeaderView(mBannerView);

        mEmptyText.setText("去看看热门里的精彩直播吧!");
    }

    @Override
    protected void requestBanner() {

    }

    @Override
    protected void requestData() {
        if (recommendWidget != null) {
            removeView(recommendWidget);
            recommendWidget = null;
        }
        LiveApi.getMyList(getContext(), start, COUNT, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<HotResult>>() {
                }.getType();
                BaseResponse<HotResult> ret = new Gson().fromJson(data, objectType);

                if (start == 0) {
                    mAdapter.setDatas(ret.data.list);
                } else {
                    mAdapter.addDatas(ret.data.list);
                }

                start++;

                if (mAdapter.getCount() < ret.data.total) {
                    mLoadMore.loadMoreFinish(false, true);
                } else {
                    mLoadMore.loadMoreFinish(false, false);
                }


                if (mAdapter.getCount() == 0) {
                    mListView.removeHeaderView(mEmptyHeader);
                    mListView.addHeaderView(mEmptyHeader);
                    recommendUser();
                } else {
                    mListView.removeHeaderView(mEmptyHeader);
                }

            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }

            @Override
            public void onFinish() {
                mPtrFrame.refreshComplete();
            }
        });
    }

    private void recommendUser() {
        UserApi.userRecommend(getContext(), new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ArrayList<UserInfoBean>>>() {
                }.getType();
                BaseResponse<ArrayList<UserInfoBean>> ret = new Gson().fromJson(data, objectType);
                recommendWidget = new RecommendWidget(getContext());
                recommendWidget.setData(ret.data);
                addView(recommendWidget, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }
}
