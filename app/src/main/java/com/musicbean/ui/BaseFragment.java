package com.musicbean.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.musicbean.http.HttpUtil;

/**
 * Created by boyo on 16/7/12.
 */
public class BaseFragment extends Fragment {

    private ProgressDialog mDialog;

    protected void showDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("加载中...");
            mDialog.setIndeterminate(true);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    HttpUtil.getInstance().cancelRequest(getActivity());
                }
            });
        }
        mDialog.show();
    }

    protected void hideDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
