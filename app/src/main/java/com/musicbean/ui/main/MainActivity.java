package com.musicbean.ui.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.musicbean.R;
import com.musicbean.manager.GiftManager;
import com.musicbean.ui.live.StartLiveActivity;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener {

    private RadioButton mRBMain;
    private RadioButton mRBMine;
    private ImageView ivCancel;
    private FrameLayout flTips;

    private Fragment mMainFragment;
    private Fragment mMineFragment;

    private RadioGroup mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setStatusbar();

        mTabLayout = ((RadioGroup) findViewById(R.id.radiogroup));
        mTabLayout.setOnCheckedChangeListener(this);
        mRBMain = (RadioButton) findViewById(R.id.tab_main);
        mRBMine = (RadioButton) findViewById(R.id.tab_mine);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        flTips = (FrameLayout) findViewById(R.id.fl_tips);

        mMainFragment = new MainFragment();
        mMineFragment = new MineFragment();

        ivCancel.setOnClickListener(this);

        setDefaultFragment();

    }

    private void setStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();//获取window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setDefaultFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.realcontent, mMainFragment);
        transaction.commit();
    }

    private void changeFragment(int checkedId) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (checkedId == R.id.tab_main) {
            transaction.replace(R.id.realcontent, mMainFragment);
            transaction.commit();
        } else if (checkedId == R.id.tab_mine) {
            transaction.replace(R.id.realcontent, mMineFragment);
            transaction.commit();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.tab_live:
                if (mMainFragment.isVisible()) {
                    mRBMain.setChecked(true);
                } else {
                    mRBMine.setChecked(true);
                }
                startActivity(new Intent(this, StartLiveActivity.class));
                break;
            default:
                changeFragment(checkedId);
                break;
        }
    }

    public void hideTabLayout(){
        mTabLayout.setVisibility(View.INVISIBLE);
    }

    public void showTabLayout(){
        mTabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                flTips.setVisibility(View.GONE);
                break;
        }
    }
}
