package cn.bidaround.ytcore.data;

import cn.bidaround.point.ChannelId;
/**
 * 分享的平台信息
 * @author youtui
 * @since 14/4/21
 */
public enum YtPlatform {
	PLATFORM_SINAWEIBO, PLATFORM_TENCENTWEIBO, PLATFORM_QZONE, PLATFORM_WECHAT, PLATFORM_RENN, PLATFORM_QQ, PLATFORM_MESSAGE, PLATFORM_EMAIL, PLATFORM_WECHATMOMENTS, PLATFORM_MORE_SHARE,
	PLATFORM_COPYLINK,PLATFORM_SCREENCAP,PLATFORM_QRCORE;
	/**平台类型为微信，QQ，人人，新浪微博，腾讯微博等社交平台*/
	public static final int PLATFORMTYPE_SOCIAL = 0;
	/**平台类型为短信，邮件，更多等系统分享*/
	public static final int PLATFORMTYPE_SYSTEM = 1;
	/**平台类型为截屏,复制链接等工具*/
	public static final int PLATFORMTYPE_UTIL = 2;
	/** 通过平台ID获取平台名字，如果没有该ID则返回null */
	public static String getPlatfornName(YtPlatform platform) {
		switch (platform) {
		case PLATFORM_SINAWEIBO:
			return "SinaWeibo";
		case PLATFORM_TENCENTWEIBO:
			return "TencentWeibo";
		case PLATFORM_QZONE:
			return "QZone";
		case PLATFORM_WECHAT:
			return "Wechat";
		case PLATFORM_RENN:
			return "Renren";
		case PLATFORM_QQ:
			return "QQ";
		case PLATFORM_MESSAGE:
			return "ShortMessage";
		case PLATFORM_EMAIL:
			return "Email";
		case PLATFORM_MORE_SHARE:
			return "More";
		case PLATFORM_WECHATMOMENTS:
			return "WechatMoments";
		case PLATFORM_COPYLINK:
			return "CopyLink";
		case PLATFORM_SCREENCAP:
			return "ScreenCap";
		case PLATFORM_QRCORE:
			return "QRCode";
		default:
			break;
		}
		return null;
	}
	/**
	 * 获取平台的ChannleId
	 * @return 平台的ChannleId
	 */
	public int getChannleId(){
		switch (this) {
		case PLATFORM_SINAWEIBO:
			return ChannelId.SINAWEIBO;
		case PLATFORM_TENCENTWEIBO:
			return ChannelId.TENCENTWEIBO;
		case PLATFORM_QZONE:
			return ChannelId.QZONE;
		case PLATFORM_WECHAT:
			return ChannelId.WECHAT;
		case PLATFORM_RENN:
			return ChannelId.RENN;
		case PLATFORM_QQ:
			return ChannelId.QQ;
		case PLATFORM_MESSAGE:
			return ChannelId.MESSAGE;
		case PLATFORM_EMAIL:
			return ChannelId.EMAIL;
		case PLATFORM_MORE_SHARE:
			return ChannelId.MORE;
		case PLATFORM_WECHATMOMENTS:
			return ChannelId.WECHATFRIEND;
		case PLATFORM_COPYLINK:
			return ChannelId.COPYLINK;
		default:
			break;
		}
		return -1;
	}
	
	public static int getPlatformType(YtPlatform platform){
		if(platform==YtPlatform.PLATFORM_COPYLINK||platform==PLATFORM_SCREENCAP||platform==PLATFORM_QRCORE){
			return PLATFORMTYPE_UTIL;
		}else if(platform==YtPlatform.PLATFORM_MESSAGE||platform==YtPlatform.PLATFORM_EMAIL||platform==PLATFORM_MORE_SHARE){
			return PLATFORMTYPE_SYSTEM;
		}else{
			return PLATFORMTYPE_SOCIAL;
		}
	}

}
