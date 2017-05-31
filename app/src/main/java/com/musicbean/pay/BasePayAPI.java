package com.musicbean.pay;

import android.app.Activity;

abstract public class BasePayAPI {
	
	protected PayCallbackListener mListener = null;

	/**
	 * 设置分享结果的回调
	 * 
	 * @param listener
	 */
	public void setCallBackListener(PayCallbackListener listener) {
		mListener = listener;
	}
	
	abstract public void doPay(String orderInfo, Activity context);
}
