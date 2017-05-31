package com.musicbean.ui.main;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.preference.RecommendPreference;
import com.musicbean.ui.search.SearchActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private RadioGroup mRadioGroup;
    private ViewPager mViewPager;
    private List<View> mViewList;

    private View mRootView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_main, container, false);
            mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.tabs_layout);
            mViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager);

            mViewList = new ArrayList<>();
            mViewList.add(new AttendsWidget(getActivity()));
            mViewList.add(new HotWidget(getActivity()));
            mViewList.add(new NewWidget(getActivity()));
            mViewList.add(new VideoListWidget(getActivity()));

            mViewPager.setAdapter(new SectionsPagerAdapter());
            mViewPager.setOnPageChangeListener(this);
            mViewPager.setCurrentItem(1);
            mRadioGroup.setOnCheckedChangeListener(this);

            mRootView.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SearchActivity.class));
                }
            });
            if (!RecommendPreference.hasRecommend(LoginManager.getInstance().getUserInfo().id)) {
                recommendUser(); // 首次使用展示推荐用户
            }
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AttendsWidget) mViewList.get(0)).refresh();
        ((HotWidget) mViewList.get(1)).refresh();
        ((NewWidget) mViewList.get(2)).refresh();
        ((VideoListWidget) mViewList.get(3)).refresh();
    }

    class SectionsPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mViewList.get(position);
            container.addView(v);

            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);

        if (position != 3) {
            ((VideoListWidget) mViewList.get(3)).removeVideoLayout();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser) {
            ((VideoListWidget) mViewList.get(3)).removeVideoLayout();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i).getId() == checkedId) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    private void recommendUser() {
        UserApi.userRecommend(getActivity(), new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ArrayList<UserInfoBean>>>() {
                }.getType();
                BaseResponse<ArrayList<UserInfoBean>> ret = new Gson().fromJson(data, objectType);
                RecommendDialog dialog = new RecommendDialog(getActivity());
                dialog.show();
                dialog.setData(ret.data);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((AttendsWidget) mViewList.get(0)).refresh();
                    }
                });
                RecommendPreference.update(LoginManager.getInstance().getUserInfo().id, true);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }
}
