package com.musicbean.im;

import android.os.Parcel;

import com.google.gson.Gson;

import org.json.JSONObject;

import io.rong.imlib.MessageTag;

/**
 * Created by boyo on 16/6/30.
 */
@MessageTag(value = "UD:GiftMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class GiftMessage extends BaseCustomMessage<GiftContent> {

    public GiftMessage(byte[] data) {
        super(data);
    }

    //给消息赋值。
    public GiftMessage(Parcel in) {
        super(in);
    }

    @Override
    protected GiftContent readContentFromParcel(Parcel in) {
        GiftContent gcontent = new GiftContent();
        gcontent.giftid = in.readInt();
        gcontent.cartoon_type = in.readInt();
        gcontent.hit = in.readInt();
        gcontent.beans = in.readInt();
        return gcontent;
    }

    @Override
    protected JSONObject getJSONContent() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("giftid", content.giftid);
            jsonObject.put("cartoon_type", content.cartoon_type);
            jsonObject.put("hit", content.hit);
            jsonObject.put("beans", content.beans);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void writeContentToParcel(Parcel dest, int flags) {
        dest.writeInt(content.giftid);
        dest.writeInt(content.cartoon_type);
        dest.writeInt(content.hit);
        dest.writeInt(content.beans);
    }

    @Override
    protected GiftContent parseContent(String str) {
        return new Gson().fromJson(str, GiftContent.class);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<GiftMessage> CREATOR = new Creator<GiftMessage>() {

        @Override
        public GiftMessage createFromParcel(Parcel source) {
            return new GiftMessage(source);
        }

        @Override
        public GiftMessage[] newArray(int size) {
            return new GiftMessage[size];
        }
    };

}
