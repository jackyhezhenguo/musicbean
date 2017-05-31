package com.musicbean.ui.pay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.PayApi;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.RechargeStatus;
import com.musicbean.http.bean.UserPageInfo;
import com.musicbean.manager.LoginManager;
import com.musicbean.pay.AliPayAPI;
import com.musicbean.pay.PayApiResult;
import com.musicbean.pay.PayCallbackListener;
import com.musicbean.ui.BackActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuyActivity extends BackActivity implements BackActivity.TitleBarRightButtonCallback,
        AdapterView.OnItemClickListener, View.OnClickListener, PayCallbackListener {

    private BuyAdapter mAdapter;
    private ListView mListView;

    private int mOrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        setTitle("充值");

        setRightButtonTitle("充值记录");
        setCallback(this);

        TextView tv = (TextView) findViewById(R.id.beans);
        tv.setText("音悦钻余额：" + LoginManager.getInstance().getUserInfo().diamond);

        findViewById(R.id.btn_ok).setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new BuyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        ArrayList<BuyInfo> datas = new ArrayList<>();
        datas.add(new BuyInfo(60, 6, 0));
        datas.add(new BuyInfo(300, 30, 0));
        datas.add(new BuyInfo(980, 98, 20));
        datas.add(new BuyInfo(2880, 288, 120));
        datas.add(new BuyInfo(5880, 588, 220));
        datas.add(new BuyInfo(9980, 998, 320));

        mAdapter.setDatas(datas);
    }

    @Override
    public void onRightButtonClick(View view) {
        startActivity(new Intent(this, BuyRecordListActivity.class));
    }

    @Override
    public void onRightButtonStatusChange(CompoundButton button, boolean status) {

    }

    private BuyInfo mSelectedBuyInfo;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (mSelectedBuyInfo != null) {
//            mSelectedBuyInfo.isSelected = false;
//        }
//        mSelectedBuyInfo = mAdapter.getItem(position);
//        mSelectedBuyInfo.isSelected = true;
//        mAdapter.notifyDataSetChanged();

        doBuy(mAdapter.getItem(position));

    }

    @Override
    public void onClick(View v) {

    }

    private void doBuy(BuyInfo binfo) {

        PayApi.recharge(this, binfo.money, binfo.diamond + binfo.free, "alipay", new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                //Type objectType = new TypeToken<BaseResponse<PayInfo>>() {
                //}.getType();
                //BaseResponse<PayInfo> ret = new Gson().fromJson(data, objectType);

                Pattern p = Pattern.compile("out_trade_no=\"(.*?)\"");
                Matcher m = p.matcher(data);
                if (m.find()) {
                    String no = m.group(1);
                    Log.e("wcb", no);

                    try {
                        mOrderNo = Integer.parseInt(no);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                AliPayAPI api = new AliPayAPI();
                api.setCallBackListener(BuyActivity.this);
                api.doPay(data, BuyActivity.this);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    @Override
    public void callback(PayApiResult ret) {
        if (ret.resultCode == PayApiResult.CODE_SUCCESS) {
//            Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
//            finish();
            showDialog();
            mListView.postDelayed(mCheckRunnable, 3000);
        }
    }

    private Runnable mCheckRunnable = new Runnable() {
        @Override
        public void run() {
            PayApi.getRechargeStatus(BuyActivity.this, mOrderNo, new ResponseHandler() {
                @Override
                public void onSuccess(String data) {
                    Type objectType = new TypeToken<BaseResponse<RechargeStatus>>() {
                    }.getType();
                    BaseResponse<RechargeStatus> ret = new Gson().fromJson(data, objectType);

                    if (ret.data.status == RechargeStatus.ST_ING) {
                        mListView.postDelayed(mCheckRunnable, 1000);
                    } else if (ret.data.status == RechargeStatus.ST_FAIL) {
                        hideDialog();
                        Toast.makeText(BuyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    } else {
                        refresh();
                    }
                }

                @Override
                public void onFailure(int responseCode, String errorMsg) {

                }
            });
        }
    };

    private void refresh() {
        UserApi.getMypage(this, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<UserPageInfo>>() {
                }.getType();
                BaseResponse<UserPageInfo> res = new Gson().fromJson(data, objectType);

                LoginManager.getInstance().setUserInfo(res.data.userinfo);

                Toast.makeText(BuyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }
}
