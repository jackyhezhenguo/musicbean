package com.musicbean.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.ListData;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.ui.BaseFragment;
import com.musicbean.ui.user.UserListAdapter;
import com.musicbean.ui.user.UserPageActivity;
import com.musicbean.widget.loadmore.LoadMoreContainer;
import com.musicbean.widget.loadmore.LoadMoreHandler;
import com.musicbean.widget.loadmore.LoadMoreListViewContainer;

import java.lang.reflect.Type;

public class SearchFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private int mType;

    private ListView mListView;
    private UserListAdapter mAdapter;

    private LoadMoreListViewContainer mLoadMore;
    private int start = 0;
    private int COUNT = 30;

    public static SearchFragment newInstance(int type) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.loadmore_listview_layout, container, false);

        mLoadMore = (LoadMoreListViewContainer) v.findViewById(R.id.load_more_container);
        mLoadMore.useDefaultFooter();
        mLoadMore.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                doSearch();
            }
        });

        mListView = (ListView) v.findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);

        mAdapter = new UserListAdapter();//TODO
        mListView.setAdapter(mAdapter);
        return v;
    }

    private String mKey;

    public void search(String key) {
        mKey = key;
        start = 0;
        showDialog();
        mAdapter.reset();
        doSearch();
    }

    private void doSearch() {
        UserApi.search(getActivity(), mType, mKey, start, COUNT, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ListData<UserInfoBean>>>() {
                }.getType();
                BaseResponse<ListData<UserInfoBean>> res = new Gson().fromJson(data, objectType);

                if (start == 0) {
                    if (res.data.total == 0) {
                        Toast.makeText(getActivity(), "没有搜索到你想要的结果", Toast.LENGTH_SHORT).show();
                        return;
                    }

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
                hideDialog();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserInfoBean info = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), UserPageActivity.class);
        intent.putExtra("uinfo", info);
        startActivity(intent);
    }

}
