package cn.bidaround.ytcore.sina;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.BaseShare;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.AuthLogin;
import cn.bidaround.ytcore.login.AuthUserInfo;
import cn.bidaround.ytcore.util.Util;
import cn.bidaround.ytcore.util.YtLog;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

/**
 * 新浪微博分享操作以及分享回调
 * 
 * @author youtui
 * @since 14/4/25
 */
public class SinaHttpShare extends BaseShare{
	
	private YtPlatform platform = YtPlatform.PLATFORM_SINAWEIBO;
	
	public SinaHttpShare(Activity activity, ShareData shareData, YtShareListener listener) {
		super(activity, shareData, listener);
	}

	/**
	 * 发送共享到新浪微博
	 */
	public void shareToSina() {
		if (shareData.getShareType() == ShareData.SHARETYPE_MUSIC || shareData.getShareType() == ShareData.SHARETYPE_VIDEO) {
			Toast.makeText(activity, "新浪微博不支持音乐和视频分享", Toast.LENGTH_SHORT).show();
			return;
		}
		if (SinaAccessTokenKeeper.readAccessToken(activity).isSessionValid()) 
			doHTTPShare();
		// 未授权的情况
		else 
			sinaWebAuth();
	}

	/**
	 * 调用web方式进行新浪微博分享
	 */
	private void doHTTPShare() {
		if (shareData.getShareType() == ShareData.SHARETYPE_IMAGE || shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
			WeiboParameters params = new WeiboParameters();
			params.put("access_token", SinaAccessTokenKeeper.readAccessToken(activity).getToken());
			// 添加新浪微博分享文字文字
			if (shareData != null) {
				String text = shareData.getText();
				if (shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
					// 如果文字太长，截取部分，不然微博无法发送
					if (text.length() > 110) {
						text = text.substring(0, 109);
						text += "...";
					}
					if (shareData.getTargetUrl() != null && !"".equals(shareData.getTargetUrl()) && !"null".equals(shareData.getTargetUrl())) {
						text += shareData.getTargetUrl();
					}
					params.put("status", text);
				} else if (shareData.getShareType() == ShareData.SHARETYPE_IMAGE) {
					params.put("status", "图片分享");
				}
			}
			// 添加新浪微博分享图片
			if (shareData != null && shareData.getImagePath() != null) {
				Bitmap bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
				params.put("pic", bitmap);
			}
			YtLog.d("SinaNoKeyShare", "statuses/upload shareToSina");
			// 发送http请求进行分享
			AsyncWeiboRunner.requestAsync("https://upload.api.weibo.com/2/statuses/upload.json", params, "POST", new RequestListener() {
				@Override
				public void onWeiboException(WeiboException e) {
					Util.dismissDialog();
					if (listener != null)
						listener.onError(platform, e.getMessage());
				}

				@Override
				public void onComplete(String result) {
					Util.dismissDialog();
					YtShareListener.sharePoint(activity, YtPlatform.PLATFORM_SINAWEIBO.getChannleId(), !shareData.isAppShare());
					if (listener != null)
						listener.onSuccess(platform, result);
				}

			});
		} else if (shareData.getShareType() == ShareData.SHARETYPE_TEXT) {
			WeiboParameters params = new WeiboParameters();
			params.put("access_token", SinaAccessTokenKeeper.readAccessToken(activity).getToken());
			// 如果文字太长，截取部分，不然微博无法发送
			String text = shareData.getText();
			if (text.length() > 110) {
				text = text.substring(0, 109);
				text += "...";
			}
			if (shareData.getTargetUrl() != null && !"".equals(shareData.getTargetUrl()) && !"null".equals(shareData.getTargetUrl())) {
				text += shareData.getTargetUrl();
			}
			params.put("status", text);
			// 发送http请求进行分享
			AsyncWeiboRunner.requestAsync("https://api.weibo.com/2/statuses/update.json", params, "POST", new RequestListener() {
				@Override
				public void onWeiboException(WeiboException e) {
					Util.dismissDialog();
					if (listener != null)
						listener.onError(platform, e.getMessage());
				}

				@Override
				public void onComplete(String result) {
					Util.dismissDialog();
					YtShareListener.sharePoint(activity, YtPlatform.PLATFORM_SINAWEIBO.getChannleId(), !shareData.isAppShare());
					if (listener != null)
						listener.onSuccess(platform, result);
				}
			});
		}
	}

	/**
	 * 采用网页形式对新浪微博进行授权
	 */
	private void sinaWebAuth() {
		AuthLogin auth = new AuthLogin();
		AuthListener listener = new AuthListener() {
			
			@Override
			public void onAuthSucess(AuthUserInfo userInfo) {
				doHTTPShare();
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
		auth.sinaAuth(activity, listener);
	}
}
