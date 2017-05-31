package com.musicbean.widget.gift;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.musicbean.R;
import com.musicbean.http.bean.MessageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by boyo on 16/7/11.
 */
public class SmallGiftGroup extends LinearLayout implements SmallGiftWidget.OnFinishListener {

    private SmallGiftWidget mGiftWidget1;
    private SmallGiftWidget mGiftWidget2;

    // 礼物消息队列
    private LinkedBlockingQueue<MessageInfo> mGiftQueue;

    private ArrayBlockingQueue<SmallGiftWidget> mIdleQueue;
    private List<SmallGiftWidget> mWorkingList;

    private AnimWorkerThread mWorkerThread;

    public SmallGiftGroup(Context context) {
        this(context, null);
    }

    public SmallGiftGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.small_gift_group, this);
        mGiftWidget1 = (SmallGiftWidget) findViewById(R.id.gift1);
        mGiftWidget2 = (SmallGiftWidget) findViewById(R.id.gift2);
        mGiftQueue = new LinkedBlockingQueue<>();
        mIdleQueue = new ArrayBlockingQueue<>(2);
        mWorkingList = Collections.synchronizedList(new ArrayList(2));

        mGiftWidget1.setOnFinishListener(this);
        mGiftWidget2.setOnFinishListener(this);

        setClipChildren(false);
        setClipToPadding(false);

        mWorkerThread = new AnimWorkerThread();
        mWorkerThread.start();
    }

    public void put(MessageInfo info) {
        boolean b = mGiftQueue.offer(info);
        Log.e(SmallGiftGroup.class.getSimpleName(), "put gift: " + b + " " + info.giftHit);
    }

    class AnimWorkerThread extends Thread {
        private boolean needStop = false;

        @Override
        public void run() {
            try {
                mIdleQueue.put(mGiftWidget1);
                mIdleQueue.put(mGiftWidget2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (!needStop) {
                try {
                    MessageInfo ginfo = mGiftQueue.take();

                    SmallGiftWidget sgw = findWorkingGift(ginfo);
                    if (sgw == null) {
                        sgw = mIdleQueue.take();
                        mWorkingList.add(sgw);
                        sgw.putGift(ginfo);
                    } else {
                        sgw.showHit(ginfo);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        SmallGiftWidget findWorkingGift(MessageInfo info) {
            for (SmallGiftWidget sgw : mWorkingList) {
                if (sgw.isSame(info)) {
                    return sgw;
                }
            }
            return null;
        }

        void setStop() {
            needStop = true;
        }
    }

    public void onDestroy() {
        mWorkerThread.setStop();
        mGiftWidget1.onDestroy();
        mGiftWidget2.onDestroy();

        try {
            mWorkerThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(SmallGiftWidget sgw) {
        mWorkingList.remove(sgw);
        mIdleQueue.offer(sgw);
    }
}
