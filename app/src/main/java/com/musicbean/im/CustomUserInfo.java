package com.musicbean.im;

import android.net.Uri;
import android.os.Parcel;

import io.rong.imlib.model.UserInfo;

/**
 * Created by boyo on 16/7/21.
 */
public class CustomUserInfo extends UserInfo {
    public String user_level;

    public CustomUserInfo(Parcel in) {
        super(in);
        user_level = in.readString();
    }

    public CustomUserInfo(String id, String name, Uri portraitUri, String level) {
        super(id, name, portraitUri);
        user_level = level;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(user_level);
    }

    public static final Creator<CustomUserInfo> CREATOR = new Creator() {
        public CustomUserInfo createFromParcel(Parcel source) {
            return new CustomUserInfo(source);
        }

        public CustomUserInfo[] newArray(int size) {
            return new CustomUserInfo[size];
        }
    };
}
