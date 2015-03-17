package cn.bidaround.ytcore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.widget.Toast;
import cn.bidaround.point.YtPoint;
import cn.bidaround.ytcore.activity.SinaShareActivity;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.qq.QQOpenShare;
import cn.bidaround.ytcore.social.OtherShare;
import cn.bidaround.ytcore.util.AppHelper;
import cn.bidaround.ytcore.util.CMyEncrypt;
import cn.bidaround.ytcore.util.HttpUtils;
import cn.bidaround.ytcore.util.Util;
import cn.bidaround.ytcore.util.YtLog;
import cn.bidaround.ytcore.wxapi.WXEntryActivity;
import cn.bidaround.ytcore.yxapi.YixinShare;

/**
 * 友推分享操作类
 * 
 * @author youtui
 * @since 14/6/11 优化 2015/1/13
 */
public class YtCore {

	/** sim卡序列号 */
	public static String cardNum;
	
	/** android手机imei */
	public static String imei;
	
	/** 应用包名 */
	public static String packName;
	
	/** 应用资源 */
	public static Resources res;
	
	/** 应用AppContent */
	public static Context appContext;
	
	/** 实例 */
	public static YtCore core;

	private Activity activity;
	private String targetUrl;
	private int statisticsType = 1;
	private String linkUrl;

	/** 是否集成检测，检测丢失配置文件则会Toast提示 */
	private boolean isCheckConfig = false;

	/** 集成检测时是否有检查出错误信息 */
	private String checkConfigError = null;

	// 微信分享是否将内容设置成标题
	public static boolean isWxCircleTextAsTitle = false;

	/** 获取友推sdk的实例 */
	public static YtCore getInstance() {
		if (core == null)
			core = new YtCore();
		return core;
	}

	private YtCore() {

	}

	/** 分享到社交平台 */
	public void share(Activity act, YtPlatform platform, YtShareListener listener, ShareData shareData) {
		// 分享前操作
		this.activity = act;
		if (listener != null)
			listener.onPreShare(platform);
		
		if (checkShareContent(shareData, listener, platform))
			doShare(act, platform, listener, shareData);
	}

	/**
	 * 检查分享的内容是否已获得，如果没有获取到，获取分享信息 1、应用分享就从服务器读取应用配置的信息 2、不是应用分享，如果有网络图片就先下载图片
	 */
	private boolean checkShareContent(ShareData data, YtShareListener listener, YtPlatform platform) {
		if (data.isAppShare()) {
			if (TextUtils.isEmpty(data.getText()) && TextUtils.isEmpty(data.getTitle()))
				HttpUtils.getAppShareData(activity, data, listener, platform);
			else
				return true;
		} else {
			if (data.getShareType() != ShareData.SHARETYPE_TEXT && TextUtils.isEmpty(data.getImagePath())) {
				if (!TextUtils.isEmpty(data.getImageUrl())){
					HttpUtils.saveImage(activity, data, listener, platform);
				}
				else
					showToast("yt_nopic");
			} 
			else{
				return true;
			}
		}
		return false;
	}

