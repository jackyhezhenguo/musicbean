package com.musicbean.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.ListData;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BackActivity;
import com.musicbean.widget.loadmore.LoadMoreContainer;
import com.musicbean.widget.loadmore.LoadMoreHandler;
import com.musicbean.widget.loadmore.LoadMoreListViewContainer;

import java.lang.reflect.Type;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by boyo on 16/7/7.
 */
public class FansRankActivity extends BackActivity implements AdapterView.OnItemClickListener {

    protected PtrClassicFrameLayout mPtrFrame;
    protected ListView mListView;
    protected LoadMoreListViewContainer mLoadMore;

    private FansRankAdapter mAdapter;

    private int start = 0;
    private int count = 20;

    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("粉丝土豪榜");

        mUid = getIntent().getStringExtra("uid");

        if (TextUtils.isEmpty(mUid)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_loadmore_ptr_list);

        initViews();

        requestData();
    }

    private void initViews() {
        mLoadMore = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        mLoadMore.useDefaultFooter();
        mLoadMore.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                requestData();
            }
        });

        mListView = (ListView) findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        //View header = LayoutInflater.from(this).inflate(R.layout.fans_rank_header, mListView, false);
        //TextView tv = (TextView) header.findViewById(R.id.beans_total);
        //tv.setText(LoginManager.getInstance().getUserInfo().bean + "豆");
        //mListView.addHeaderView(header, null, false);
        //mListView.setHeaderDividersEnabled(false);

        mAdapter = new FansRankAdapter();
        mListView.setAdapter(mAdapter);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.ptr_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                start = 0;
                requestData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canScroll = mListView.getChildCount() > 0
                        && (mListView.getFirstVisiblePosition() > 0 || mListView.getChildAt(0)
                        .getTop() < mListView.getPaddingTop());
                return !canScroll;
            }
        });
    }

    private void requestData() {
        UserApi.getFollowList(this, mUid, start, count, 2, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ListData<UserInfoBean>>>() {
                }.getType();
                BaseResponse<ListData<UserInfoBean>> res = new Gson().fromJson(data, objectType);

                if (start == 0) {
                    mAdapter.setDatas(res.data.list);
                } else {
                    mAdapter.addDatas(res.data.list);
                }

                start++;

                if (mAdapter.getCount() < res.data.total) {
                    mLoadMore.loadMoreFinish(false, true);
                } else {
                    mLoadMore.loadMoreFinish(false, false);
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
        UserInfoBean info = mAdapter.getItem(position);
        Intent intent = new Intent(this, UserPageActivity.class);
        intent.putExtra("uinfo", info);
        startActivity(intent);
    }
}
