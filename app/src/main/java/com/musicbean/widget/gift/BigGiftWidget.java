package com.musicbean.widget.gift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.musicbean.R;
import com.musicbean.http.bean.GiftBean;
import com.musicbean.http.bean.MessageInfo;

import net.tsz.afinal.FinalBitmap;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by boyo on 16/7/11.
 */
public class BigGiftWidget extends LinearLayout implements Animation.AnimationListener {

    private ImageView mHeadImg;
    private TextView mName;
    private ImageView mGiftImg;

    // 礼物消息队列
    private LinkedBlockingQueue<MessageInfo> mGiftQueue;

    private boolean isAnimating = false;

    private AnimWorkerThread mWorkerThread;

    private Animation mAnimation;
    private Animation mGifAnimation;

    public BigGiftWidget(Context context) {
        this(context, null);
    }

    public BigGiftWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.big_gift_widget, this);
        mHeadImg = (ImageView) findViewById(R.id.head_img);
        mName = (TextView) findViewById(R.id.name);
        mGiftImg = (ImageView) findViewById(R.id.gift_img);

        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.big_gift_anim);
        mAnimation.setAnimationListener(this);
        mGifAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.gif_gift_anim);
        mGifAnimation.setAnimationListener(this);

        mGiftQueue = new LinkedBlockingQueue<>();

        setClipChildren(false);
        setClipToPadding(false);

        mWorkerThread = new AnimWorkerThread();
        mWorkerThread.start();
    }

    public void put(MessageInfo info) {
        boolean b = mGiftQueue.offer(info);
    }

    class AnimWorkerThread extends Thread {
        private boolean needStop = false;
        private Object mLock = new Object();

        @Override
        public void run() {
            while (!needStop) {
                try {
                    synchronized (mLock) {
                        if (!isAnimating) {
                            MessageInfo ginfo = mGiftQueue.take();
                            putGift(ginfo);
                        } else {
                            mLock.wait();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        void setStop() {
            needStop = true;
        }

        void wakeup() {
            synchronized (mLock) {
                mLock.notify();
            }
        }
    }

    public void onDestroy() {
        mWorkerThread.setStop();
        try {
            mWorkerThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putGift(final MessageInfo giftMsg) {
        isAnimating = true;
        post(new Runnable() {
            @Override
            public void run() {
                showGift(giftMsg);
            }
        });
    }

    private void showGift(MessageInfo giftMsg) {
        FinalBitmap.getInstance().display(mHeadImg, giftMsg.image);
        mName.setText(giftMsg.name);
        //FinalBitmap.getInstance().display(mGiftImg, giftMsg.gift.image);
        Glide.with(getContext()).load(giftMsg.gift.image).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mGiftImg);

        if (giftMsg.gift.cartoon_type == GiftBean.TYPE_GIF) {
            startAnimation(mGifAnimation);
        } else {
            startAnimation(mAnimation);
        }

    }

    @Override
    public void onAnimationStart(Animation animation) {
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        isAnimating = false;
        mWorkerThread.wakeup();
        setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
