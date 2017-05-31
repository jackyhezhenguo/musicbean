package com.musicbean.ui.main;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.NetworkUtil;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.LiveApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.ListData;
import com.musicbean.ui.MyBaseAdapter;
import com.musicbean.ui.PopularityList.ChildHeartActivity;
import com.musicbean.ui.WebViewActivity;
import com.musicbean.ui.live.LivePlayActivity;
import com.musicbean.ui.live.PlaybackActivity;
import com.musicbean.util.ScreenUtils;
import com.musicbean.widget.BannerView;
import com.musicbean.widget.HeaderGridView;
import com.musicbean.widget.loadmore.LoadMoreContainer;
import com.musicbean.widget.loadmore.LoadMoreHandler;
import com.musicbean.widget.loadmore.LoadMoreListViewContainer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotWidget extends FrameLayout implements AdapterView.OnItemClickListener, BannerView.OnBannerClickListener {

    protected BannerView mBannerView;
    protected PtrClassicFrameLayout mPtrFrame;
    protected ListView mListView;
    protected MyBaseAdapter mAdapter;
    private com.musicbean.widget.HeaderGridView gview;
    // 图片封装为一个数组
    private int[] icon = { R.drawable.abc_btn_borderless_material, R.drawable.yinfu2,
            R.drawable.yinfu5, R.drawable.yinfu4 , R.drawable.yinfu4 , R.drawable.yinfu4 , R.drawable.yinfu4 , R.drawable.yinfu4 , R.drawable.yinfu4 };
    private String[] iconName = { "通讯录", "日历", "照相机", "时钟", "时钟", "时钟", "时钟", "时钟", "时钟"};
    private SimpleAdapter sim_adapter;
    private List<Map<String, Object>> data_list;


    protected LoadMoreListViewContainer mLoadMore;

    protected int start = 0;
    protected int COUNT = 10;

    protected View mEmptyHeader;
    protected TextView mEmptyText;

    public HotWidget(Context context) {
        super(context);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_hot, this);

        setBackgroundColor(getResources().getColor(R.color.common_bg));

        mLoadMore = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        mLoadMore.useDefaultFooter();
        mLoadMore.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                requestData();
            }
        });

        mListView = (ListView) findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);

//        initGridView();

        mEmptyHeader = LayoutInflater.from(getContext()).inflate(R.layout.list_empty_header, mListView, false);
        mEmptyText = (TextView) mEmptyHeader.findViewById(R.id.empty_tip);
        mEmptyText.setText("全宇宙都在等你直播哦!");

        initHeader();


        mAdapter = createAdapter();
        mListView.setAdapter(mAdapter);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.disableWhenHorizontalMove(true);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                start = 0;
                requestBanner();
                requestData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canScroll = mListView.getChildCount() > 0
                        && (mListView.getFirstVisiblePosition() > 0 || mListView.getChildAt(0)
                        .getTop() < mListView.getPaddingTop());
                return !canScroll;
            }
//            @Override
//            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//                boolean canScroll = gview.getChildCount() > 0
//                        && (gview.getFirstVisiblePosition() > 0 || gview.getChildAt(0)
//                        .getTop() < gview.getPaddingTop());
//                return !canScroll;
//            }
        });

        requestBanner();
        //requestData();
    }

    protected void initHeader() {
        mBannerView = new BannerView(getContext());
        mBannerView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dp2px(getContext(), 125)));
        mBannerView.setOnBannerClickListener(this);
        mListView.addHeaderView(mBannerView, null, false);
        mListView.setHeaderDividersEnabled(false);
    }

    private void initGridView() {
        gview = (com.musicbean.widget.HeaderGridView)findViewById(R.id.gv_mainvideo);
        gview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                Log.d("11", i + "ggggg");
                Log.d("11", i2 + "ggggg2");
                if (i < 3 && i == i2 - 3) {
                    Log.d("11", "ggggg");
                    Toast.makeText(getContext(), "全部浏览完毕~", Toast.LENGTH_LONG).show();
                }
            }
        });//新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String [] from ={"im","tv","tv_title","tv_count","tv_play_count"};
        int [] to = {R.id.im,R.id.tv_title,R.id.tv_count,R.id.tv_play_count};

        mBannerView = new BannerView(getContext());
        mBannerView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dp2px(getContext(), 125)));
        mBannerView.setOnBannerClickListener(this);
        gview.addHeaderView(mBannerView); // 他需要在setAdapter()之前

        sim_adapter = new SimpleAdapter(getContext(), data_list, R.layout.item_mainvideo, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);

    }
    public List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("im", icon[i]);
            map.put("tv_title", iconName[i]);
            map.put("tv_count", iconName[i]);

            map.put("tv_play_count", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }

    protected MyBaseAdapter createAdapter() {
        return new HotAdapter();
    }

    public void refresh() {
        start = 0;
        requestData();
    }

    protected void requestBanner() {
        if (mBannerView == null) {
            return;
        }
        LiveApi.getHotBanner(getContext(), new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ArrayList<BannerView.BannerInfo>>>() {
                }.getType();
                BaseResponse<ArrayList<BannerView.BannerInfo>> ret = new Gson().fromJson(data, objectType);

                mBannerView.setDataList(ret.data);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    protected void requestData() {
        if (!NetworkUtil.checkNetwork(getContext())) {
            Toast.makeText(getContext(), "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        LiveApi.getHotList(getContext(), start, COUNT, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ListData<HotItemInfo>>>() {
                }.getType();
                BaseResponse<ListData<HotItemInfo>> ret = new Gson().fromJson(data, objectType);

                if (start == 0) {
                    mAdapter.setDatas(ret.data.list);
                } else {
                    mAdapter.addDatas(ret.data.list);
                }

                start++;

                if (mAdapter.getCount() < ret.data.total) {
                    mLoadMore.loadMoreFinish(false, true);
                } else {
                    mLoadMore.loadMoreFinish(false, false);
                }

                if (mAdapter.getCount() == 0) {
                    mListView.removeHeaderView(mEmptyHeader);
                    mListView.addHeaderView(mEmptyHeader);
                } else {
                    mListView.removeHeaderView(mEmptyHeader);
                }

            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }

            @Override
            public void onFinish() {
                mPtrFrame.refreshComplete();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int off = mListView.getHeaderViewsCount();
        int idx = position - off;
        if (idx < 0 || idx > mAdapter.getCount()) {
            return;
        }
        if (!NetworkUtil.checkNetwork(getContext())) {
            Toast.makeText(getContext(), "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        HotItemInfo info = (HotItemInfo) mAdapter.getItem(position - off);

        if (info.isLive()) {
            Intent intent = new Intent(getContext(), LivePlayActivity.class);
            intent.putExtra("data", info);
            getContext().startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), PlaybackActivity.class);
            intent.putExtra(PlaybackActivity.EXTRA_ANCHOR_DATA, info.anchorinfo);
            intent.putExtra(PlaybackActivity.EXTRA_PLAY_URL, info.playaddr.flv);
            intent.putExtra(PlaybackActivity.EXTRA_VIDEO_ID, info.id);
            intent.putExtra(PlaybackActivity.EXTRA_TYPE, info.video_type);
            getContext().startActivity(intent);
        }

    }

    @Override
    public void onBannerClicked(BannerView.BannerInfo info) {
        if (!TextUtils.isEmpty(info.url)) {
            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.putExtra("url", info.url);
            intent.putExtra("title", info.title);
            intent.putExtra("type", info.type);
            getContext().startActivity(intent);
        }
    }
}