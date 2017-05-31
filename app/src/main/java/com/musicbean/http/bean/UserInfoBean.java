package com.musicbean.http.bean;

import java.io.Serializable;

/**
 * Created by boyo on 16/7/2.
 */
public class UserInfoBean implements Serializable {
    public String id;
    public String musicid;
    public String nickname;
    public String image;
    public String h_cover;
    public String gender;
    public String studio;
    public String spec_sign;
    public String honor;
    public String opus; // 代表作
    public String experence;
    public int diamond;
    public int bean;
    public int in_bean;
    public int user_level;
    public String occupation;
    public String phone;

    public String contribution;

    public int relation;

    public int is_remind;
    public int remind_one;

    public int is_certified;
    public int certify_level;
    public String certify_level_desc;

    public String getSexStr() {
        if ("M".equals(gender)) {
            return "男";
        } else if ("F".equals(gender)) {
            return "女";
        } else {
            return "";
        }
    }

    public boolean isWoman() {
        return "F".equals(gender);
    }
}
