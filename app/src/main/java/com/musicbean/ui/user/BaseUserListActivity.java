package com.musicbean.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.ListData;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.ui.BackActivity;
import com.musicbean.widget.loadmore.LoadMoreContainer;
import com.musicbean.widget.loadmore.LoadMoreHandler;
import com.musicbean.widget.loadmore.LoadMoreListViewContainer;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by boyo on 16/7/8.
 */
public class BaseUserListActivity extends BackActivity implements AdapterView.OnItemClickListener {
    protected ListView mListView;
    protected LoadMoreListViewContainer mLoadMore;

    private UserListAdapter mAdapter;

    protected int start = 0;
    protected int count = 20;

    protected String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loadmore_list);

        mUid = getIntent().getStringExtra("uid");
        if (TextUtils.isEmpty(mUid)) {
            finish();
            return;
        }

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

        mAdapter = getAdapter();
        mListView.setAdapter(mAdapter);

        requestData();
    }

    protected UserListAdapter getAdapter() {
        return new UserListAdapter();
    }

    protected void requestData() {

    }

    protected void processData(ArrayList<UserInfoBean> data) {

    }

    protected ResponseHandler mResponseHandler = new ResponseHandler() {
        @Override
        public void onSuccess(String data) {
            Type objectType = new TypeToken<BaseResponse<ListData<UserInfoBean>>>() {
            }.getType();
            BaseResponse<ListData<UserInfoBean>> res = new Gson().fromJson(data, objectType);

            processData(res.data.list);

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
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserInfoBean info = mAdapter.getItem(position);
        Intent intent = new Intent(this, UserPageActivity.class);
        intent.putExtra("uinfo", info);
        startActivity(intent);
    }
}
