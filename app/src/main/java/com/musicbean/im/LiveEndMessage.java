package com.musicbean.im;

import android.os.Parcel;

import com.google.gson.Gson;

import org.json.JSONObject;

import io.rong.imlib.MessageTag;

/**
 * Created by boyo on 16/6/30.
 */
@MessageTag(value = "UD:LiveEndMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class LiveEndMessage extends BaseCustomMessage<LiveEndContent> {

    public LiveEndMessage(byte[] data) {
        super(data);
    }

    //给消息赋值。
    public LiveEndMessage(Parcel in) {
        super(in);
    }

    @Override
    protected LiveEndContent readContentFromParcel(Parcel in) {
        LiveEndContent gcontent = new LiveEndContent();
        gcontent.act = in.readString();
        return gcontent;
    }

    @Override
    protected JSONObject getJSONContent() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("act", content.act);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void writeContentToParcel(Parcel dest, int flags) {
        dest.writeString(content.act);
    }

    @Override
    protected LiveEndContent parseContent(String str) {
        return new Gson().fromJson(str, LiveEndContent.class);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<LiveEndMessage> CREATOR = new Creator<LiveEndMessage>() {

        @Override
        public LiveEndMessage createFromParcel(Parcel source) {
            return new LiveEndMessage(source);
        }

        @Override
        public LiveEndMessage[] newArray(int size) {
            return new LiveEndMessage[size];
        }
    };

}
