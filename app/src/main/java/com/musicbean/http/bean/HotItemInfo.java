package com.musicbean.http.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by boyo on 16/6/18.
 */
public class HotItemInfo implements Serializable {
//    "roomid": "9dd0c93368c26a7d2447f8d0e9e4ed1f",
//            "title": "直播标题",
//            "topic": "直播话题",
//            "location": "北京",
//            "image": "http://image.huajiao.com/18abf218f7787452a6bf33da9c8905f7-172_245.jpg",
//            "play_addr": {
//        "rtmp": "rtmp://vedio.ureactor.com/live/9dd0c93368c26a7d2447f8d0e9e4ed1f",
//                "hdl": "http://vedio.ureactor.com/live/9dd0c93368c26a7d2447f8d0e9e4ed1f.flv",
//                "hls": "http://vedio.ureactor.com/live/9dd0c93368c26a7d2447f8d0e9e4ed1f/playlist.m3u8"
//    },
//            "share_addr": {
//        "rtmp": "rtmp://vedio.ureactor.com/live/9dd0c93368c26a7d2447f8d0e9e4ed1f",
//                "hdl": "http://vedio.ureactor.com/live/9dd0c93368c26a7d2447f8d0e9e4ed1f.flv",
//                "hls": "http://vedio.ureactor.com/live/9dd0c93368c26a7d2447f8d0e9e4ed1f/playlist.m3u8"
//    },
//            "client": "ios",
//            "version": "1.0",
//            "online_users": "0",
//            "pubtime": "2016-06-18 09:45:29",
//            "anchorinfo": {
//        "id": "123456",
//                "nickname": "皮皮狗",
//                "thumb": "http://image.huajiao.com/18abf218f7787452a6bf33da9c8905f7-172_245.jpg",
//                "studio": "皮皮狗工作室"
//    }

    public int id;
    public String roomid;
    public String title;
    public String topic;
    public String location;
    public String image;
    public PlayAddr play_addr;
    public PlayAddr playaddr;
    public String online_users;
    public UserInfoBean anchorinfo;
    public String video_type;
    public String live_roomid;
    public String live_image;
    public String income_users;
    public String playnum;
    public String live_title;

    public static final String live = "live";
    public static final String record = "record";
    public static final String video = "video";

    public String getTitle() {
        if (!TextUtils.isEmpty(title)) {
            return title;
        }

        if (!TextUtils.isEmpty(live_title)) {
            return live_title;
        }

        return "";
    }

    public boolean isLive() {
        return live.equals(video_type);
    }

    public boolean isVideo() {
        return video.equals(video_type);
    }
}