	/**
	 * 跳转到分享页面
	 */
	public void doShare(Activity act, final YtPlatform platform, final YtShareListener listener, final ShareData oriData) {

		ShareData shareData = null;
		try {
			shareData = oriData.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return;
		}

		if (shareData == null)
			return;

		automaticShareType(shareData);
		
		String shortUrl = null;
		final String realUrl = shareData.getTargetUrl();
		if (!shareData.isAppShare() && shareData.getTargetUrl() != null && !shareData.getTargetUrl().equals("")) {
			shortUrl = CMyEncrypt.shortUrl(shareData.getTargetUrl());
			// 如果不是截屏，复制链接等平台，发送真实url和短链接
			if (YtPlatform.PLATFORMTYPE_UTIL != YtPlatform.getPlatformType(platform)){
				HttpUtils.sendUrl(platform.getChannleId(), shareData.getTargetUrl(), !shareData.isAppShare(), shortUrl, cardNum, 
						imei, shareData.getPublishTime(), shareData.getTitle(), shareData.getDescription(), shareData.getTargetId(), shareData.getImageUrl());
			}
		}
		
		
		// 处理url
		if (!TextUtils.isEmpty(shareData.getTargetUrl()))
			Util.dealWithUrl(platform.getChannleId(), shortUrl, linkUrl, statisticsType, shareData);

		// 易信
		if (platform == YtPlatform.PLATFORM_YIXIN) {
			
			if (AppHelper.isYixinExisted(act))
				new YixinShare(act, false, shareData, listener, realUrl, shortUrl, shareData.isAppShare()).shareToYixin();
			else
				showToast("yt_noyixinclient");
		}

		// 易信朋友圈
		else if (platform == YtPlatform.PLATFORM_YIXINFRIENDS) {
			if (AppHelper.isYixinExisted(act))
				new YixinShare(act, true, shareData, listener, realUrl, shortUrl, shareData.isAppShare()).shareToYixin();
			else
				showToast("yt_noyixinclient");
		}

		// 开心网
		else if (platform == YtPlatform.PLATFORM_KAIXIN) {
			showYtShareDialog(act, platform, listener, shareData, shortUrl, realUrl);
		}

		// 微信和朋友圈
		else if (platform == YtPlatform.PLATFORM_WECHAT || platform == YtPlatform.PLATFORM_WECHATMOMENTS
				|| platform == YtPlatform.PLATFORM_WECHATFAVORITE) {
			if (AppHelper.isWeixinExisted(act)) {
				try {
					Intent it = new Intent(act, Class.forName(packName + ".wxapi.WXEntryActivity"));
					WXEntryActivity.listener = listener;
					WXEntryActivity.platform = platform;
					WXEntryActivity.shareData = shareData;

					it.putExtra("shareData", shareData);
					it.putExtra("platform", platform);
					it.putExtra("fromShare", true);
					act.startActivity(it);
				} catch (ClassNotFoundException e) {
					YtLog.w("YouTui", e.getMessage());
				}
			} else
				showToast("yt_nowechatclient");

		}

		// 分享到Email
		else if (platform == YtPlatform.PLATFORM_EMAIL) {
			if (shareData.getTargetUrl() != null)
				new OtherShare(act).sendMail(shareData.getText() + shareData.getTargetUrl());
			else
				new OtherShare(act).sendMail(shareData.getText());
		}

		// 分享到短信
		else if (platform == YtPlatform.PLATFORM_SHORTMESSAGE) {
			if (shareData.getShareType() == ShareData.SHARETYPE_TEXT)
				new OtherShare(act).sendSMS(shareData);
			else
				new OtherShare(act).sendMMS(shareData);
		}

		// 更多分享
		else if (platform == YtPlatform.PLATFORM_MORE) {
			Util.openSystemShare(activity, shareData);
		}

		// 分享到腾讯微博
		else if (platform == YtPlatform.PLATFORM_TENCENTWEIBO) {
			showYtShareDialog(act, platform, listener, shareData, shortUrl, realUrl);
		}

		// 分享到qq和qq空间
		else if (platform == YtPlatform.PLATFORM_QQ) {
			if (AppHelper.isTencentQQExisted(act))
				new QQOpenShare(act, "QQ", listener, shareData).shareToQQ();
			else
				showToast("yt_noqqclient");
		}

		// 分享到qq和qq空间
		else if (platform == YtPlatform.PLATFORM_QZONE) {
			if (AppHelper.isTencentQQExisted(act))
				new QQOpenShare(act, "Qzone", listener, shareData).shareToQzone();
			else
				showToast("yt_noqqclient");
		}

		// 分享到新浪微博
		else if (platform == YtPlatform.PLATFORM_SINAWEIBO) {
			
			if(!Util.isSinaClientShare(act))
				showYtShareDialog(act, platform, listener, shareData, shortUrl, realUrl);
			else{
				Intent intent = new Intent(activity, SinaShareActivity.class);
				SinaShareActivity.listener = listener;
				SinaShareActivity.shareData = shareData;
				
				intent.putExtra("platform", platform);
				intent.putExtra("shortUrl", shortUrl);
				intent.putExtra("realUrl", realUrl);
				
				activity.startActivity(intent);
			}
		
		}

		// 分享到人人网
		else if (platform == YtPlatform.PLATFORM_RENREN) {
			if (AppHelper.isRenrenExisted(act))
				showYtShareDialog(act, platform, listener, shareData, shortUrl, realUrl);
			else
				showToast("yt_norennclient");
		}

		// 复制链接
		else if (platform == YtPlatform.PLATFORM_COPYLINK) {
			if (shareData.getTargetUrl() != null)
				Util.copyLink(act, shareData.getTargetUrl(), platform.getChannleId(), !shareData.isAppShare());
		}
	}
	
