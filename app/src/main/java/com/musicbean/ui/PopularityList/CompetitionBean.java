package com.musicbean.ui.PopularityList;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/5/22 0022.
 */

public class CompetitionBean {
    private Bitmap image;
    private String competitionName;
    private String gainName;
    private String cooperativePartnerName;
    private String city;
    private String age;

    public CompetitionBean(Bitmap image, String competitionName, String gainName, String cooperativePartnerName, String city, String age) {
        this.image = image;
        this.competitionName = competitionName;
        this.gainName = gainName;
        this.cooperativePartnerName = cooperativePartnerName;
        this.city = city;
        this.age = age;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getGainName() {
        return gainName;
    }

    public void setGainName(String gainName) {
        this.gainName = gainName;
    }

    public String getCooperativePartnerName() {
        return cooperativePartnerName;
    }

    public void setCooperativePartnerName(String cooperativePartnerName) {
        this.cooperativePartnerName = cooperativePartnerName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
