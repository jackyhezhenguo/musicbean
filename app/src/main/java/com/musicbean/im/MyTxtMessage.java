package com.musicbean.im;

import android.os.Parcel;

import com.google.gson.Gson;

import org.json.JSONObject;

import io.rong.imlib.MessageTag;

/**
 * Created by boyo on 16/6/30.
 */
@MessageTag(value = "UD:TxtMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class MyTxtMessage extends BaseCustomMessage<TextContent> {

    public MyTxtMessage(byte[] data) {
        super(data);
    }

    public MyTxtMessage() {
        super();
    }

    //给消息赋值。
    public MyTxtMessage(Parcel in) {
        super(in);
    }

    @Override
    protected TextContent readContentFromParcel(Parcel in) {
        TextContent gcontent = new TextContent();
        gcontent.text = in.readString();
        gcontent.text_type = in.readInt();
        return gcontent;
    }

    @Override
    protected JSONObject getJSONContent() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", content.text);
            jsonObject.put("text_type", content.text_type);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void writeContentToParcel(Parcel dest, int flags) {
        dest.writeString(content.text);
        dest.writeInt(content.text_type);
    }

    @Override
    protected TextContent parseContent(String str) {
        return new Gson().fromJson(str, TextContent.class);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<MyTxtMessage> CREATOR = new Creator<MyTxtMessage>() {

        @Override
        public MyTxtMessage createFromParcel(Parcel source) {
            return new MyTxtMessage(source);
        }

        @Override
        public MyTxtMessage[] newArray(int size) {
            return new MyTxtMessage[size];
        }
    };

}
