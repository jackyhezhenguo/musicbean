package com.musicbean.ui.uploadvideos;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.musicbean.R;

import java.util.ArrayList;
import java.util.List;

public class VideoCollectionActivity extends Activity implements View.OnClickListener{
    private ListView lvVideos;
    private LinearLayout llList;
    private List videos;
    private VideosAdapter videosAdapter;
    private TextView tvSelect1;
    private TextView tvSelect2;
    private TextView tvVideos;
    private TextView tvExamining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_collection);

        initViews();
        initListView();
    }

    private void initListView() {
        lvVideos = (ListView) findViewById(R.id.lv_videos);
        videos = new ArrayList<>();

        videos.add(new UploadVideoBean("dd", BitmapFactory.decodeResource(getResources(),R.drawable.abc_ic_search_api_mtrl_alpha)));
        videosAdapter = new VideosAdapter(this,videos,R.layout.video_item);
        lvVideos.setAdapter(videosAdapter);
    }

    private void initViews() {
        tvSelect1 = (TextView) findViewById(R.id.tv_select1);
        tvSelect2 = (TextView) findViewById(R.id.tv_select2);
        tvVideos = (TextView) findViewById(R.id.tv_videos);
        tvExamining = (TextView) findViewById(R.id.tv_examining);
        llList = (LinearLayout) findViewById(R.id.ll_list);

        tvVideos.setOnClickListener(this);
        tvExamining.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_videos :
                tvSelect1.setVisibility(View.VISIBLE);
                tvSelect2.setVisibility(View.INVISIBLE);
                lvVideos.setVisibility(View.VISIBLE);
                llList.setVisibility(View.GONE);

                break;
            case R.id.tv_examining :
                tvSelect2.setVisibility(View.VISIBLE);
                tvSelect1.setVisibility(View.INVISIBLE);
                llList.setVisibility(View.VISIBLE);
                lvVideos.setVisibility(View.GONE);
                break;
        }
    }
}
