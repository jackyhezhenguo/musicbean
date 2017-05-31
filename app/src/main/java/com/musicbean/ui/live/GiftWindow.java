package com.musicbean.ui.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.App;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.ChatApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.DiamondLeftInfo;
import com.musicbean.http.bean.GiftBean;
import com.musicbean.http.bean.MessageInfo;
import com.musicbean.manager.GiftManager;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.pay.BuyActivity;
import com.musicbean.ui.pay.ConfirmDialog;
import com.musicbean.util.ScreenUtils;
import com.musicbean.widget.WrapContentGridView;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by boyo on 16/7/3.
 */
public class GiftWindow extends BottomPopupWindow implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ViewPager mViewPager;
    LinearLayout mIndicator;

    private View mDoubleHitLayout;

    private static final int NUM_PER_PAGE = 8;
    private static final int NUM_COLUMN = 4;
    private static final int INDICATOR_DOT_HEIGHT = ScreenUtils.dp2px(App.getContext(), 7);

    private ArrayList<View> mPageViews;

    private ArrayList<ArrayList<GiftBean>> mDataList;

    private TextView mTxtDiamond;

    private String mRooomid;
    private String mAnchorid;

    private TextView mTxtCountdown;

    private TextView mBtnSend;

    private MessageInfo mGiftMessage;

    public GiftWindow(Activity context, String roomid, String anchorid) {
        super(context);
        mRooomid = roomid;
        mAnchorid = anchorid;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.gift_layout;
    }

    @Override
    protected void setSize() {
        setWidth(ScreenUtils.getScreenWidth(mActivityRef.get()));
        setHeight(ScreenUtils.dp2px(mActivityRef.get(), 225));
    }

    @Override
    protected void initViews() {
        View v = getContentView();
        mViewPager = (ViewPager) v.findViewById(R.id.gift_viewpaget);
        mIndicator = (LinearLayout) v.findViewById(R.id.indicator);
        mDoubleHitLayout = v.findViewById(R.id.double_hit_layout);
        mTxtDiamond = (TextView) v.findViewById(R.id.diamond);
        mTxtCountdown = (TextView) v.findViewById(R.id.countdown);

        mBtnSend = (TextView) v.findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        v.findViewById(R.id.double_hit).setOnClickListener(this);
        v.findViewById(R.id.double_hit_layout).setOnClickListener(this);
        v.findViewById(R.id.go_buy).setOnClickListener(this);

        initData();
        initViewPagerData();
        initIndicator();
        setupViewPager();

    }

    @Override
    public void show() {
        mTxtDiamond.setText(LoginManager.getInstance().getUserCookie().userinfo.diamond + "");
        if (mSelectedGift != null) {
            mSelectedGift.isSelected = false;
            mSelectedGift = null;
        }
        mBtnSend.setEnabled(false);
        super.show();
    }

    private void initData() {
        mDataList = new ArrayList<>();

        ArrayList<GiftBean> list = new ArrayList<>();
        for (GiftBean bean : GiftManager.getInstance().getGiftList()) {
            list.add(bean);
            if (list.size() == NUM_PER_PAGE) {
                mDataList.add(list);
                list = new ArrayList<>();
            }
        }

        if (list.size() > 0 && list.size() < NUM_PER_PAGE) {
            mDataList.add(list);
        }
    }

    private void initViewPagerData() {
        mPageViews = new ArrayList<>();

        for (int i = 0; i < mDataList.size(); i++) {
            GridView view = new WrapContentGridView(mActivityRef.get());
            GiftAdapter adapter = new GiftAdapter();
            adapter.setDatas(mDataList.get(i));
            view.setAdapter(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(NUM_COLUMN);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setVerticalSpacing(ScreenUtils.dp2px(mActivityRef.get(), 6));
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setGravity(Gravity.CENTER);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            mPageViews.add(view);
        }

    }

    private void initIndicator() {
        mIndicator.removeAllViews();
        for (int i = 0; i < mDataList.size(); i++) {
            ImageView img = new ImageView(mActivityRef.get());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    INDICATOR_DOT_HEIGHT, INDICATOR_DOT_HEIGHT);
            params.leftMargin = params.rightMargin = INDICATOR_DOT_HEIGHT / 2;
            img.setLayoutParams(params);
            img.setImageResource(R.drawable.ic_dot_uncheck);
            mIndicator.addView(img);
        }
    }

    private void setupViewPager() {
        mViewPager.setAdapter(new MyPagerAdapter(mPageViews));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setIndicator(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setIndicator(int idx) {
        for (int i = 0; i < mPageViews.size(); i++) {
            ((ImageView) mIndicator.getChildAt(i))
                    .setImageResource(i == idx ? R.drawable.ic_dot_check
                            : R.drawable.ic_dot_uncheck);
        }
    }

    private GiftBean mSelectedGift;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mSelectedGift != null) {
            mSelectedGift.isSelected = false;
        }

        GiftAdapter adapter = (GiftAdapter) parent.getAdapter();
        GiftBean bean = adapter.getItem(position);

        if (bean == mSelectedGift) {
            mSelectedGift = null;
            mBtnSend.setEnabled(false);
        } else {
            bean.isSelected = true;
            mSelectedGift = bean;
            mBtnSend.setEnabled(true);
        }

        for (int i = 0; i < mPageViews.size(); i++) {
            GridView gv = (GridView) mPageViews.get(i);
            ((GiftAdapter) gv.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (mSelectedGift == null) {
                    return;
                }

                if (mSelectedGift.diamond > LoginManager.getInstance().getUserInfo().diamond) {
                    //Toast.makeText(v.getContext(), "音悦钻不足，请充值", Toast.LENGTH_SHORT).show();
                    showDialog();
                    return;
                }

                if (mSelectedGift.is_doublehit == 1) {
                    sendGift(1);
                    //showGiftSelf(1);
                    mDoubleHitLayout.setVisibility(View.VISIBLE);
                    startCountDown();
                } else {
                    //showGiftSelf(1);
                    sendGift(1);
                }
                break;
            case R.id.double_hit:
                mCountdown = 3000;

                mGiftHits++;
                // TODO
                sendGift(mGiftHits);
                //showGiftSelf(mGiftHits);

                break;
            case R.id.go_buy:
                Context context = getContentView().getContext();
                context.startActivity(new Intent(context, BuyActivity.class));
                dismiss();
                break;
        }
    }

    private ConfirmDialog mDialog;

    private void showDialog() {
        if (mDialog == null) {
            mDialog = new ConfirmDialog(getContentView().getContext());
            mDialog.setOnDialogListener(new ConfirmDialog.OnDialogListener() {
                @Override
                public void onEnsure() {
                    Context context = getContentView().getContext();
                    context.startActivity(new Intent(context, BuyActivity.class));
                }
            });
        }

        mDialog.show("对不起，当前余额不足，充值后才可以送礼物，是否去充值？");
    }

    private int mGiftHits = 1;
    private int mCountdown = 3000;
    private static final int INTERVAL = 60;

    private void startCountDown() {
        mGiftHits = 1;
        mCountdown = 3000;
        mDoubleHitLayout.post(mCountDownRunnable);
    }

    private Runnable mCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            mTxtCountdown.setText(mCountdown / 100 + "");
            if (mCountdown > 0) {
                mCountdown -= 100;
                mDoubleHitLayout.postDelayed(mCountDownRunnable, INTERVAL);
            } else {
                mDoubleHitLayout.setVisibility(View.GONE);
                //sendGift(mGiftHits);
            }
        }
    };

    private void showGiftSelf(int hit) {
        // TODO 本地展示
        if (mGiftListener != null) {
            mGiftMessage = new MessageInfo();
            mGiftMessage.name = LoginManager.getInstance().getUserInfo().nickname;
            mGiftMessage.image = LoginManager.getInstance().getUserInfo().image;
            mGiftMessage.id = LoginManager.getInstance().getUserInfo().id;
            mGiftMessage.level = LoginManager.getInstance().getUserInfo().user_level + "";

            mGiftMessage.gift = mSelectedGift;
            mGiftMessage.giftHit = hit;
            mGiftMessage.msg = "送了礼物" + mSelectedGift.name;
            mGiftListener.onSendGift(mGiftMessage);
        }
    }

    private void sendGift(int hit) {
        showGiftSelf(hit);

        ChatApi.sendGift(getContentView().getContext(), mSelectedGift, hit, mRooomid, mAnchorid, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                // TODO 同步
                Type typeObject = new TypeToken<BaseResponse<DiamondLeftInfo>>() {
                }.getType();
                BaseResponse<DiamondLeftInfo> ret = new Gson().fromJson(data, typeObject);


                LoginManager.getInstance().getUserInfo().diamond = ret.data.curDiamond;
                LoginManager.getInstance().saveUserInfo();
                mTxtDiamond.setText(LoginManager.getInstance().getUserCookie().userinfo.diamond + "");
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    public interface OnGiftSendListener {
        void onSendGift(MessageInfo info);
    }

    private OnGiftSendListener mGiftListener;

    public void setOnGiftSendListener(OnGiftSendListener l) {
        mGiftListener = l;
    }

    @Override
    public void dismiss() {
        if (mSelectedGift != null) {
            mSelectedGift.isSelected = false;
            mSelectedGift = null;
            mDoubleHitLayout.removeCallbacks(mCountDownRunnable);
            mDoubleHitLayout.setVisibility(View.GONE);
        }
        super.dismiss();
    }
}
