package com.musicbean.ui.user;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.musicbean.R;

import java.util.ArrayList;
import java.util.List;

public class ChilsStarActivity extends Activity {

    private RecyclerView rv ;
    private List<String> mDatas ;
    private ChildZoneAdapter cza ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chils_star);
//        initData();
        init();
    }

//    private void initData() {
//        mDatas = new ArrayList<String>();
//        for (int i = 'A';i<'z';i++){
//            mDatas.add(""+(char)i);
//        }
//    }

    private void init(){
//        rv = (RecyclerView) findViewById(R.id.rv);
//        //设置布局管理器
////        rv.setLayoutManager(new LinearLayoutManager(this));
//        rv.setLayoutManager(new GridLayoutManager(this,4));
//        //设置adapter
//        rv.setAdapter(cza=new ChildZoneAdapter(mDatas,ChilsStarActivity.this));


    }
}
