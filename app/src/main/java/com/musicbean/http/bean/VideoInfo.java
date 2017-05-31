package com.musicbean.http.bean;

public class VideoInfo {

    private int id;
    private int musicid;
    private String nickname;
    private String title;
    private String cover;
    private PlayAddr playaddr;
    private int playnum;
    private int duration;
    private String pubdate;
    private String video_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMusicid() {
        return musicid;
    }

    public void setMusicid(int musicid) {
        this.musicid = musicid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return cover;
    }

    public void setImage(String cover) {
        this.cover = cover;
    }

    public String getPlay_addr() {
        return playaddr.flv;
    }

    public int getPlaynum() {
        return playnum;
    }

    public void setPlaynum(int playnum) {
        this.playnum = playnum;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public PlayAddr getPlayaddr() {
        return playaddr;
    }

    public void setPlayaddr(PlayAddr playaddr) {
        this.playaddr = playaddr;
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

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }
}