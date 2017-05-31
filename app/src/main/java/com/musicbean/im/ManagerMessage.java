package com.musicbean.im;

import android.os.Parcel;

import com.google.gson.Gson;

import org.json.JSONObject;

import io.rong.imlib.MessageTag;

/**
 * Created by boyo on 16/6/30.
 */
@MessageTag(value = "UD:ManagerMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class ManagerMessage extends BaseCustomMessage<ManagerContent> {

    public ManagerMessage(byte[] data) {
        super(data);
    }

    //给消息赋值。
    public ManagerMessage(Parcel in) {
        super(in);
    }

    @Override
    protected ManagerContent readContentFromParcel(Parcel in) {
        ManagerContent gcontent = new ManagerContent();
        gcontent.managerid = in.readString();
        gcontent.managernickname = in.readString();
        gcontent.managerimage = in.readString();
        return gcontent;
    }

    @Override
    protected JSONObject getJSONContent() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("managerid", content.managerid);
            jsonObject.put("managernickname", content.managernickname);
            jsonObject.put("managerimage", content.managerimage);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void writeContentToParcel(Parcel dest, int flags) {
        dest.writeString(content.managerid);
        dest.writeString(content.managernickname);
        dest.writeString(content.managerimage);
    }

    @Override
    protected ManagerContent parseContent(String str) {
        return new Gson().fromJson(str, ManagerContent.class);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<ManagerMessage> CREATOR = new Creator<ManagerMessage>() {

        @Override
        public ManagerMessage createFromParcel(Parcel source) {
            return new ManagerMessage(source);
        }

        @Override
        public ManagerMessage[] newArray(int size) {
            return new ManagerMessage[size];
        }
    };

}
