package com.musicbean.ui.pay;

import android.os.Bundle;

import com.musicbean.http.api.PayApi;
import com.musicbean.http.bean.AccountInfo;
import com.musicbean.http.bean.RecordBean;

/**
 * Created by boyo on 16/7/27.
 */
public class BuyRecordListActivity extends RecordListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("充值记录");
        mRecordTitle.setText("累计充值（元）");
    }

    @Override
    protected void setAccountInfo(AccountInfo ainfo) {
        mMoney.setText(ainfo.in_money + "");
    }

    @Override
    protected String getRecordTitle(RecordBean bean) {
        return super.getRecordTitle(bean);
    }

    @Override
    protected void requestRecord() {
        PayApi.getRechargeRecord(this, start, count, mResponseHandler);
    }
}
