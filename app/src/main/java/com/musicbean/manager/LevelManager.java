package com.musicbean.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.musicbean.App;
import com.musicbean.R;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.util.ScreenUtils;
import com.musicbean.widget.LevelDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boyo on 16/7/19.
 */
public class LevelManager {

    private static final String TAG = "IMManager";

    private static LevelManager instance = new LevelManager();

    private int[] mBitmapIds = {R.drawable.level_star, R.drawable.level_moon,
            R.drawable.level_sun, R.drawable.level_diamond, R.drawable.level_king};

    private int[] mLevelResId = {
            R.drawable.star, R.drawable.moon, R.drawable.sun, R.drawable.diamond, R.drawable.king50,
            R.drawable.king60, R.drawable.king70, R.drawable.king80, R.drawable.king90, R.drawable.king100

    };

    private int[] mCertLevelResId = {
            R.drawable.level1, R.drawable.level2, R.drawable.level3, R.drawable.level4, R.drawable.level5,
            R.drawable.level6, R.drawable.level7
    };

    private List<Bitmap> mBitmapList;

    private BitmapDrawable mBmpWoman;
    private BitmapDrawable mBmpMan;

    private LevelManager() {
        mBitmapList = new ArrayList<>(mBitmapIds.length);
        for (int i = 0; i < mBitmapIds.length; i++) {
            Bitmap bmp = BitmapFactory.decodeResource(App.getContext().getResources(), mBitmapIds[i]);
            mBitmapList.add(bmp);
        }
        int width = ScreenUtils.dp2px(App.getContext(), 14);
        int height = ScreenUtils.dp2px(App.getContext(), 12);
        Bitmap bmp = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.icon_woman);
        mBmpWoman = new BitmapDrawable(bmp);
        mBmpWoman.setBounds(0, 0, width, height);
        bmp = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.icon_man);
        mBmpMan = new BitmapDrawable(bmp);
        mBmpMan.setBounds(0, 0, width, height);
    }

    public static LevelManager getInstance() {
        return instance;
    }

    public SpannableString getNameSexLevelSS(UserInfoBean ubean) {
        if (TextUtils.isEmpty(ubean.nickname)) {
            return new SpannableString("");
        }
        SpannableString ssb = new SpannableString(ubean.nickname + " . .");
        ImageSpan is = new ImageSpan(ubean.isWoman() ? mBmpWoman : mBmpMan, ImageSpan.ALIGN_BASELINE);
        ssb.setSpan(is, ubean.nickname.length() + 1, ubean.nickname.length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        int idx = (ubean.user_level - 1) / 10;
        if (idx < 0) {
            idx = 0;
        } else if (idx > 4) {
            idx = 4;
        }
        LevelDrawable ld = new LevelDrawable(mBitmapList.get(idx), ubean.user_level);
        is = new ImageSpan(ld, ImageSpan.ALIGN_BASELINE);
        ssb.setSpan(is, ubean.nickname.length() + 3, ubean.nickname.length() + 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return ssb;
    }

    public SpannableString getLevelNameSS(UserInfoBean ubean) {
        return getLevelNameSS(ubean.user_level, ubean.nickname);
    }

    public SpannableString getLevelNameSS(int level, String name) {
        SpannableString ssb = new SpannableString(". " + name);
        int idx = (level - 1) / 10;
        if (idx < 0) {
            idx = 0;
        } else if (idx > 4) {
            idx = 4;
        }
        LevelDrawable ld = new LevelDrawable(mBitmapList.get(idx), level);
        ImageSpan is = new ImageSpan(ld, ImageSpan.ALIGN_BASELINE);
        ssb.setSpan(is, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return ssb;
    }

    public ImageSpan getLevelSpan(int level) {
        int idx = (level - 1) / 10;
        if (idx < 0) {
            idx = 0;
        } else if (idx > 4) {
            idx = 4;
        }
        LevelDrawable ld = new LevelDrawable(mBitmapList.get(idx), level);
        return new ImageSpan(ld, ImageSpan.ALIGN_BASELINE);
    }

    public int getLevelBgId(int level) {
        int idx = (level - 1) / 10;
        if (idx < 0) {
            idx = 0;
        } else if (idx > 9) {
            idx = 9;
        }
        return mLevelResId[idx];
    }

    public int getCertLevelResId(int level) {
        level = level - 1;
        if (level < 0) {
            level = 0;
        }
        if (level > 6) {
            level = 6;
        }
        return mCertLevelResId[level];
    }
}
