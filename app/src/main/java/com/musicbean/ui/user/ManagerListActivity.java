package com.musicbean.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.UserInfoBean;

import java.util.ArrayList;

/**
 * Created by boyo on 16/7/8.
 */
public class ManagerListActivity extends BaseUserListActivity {

    private TextView mManagerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("管理员列表");

        View v = LayoutInflater.from(this).inflate(R.layout.manager_list_header, mListView, false);
        ViewGroup root = (ViewGroup) findViewById(R.id.root);
        root.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mManagerTitle = (TextView) v.findViewById(R.id.manager_title);
    }

    @Override
    protected void requestData() {
        UserApi.getManagerList(this, mResponseHandler);
    }

    @Override
    protected UserListAdapter getAdapter() {
        return new ManagerListAdapter();
    }

    @Override
    protected void processData(ArrayList<UserInfoBean> data) {
        int count = data.size();
        if (count == 0) {
            mManagerTitle.setText("您还未设置管理员");
        } else {
            mManagerTitle.setText("当前管理员(" + count + "/5)");
        }
    }
}
