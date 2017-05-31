package com.musicbean.http.bean;

/**
 * Created by boyo on 16/7/2.
 */
public class UserPageInfo {
    public UserInfoBean userinfo;
    public ListData<UserInfoBean> conceninfo; // 关注
    public ListData<UserInfoBean> followinfo; // 粉丝
    public int manager_power;
    public RecordInfo recordinfo;
}
