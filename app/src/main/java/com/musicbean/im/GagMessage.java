package com.musicbean.im;

import android.os.Parcel;

import com.google.gson.Gson;

import org.json.JSONObject;

import io.rong.imlib.MessageTag;

/**
 * Created by boyo on 16/6/30.
 */
@MessageTag(value = "UD:GagMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class GagMessage extends BaseCustomMessage<GagContent> {

    public GagMessage(byte[] data) {
        super(data);
    }

    //给消息赋值。
    public GagMessage(Parcel in) {
        super(in);
    }

    @Override
    protected GagContent readContentFromParcel(Parcel in) {
        GagContent gcontent = new GagContent();
        gcontent.gagid = in.readString();
        gcontent.gagnickname = in.readString();
        gcontent.gagimage = in.readString();
        gcontent.gagminute = in.readInt();
        return gcontent;
    }

    @Override
    protected JSONObject getJSONContent() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("gagid", content.gagid);
            jsonObject.put("gagnickname", content.gagnickname);
            jsonObject.put("gagimage", content.gagimage);
            jsonObject.put("gagminute", content.gagminute);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void writeContentToParcel(Parcel dest, int flags) {
        dest.writeString(content.gagid);
        dest.writeString(content.gagnickname);
        dest.writeString(content.gagimage);
        dest.writeInt(content.gagminute);
    }

    @Override
    protected GagContent parseContent(String str) {
        return new Gson().fromJson(str, GagContent.class);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<GagMessage> CREATOR = new Creator<GagMessage>() {

        @Override
        public GagMessage createFromParcel(Parcel source) {
            return new GagMessage(source);
        }

        @Override
        public GagMessage[] newArray(int size) {
            return new GagMessage[size];
        }
    };

}
