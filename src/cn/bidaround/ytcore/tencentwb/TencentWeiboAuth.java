package cn.bidaround.ytcore.tencentwb;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import cn.bidaround.point.YtConstants;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.BaseAuth;
import cn.bidaround.ytcore.social.WebDialog;
import cn.bidaround.ytcore.social.WebDialog.OnAfterRequest;

import com.tencent.weibo.sdk.android.api.util.Util;

/**
 * 腾讯微博处理类
 * 
 * @author youtui
 * @since 2015/1/22
 */
public class TencentWeiboAuth extends BaseAuth {

	/** 让dialog显示网络连接情况 */
	public static final int ALERT_NETWORK = 4;

	private String url;

	private String tencentWbAccessToken;

	public TencentWeiboAuth(Activity activity, AuthListener listener) {
		super(activity, listener);
		platform = YtPlatform.PLATFORM_TENCENTWEIBO;
		initTencentWb();
	}

	private void initTencentWb() {
		int state = (int) Math.random() * 1000 + 111;
		url = "https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id=" + platform.getAppId() + "&response_type=token&redirect_uri="
				+ platform.getAppRedirectUrl() + "&state=" + state;
		showDialog();
	}

	/**
	 * 初始化界面使用控件，并设置相应监听
	 * */
	@SuppressLint("SetJavaScriptEnabled")
	public void showDialog() {
		new WebDialog(context, url, platform.getAppRedirectUrl(), listener, new OnAfterRequest() {

			@Override
			public void onAfterRequest(Bundle bundle) {
				jumpResultParser(bundle);
			}
		}).show();
	}

	/**
	 * 
	 * 获取授权后的返回地址，并对其进行解析
	 */
	private void jumpResultParser(Bundle bundle) {
		String accessToken = bundle.getString("access_token");
		String expiresIn = bundle.getString("expires_in");
		String openid = bundle.getString("openid");
		String openkey = bundle.getString("openkey");
		String refreshToken = bundle.getString("refresh_token");
		String name = bundle.getString("name");
		String nick = bundle.getString("nick");
		if (accessToken != null && !"".equals(accessToken)) {
			Util.saveSharePersistent(context, "ACCESS_TOKEN", accessToken);
			Util.saveSharePersistent(context, "EXPIRES_IN", expiresIn);
			Util.saveSharePersistent(context, "OPEN_ID", openid);
			Util.saveSharePersistent(context, "OPEN_KEY", openkey);
			Util.saveSharePersistent(context, "REFRESH_TOKEN", refreshToken);
			Util.saveSharePersistent(context, "NAME", name);
			Util.saveSharePersistent(context, "NICK", nick);
			Util.saveSharePersistent(context, "CLIENT_ID", platform.getAppId());
			Util.saveSharePersistent(context, "AUTHORIZETIME", String.valueOf(System.currentTimeMillis() / 1000l));

			userInfo.setTencentWbNick(nick);
			userInfo.setTencentWbName(name);
			userInfo.setTencentWbOpenid(openid);
			tencentWbAccessToken = accessToken;

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String result = getTencentEx();
						JSONObject json = new JSONObject(result).getJSONObject("data");

						String birth_day = json.getString("birth_day");
						String birth_month = json.getString("birth_month");
						String birth_year = json.getString("birth_year");

						if (!TextUtils.isEmpty(birth_day) && !TextUtils.isEmpty(birth_month) && !TextUtils.isEmpty(birth_year))
							userInfo.setTencentWbBirthday(birth_year + "-" + birth_month + "-" + birth_day);

						userInfo.setTencentWbHead(json.getString("https_head"));
						userInfo.setTencentWbGender(json.getString("sex"));
						userInfo.setTencentUserInfoResponse(result);

						sendSuccess();
					} catch (Exception e) {
						sendFail();
					}

				}
			}).start();
		}
	}

	/**
	 * 查询详细的数据
	 */
	private String getTencentEx() {
		String url = "http://open.t.qq.com/api/user/info?format=json" + "&openid=" + userInfo.getTencentWbOpenid() + "&oauth_consumer_key="
				+ YtPlatform.PLATFORM_TENCENTWEIBO.getAppId() + "&access_token=" + tencentWbAccessToken + "&clientip="
				+ Util.getLocalIPAddress(context) + "&oauth_version=2.a&scope=" + YtConstants.TENCENT_SCOPE;
		String ret = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse resp = client.execute(get);
			ret = EntityUtils.toString(resp.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
