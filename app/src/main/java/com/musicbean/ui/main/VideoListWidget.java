package com.musicbean.ui.main;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.VideoApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.ListData;
import com.musicbean.http.bean.VideoListItem;
import com.musicbean.ui.MyBaseAdapter;
import com.musicbean.widget.play.VideoPlayWidget;

import java.lang.reflect.Type;

/**
 * Created by boyo on 16/11/26.
 */
public class VideoListWidget extends HotWidget implements AbsListView.OnScrollListener, VideoListAdapter.OnPlayListener {

    private VideoPlayWidget mVideoWidget;
    private int mCurPosition = -1;
    private EditText mEdtSearch;

    public VideoListWidget(Context context) {
        super(context);

        mEmptyText.setText("没有点播视频～");

        mLoadMore.setOnScrollListener(this);

        mVideoWidget = new VideoPlayWidget(getContext());
    }

    @Override
    protected void initHeader() {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.layout_video_search_header, mListView, false);
        mEdtSearch = (EditText) header.findViewById(R.id.edt_search);
        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = mEdtSearch.getText().toString();

                    search(key);
                }

                return false;
            }
        });
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString()) && mAdapter.getCount() == 0) {
                    search("");
                }
            }
        });
        mListView.addHeaderView(header, null, false);
    }

    private String mSearchKey;

    private void search(String key) {
        start = 0;
        mSearchKey = key;
        requestData();
    }

    @Override
    protected MyBaseAdapter createAdapter() {
        VideoListAdapter adapter = new VideoListAdapter();
        adapter.setOnPlayListener(this);
        return adapter;
    }

    @Override
    protected void requestData() {
        if (TextUtils.isEmpty(mSearchKey)) {
            VideoApi.getVideoList(getContext(), start, COUNT, mResponseHandler);
        } else {
            VideoApi.search(getContext(), mSearchKey, start, COUNT, mResponseHandler);
        }
    }

    private ResponseHandler mResponseHandler = new ResponseHandler() {
        @Override
        public void onSuccess(String data) {
            Type objectType = new TypeToken<BaseResponse<ListData<VideoListItem>>>() {
            }.getType();
            BaseResponse<ListData<VideoListItem>> ret = new Gson().fromJson(data, objectType);

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

            removeVideoLayout();
        }
    };

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mCurPosition != -1) {
            for (int i = 0; i < view.getChildCount(); i++) {
                View cView = view.getChildAt(i);
                if (VideoListAdapter.getItemViewPosition(cView) == mCurPosition) {
                    return;
                }
            }

            if (!mVideoWidget.isFullScreen()) {
                removeVideoLayout();
            }
        }
    }

    public void removeVideoLayout() {
        mVideoWidget.clearContainer();
        mCurPosition = -1;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            mVideoWidget.pause();
        } else {
            mVideoWidget.resume();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeVideoLayout();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void playInsideView(ViewGroup vg, ViewGroup.LayoutParams lp, int position) {
        if (mCurPosition != position) {
            mCurPosition = position;
            VideoListItem info = (VideoListItem) mAdapter.getItem(position);
            mVideoWidget.playInsideView(vg, lp, info);
        }
    }

    @Override
    public void refresh() {
        if (!mVideoWidget.isFullScreen()) {
            super.refresh();
        }
    }
}
