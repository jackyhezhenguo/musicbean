package com.musicbean.pay;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

public class AliPayAPI extends BasePayAPI {

	@Override
	public void doPay(final String orderInfo, final Activity context) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				PayTask alipay = new PayTask(context);
				String result = alipay.pay(orderInfo);
				Log.i("AlyPayAPI", "alipay result: " + result);
				String tradeStatus = parseResult(result);
				boolean ret = "9000".equals(tradeStatus);

				if (mListener != null) {
					final PayApiResult payRet = new PayApiResult();
					payRet.resultCode = ret ? PayApiResult.CODE_SUCCESS : PayApiResult.CODE_FAILED;
					context.runOnUiThread(new Runnable() {
						public void run() {
							mListener.callback(payRet);
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 处理支付宝付费结果
	 * 
	 * @param result
	 * @return
	 */
	private String parseResult(String result) {
		String tradeStatus = "";
		if (!TextUtils.isEmpty(result)) {
			// 获取交易状态码，具体状态代码请参看文档
			try {
				tradeStatus = "resultStatus={";
				int imemoStart = result.indexOf("resultStatus=");
				imemoStart += tradeStatus.length();
				int imemoEnd = result.indexOf("};memo=");
				tradeStatus = result.substring(imemoStart, imemoEnd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tradeStatus;
	}
}
