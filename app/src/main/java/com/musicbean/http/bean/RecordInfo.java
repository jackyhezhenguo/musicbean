package com.musicbean.http.bean;

public class RecordInfo {
    private int id;
    private int uid;
    private String live_roomid;
    private String live_image;
    private int income_users;
    private PlayAddr playaddr;
    private int playnum;
    private int duration;
    private String pubdate;
    private String pubdate_desc;
    private String video_type;
    private UserInfoBean anchorinfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLive_roomid() {
        return live_roomid;
    }

    public void setLive_roomid(String live_roomid) {
        this.live_roomid = live_roomid;
    }

    public String getLive_image() {
        return live_image;
    }

    public void setLive_image(String live_image) {
        this.live_image = live_image;
    }

    public int getIncome_users() {
        return income_users;
    }

    public void setIncome_users(int income_users) {
        this.income_users = income_users;
    }

    public String getPlayaddr() {
        return playaddr.flv;
    }

    public int getPlaynum() {
        return playnum;
    }

    public void setPlaynum(int playnum) {
        this.playnum = playnum;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getPubdate_desc() {
        return pubdate_desc;
    }

    public void setPubdate_desc(String pubdate_desc) {
        this.pubdate_desc = pubdate_desc;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }

    public UserInfoBean getAnchorinfo() {
        return anchorinfo;
    }

    public void setAnchorinfo(UserInfoBean anchorinfo) {
        this.anchorinfo = anchorinfo;
    }
}
