package com.musicbean.pay;

import android.os.Parcel;
import android.os.Parcelable;

public class PayParam implements Parcelable {

	public int cat;
	public int type;
	public String title;
	public String vid;
	public String num;
	public String uid;
	public String expiry;
	public String state;
	public String price;

	public PayParam() {

	}

	public PayParam(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(cat);
		dest.writeInt(type);
		dest.writeString(title);
		dest.writeString(vid);
		dest.writeString(num);
		dest.writeString(uid);
		dest.writeString(expiry);
		dest.writeString(state);
		dest.writeString(price);
	}

	private void readFromParcel(Parcel in) {
		cat = in.readInt();
		type = in.readInt();
		title = in.readString();
		vid = in.readString();
		num = in.readString();
		uid = in.readString();
		expiry = in.readString();
		state = in.readString();
		price = in.readString();
	}

	public static Parcelable.Creator<PayParam> CREATOR = new Creator<PayParam>() {

		@Override
		public PayParam[] newArray(int size) {
			return new PayParam[size];
		}

		@Override
		public PayParam createFromParcel(Parcel source) {
			return new PayParam(source);
		}
	};

}
