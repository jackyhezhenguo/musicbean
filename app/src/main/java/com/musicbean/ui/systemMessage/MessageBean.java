package com.musicbean.ui.systemMessage;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class MessageBean {
    //消息的头像
    private Bitmap imgPortrait;
    private String userName;
    private String comment;
    private String commentTime;
    private String videoName;

    public MessageBean(Bitmap imgPortrait, String userName, String comment, String commentTime, String videoName) {
        this.imgPortrait = imgPortrait;
        this.userName = userName;
        this.comment = comment;
        this.commentTime = commentTime;
        this.videoName = videoName;
    }

    public Bitmap getImgPortrait() {
        return imgPortrait;
    }

    public void setImgPortrait(Bitmap imgPortrait) {
        this.imgPortrait = imgPortrait;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
