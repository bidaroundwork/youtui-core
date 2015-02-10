package cn.bidaround.ytcore.qq;

import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.BaseShare;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.util.YtLog;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 该类实现QQ,QZone分享和回调
 * 
 * @author youtui
 * @since 14/6/19
 */
public class QQOpenShare extends BaseShare{
	/** qq分享类 */
	private Tencent mTencent;
	
	/** 判断是qq分享还是qq空间分享 */
	private String flag;
	
	private YtPlatform platform;
	
	private String appName;

	public QQOpenShare(Activity act, String flag, YtShareListener listener, ShareData shareData) {
		super(act, shareData, listener);
		this.flag = flag;
		init(act);
	}

	/**
	 * 初始化，如果没有授权则进行登录授权
	 */
	private void init(Activity act) {
		// 获取appName
		ApplicationInfo info = null;
		try {
			info = act.getPackageManager().getApplicationInfo(act.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		appName = (String) act.getPackageManager().getApplicationLabel(info);
		if ("QQ".equals(flag)) {
			mTencent = Tencent.createInstance(YtPlatform.PLATFORM_QQ.getAppId(), act);
			platform = YtPlatform.PLATFORM_QQ;
		} else if ("Qzone".equals(flag)) {
			mTencent = Tencent.createInstance(YtPlatform.PLATFORM_QZONE.getAppId(), act);
			platform = YtPlatform.PLATFORM_QZONE;
		}
	}

	/**
	 * 分享到qq
	 */
	public void shareToQQ() {
		if(shareData!=null){
			Bundle params = new Bundle();
			if(shareData.getShareType()==ShareData.SHARETYPE_IMAGEANDTEXT){
				//图文分享
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);			
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,shareData.getTargetUrl());
				YtLog.w("shareToQQ", shareData.getTargetUrl());
				// 判断传输的是网络图片还是本地图片
				if (shareData.getImagePath() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareData.getImageUrl());
				} 				
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
				params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);		
			}else if(shareData.getShareType()==ShareData.SHARETYPE_IMAGE){
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
				if (shareData.getImagePath() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareData.getImageUrl());
				} 
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);				
			}else if(shareData.getShareType()==ShareData.SHARETYPE_TEXT){
				//图文分享
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);			
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,shareData.getTargetUrl());
				// 判断传输的是网络图片还是本地图片
				if (shareData.getImagePath() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareData.getImageUrl());
				} 				
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
				params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
			}else if(shareData.getShareType()==ShareData.SHARETYPE_MUSIC){
				//音乐分享
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);			
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,shareData.getTargetUrl());
				// 判断传输的是网络图片还是本地图片
				if (shareData.getImagePath() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareData.getImageUrl());
				} 	
				params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, shareData.getMusicUrl());
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
				params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
			}
			
			mTencent.shareToQQ(activity, params, new MyQQShareUIListener());
		}
	}


	/**
	 * 分享到qq空间
	 */
	public void shareToQzone() {
		if(shareData!=null){
			Bundle params = new Bundle();
			if(shareData.getShareType()==ShareData.SHARETYPE_IMAGEANDTEXT){
				//QQ空间只支持图文分享
				if(shareData.getShareType()==ShareData.SHARETYPE_IMAGEANDTEXT){
					
					params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);

					params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
					params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
					params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.getTargetUrl());
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);

					ArrayList<String> list = new ArrayList<String>();
					// 判断传输的是网络图片还是本地图片
					 if (shareData.getImageUrl() != null) {
						list.add(shareData.getImageUrl());
					}else if (shareData.getImagePath() != null) {
						list.add(shareData.getImagePath());
					} 
					params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list);
				}
			}else if(shareData.getShareType()==ShareData.SHARETYPE_IMAGE){
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.getTargetUrl()); 
				//YtLog.d("shareToQzone shareData.getTargetUrl()", shareData.getTargetUrl());
				ArrayList<String> list = new ArrayList<String>();
				// 判断传输的是网络图片还是本地图片
				 if (shareData.getImageUrl() != null) {
					list.add(shareData.getImageUrl());
				}else if (shareData.getImagePath() != null) {
					list.add(shareData.getImagePath());
				} 
				params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list);
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
			}else if(shareData.getShareType()==ShareData.SHARETYPE_TEXT){
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.getTargetUrl());
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
				
				ArrayList<String> list = new ArrayList<String>();
				// 判断传输的是网络图片还是本地图片
				if (shareData.getImagePath() != null) {
					list.add(shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					list.add(shareData.getImageUrl());
				}
				params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list);
			}
			mTencent.shareToQzone(activity, params, new MyQQShareUIListener());
		}
	}

	/**
	 * 分享回调
	 */
	class MyQQShareUIListener implements IUiListener {

		@Override
		public void onCancel() {
			if (listener != null) 
				listener.onCancel(platform);
		}

		@Override
		public void onComplete(Object obj) {
			YtShareListener.sharePoint(activity, platform.getChannleId(), !shareData.isAppShare());
			if (listener != null)
				listener.onSuccess(platform, obj.toString());
		}

		@Override
		public void onError(UiError error) {
			if (listener != null)
				listener.onError(platform, error.errorMessage);
		}
	}
}
