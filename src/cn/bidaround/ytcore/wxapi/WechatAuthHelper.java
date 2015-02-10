package cn.bidaround.ytcore.wxapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.AuthUserInfo;
import cn.bidaround.ytcore.util.Util;
import cn.bidaround.ytcore.util.YtLog;

public class WechatAuthHelper {

	private Activity activity;

	private AuthListener listener;

	private final int SUC = 1;

	private final int FAIL = 2;

	public WechatAuthHelper(Activity activity, AuthListener listener) {
		this.activity = activity;
		this.listener = listener;
	}

	public void httpForUserinfor(final Bundle bundle) {

		String loading = activity.getResources().getString(activity.getResources().getIdentifier("yt_authing", "string", activity.getPackageName()));

		Util.showProgressDialog(activity, loading, true);

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (bundle.containsKey("_wxapi_sendauth_resp_token"))
					getToken(bundle.get("_wxapi_sendauth_resp_token"));
				else
					handler.sendEmptyMessage(FAIL);
			}
		}).start();
	}

	public void getToken(Object code) {
		final String url = "https://api.weixin.qq.com/sns/oauth2/access_token" + "?appid=" + YtPlatform.PLATFORM_WECHAT.getAppId() + "&secret="
				+ YtPlatform.PLATFORM_WECHAT.getAppSecret() + "&code=" + code + "&grant_type=authorization_code";

		try {
			String result = getForResult(activity, url);

			if (result == null) {
				handler.sendEmptyMessage(FAIL);
				return;
			}

			JSONObject json = new JSONObject(result);

			if (json.has("errcode") && json.has("errmsg"))
				handler.sendEmptyMessage(FAIL);
			else {
				String accessToken = json.getString("access_token");
				String openId = json.getString("openid");
				getUserInfo(accessToken, openId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(FAIL);
		}
	}

	/**
	 * 通过Http请求获取微信个人信息
	 * 
	 * @param token
	 * @param openId
	 */
	public void getUserInfo(String token, String openId) {
		final String url = "https://api.weixin.qq.com/sns/userinfo" + "?access_token=" + token + "&openid=" + openId;

		try {
			String result = getForResult(activity, url);

			YtLog.d("wx : getUserInfo", result);
			if (result == null) {
				handler.sendEmptyMessage(FAIL);
				return;
			}

			JSONObject json = new JSONObject(result);
			// 获取用户信息失败
			if (json.has("errcode") && json.has("errmsg"))
				handler.sendEmptyMessage(FAIL);
			else {
				String nickname = new String(json.getString("nickname").getBytes("ISO8859_1"));
				String city = json.getString("city");
				String province = json.getString("province");
				String country = json.getString("country");
				String headimgurl = json.getString("headimgurl");
				String language = json.getString("language");

				// 1为男性，2为女性
				int sex = json.getInt("sex");

				String sexValue = "男";
				if (sex == 2)
					sexValue = "女";

				AuthUserInfo info = new AuthUserInfo();

				info.setWeChatUserInfoResponse(result);

				info.setWechatCity(city);
				info.setWechatNickName(nickname);
				info.setWechatProvince(province);
				info.setWechatCountry(country);
				info.setWechatSex(sexValue);
				info.setWechatLanguage(language);
				info.setWechatImageUrl(headimgurl);
				info.setWechatOpenId(openId);

				Util.dismissDialog();

				Message msg = new Message();
				msg.obj = info;
				msg.what = SUC;
				handler.sendMessage(msg);

			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(FAIL);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUC:
				if (listener != null)
					listener.onAuthSucess((AuthUserInfo) msg.obj);
				break;
			case FAIL:
				showFail();
				break;
			}

		};
	};

	private String getForResult(Context context, String url) throws Exception {
		HttpGet httpRequest = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(httpRequest);
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null)
			return EntityUtils.toString(resEntity);
		return null;
	}

	private void showFail() {
		if (listener != null)
			listener.onAuthFail();
		Util.dismissDialog();
	}

	/** 微信签名存在问题时，则会在WXEntryActivity很短时间执行两次 onResume方法 */
	private static long WECHAT_LAST_TIME = 0;

	public static void checkWeChatSign() {
		if (System.currentTimeMillis() - WECHAT_LAST_TIME < 400) {
			YtLog.e("YouTui",
					"code:1000;>>>If you use youtui official appid, copy debug.keystore(in demo project) to C:\\Users\\Administrator\\.android, restart eclipse, to run again."
							+ "If using yourself appid, please use the signature tool(GetSignature.apk), fill in the signature value to the open platform for signature application signature");
			return;
		}
		WECHAT_LAST_TIME = System.currentTimeMillis();
	}
}
