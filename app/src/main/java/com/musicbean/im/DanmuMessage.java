package com.musicbean.im;

import android.os.Parcel;

import com.google.gson.Gson;

import org.json.JSONObject;

import io.rong.imlib.MessageTag;

/**
 * Created by boyo on 16/6/30.
 */
@MessageTag(value = "UD:DanmuMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class DanmuMessage extends BaseCustomMessage<DanmuContent> {

    public DanmuMessage(byte[] data) {
        super(data);
    }

    //给消息赋值。
    public DanmuMessage(Parcel in) {
        super(in);
    }

    @Override
    protected DanmuContent readContentFromParcel(Parcel in) {
        DanmuContent gcontent = new DanmuContent();
        gcontent.content = in.readString();
        return gcontent;
    }

    @Override
    protected JSONObject getJSONContent() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content.content);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void writeContentToParcel(Parcel dest, int flags) {
        dest.writeString(content.content);
    }

    @Override
    protected DanmuContent parseContent(String str) {
        return new Gson().fromJson(str, DanmuContent.class);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<DanmuMessage> CREATOR = new Creator<DanmuMessage>() {

        @Override
        public DanmuMessage createFromParcel(Parcel source) {
            return new DanmuMessage(source);
        }

        @Override
        public DanmuMessage[] newArray(int size) {
            return new DanmuMessage[size];
        }
    };

}
