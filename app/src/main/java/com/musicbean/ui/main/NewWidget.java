package com.musicbean.ui.main;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.LiveApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.HotResult;
import com.musicbean.ui.live.LivePlayActivity;
import com.musicbean.ui.live.PlaybackActivity;
import com.musicbean.ui.user.UserPageActivity;
import com.musicbean.widget.loadmore.GridViewWithHeaderAndFooter;
import com.musicbean.widget.loadmore.LoadMoreContainer;
import com.musicbean.widget.loadmore.LoadMoreGridViewContainer;
import com.musicbean.widget.loadmore.LoadMoreHandler;

import java.lang.reflect.Type;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewWidget extends LinearLayout implements AdapterView.OnItemClickListener {
    private PtrClassicFrameLayout mPtrFrame;
    private GridViewWithHeaderAndFooter mGridView;
    private LoadMoreGridViewContainer mLoadMore;

    private NewAdapter mAdapter;

    private int start = 0;
    private int COUNT = 30;

    private View mEmptyHeader;
    protected TextView mEmptyText;

    public NewWidget(Context context) {
        super(context);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_new, this);
        mLoadMore = (LoadMoreGridViewContainer) findViewById(R.id.load_more_grid_view_container);
        mLoadMore.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                requestData();
            }
        });

        mGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.new_gridview);
        mGridView.setOnItemClickListener(this);

        View header = LayoutInflater.from(getContext()).inflate(R.layout.new_header, mGridView, false);
        mGridView.addHeaderView(header, null, false);

        mEmptyHeader = LayoutInflater.from(getContext()).inflate(R.layout.list_empty_header, mGridView, false);
        mEmptyText = (TextView) mEmptyHeader.findViewById(R.id.empty_tip);
        mEmptyText.setText("去看看热门里的精彩直播吧!");

        mAdapter = new NewAdapter();
        mGridView.setAdapter(mAdapter);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.ptr_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canScroll = mGridView.getChildCount() > 0
                        && (mGridView.getFirstVisiblePosition() > 0 || mGridView.getChildAt(0)
                        .getTop() < mGridView.getPaddingTop());
                return !canScroll;
            }
        });

        //requestData();

    }

    public void refresh() {
        start = 0;
        requestData();
    }

    private void requestData() {

        LiveApi.getNewList(getContext(), start, COUNT, new ResponseHandler() {
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

                if (mAdapter.getCount() == 0 && mEmptyHeader.getParent() == null) {
                    mGridView.addHeaderView(mEmptyHeader);
                } else {
                    mGridView.removeHeaderView(mEmptyHeader);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HotItemInfo info = mAdapter.getItem(position);
        if (HotItemInfo.live.equals(info.video_type)) {
            Intent intent = new Intent(getContext(), LivePlayActivity.class);
            intent.putExtra("data", info);
            getContext().startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), PlaybackActivity.class);
            intent.putExtra(PlaybackActivity.EXTRA_ANCHOR_DATA, info.anchorinfo);
            intent.putExtra(PlaybackActivity.EXTRA_PLAY_URL, info.playaddr.flv);
            intent.putExtra(PlaybackActivity.EXTRA_VIDEO_ID, info.id);
            intent.putExtra(PlaybackActivity.EXTRA_TYPE, info.video_type);
            getContext().startActivity(intent);
        }
    }
}
