package com.musicbean.ui.pay;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.PayApi;
import com.musicbean.http.bean.AccountInfo;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.ListData;
import com.musicbean.http.bean.RecordBean;
import com.musicbean.ui.BackActivity;
import com.musicbean.widget.loadmore.LoadMoreContainer;
import com.musicbean.widget.loadmore.LoadMoreHandler;
import com.musicbean.widget.loadmore.LoadMoreListViewContainer;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by boyo on 16/7/8.
 */
public class RecordListActivity extends BackActivity {
    protected ListView mListView;
    protected LoadMoreListViewContainer mLoadMore;

    private RecordListAdapter mAdapter;

    protected int start = 0;
    protected int count = 20;

    protected TextView mMoney;
    protected TextView mRecordTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record_list);

        setTitle("提现记录");

        mMoney = (TextView) findViewById(R.id.money);
        mRecordTitle = (TextView) findViewById(R.id.record_title);

        mLoadMore = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        mLoadMore.useDefaultFooter();
        mLoadMore.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                requestData();
            }
        });

        mListView = (ListView) findViewById(R.id.listview);

        mAdapter = getAdapter();
        mListView.setAdapter(mAdapter);

        requestData();
    }

    protected RecordListAdapter getAdapter() {
        return new RecordListAdapter();
    }

    private void requestData() {
        requestRecord();

        PayApi.getAccountInfo(this, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<AccountInfo>>() {
                }.getType();
                BaseResponse<AccountInfo> res = new Gson().fromJson(data, objectType);

                setAccountInfo(res.data);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    protected void requestRecord() {
        PayApi.getCashRecord(this, start, count, mResponseHandler);
    }

    protected void setAccountInfo(AccountInfo ainfo) {
        mRecordTitle.setText("已提现（元）");
        mMoney.setText(ainfo.out_money + "");
    }

    private ArrayList<RecordListAdapter.RecordAdapterBean> createRecordAdapterBeans(ArrayList<RecordBean> rbList) {
        ArrayList<RecordListAdapter.RecordAdapterBean> datas = new ArrayList<>();
        for (RecordBean bean : rbList) {
            datas.add(createAdpaterBean(bean));
        }
        return datas;
    }

    private RecordListAdapter.RecordAdapterBean createAdpaterBean(RecordBean bean) {
        return new RecordListAdapter.RecordAdapterBean(getRecordTitle(bean), bean.createtime);
    }

    protected String getRecordTitle(RecordBean bean) {
        return "金额:" + bean.money + "元(" + bean.status + ")";
    }

    protected ResponseHandler mResponseHandler = new ResponseHandler() {
        @Override
        public void onSuccess(String data) {
            Type objectType = new TypeToken<BaseResponse<ListData<RecordBean>>>() {
            }.getType();
            BaseResponse<ListData<RecordBean>> res = new Gson().fromJson(data, objectType);


            if (start == 0) {
                mAdapter.setDatas(createRecordAdapterBeans(res.data.list));
            } else {
                mAdapter.addDatas(createRecordAdapterBeans(res.data.list));
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

}
