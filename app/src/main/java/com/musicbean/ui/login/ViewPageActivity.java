package com.musicbean.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.BoardInfo;
import com.musicbean.http.bean.GuidanceInfo;
import com.musicbean.preference.MySettings;
import com.musicbean.preference.PreferencesManager;
import com.musicbean.ui.SplashActivity;
import com.musicbean.ui.main.MainActivity;
import com.musicbean.util.VolleyLoadPicture;
import com.musicbean.widget.BannerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewPageActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String FIRST_VIEW_PAGE = "FIRST_VIEW_PAGE";
    private static final String FIRST_VISIT = "FIRST_VISIT";

    private ViewPager vp = null;
    private MyPagerAdapter mpa = null;
    private View[] views = new View[3];
    private int[] viewId = {R.layout.layout1,R.layout.layout2,R.layout.layout4};
    //数据源
    private List<View> viewList = null;
    private TextView tvOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);

        initViews();
        mpa = new MyPagerAdapter(viewList);
        vp.setAdapter(mpa);

        SharedPreferences settings = getSharedPreferences(FIRST_VIEW_PAGE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(FIRST_VISIT,1);
        editor.commit();
    }

    private void initViews() {
        vp = (ViewPager) findViewById(R.id.vp);
        viewList = new ArrayList<>();

        HttpUtil.getInstance().get(this, "http://musicbean-app-server-dev.obaymax.com/start/appGuide", null, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Log.d("11",data);
                Type objectType = new TypeToken<BaseResponse<ArrayList<GuidanceInfo>>>() {
                }.getType();
                BaseResponse<ArrayList<GuidanceInfo>> ret = new Gson().fromJson(data, objectType);
                Log.d("11",ret.data.get(1).img);

                for (int i = 0;i<viewId.length;i++){

                    views[i] = View.inflate(ViewPageActivity.this,viewId[i],null);
                    viewList.add(views[i]);

                        ImageView img = (ImageView) views[i].findViewById(R.id.img);//几个layout命名都一样
                        VolleyLoadPicture vlp = new VolleyLoadPicture(ViewPageActivity.this, img);
                        vlp.getmImageLoader().get(ret.data.get(i).img, vlp.getOne_listener());
                        mpa.notifyDataSetChanged();

                    if (i == 2) {
                        tvOpen = (TextView) views[i].findViewById(R.id.tv_open);
                        tvOpen.setOnClickListener(ViewPageActivity.this);
                    }

                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_open:
                startActivity(new Intent(ViewPageActivity.this, SplashActivity.class));
                break;
        }
    }
}
