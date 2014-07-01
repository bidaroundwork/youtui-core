package cn.bidaround.ytcore.data;

import cn.bidaround.point.ChannelId;
/**
 * 分享的平台信息
 * @author youtui
 * @since 14/4/21
 */
public enum YtPlatform {
	PLATFORM_SINAWEIBO, PLATFORM_TENCENTWEIBO, PLATFORM_QZONE, PLATFORM_WECHAT, PLATFORM_RENN, PLATFORM_QQ, PLATFORM_MESSAGE, PLATFORM_EMAIL, PLATFORM_WECHATMOMENTS, PLATFORM_MORE_SHARE,
	PLATFORM_COPYLINK;

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

}
