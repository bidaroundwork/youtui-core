package cn.bidaround.ytcore.social;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import cn.bidaround.point.ChannelId;
import cn.bidaround.ytcore.ErrorInfo;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.util.Util;

import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.SharePersistent;
import com.tencent.weibo.sdk.android.model.ModelResult;
import com.tencent.weibo.sdk.android.network.HttpCallback;

/**
 * 腾讯微博分享
 * 
 * @author youtui
 * @since 14/5/8
 */
public class TencentWbShare {
	/** 传入的activity */
	private Activity act;
	/** 短链接 */
	private String shortUrl;
	/** 真实网址 */
	private String realUrl;
	/** 分享监听 */
	private YtShareListener listener;
	/** 待分享数据 */
	private ShareData shareData;
	private Resources res;
	private String packName;

	public TencentWbShare(Activity act, YtShareListener listener, ShareData shareData) {
		this.listener = listener;
		this.act = act;
		this.shareData = shareData;
		shortUrl = act.getIntent().getExtras().getString("shortUrl");
		realUrl = act.getIntent().getExtras().getString("realUrl");
		res = act.getResources();
		packName = act.getPackageName();
	}

	/**
	 * 分享到腾讯微博
	 */
	public void shareToTencentWb() {
		WeiboAPI weibo = new WeiboAPI(SharePersistent.getInstance().getAccount(act));
		if (shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT || shareData.getShareType() == ShareData.SHARETYPE_IMAGE) {
			Bitmap bm = BitmapFactory.decodeFile(shareData.getImagePath());
			String text = shareData.getText();
			// 如果腾讯微博分享文字过长，截取前面内容和跳转url
			if (shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
				if (text.length() > 110) {
					text = text.substring(0, 109);
					text += "...";
				}
				if (shareData.getTarget_url() != null && !"".equals(shareData.getTarget_url()) && !"null".equals(shareData.getTarget_url())) {
					text += shareData.getTarget_url();
				}
			} else if (shareData.getShareType() == ShareData.SHARETYPE_IMAGE) {
				// text = "分享图片";
				text += res.getString(res.getIdentifier("yt_sharepic", "string", packName));
			}

			if (bm == null) {
				// Toast.makeText(act, "加载分享图片失败,请重新设置分享图片...",
				// Toast.LENGTH_SHORT).show();
				Toast.makeText(act, res.getString(res.getIdentifier("yt_nopic", "string", packName)), Toast.LENGTH_SHORT).show();
				Util.dismissDialog();
			} else {
				weibo.addPic(act, text, "json", 0d, 0d, bm, -1, 0, mCallBack, null, 4);
			}
		} else if (shareData.getShareType() == ShareData.SHARETYPE_TEXT) {
			String text = shareData.getText();
			// 如果腾讯微博分享文字过长，截取前面内容和跳转url

			if (text.length() > 110) {
				text = text.substring(0, 109);
				text += "...";
			}
			if (shareData.getTarget_url() != null && !"".equals(shareData.getTarget_url()) && !"null".equals(shareData.getTarget_url())) {
				text += shareData.getTarget_url();
			}

			weibo.addWeibo(act, text, "json", 0d, 0d, -1, 0, mCallBack, null, 4);
		}

	}

	/**
	 * 腾讯微博分享回调
	 */
	HttpCallback mCallBack = new HttpCallback() {
		@Override
		public void onResult(Object object) {
			ModelResult result = (ModelResult) object;
			if (result != null && result.isSuccess()) {
				YtShareListener.sharePoint(act, KeyInfo.youTui_AppKey, ChannelId.TENCENTWEIBO, realUrl, !shareData.isAppShare, shortUrl);
				if (listener != null) {
					ErrorInfo error = new ErrorInfo();
					String errorMessage = result.getError_message();
					error.setErrorMessage(errorMessage);
					listener.onSuccess(error);
				}
				act.finish();
			} else {
				if (listener != null) {
					ErrorInfo error = new ErrorInfo();
					String errorMessage = null;
					if (result != null) {
						errorMessage = result.getError_message();
					} else {
						errorMessage = null;
					}
					error.setErrorMessage(errorMessage);
					listener.onError(error);
				}
				act.finish();
			}
		}
	};

};
