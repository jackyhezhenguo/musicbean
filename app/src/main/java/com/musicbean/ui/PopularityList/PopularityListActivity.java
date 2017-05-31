package com.musicbean.ui.PopularityList;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.musicbean.R;

import java.util.ArrayList;
import java.util.List;

public class PopularityListActivity extends Activity implements View.OnClickListener{
    private TextView tvSpeedFast;
    private TextView tvPopularityHighest;
    private TextView tvS1;
    private TextView tvS2;
    private ListView lvList;
    private List childStars;
    private ChildStarAdapter csa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popularity_list);

        initListView();
        init();
    }

    private void initListView() {
        lvList = (ListView) findViewById(R.id.lv_list);
        childStars = new ArrayList(){};
        childStars.add(new ChildStarBean(null, BitmapFactory.decodeResource(getResources(),R.drawable.abc_btn_radio_material),"hahha","hajhjhja","jfhfjsdfhj"));
        csa = new ChildStarAdapter(this,childStars,R.layout.child_star_item);
        lvList.setAdapter(csa);
    }

    private void init(){
        tvSpeedFast = (TextView) findViewById(R.id.tv_speed_fastest);
        tvPopularityHighest = (TextView) findViewById(R.id.tv_popularity_highest);
        tvS1 = (TextView) findViewById(R.id.tv_select1);
        tvS2 = (TextView) findViewById(R.id.tv_select2);

        tvSpeedFast.setOnClickListener(this);
        tvPopularityHighest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_speed_fastest:
                tvS1.setVisibility(View.VISIBLE);
                tvS2.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_popularity_highest:
                tvS2.setVisibility(View.VISIBLE);
                tvS1.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
