package cn.bidaround.ytcore.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

/**
 * 读取保存在youtui_sdk.xml下的保存的key信息以便于后续使用
 * 
 * @author youtui
 * @since 14/4/21
 */
public class KeyInfo {
	/** 友推AppKey */
	public static String youTui_AppKey;
	/**新浪微博是否不设置key进行分享*/
	public static String sinaWeibo_IsNoKeyShare;
	/** 新浪微博AppKey */
	public static String sinaWeibo_AppKey;
	/** 新浪微博微博Appsecret */
	public static String sinaWeibo_AppSecret;
	/** 新浪微博Enable属性 */
	public static String sinaWeibo_Enable;
	/** 新浪微博RedirectUrl */
	public static String sinaWeibo_RedirectUrl;
	/** 腾讯微博AppKey */
	public static String tencentWeibo_AppKey;
	/** 腾讯微博AppSecret */
	public static String tencentWeibo_AppSecret;
	/** 腾讯微博Enable */
	public static String tencentWeibo_Enable;
	/** 腾讯微博RedirectUrl */
	public static String tencentWeibo_RedirectUrl;
	/** qq空间AppKey */
	public static String qZone_AppKey;
	/** qq空间AppId */
	public static String qZone_AppId;
	/** qq空间Enable */
	public static String qZone_Enable;
	/** 微信AppId */
	public static String wechat_AppId;
	/** 微信Enable */
	public static String wechat_Enable;
	/** 微信朋友圈AppId */
	public static String wechatMoments_AppId;
	/** 微信朋友圈Enable */
	public static String WechatMoments_Enable;
	/** qq AppKey */
	public static String qQ_AppKey;
	/** qq AppId */
	public static String qQ_AppId;
	/** qq Enable */
	public static String qQ_Enable;
	/** 人人网AppKey */
	public static String renren_AppKey;
	/** 人人网AppId */
	public static String renren_AppId;
	/** 人人网Enable */
	public static String renren_Enable;
	/** 人人网SecretKey */
	public static String renren_SecretKey;
	/** 邮件 Enable */
	public static String email_Enable;
	/** 短信Enable */
	public static String shortMessage_Enable;
	/** 更多分享Enable */
	public static String more_Enable;
	/**复制链接*/
	public static String copyLink;
	
	public static String screenCap_Enable;

	public static ArrayList<String> enList = new ArrayList<String>();

	/**
	 * 从youtui_sdk.xml文件中解析开发者配置的平台信息
	 * 
	 * @param context
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static void parseXML(Context context) throws IOException, XmlPullParserException {
		enList.clear();
		InputStream in = context.getResources().getAssets().open("youtui_sdk.xml");
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(in, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (parser.getEventType()) {
			case XmlPullParser.START_TAG:
				String tag = parser.getName();
				if ("YouTui".equals(tag)) {
					// 设置友推 AppKey
					youTui_AppKey = parser.getAttributeValue(null, "AppKey");
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHAT).equals(tag)) {
					// 设置微信AppId和Enable
					wechat_AppId = parser.getAttributeValue(null, "AppId");
					wechat_Enable = parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHAT));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHATMOMENTS).equals(tag)) {
					// 设置朋友圈AppId和Enable
					wechatMoments_AppId = parser.getAttributeValue(null, "AppId");
					WechatMoments_Enable = parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHATMOMENTS));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_SINAWEIBO).equals(tag)) {
					// 设置新浪微博AppSecret,Enable,RedirectUrl
					sinaWeibo_AppKey = parser.getAttributeValue(null, "AppKey");
					sinaWeibo_AppSecret = parser.getAttributeValue(null, "AppSecret");
					sinaWeibo_Enable = parser.getAttributeValue(null, "Enable");
					sinaWeibo_RedirectUrl = parser.getAttributeValue(null, "RedirectUrl");
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_SINAWEIBO));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QQ).equals(tag)) {
					// 设置QQ AppId,AppKey,Enable
					qQ_AppId = parser.getAttributeValue(null, "AppId");
					qQ_AppKey = parser.getAttributeValue(null, "AppKey");
					qQ_Enable = parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QQ));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QZONE).equals(tag)) {
					// 设置QZone AppId,AppKey,Enable
					qZone_AppId = parser.getAttributeValue(null, "AppId");
					qZone_AppKey = parser.getAttributeValue(null, "AppKey");
					qZone_Enable = parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QZONE));
					}

				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_TENCENTWEIBO).equals(tag)) {
					// 设置TencentWeibo AppId,AppKey,Enable
					tencentWeibo_AppKey = parser.getAttributeValue(null, "AppKey");
					tencentWeibo_AppSecret = parser.getAttributeValue(null, "AppSecret");
					tencentWeibo_Enable = parser.getAttributeValue(null, "Enable");
					tencentWeibo_RedirectUrl = parser.getAttributeValue(null, "RedirectUrl");
					//如果是无key分享,默认使用友推的腾讯微博key
					if(parser.getAttributeValue(null, "IsNoKeyShare")!=null&&parser.getAttributeValue(null, "IsNoKeyShare").contains("true")){
						tencentWeibo_AppKey = "801443192";
						tencentWeibo_AppSecret = "45d65f2d2650637c96ece74f4a67b686";
						tencentWeibo_RedirectUrl = "http://yt.bidaround.cn/";
					}
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_TENCENTWEIBO));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_RENN).equals(tag)) {
					// 设置人人AppId,AppKey,Enable
					renren_AppId = parser.getAttributeValue(null, "AppId");
					renren_AppKey = parser.getAttributeValue(null, "AppKey");
					renren_Enable = parser.getAttributeValue(null, "Enable");
					renren_SecretKey = parser.getAttributeValue(null, "SecretKey");
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_RENN));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MESSAGE).equals(tag)) {
					// 短信Enable
					shortMessage_Enable = parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MESSAGE));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_EMAIL).equals(tag)) {
					// 邮箱Enable
					email_Enable = parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_EMAIL));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MORE_SHARE).equals(tag)) {
					//更多Enable
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MORE_SHARE));
					}
				} else if(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_COPYLINK).equals(tag)){
					//复制链接Enable
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_COPYLINK));
					}
				}else if(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_SCREENCAP).equals(tag)){
					//截屏Enable
					if (parser.getAttributeValue(null, "Enable")!=null&&parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_SCREENCAP));
					}
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
	}	
	/**
	 * 根据key获取youtui_sdk.xml文件中的value
	 * @param context
	 * @param paltform
	 * @param key
	 * @return 获取的value
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static String getKeyValue(Context context, YtPlatform paltform, String key) {
		String value = null;
		InputStream in;
		try {
			in = context.getResources().getAssets().open("youtui_sdk.xml");
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(in, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					String tag = parser.getName();
					if (YtPlatform.getPlatfornName(paltform).equals(tag)) {
						value = parser.getAttributeValue(null, key);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				default:
					break;
				}
				eventType = parser.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return value;
	}
}
