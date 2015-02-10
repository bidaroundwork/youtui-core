package cn.bidaround.ytcore.sina;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.bidaround.point.YtConstants;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.BaseAuth;
import cn.bidaround.ytcore.util.AppHelper;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

public class SinaWeiboAuth extends BaseAuth {

	/** 新浪accessToken */
	private Oauth2AccessToken oauth2AccessToken;

	/** 新浪微博授权类 */
	private WeiboAuth mWeiboAuth;

	/** 新浪微博sso授权类 */
	private SsoHandler mSsoHandler;

	private String sinaWbAccessToken;

	public SinaWeiboAuth(Activity activity, AuthListener listener) {
		super(activity, listener);
		platform = YtPlatform.PLATFORM_SINAWEIBO;
		init();
	}

	private void init() {
		mWeiboAuth = new WeiboAuth(context, platform.getAppId(), platform.getAppRedirectUrl(), YtConstants.SINA_WEIBO_SCOPE);
		if (AppHelper.isSinaWeiboExisted(context)) {
			mSsoHandler = new SsoHandler((Activity) context, mWeiboAuth);
			mSsoHandler.authorize(new SinaAuthListener());
		} else {
			mWeiboAuth.anthorize(new SinaAuthListener());
		}
	}

	/**
	 * 新浪微博授权监听
	 */
	class SinaAuthListener implements WeiboAuthListener {
		@Override
		public void onCancel() {
			sendCancel();
		}

		@Override
		public void onComplete(Bundle bundle) {

			oauth2AccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			if (oauth2AccessToken.isSessionValid()) {
				SinaAccessTokenKeeper.writeAccessToken(context, oauth2AccessToken);
			}
			/** 获取新浪微博用户信息 */
			new Thread() {
				public void run() {
					HttpClient client = new DefaultHttpClient();
					sinaWbAccessToken = oauth2AccessToken.getToken();
					String url = "https://api.weibo.com/2/users/show.json";
					url += "?" + "access_token=" + oauth2AccessToken.getToken();
					url += "&" + "uid=" + oauth2AccessToken.getUid();
					HttpGet get = new HttpGet(url);
					try {
						HttpResponse resp = client.execute(get);
						String str = EntityUtils.toString(resp.getEntity());

						/** 解析获取的用户信息并赋值给保存用户信息的字段 */
						JSONObject sinaJson = new JSONObject(str);
						userInfo.setSinaAccessToken(sinaWbAccessToken);
						userInfo.setSinaUid(sinaJson.getString("id"));
						userInfo.setSinaScreenname(sinaJson.getString("screen_name"));
						userInfo.setSinaProfileImageUrl(sinaJson.getString("profile_image_url"));
						userInfo.setSinaUserInfoResponse(str);
						userInfo.setSinaGender(sinaJson.getString("gender"));
						userInfo.setSinaName(sinaJson.getString("name"));

						sendSuccess();
					} catch (Exception e) {
						sendFail();
					}
				};
			}.start();
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			sendFail();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
