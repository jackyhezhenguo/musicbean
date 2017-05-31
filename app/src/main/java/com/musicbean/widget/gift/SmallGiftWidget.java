package com.musicbean.widget.gift;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.musicbean.R;
import com.musicbean.http.bean.MessageInfo;

import net.tsz.afinal.FinalBitmap;

import java.util.LinkedList;

/**
 * Created by boyo on 16/7/11.
 */
public class SmallGiftWidget extends RelativeLayout implements Animation.AnimationListener {

    private static final String TAG = "SmallGiftWidget";

    private ImageView mHeadImg;
    private TextView mName;
    private TextView mMessage;
    private ImageView mGiftImg;
    private TextView mGiftHit;

    private Animation mAnimIn;
    private Animation mAnimOut;
    private Animation mAnimHit;

    private LinkedList<Integer> mHitList = new LinkedList<>();

    public static final int STAY_DURATION = 2000;

    public SmallGiftWidget(Context context) {
        this(context, null);
    }

    public SmallGiftWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.small_gift_layout, this);
        mHeadImg = (ImageView) findViewById(R.id.head_img);
        mName = (TextView) findViewById(R.id.name);
        mMessage = (TextView) findViewById(R.id.message);
        mGiftHit = (TextView) findViewById(R.id.gift_hit);
        mGiftImg = (ImageView) findViewById(R.id.gift_img);

        mAnimIn = AnimationUtils.loadAnimation(getContext(), R.anim.left_in);
        mAnimOut = AnimationUtils.loadAnimation(getContext(), R.anim.top_out);
        mAnimHit = AnimationUtils.loadAnimation(getContext(), R.anim.scale);

        mAnimIn.setAnimationListener(this);
        mAnimOut.setAnimationListener(this);
        mAnimHit.setAnimationListener(this);

        setClipChildren(false);
        setClipToPadding(false);
    }

    public void putGift(final MessageInfo giftMsg) {
        mCurrentGiftMsg = giftMsg;
        mHitCount = 1;
        post(new Runnable() {
            @Override
            public void run() {
                showGift(giftMsg);
            }
        });
    }

    private MessageInfo mCurrentGiftMsg;

    private void showGift(MessageInfo giftMsg) {
        FinalBitmap.getInstance().display(mHeadImg, giftMsg.image);
        mName.setText(giftMsg.name);
        mMessage.setText(giftMsg.msg);
        //FinalBitmap.getInstance().display(mGiftImg, giftMsg.gift.image);
        Glide.with(getContext()).load(giftMsg.gift.image).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mGiftImg);
        mGiftHit.setText("X1");

        startAnimation(mAnimIn);

        isWaitForFinish = false;
    }

    private boolean isPlayingHitAnim = false;
    private boolean isWaitForFinish = false;
    private int mHitCount = 1;

    public void showHit(final MessageInfo info) {
        mHitList.offer(++mHitCount);

        if (isWaitForFinish) {
            removeCallbacks(mFinishRunnable);
            isWaitForFinish = false;

            startHit();
        }

    }

    private void startHit() {
        post(new Runnable() {
            @Override
            public void run() {
                int t = mHitList.poll();
                Log.e(TAG, "startHitAnim:" + t);
                mGiftHit.setText("X" + t);
                mAnimOut.cancel();
                mGiftHit.startAnimation(mAnimHit);
                isPlayingHitAnim = true;
            }
        });
    }

    @Override
    public void onAnimationStart(Animation animation) {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == mAnimOut) {
            // TODO 结束
            if (mOnFinishListener != null) {
                mOnFinishListener.onFinish(this);
            }
        } else {
            if (animation == mAnimHit) {
                isPlayingHitAnim = false;
            }

            Log.e(TAG, "onAnimationEnd:" + animation);
            if (mHitList.isEmpty()) {
                removeCallbacks(mFinishRunnable);
                postDelayed(mFinishRunnable, STAY_DURATION);
                isWaitForFinish = true;
            } else if (!isPlayingHitAnim) {
                startHit();
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public boolean isSame(MessageInfo info) {
        return mCurrentGiftMsg != null && mCurrentGiftMsg.equals(info);
    }

    private Runnable mFinishRunnable = new Runnable() {
        @Override
        public void run() {
            startAnimation(mAnimOut);
        }
    };

    public void onDestroy() {
        removeCallbacks(mFinishRunnable);
    }

    public interface OnFinishListener {
        void onFinish(SmallGiftWidget sgw);
    }

    private OnFinishListener mOnFinishListener;

    public void setOnFinishListener(OnFinishListener l) {
        mOnFinishListener = l;
    }
}