	private void showYtShareDialog(Activity act, YtPlatform platform, final YtShareListener listener, ShareData shareData, String shortUrl, String realUrl){
		
		final YtShareDialog dialog = new YtShareDialog(act, shareData, platform, shortUrl, realUrl);
		
		YtShareListener shareListener = new YtShareListener() {
			
			@Override
			public void onSuccess(YtPlatform platform, String result) {
				dialog.dismiss();
				if(listener != null)
					listener.onSuccess(platform, result);
			}
			
			@Override
			public void onPreShare(YtPlatform platform) {
				dialog.dismiss();
				if(listener != null)
					listener.onPreShare(platform);
			}
			
			@Override
			public void onError(YtPlatform platform, String error) {
				dialog.dismiss();
				if(listener != null)
					listener.onError(platform, error);
			}
			
			@Override
			public void onCancel(YtPlatform platform) {
				dialog.dismiss();
				if(listener != null)
					listener.onCancel(platform);
			}
		};
		dialog.setListener(shareListener);
		dialog.show();
	}
	

	/** ytcore初始化操作 */
	public static void init(final Activity act) {
		init(act, null);
	}

	/** ytcore初始化操作,有用户id */
	public static void init(final Activity act, final String appUserId) {
		getInstance();
		// 读取youtui_sdk.xml配置
		KeyInfo.parseXML(act);

		// 初始化积分组件
		new Thread() {
			@Override
			public void run() {
				YtPoint.init(act, KeyInfo.youTui_AppKey, KeyInfo.youTui_AppSecret, appUserId);
			}
		}.start();

		HttpUtils.getStatisticsType();

		packName = act.getPackageName();
		res = act.getResources();
		appContext = act.getApplicationContext();
		cardNum = Util.getSimNumber(act);
		imei = Util.getImei(act);
	}

	/** 设置是否输出YtLog信息,开发时输出有助于定位错误,正式打包时请关闭输出 */
	public static void showLog(boolean bool) {
		YtLog.showLog = bool;
	}

	/** 释放内存和统计应用使用情况 */
	public static void release(Context context) {
		YtPoint.release(context);
	}

	private void showToast(String strId) {
		Toast.makeText(activity, res.getString(res.getIdentifier(strId, "string", packName)), Toast.LENGTH_SHORT).show();
	}

	/** 该方法仅在isAppShare为true时使用 */
	public String getTargetUrl() {
		return targetUrl;
	}

	/** 获取应用的application */
	public static Context getAppContext() {
		return appContext;
	}

	public static void setAppContext(Context appContext) {
		YtCore.appContext = appContext;
	}

	public static void checkConfig(boolean isCheckConfig) {
		YtCore.getInstance().isCheckConfig = isCheckConfig;
	}

	public boolean isCheckConfig() {
		return isCheckConfig;
	}

	public void setCheckConfigHasError(String error) {
		YtCore.getInstance().checkConfigError = error;
	}

	public String getCheckConfigError() {
		return YtCore.getInstance().checkConfigError;
	}

	public void setStatisticsType(int statisticsType) {
		this.statisticsType = statisticsType;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	/**
	 * 如果用户未设置分享类型，就自动设置分享类型
	 */
	private void automaticShareType(ShareData shareData) {
		// 用户主动设置过分享类型，不再智能判断分享类型
		YtLog.e("YouTui", shareData.getShareType() + "");
		if (shareData.getShareType() < 0) {
			int type = -1;
			if (!TextUtils.isEmpty(shareData.getMusicUrl()))
				type = ShareData.SHARETYPE_MUSIC;
			else if (!TextUtils.isEmpty(shareData.getVideoUrl()))
				type = ShareData.SHARETYPE_VIDEO;
			else if (!TextUtils.isEmpty(shareData.getImage()) || !TextUtils.isEmpty(shareData.getImageUrl())
					|| !TextUtils.isEmpty(shareData.getImagePath())) {
				if (!TextUtils.isEmpty(shareData.getText()))
					type = ShareData.SHARETYPE_IMAGEANDTEXT;
				else
					type = ShareData.SHARETYPE_IMAGE;
			} else if (!TextUtils.isEmpty(shareData.getText()))
				type = ShareData.SHARETYPE_TEXT;
			if (type != -1)
				shareData.setShareType(type);
		}
	}

}
