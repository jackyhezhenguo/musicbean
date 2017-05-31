package com.musicbean.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.util.ScreenUtils;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.Stack;

/**
 * bannerview
 */
public class BannerView extends RelativeLayout implements OnPageChangeListener,
        Runnable {
    private int PERIOD = 3000;
    private ViewPager mViewPager;
    private TextView mTitleView;
    private LinearLayout mIndicator;
    private BannerAdapter mAdapter = new BannerAdapter();
    private static int INDICATOR_DOT_HEIGHT = 0;
    private ArrayList<BannerInfo> mDataList;

    public static class BannerInfo {
        public String image;
        public String title;
        public String url;
        public int type;
    }

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setPeriod(int period) {
        PERIOD = period;
    }

    protected void init(Context context) {
        INDICATOR_DOT_HEIGHT = context.getResources().getDimensionPixelSize(
                R.dimen.banner_indicator_dot_height);
        LayoutInflater.from(context).inflate(R.layout.view_banner, this);

        mViewPager = (ViewPager) findViewById(R.id.bannerPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setVisibility(INVISIBLE);
        mIndicator = (LinearLayout) findViewById(R.id.indicator);
        mIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化默认的高度
     */
    public void initLayoutParams() {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        //final int screenWidth = dm.widthPixels;
        final int bannerHeight = ScreenUtils.dp2px(getContext(), 150);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, bannerHeight));
    }

    public void setDataList(ArrayList<BannerInfo> dataList) {
        if (dataList != null) {
            //mTitleView.setVisibility(dataList.length > 0 ? VISIBLE : INVISIBLE);
            mDataList = dataList;

            // 调用setAdapter方法，可以将ViewPager中的mItems清空，避免setCurrentItem出现阻塞
            mViewPager.setAdapter(mAdapter);
            // 刷新数据
            mAdapter.notifyDataSetChanged();

            mViewPager.setCurrentItem(0, false);

            initIndicator();

            if (mDataList.size() > 0) {
                onPageSelected(0);
                mViewPager.setBackgroundDrawable(null);
            }

            this.post(new Runnable() {
                @Override
                public void run() {
                    startAutoSlide();
                }
            });
        }
    }

    private boolean mStopSlideOnTouch = false;

    public void setStopSlideOnTouch() {
        mStopSlideOnTouch = true;
    }

    private int mDefaultResId = R.color.custom_green;

    public void setDefaultImage(int resId) {
        mDefaultResId = resId;
        mViewPager.setBackgroundResource(resId);
    }

    private class BannerAdapter extends PagerAdapter implements OnClickListener {
        private Stack<ImageView> mStack = new Stack<ImageView>();

        @Override
        public int getCount() {
            // 没有数据时不能滑动
            if (mDataList == null) {
                return 0;
            }
            return mDataList.size() == 0 ? 0 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = null;
            if (mStack.size() > 0) {
                imageView = mStack.pop();
            }

            if (imageView == null) {
                imageView = new ImageView(getContext());
                imageView.setBackgroundResource(mDefaultResId);
                imageView.setScaleType(ScaleType.CENTER_CROP);
                imageView.setOnClickListener(this);
            }

            int index = position % mDataList.size();
            loadImage(imageView, mDataList.get(index), index);

            container.addView(imageView);

            return imageView;
        }

        /**
         * 异步加载图片
         */
        private void loadImage(final ImageView imageView, final BannerInfo bean, int index) {
            imageView.setTag(index);
            if (imageView == null || bean == null || bean.image == null) {
                return;
            }

            FinalBitmap.getInstance().display(imageView, bean.image);
            imageView.setContentDescription(bean.title);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageView = (ImageView) object;
            // 删除View
            container.removeView(imageView);
            // 回收View
            mStack.push(imageView);
        }

        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();

            if (mListener != null) {
                mListener.onBannerClicked(mDataList.get(index));
            }

        }
    }

    @Override
    public void onPageScrollStateChanged(int status) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        // 滑到某页
        int size = mDataList.size();
        if (size == 0) {
            return;
        } else {
            position %= size;
        }

        // 设置标题
        BannerInfo bean = mDataList.get(position);
        mTitleView.setText(bean.title);

        // 设置指示器
        setIndicator(position);
    }

    /**
     * 初始化指示器
     */
    private void initIndicator() {
        mIndicator.removeAllViews();
        for (int i = 0; i < mDataList.size(); i++) {
            ImageView img = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    INDICATOR_DOT_HEIGHT, INDICATOR_DOT_HEIGHT);
            params.leftMargin = params.rightMargin = INDICATOR_DOT_HEIGHT;
            img.setLayoutParams(params);
            img.setImageResource(R.drawable.ic_dot_uncheck);
            mIndicator.addView(img);
        }
    }

    /**
     * 设置指示器显示当前页
     */
    private void setIndicator(int position) {
        for (int i = 0; i < mDataList.size(); i++) {
            ((ImageView) mIndicator.getChildAt(i))
                    .setImageResource(i == position ? R.drawable.ic_dot_check
                            : R.drawable.ic_dot_uncheck);
        }
    }

    /**
     * 开启自动滑动
     */
    public void startAutoSlide() {
        removeCallbacks(this);
        if (mDataList.size() > 1) {
            postDelayed(this, PERIOD);
        }
    }


    private boolean mIsLoop = true;

    public void setLoop(boolean isLoop) {
        mIsLoop = isLoop;
    }

    private OnLoopCompleteListener mLoopListener;

    public void setOnLoopCompleteListener(OnLoopCompleteListener l) {
        mLoopListener = l;
    }

    public interface OnLoopCompleteListener {
        void onLoopComplete();
    }

    /**
     * 停止自动滑动
     */
    public void stopAutoSlide() {
        removeCallbacks(this);
    }

    @Override
    public void run() {
        // 翻页
        int next = mViewPager.getCurrentItem() + 1;

        if (next < mDataList.size() || mIsLoop) {
            mViewPager.setCurrentItem(next, true);
            postDelayed(this, PERIOD);
        } else if (mLoopListener != null) {
            mLoopListener.onLoopComplete();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 按下时停止自动滑动，手指抬起时开启自动滑动
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoSlide();
                requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!mStopSlideOnTouch) {
                    startAutoSlide();
                }
                requestDisallowInterceptTouchEvent(false);
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    public interface OnBannerClickListener {
        void onBannerClicked(BannerInfo info);
    }

    private OnBannerClickListener mListener;

    public void setOnBannerClickListener(OnBannerClickListener l) {
        mListener = l;
    }
}
