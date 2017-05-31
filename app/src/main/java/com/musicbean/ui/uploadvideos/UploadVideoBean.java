package com.musicbean.ui.uploadvideos;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class UploadVideoBean {
    private String videoName;
    private Bitmap videoBitmap;

    public UploadVideoBean(String videoName, Bitmap videoBitmap) {
        this.videoName = videoName;
        this.videoBitmap = videoBitmap;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public Bitmap getVideoBitmap() {
        return videoBitmap;
    }

    public void setVideoBitmap(Bitmap videoBitmap) {
        this.videoBitmap = videoBitmap;
    }
}
