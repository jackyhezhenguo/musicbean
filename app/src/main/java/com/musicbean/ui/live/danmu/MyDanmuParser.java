package com.musicbean.ui.live.danmu;

import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;

public class MyDanmuParser extends BiliDanmukuParser {

    @Override
    public Danmakus parse() {
        DanmakuFactory.notifyDispSizeChanged(mDisp);
        Danmakus d = super.parse();
        if (d == null) {
            d = new Danmakus();
        }
        return d;
    }
}
