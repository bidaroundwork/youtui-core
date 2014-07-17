package cn.bidaround.ytcore.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import cn.bidaround.point.ChannelId;
import cn.bidaround.point.YoutuiConstants;
import cn.bidaround.point.YtLog;
import cn.bidaround.ytcore.ErrorInfo;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.activity.ShareActivity;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.util.AccessTokenKeeper;
import cn.bidaround.ytcore.util.Util;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

/**
 * 使用于开发者未设置新浪微博key的情况下进行授权,使用的key为友推提供
 * 
 * @author youtui
 * @since 14/6/26
 */
public class SinaNoKeyShare {
	private WeiboAuth mWeiboAuth;
	private final String REDIRECT_URI = "http://youtui.mobi/weiboResponse";
	private final String CLIENT_ID = "2502314449";
	private final String CLIENT_SECRET = "df10502d7b422937adea431cd985904a";
	private String realUrl;
	private String shortUrl;
	private String mCode;
	private Activity act;
	/** 通过 code 获取 Token 的 URL */
	private final String OAUTH2_ACCESS_TOKEN_URL = "https://open.weibo.cn/oauth2/access_token";

	public void sinaAuth(Activity act, String realUrl, String shortUrl) {
		mWeiboAuth = new WeiboAuth(act, CLIENT_ID, REDIRECT_URI, YoutuiConstants.SINA_WEIBO_SCOPE);
		mWeiboAuth.authorize(new AuthListener(), WeiboAuth.OBTAIN_AUTH_CODE);
		this.act = act;
		this.realUrl = realUrl;
		this.shortUrl = shortUrl;
	}

	/**
	 * 微博认证授权回调类。
	 */
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			if (null == values) {
				return;
			}
			String code = values.getString("code");
			if (TextUtils.isEmpty(code)) {
				return;
			}
			mCode = code;
			Util.showProgressDialog(act, "加载中", false);
			fetchTokenAsync(mCode, CLIENT_SECRET);
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onWeiboException(WeiboException e) {
		}

	}

	/**
	 * 异步获取 Token。
	 * 
	 * @param authCode
	 *            授权 Code，该 Code 是一次性的，只能被获取一次 Token
	 * @param appSecret
	 *            应用程序的 APP_SECRET，请务必妥善保管好自己的 APP_SECRET，
	 *            不要直接暴露在程序中，此处仅作为一个DEMO来演示。
	 */
	public void fetchTokenAsync(String authCode, String appSecret) {

		WeiboParameters requestParams = new WeiboParameters();
		requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_ID, CLIENT_ID);
		requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_SECRET, appSecret);
		requestParams.put(WBConstants.AUTH_PARAMS_GRANT_TYPE, "authorization_code");
		requestParams.put(WBConstants.AUTH_PARAMS_CODE, authCode);
		requestParams.put(WBConstants.AUTH_PARAMS_REDIRECT_URL, REDIRECT_URI);

		// 异步请求，获取 Token
		AsyncWeiboRunner.requestAsync(OAUTH2_ACCESS_TOKEN_URL, requestParams, "POST", new RequestListener() {
			@Override
			public void onComplete(String response) {
				// 获取 Token 成功
				Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(response);
				if (token != null && token.isSessionValid()) {
					AccessTokenKeeper.writeAccessToken(act, token);
					YtLog.e("get AccessToken success", response);
					// 如果获取AccessToken成功,打开分享界面
					Intent it = new Intent(act, ShareActivity.class);
					it.putExtra("platform", YtPlatform.PLATFORM_SINAWEIBO);
					it.putExtra("sinaWeiboIsNoKeyShare", true);
					it.putExtra("realUrl", realUrl);
					it.putExtra("shortUrl", shortUrl);
					Util.dismissDialog();
					act.startActivity(it);
				} else {
					Util.dismissDialog();
					YtLog.e("get AccessToken fail", "Failed to receive access token");
				}
			}

			public void onWeiboException(WeiboException e) {
				Util.dismissDialog();
				YtLog.e("get AccessToken fail", "onWeiboException:" + e.getMessage());
			}
		});
	}

	/**
	 * 调用web方式进行新浪微博分享
	 * 
	 * @param context
	 * @param shareData
	 */
	public static void shareToSina(final Activity act, final ShareData shareData, final YtShareListener listener, final String realUrl, final String shortUrl) {
		WeiboParameters params = new WeiboParameters();
		params.put("access_token", AccessTokenKeeper.readAccessToken(act).getToken());
		// 添加新浪微博分享文字文字
		if (shareData != null) {
			String text = shareData.getText();
			if (shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
				// 如果文字太长，截取部分，不然微博无法发送
				if (text.length() > 110) {
					text = text.substring(0, 109);
					text += "...";
				}
				text += shareData.getTarget_url();
				params.put("status", text);
			}else if(shareData.getShareType()==ShareData.SHARETYPE_IMAGE){
				String picText = shareData.getText();
				picText = "分享图片";
				params.put("status", picText);
			}
		}
		// 添加新浪微博分享图片
		if (shareData != null && shareData.getImagePath() != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			params.put("pic", bitmap);
		}
		// 发送http请求进行分享
		AsyncWeiboRunner.requestAsync("https://upload.api.weibo.com/2/statuses/upload.json", params, "POST", new RequestListener() {
			@Override
			public void onWeiboException(WeiboException arg0) {
				Util.dismissDialog();
				if (listener != null) {
					ErrorInfo error = new ErrorInfo();
					error.setErrorMessage(arg0.getMessage());
					listener.onSuccess(error);
				}
				act.finish();
			}

			@Override
			public void onComplete(String arg0) {
				Util.dismissDialog();
				YtShareListener.sharePoint(act, KeyInfo.youTui_AppKey, ChannelId.SINAWEIBO, realUrl, !shareData.isAppShare, shortUrl);
				if (listener != null) {
					YtLog.e("listener", "!=null");
					ErrorInfo error = new ErrorInfo();
					error.setErrorMessage(arg0);
					listener.onError(error);
				}
				act.finish();
			}
		});
	}
}
