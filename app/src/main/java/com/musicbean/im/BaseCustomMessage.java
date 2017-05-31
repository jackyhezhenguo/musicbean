package com.musicbean.im;

import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.model.MessageContent;

/**
 * Created by boyo on 16/6/30.
 */
public abstract class BaseCustomMessage<T> extends MessageContent {

    public T content;

    private CustomUserInfo cUserInfo;

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", getJSONContent());

            if (this.getJSONUserInfo() != null) {
                jsonObj.putOpt("user", this.getJSONUserInfo());
            }
        } catch (JSONException var4) {
            RLog.e("MyTxtMessage", "JSONException " + var4.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    protected abstract JSONObject getJSONContent();


    protected BaseCustomMessage() {

    }

    public BaseCustomMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        Log.e("BaseCustomMessage", "customMessage:" + jsonStr);
        try {
            JSONObject e = new JSONObject(jsonStr);
            if (e.has("content")) {
                String str = e.optString("content");
                this.content = parseContent(str);
            }

            if (e.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
            }
        } catch (JSONException var4) {
            Log.e("GiftMessage", "JSONException " + var4.getMessage());
        }

    }

    public CustomUserInfo getUserInfo() {
        return this.cUserInfo;
    }

    public void setUserInfo(CustomUserInfo info) {
        this.cUserInfo = info;
    }

    public JSONObject getJSONUserInfo() {
        if (this.getUserInfo() != null && this.getUserInfo().getUserId() != null) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("id", this.getUserInfo().getUserId());
                if (!TextUtils.isEmpty(this.getUserInfo().getName())) {
                    jsonObject.put("name", this.getUserInfo().getName());
                }

                if (this.getUserInfo().getPortraitUri() != null) {
                    jsonObject.put("portrait", this.getUserInfo().getPortraitUri());
                }

                jsonObject.put("user_level", getUserInfo().user_level);
            } catch (JSONException var3) {
                RLog.e("MessageContent", "JSONException " + var3.getMessage());
            }

            return jsonObject;
        } else {
            return null;
        }
    }

    public CustomUserInfo parseJsonToUserInfo(JSONObject jsonObj) {
        CustomUserInfo info = null;
        String id = jsonObj.optString("id");
        String name = jsonObj.optString("name");
        String icon = jsonObj.optString("portrait");
        if (TextUtils.isEmpty(icon)) {
            icon = jsonObj.optString("icon");
        }
        String level = jsonObj.optString("user_level");

        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name)) {
            Uri portrait = icon != null ? Uri.parse(icon) : null;
            info = new CustomUserInfo(id, name, portrait, level);
        }

        return info;
    }

    protected abstract T parseContent(String str);

    //给消息赋值。
    public BaseCustomMessage(Parcel in) {
        content = readContentFromParcel(in);
        this.setUserInfo(ParcelUtils.readFromParcel(in, CustomUserInfo.class));
    }

    protected abstract T readContentFromParcel(Parcel in);

    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     *
     * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
     */
    public int describeContents() {
        return 0;
    }

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeContentToParcel(dest, flags);
        ParcelUtils.writeToParcel(dest, this.getUserInfo());
    }

    protected abstract void writeContentToParcel(Parcel dest, int flags);
}
