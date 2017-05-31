package com.musicbean.ui.PopularityList;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/5/22 0022.
 */

public class ContributionBean {
    private String number;
    private Bitmap image;
    private String name;
    private String count;

    public ContributionBean(String number, Bitmap image, String name, String count) {
        this.number = number;
        this.image = image;
        this.name = name;
        this.count = count;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
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
