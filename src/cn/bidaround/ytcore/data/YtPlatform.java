package cn.bidaround.ytcore.data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;

import cn.bidaround.point.ChannelId;
import cn.bidaround.ytcore.util.Constant;

/**
 * 分享的平台信息
 * 
 * @author youtui
 * @since 14/4/21 2015/1/12优化
 */
public enum YtPlatform {

	/**
	 * 平台命名规则： PLATFORM + "_" + youtui_sdk.xml的平台名称大写
	 */
	PLATFORM_WECHAT, 
	PLATFORM_WECHATMOMENTS, 
	PLATFORM_WECHATFAVORITE, 
	PLATFORM_QQ, 
	PLATFORM_QZONE, 
	PLATFORM_SINAWEIBO, 
	PLATFORM_TENCENTWEIBO, 
	PLATFORM_YIXIN, 
	PLATFORM_YIXINFRIENDS, 
	PLATFORM_KAIXIN, 
	PLATFORM_RENREN, 
	PLATFORM_SHORTMESSAGE, 
	PLATFORM_EMAIL, 
	PLATFORM_COPYLINK, 
	PLATFORM_SCREENCAP, 
	PLATFORM_MORE;

	/** 平台类型为微信，QQ，人人，新浪微博，腾讯微博等社交平台 */
	public static final int PLATFORMTYPE_SOCIAL = 0;

	/** 平台类型为短信，邮件，更多等系统分享 */
	public static final int PLATFORMTYPE_SYSTEM = 1;

	/** 平台类型为截屏,复制链接等工具 */
	public static final int PLATFORMTYPE_UTIL = 2;

	private static Map<YtPlatform, String> platMap = new HashMap<YtPlatform, String>();

	/** 通过平台ID获取平台名字，如果没有该ID则返回null */
	public static String getPlatformName(YtPlatform platform) {
		if (platMap.containsKey(platform))
			return platMap.get(platform);
		return platform.toString().toLowerCase(Locale.US).split("platform_")[1];
	}

	/** 通过平台名字获取平台对象 */
	public static YtPlatform getPlatformByName(String platform) {
		for (YtPlatform p : YtPlatform.values()) {
			if (p.getName().equalsIgnoreCase(platform))
				return p;
		}
		return null;
	}

	public String getName() {
		return getPlatformName(this);
	}
	
	public String getTitleName(Context context) {
		return context.getString(context.getResources().getIdentifier("yt_" + getName(), "string", context.getPackageName()));
	}

	/**
	 * 获取平台的ChannleId
	 * 
	 * @return 平台的ChannleId
	 */
	public int getChannleId() {
		String platName = this.toString().toLowerCase(Locale.US)
				.split("platform_")[1];
		for (ChannelId channelId : ChannelId.values()) {
			String name = channelId.toString();
			int index = name.lastIndexOf("_");
			if (name.substring(0, index).equalsIgnoreCase(platName)) {
				return Integer.parseInt(name.substring(index + 1));
			}
		}
		return -1;
	}

	public static int getPlatformType(YtPlatform platform) {
		if (platform == YtPlatform.PLATFORM_COPYLINK
				|| platform == PLATFORM_SCREENCAP)
			return PLATFORMTYPE_UTIL;
		else if (platform == YtPlatform.PLATFORM_SHORTMESSAGE
				|| platform == YtPlatform.PLATFORM_EMAIL
				|| platform == PLATFORM_MORE)
			return PLATFORMTYPE_SYSTEM;
		else
			return PLATFORMTYPE_SOCIAL;

	}

	public String getAppId() {
		return getKeyInfor(this.getName() + Constant.APPID);
	}

	public String getAppKey() {
		return getKeyInfor(this.getName() + Constant.APPKEY);
	}

	public String getAppSecret() {
		return getKeyInfor(this.getName() + Constant.APPSECRET);
	}

	public String getEnable() {
		return getKeyInfor(this.getName() + Constant.ENABLE);
	}

	public String getAppRedirectUrl() {
		return getKeyInfor(this.getName() + Constant.REDIRECTURL);
	}

	private static String getKeyInfor(String name) {
		if (KeyInfo.KeyInforMap.containsKey(name)) {
			String value = KeyInfo.KeyInforMap.get(name);
			if (value != null)
				return value;
		}
		return "";
	}
}
