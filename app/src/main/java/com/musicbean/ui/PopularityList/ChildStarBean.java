package com.musicbean.ui.PopularityList;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/5/19 0019.
 */

public class ChildStarBean {
    private Bitmap imgNumber;
    private Bitmap imgUser;
    private String number;
    private String name;
    private String count;

    public ChildStarBean(Bitmap imgNumber, Bitmap imgUser, String number, String name, String count) {
        this.imgNumber = imgNumber;
        this.imgUser = imgUser;
        this.number = number;
        this.name = name;
        this.count = count;
    }

    public Bitmap getImgNumber() {
        return imgNumber;
    }

    public void setImgNumber(Bitmap imgNumber) {
        this.imgNumber = imgNumber;
    }

    public Bitmap getImgUser() {
        return imgUser;
    }

    public void setImgUser(Bitmap imgUser) {
        this.imgUser = imgUser;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
