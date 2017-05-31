package com.musicbean.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.UserInfoBean;

/**
 * Created by boyo on 16/7/9.
 */
public class BaseEditActivity extends BackActivity implements View.OnClickListener {

    protected EditText mEditInput;
    protected ImageView mClear;
    protected TextView mTips;
    protected Button mBtnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        mEditInput = (EditText) findViewById(R.id.edt_input);
        mClear = (ImageView) findViewById(R.id.clear_input);
        mTips = (TextView) findViewById(R.id.tip);
        mBtnOk = (Button) findViewById(R.id.btn_ok);

        mBtnOk.setOnClickListener(this);
        mClear.setOnClickListener(this);

        String str = getIntent().getStringExtra("str");
        if (!TextUtils.isEmpty(str)) {
            mEditInput.setText(str);
            mEditInput.setSelection(str.length());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_input:
                mEditInput.setText("");

                break;
            case R.id.btn_ok:
                String data = mEditInput.getText().toString();
                if (TextUtils.isEmpty(data)) {
                    Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                }

                commit(data);

                break;
        }
    }

    protected void commit(String str) {

    }

    protected void refreshData() {

    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void saveUserInfo(UserInfoBean bean) {
        showDialog();
        UserApi.saveInfo(this, bean, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                showToast("保存成功");
                refreshData();
                finish();
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
}
