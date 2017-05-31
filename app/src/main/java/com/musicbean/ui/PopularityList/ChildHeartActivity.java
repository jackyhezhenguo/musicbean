package com.musicbean.ui.PopularityList;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.musicbean.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildHeartActivity extends Activity implements View.OnClickListener{

    private GridView gview;
    private ListView lvMatchOfPlace;
    private ListView lvContribution;
    private List<Map<String, Object>> data_list;
    private List places;
    private List contributions;
    private SimpleAdapter sim_adapter;
    private ChildContributionAdapter ccAdapter;

    private ChildPlaceAdapter cpAdapter;

    private TextView tvGive;
    private TextView tvMyvideo;
    private TextView tvList;
    private TextView tvMyhonor;
    private TextView tvs1;
    private TextView tvs2;
    private TextView tvs3;
    private TextView tvCancel;
    private TextView tvComfirm;
    private FrameLayout flPay;

    // 图片封装为一个数组
    private int[] icon = { R.drawable.abc_btn_borderless_material, R.drawable.yinfu2,
            R.drawable.yinfu5, R.drawable.yinfu4 , R.drawable.yinfu4 , R.drawable.yinfu4 , R.drawable.yinfu4 , R.drawable.yinfu4 , R.drawable.yinfu4 };
    private String[] iconName = { "通讯录", "日历", "照相机", "时钟", "时钟", "时钟", "时钟", "时钟", "时钟"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_heart);


        init();
    }

    private void init() {
        initGridView();
        initListCView();
        initListMView();
        tvList = (TextView) findViewById(R.id.tv_list);
        tvMyhonor = (TextView) findViewById(R.id.tv_myhonor);
        tvMyvideo = (TextView) findViewById(R.id.tv_myvideo);
        tvs1 = (TextView) findViewById(R.id.tv_s1);
        tvs2 = (TextView) findViewById(R.id.tv_s2);
        tvs3 = (TextView) findViewById(R.id.tv_s3);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvComfirm = (TextView) findViewById(R.id.tv_comfirm);
        tvGive = (TextView) findViewById(R.id.tv_give);
        flPay = (FrameLayout) findViewById(R.id.fl_pay);



        tvList.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvComfirm.setOnClickListener(this);
        tvMyhonor.setOnClickListener(this);
        tvMyvideo.setOnClickListener(this);
        tvGive.setOnClickListener(this);
    }
    private void initListCView() {
        lvContribution = (ListView) findViewById(R.id.lv_contribution);
        contributions = new ArrayList(){};
        contributions.add(new ContributionBean("1", BitmapFactory.decodeResource(getResources(),R.drawable.abc_btn_radio_material),"hahha","hajhjhja"));
        ccAdapter = new ChildContributionAdapter(this,contributions,R.layout.item_contribution);
        lvContribution.setAdapter(ccAdapter);
    }

    private void initListMView() {
        lvMatchOfPlace = (ListView) findViewById(R.id.lv_match_of_place);
        places = new ArrayList(){};
        //xiugai
        places.add(new CompetitionBean( BitmapFactory.decodeResource(getResources(),R.drawable.abc_btn_radio_material),"jjjh","hahha","hajhjhja","ghjghjg","hhh"));
        cpAdapter = new ChildPlaceAdapter(this,places,R.layout.match_place_item);
        lvMatchOfPlace.setAdapter(cpAdapter);
    }

    private void initGridView() {
        gview = (GridView) findViewById(R.id.gv_video);
        gview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                Log.d("11",i+"ggggg");
                Log.d("11",i2+"ggggg2");
                if (i == i2 - 3) {
                    Log.d("11","ggggg");
                    Toast.makeText(ChildHeartActivity.this,"全部浏览完毕~",Toast.LENGTH_LONG).show();
                }
            }
        });
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String [] from ={"im","tv"};
        int [] to = {R.id.im,R.id.tv};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item_myvideo, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);
    }
    public List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("im", icon[i]);
            map.put("tv", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_give :
                    flPay.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel :
                    flPay.setVisibility(View.GONE);
                break;
            case R.id.tv_comfirm :
                    flPay.setVisibility(View.GONE);
                break;
            case R.id.tv_myvideo :
                tvs1.setVisibility(View.VISIBLE);
                tvs2.setVisibility(View.INVISIBLE);
                tvs3.setVisibility(View.INVISIBLE);
                gview.setVisibility(View.VISIBLE);
                lvMatchOfPlace.setVisibility(View.GONE);
                lvContribution.setVisibility(View.GONE);

                break;
            case R.id.tv_list :
                tvs2.setVisibility(View.VISIBLE);
                tvs1.setVisibility(View.INVISIBLE);
                tvs3.setVisibility(View.INVISIBLE);
                lvContribution.setVisibility(View.VISIBLE);
                gview.setVisibility(View.GONE);
                lvMatchOfPlace.setVisibility(View.GONE);
                break;
            case R.id.tv_myhonor :
                tvs3.setVisibility(View.VISIBLE);
                tvs2.setVisibility(View.INVISIBLE);
                tvs1.setVisibility(View.INVISIBLE);
                lvMatchOfPlace.setVisibility(View.VISIBLE);
                gview.setVisibility(View.GONE);
                lvContribution.setVisibility(View.GONE);
                break;
        }
    }
}
