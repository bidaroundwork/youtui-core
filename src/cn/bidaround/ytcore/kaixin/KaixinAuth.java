package cn.bidaround.ytcore.kaixin;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.webkit.CookieSyncManager;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.BaseAuth;
import cn.bidaround.ytcore.social.WebDialog;
import cn.bidaround.ytcore.social.WebDialog.OnAfterRequest;

public class KaixinAuth extends BaseAuth{
	private String mUrl;
	
	private Kaixin mKaixin = Kaixin.getInstance();
	
	// 解析网址会执行多次
	private boolean isExcute = false;
	
	public KaixinAuth(Context context, AuthListener listener, String url){
		super(context, listener);
		platform = YtPlatform.PLATFORM_KAIXIN;
		this.mUrl = url;
		init();
	}
	
	
	private void init(){
		new WebDialog(context, mUrl, Kaixin.KX_AUTHORIZE_CALLBACK_URL, listener, new OnAfterRequest() {
			
			@Override
			public void onAfterRequest(Bundle bundle) {
				
				if(isExcute) return;
				
				isExcute = true;
				
				jumpResultParser(bundle);
			}
		}).show();
	}
	
	public void jumpResultParser(Bundle bundle) {
		String error = bundle.getString("error");// 授权服务器返回的错误代码
		if (error != null) {
			if (Kaixin.ACCESS_DENIED.equalsIgnoreCase(error) || Kaixin.LOGIN_DENIED.equalsIgnoreCase(error)) {
				if(listener != null)
					listener.onAuthCancel();
			} else {
				if(listener != null)	
					listener.onAuthFail();
			}
			KaixinUtil.clearCookies(context);
		} 
		else 
			authComplete(bundle);
		
	}
	
	private void authComplete(Bundle values) {
		CookieSyncManager.getInstance().sync();
		String accessToken = values.getString(Kaixin.ACCESS_TOKEN);
		String refreshToken = values.getString(Kaixin.REFRESH_TOKEN);
		String expiresIn = values.getString(Kaixin.EXPIRES_IN);
		if (accessToken != null && refreshToken != null
				&& expiresIn != null) {
			try {
				mKaixin.writeAccessToken(context, accessToken, refreshToken, expiresIn);
				cn.bidaround.ytcore.util.Util.showProgressDialog(context, context.getResources().getString(
						context.getResources().getIdentifier("yt_authing", "string", context.getPackageName())), true);
				new Thread() {
					public void run() {
						getUserInfo();
					}
				}.start();
			} catch (Exception e) {
				sendFail();
			}
		} 
		else 
			sendFail();
	}
	
	/**
	 * http请求获取个人用户信息
	 * @param activity
	 * @param authListener
	 */
	private void getUserInfo(){
		Bundle bundle = new Bundle();
		bundle.putString("fields", "uid,name,gender,logo50,hometown,city");
		
		try {
			// 获取当前登录用户的资料
			String jsonResult = mKaixin.request(context, "/users/me.json", bundle, "GET");
			KaixinError kaixinError = parseRequestError(jsonResult);
			if (kaixinError == null) {
				JSONObject jsonObj = new JSONObject(jsonResult);
				
				userInfo.setKaixinUid(jsonObj.getString("uid"));
				userInfo.setKaixinName(jsonObj.getString("name"));
				userInfo.setKaixinGender("0".equals(jsonObj.getString("gender")) ? "男" : "女");
				userInfo.setKaixinImageUrl(jsonObj.getString("logo50"));
				userInfo.setKaixinHometown(jsonObj.getString("hometown"));
				userInfo.setKaixinCity(jsonObj.getString("city"));
				userInfo.setKaixinUserInfoResponse(jsonResult);
				sendSuccess();
			}
			else
				sendFail();
		} catch (Exception e) {
			sendFail();
		} 
	}
	
	
	private KaixinError parseRequestError(String response) {
		if (response.indexOf("error_code") < 0)
			return null;
		return parseJson(response);
	}

	private KaixinError parseJson(String response) {
		try {
			JSONObject json = new JSONObject(response);
			return new KaixinError(json.getInt("error_code"), json.optString(
					"error", ""), json.optString("request", ""), response);
		} catch (JSONException e) {
			return null;
		}
	}
}
