package com.musicbean.im;

import android.os.Parcel;

import com.google.gson.Gson;

import org.json.JSONObject;

import io.rong.imlib.MessageTag;

/**
 * Created by boyo on 16/6/30.
 */
@MessageTag(value = "UD:LiveStatusMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class LiveStatusMessage extends BaseCustomMessage<LiveStatusContent> {

    public LiveStatusMessage(){

    }

    public LiveStatusMessage(byte[] data) {
        super(data);
    }

    //给消息赋值。
    public LiveStatusMessage(Parcel in) {
        super(in);
    }

    @Override
    protected LiveStatusContent readContentFromParcel(Parcel in) {
        LiveStatusContent gcontent = new LiveStatusContent();
        gcontent.status = in.readString();
        return gcontent;
    }

    @Override
    protected JSONObject getJSONContent() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", content.status);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void writeContentToParcel(Parcel dest, int flags) {
        dest.writeString(content.status);
    }

    @Override
    protected LiveStatusContent parseContent(String str) {
        return new Gson().fromJson(str, LiveStatusContent.class);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<LiveStatusMessage> CREATOR = new Creator<LiveStatusMessage>() {

        @Override
        public LiveStatusMessage createFromParcel(Parcel source) {
            return new LiveStatusMessage(source);
        }

        @Override
        public LiveStatusMessage[] newArray(int size) {
            return new LiveStatusMessage[size];
        }
    };

}
