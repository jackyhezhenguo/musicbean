package com.musicbean.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.musicbean.R;


public class ChooseImageDialog extends Dialog {

    private Button btnTakePhoto;
    private Button btnPickPhoto;
    private Button btnCancel;
    private View.OnClickListener mListener;

    public ChooseImageDialog(Context context, int theme) {
        super(context, R.style.Dialog_No_Board);
    }

    public ChooseImageDialog(Context context) {
        super(context, R.style.Dialog_No_Board);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_image);
        initView();
    }

    public void setOnClickListener(View.OnClickListener onclicklistener) {
        mListener = onclicklistener;
    }

    private void initView() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = getContext().getResources().getDisplayMetrics().widthPixels; // 设置宽度
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnPickPhoto = (Button) findViewById(R.id.btn_pickphoto);
        btnTakePhoto = (Button) findViewById(R.id.btn_takephoto);
        btnCancel.setOnClickListener(mListener);
        btnPickPhoto.setOnClickListener(mListener);
        btnTakePhoto.setOnClickListener(mListener);
    }

}
