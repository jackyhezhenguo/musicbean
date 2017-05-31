package com.musicbean.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.musicbean.R;
import com.musicbean.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private EditText mEdtKey;
    private View mBtnClear;

    private ViewPager mViewPager;
    private MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mEdtKey = (EditText) findViewById(R.id.edt_input);
        mEdtKey.addTextChangedListener(this);
        mBtnClear = findViewById(R.id.clear_input);
        mBtnClear.setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mEdtKey.setHint("请输入用户昵称");
                } else {
                    mEdtKey.setHint("请输入工作室");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.search_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_input:
                mEdtKey.setText("");
                break;
            case R.id.btn_search:
                String key = mEdtKey.getText().toString();
                if (TextUtils.isEmpty(key)) {
                    Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    search(key);
                }

                break;
        }
    }

    private void search(String key) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtKey.getWindowToken(), 0);

        mPagerAdapter.getItem(mViewPager.getCurrentItem()).search(key);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString())) {
            mBtnClear.setVisibility(View.VISIBLE);
        } else {
            mBtnClear.setVisibility(View.GONE);
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        List<SearchFragment> mList;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            mList = new ArrayList<>();
            mList.add(SearchFragment.newInstance(0));
            mList.add(SearchFragment.newInstance(1));
        }

        @Override
        public SearchFragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "用户";
                case 1:
                    return "工作室";
            }
            return "";
        }
    }

}
