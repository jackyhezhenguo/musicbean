package com.musicbean.pay;

import android.app.Activity;

import com.qihoo.share.framework.ShareCallBackListener;
import com.qihoo.share.framework.ShareResult;
import com.qihoo.share.framework.ShareSdk;
import com.qihoo.share.framework.ShareSdk.API_NAME;
import com.qihoo.share.weixin.WXShareSessionAPI;
import com.tencent.mm.sdk.modelpay.PayReq;

import org.json.JSONObject;

public class WXPayAPI extends BasePayAPI {

	@Override
	public void doPay(String orderInfo, final Activity context) {
		// TODO Auto-generated method stub
		WXShareSessionAPI api = (WXShareSessionAPI) ShareSdk.getShareAPI(API_NAME.WXSession, context);

		PayReq req = parseInfo(orderInfo);
		if (req == null) {
			if (mListener != null) {
				context.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						PayApiResult ret = new PayApiResult();
						ret.resultCode = PayApiResult.CODE_FAILED;
						ret.resultMsg = "订单解析失败";
						mListener.callback(ret);
					}
				});
			}
		} else {
			api.setCallBackListener(new ShareCallBackListener() {

				@Override
				public void callback(final ShareResult ret) {
					if (mListener != null) {
						context.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								PayApiResult payRet = new PayApiResult();
								payRet.resultCode = ret.resultCode == ShareResult.CODE_SUCCESS ? PayApiResult.CODE_SUCCESS
										: PayApiResult.CODE_FAILED;
								payRet.resultMsg = ret.resultMsg;
								mListener.callback(payRet);
							}
						});
					}
				}
			});
			api.requestPay(req, context);
		}
	}

	private PayReq parseInfo(String json) {
		PayReq req = null;
		try {
			req = new PayReq();
			JSONObject jobject = new JSONObject(json);
			req.appId = jobject.optString("appid");
			req.partnerId = jobject.optString("partnerid");
			req.prepayId = jobject.optString("prepayid");
			req.packageValue = jobject.optString("package");
			req.nonceStr = jobject.optString("noncestr");
			req.timeStamp = jobject.optString("timestamp");
			req.sign = jobject.optString("sign");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return req;
	}

}
