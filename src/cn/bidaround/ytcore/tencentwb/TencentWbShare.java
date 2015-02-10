package cn.bidaround.ytcore.tencentwb;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.BaseShare;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.AuthLogin;
import cn.bidaround.ytcore.login.AuthUserInfo;
import cn.bidaround.ytcore.util.Util;

import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.SharePersistent;
import com.tencent.weibo.sdk.android.model.ModelResult;
import com.tencent.weibo.sdk.android.network.HttpCallback;

/**
 * 腾讯微博分享
 * @author youtui
 * @since 14/5/8
 */
public class TencentWbShare extends BaseShare {
	private YtPlatform platform = YtPlatform.PLATFORM_TENCENTWEIBO;

	public TencentWbShare(Activity activity, YtShareListener listener, ShareData shareData) {
		super(activity, shareData, listener);
	}

	/**
	 * 分享到腾讯微博
	 */
	public void shareToTencentWb() {
		// 分享到腾讯微博
		if (shareData.getShareType() == ShareData.SHARETYPE_MUSIC || shareData.getShareType() == ShareData.SHARETYPE_VIDEO) {
			Toast.makeText(YtCore.getAppContext(), "腾讯微博不支持音乐和视频分享", Toast.LENGTH_SHORT).show();
			return;
		}

		// 如果腾讯微博授权过期,先获取授权
		if (isTencentWbAuthExpired())
			doAuth();
		else
			doShare();

	}

	/**
	 * 判断腾讯微博授权是否过期
	 */
	private boolean isTencentWbAuthExpired() {
		boolean expired = true;
		SharedPreferences preference = activity.getSharedPreferences("ANDROID_SDK", 0);
		String authorizeTimeStr = preference.getString("AUTHORIZETIME", null);
		String expiresTime = preference.getString("EXPIRES_IN", null);
		long currentTime = System.currentTimeMillis() / 1000;
		if (expiresTime != null && expiresTime != "" && authorizeTimeStr != null && authorizeTimeStr != "") {
			if ((Long.valueOf(authorizeTimeStr) + Long.valueOf(expiresTime)) > currentTime) {
				expired = false;
			}
		}
		return expired;
	}

	private void doAuth() {
		AuthLogin tencentWbLogin = new AuthLogin();
		AuthListener tencentWbListener = new AuthListener() {
			@Override
			public void onAuthSucess(AuthUserInfo userInfo) {
				doShare();
			}

			@Override
			public void onAuthFail() {
				Util.dismissDialog();
			}

			@Override
			public void onAuthCancel() {
				Util.dismissDialog();
			}
		};
		tencentWbLogin.tencentWbAuth(YtCore.getAppContext(), tencentWbListener);
	}

	private void doShare() {
		WeiboAPI weibo = new WeiboAPI(SharePersistent.getInstance().getAccount(activity));
		if (shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT || shareData.getShareType() == ShareData.SHARETYPE_IMAGE) {
			Bitmap bm = BitmapFactory.decodeFile(shareData.getImagePath());
			String text = shareData.getText();
			// 如果腾讯微博分享文字过长，截取前面内容和跳转url
			if (shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
				if (text.length() > 110) {
					text = text.substring(0, 109);
					text += "...";
				}
				if (shareData.getTargetUrl() != null && !"".equals(shareData.getTargetUrl()) && !"null".equals(shareData.getTargetUrl())) {
					text += shareData.getTargetUrl();
				}
			} else if (shareData.getShareType() == ShareData.SHARETYPE_IMAGE) {
				text = "";
			}

			if (bm == null) {
				Toast.makeText(activity, activity.getResources().getString(activity.getResources()
						.getIdentifier("yt_nopic", "string", activity.getPackageName())), Toast.LENGTH_SHORT).show();
				Util.dismissDialog();
			} else {
				weibo.addPic(activity, text, "json", 0d, 0d, bm, -1, 0, mCallBack, null, 4);
			}
		} else if (shareData.getShareType() == ShareData.SHARETYPE_TEXT) {
			String text = shareData.getText();
			// 如果腾讯微博分享文字过长，截取前面内容和跳转url
			if (text.length() > 110) {
				text = text.substring(0, 109);
				text += "...";
			}
			if (shareData.getTargetUrl() != null && !"".equals(shareData.getTargetUrl()) && !"null".equals(shareData.getTargetUrl())) {
				text += shareData.getTargetUrl();
			}
			weibo.addWeibo(activity, text, "json", 0d, 0d, -1, 0, mCallBack, null, 4);
		}
	}

	/**
	 * 腾讯微博分享回调
	 */
	private HttpCallback mCallBack = new HttpCallback() {
		@Override
		public void onResult(Object object) {
			ModelResult result = (ModelResult) object;
			if (result != null && result.isSuccess()) {
				YtShareListener.sharePoint(activity, platform.getChannleId(), !shareData.isAppShare());
				if (listener != null)
					listener.onSuccess(platform, result.getError_message());
			} else {
				if (listener != null) {
					String errorMessage = null;
					if (result != null)
						errorMessage = result.getError_message();
					listener.onError(platform, errorMessage);
				}
			}
			Util.dismissDialog();
		}
	};
};
