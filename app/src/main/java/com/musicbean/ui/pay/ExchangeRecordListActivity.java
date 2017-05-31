package com.musicbean.ui.pay;

import android.os.Bundle;

import com.musicbean.http.api.PayApi;
import com.musicbean.http.bean.AccountInfo;
import com.musicbean.http.bean.RecordBean;

/**
 * Created by boyo on 16/7/27.
 */
public class ExchangeRecordListActivity extends RecordListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("兑换记录");
        mRecordTitle.setText("累计兑换（钻）");
    }

    @Override
    protected void setAccountInfo(AccountInfo ainfo) {
        mMoney.setText(ainfo.exchange_diamond + "");
    }

    @Override
    protected String getRecordTitle(RecordBean bean) {
        return "数量:" + bean.diamond + "(" + bean.status + ")";
    }

    @Override
    protected void requestRecord() {
        PayApi.getExchangeRecord(this, start, count, mResponseHandler);
    }
}
