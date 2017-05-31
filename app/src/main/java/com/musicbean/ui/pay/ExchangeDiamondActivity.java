package com.musicbean.ui.pay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.ExchangeInfo;
import com.musicbean.http.bean.ExchangeResult;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BackActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by boyo on 16/7/26.
 */
public class ExchangeDiamondActivity extends BackActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, BackActivity.TitleBarRightButtonCallback {

    private ExchangeDiamondAdapter mAdapter;

    private TextView mTxtBean;
    private TextView mTxtDiamond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("兑换钻石");
        setRightButtonTitle("兑换记录");
        setCallback(this);

        setContentView(R.layout.activity_exchange_diamond);

        ListView listView = (ListView) findViewById(R.id.exchange_list);
        mAdapter = new ExchangeDiamondAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        findViewById(R.id.btn_ok).setOnClickListener(this);

        ArrayList<ExchangeInfo> datas = new ArrayList<>();
        datas.add(new ExchangeInfo(6, 1));
        datas.add(new ExchangeInfo(60, 10));
        datas.add(new ExchangeInfo(600, 100));
        mAdapter.addDatas(datas);

        mTxtBean = (TextView) findViewById(R.id.beans);
        mTxtBean.setText(Html.fromHtml(getString(R.string.exchange_beans, LoginManager.getInstance().getUserInfo().bean)));

        mTxtDiamond = (TextView) findViewById(R.id.diamonds);
        mTxtDiamond.setText(Html.fromHtml(getString(R.string.exchange_diamonds, LoginManager.getInstance().getUserInfo().diamond)));
    }

    @Override
    public void onRightButtonClick(View view) {
        startActivity(new Intent(this, ExchangeRecordListActivity.class));
    }

    @Override
    public void onRightButtonStatusChange(CompoundButton button, boolean status) {

    }

    private ExchangeInfo mExchangeInfo;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mExchangeInfo = mAdapter.getItem(position);

        if (LoginManager.getInstance().getUserInfo().bean < mExchangeInfo.beans) {
            Toast.makeText(ExchangeDiamondActivity.this, "您的豆不足", Toast.LENGTH_SHORT).show();
            return;
        }
        showConfirm();
    }

    private ConfirmDialog mConfirmDialog;
    private CustomExchangeDialog mExchangeDialog;

    @Override
    public void onClick(View v) {
        showCustomExchange();
    }

    private void showCustomExchange() {
        if (mExchangeDialog == null) {
            mExchangeDialog = new CustomExchangeDialog(this);
            mExchangeDialog.setOnExchangeDialogListener(new CustomExchangeDialog.OnExchangeDialogListener() {
                @Override
                public void onExchangeSelected(ExchangeInfo info) {
                    mExchangeInfo = info;
                    showConfirm();
                }
            });
        }

        mExchangeDialog.show("音悦豆余额：" + LoginManager.getInstance().getUserInfo().bean);
    }

    private void showConfirm() {
        if (mConfirmDialog == null) {
            mConfirmDialog = new ConfirmDialog(this);
            mConfirmDialog.setOnDialogListener(new ConfirmDialog.OnDialogListener() {
                @Override
                public void onEnsure() {
                    doExchange();
                }
            });
        }

        String msg = "您将使用" + mExchangeInfo.beans + "豆兑换" + mExchangeInfo.diamonds + "钻";
        mConfirmDialog.show(msg);
    }

    private void doExchange() {
        showDialog();
        PayApi.exchangeDiamond(this, mExchangeInfo.beans, mExchangeInfo.diamonds, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(ExchangeDiamondActivity.this, "兑换成功", Toast.LENGTH_SHORT).show();

                Type objectType = new TypeToken<BaseResponse<ExchangeResult>>() {
                }.getType();
                BaseResponse<ExchangeResult> res = new Gson().fromJson(data, objectType);

                LoginManager.getInstance().getUserInfo().bean = res.data.bean;
                LoginManager.getInstance().getUserInfo().diamond = res.data.diamond;
                LoginManager.getInstance().saveUserInfo();

                mTxtBean.setText(Html.fromHtml(getString(R.string.exchange_beans, LoginManager.getInstance().getUserInfo().bean)));
                mTxtDiamond.setText(Html.fromHtml(getString(R.string.exchange_diamonds, LoginManager.getInstance().getUserInfo().diamond)));
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                Toast.makeText(ExchangeDiamondActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                hideDialog();
            }
        });
    }
}
