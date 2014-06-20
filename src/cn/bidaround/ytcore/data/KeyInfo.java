package cn.bidaround.ytcore.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.bidaround.point.YtLog;
import android.content.Context;


/**
 * 读取保存在youtui_sdk.xml下的保存的key信息以便于后续使用
 * @author youtui
 * @since 14/4/21  
 */
public class KeyInfo {
	/**友推AppKey*/
	public static String youTui_AppKey;
	/**新浪微博AppKey*/
	public static String sinaWeibo_AppKey;
	/**新浪微博微博Appsecret*/
	public static String sinaWeibo_AppSecret;
	/**新浪微博Enable属性*/
	public static String sinaWeibo_Enable;
	/**新浪微博RedirectUrl*/
	public static String sinaWeibo_RedirectUrl;
	/**腾讯微博AppKey*/
	public static String tencentWeibo_AppKey;
	/**腾讯微博AppSecret*/
	public static String tencentWeibo_AppSecret;
	/**腾讯微博Enable*/
	public static String tencentWeibo_Enable;
	/**腾讯微博RedirectUrl*/
	public static String tencentWeibo_RedirectUrl;
	/**qq空间AppKey*/
	public static String qZone_AppKey;
	/**qq空间AppId*/
	public static String qZone_AppId;
	/**qq空间Enable*/
	public static String qZone_Enable;
	/**微信AppId*/
	public static String wechat_AppId;
	/**微信Enable*/
	public static String wechat_Enable;
	/**微信朋友圈AppId*/
	public static String wechatMoments_AppId;
	/**微信朋友圈Enable*/
	public static String WechatMoments_Enable;
	/**qq AppKey*/
	public static String qQ_AppKey;
	/**qq AppId*/
	public static String qQ_AppId;
	/**qq Enable*/
	public static String qQ_Enable;
	/**人人网AppKey*/
	public static String renren_AppKey;
	/**人人网AppId*/
	public static String renren_AppId;
	/**人人网Enable*/
	public static String renren_Enable;
	/**人人网SecretKey*/
	public static String renren_SecretKey;
	/**邮件 Enable*/
	public static String email_Enable;
	/**短信Enable*/
	public static String shortMessage_Enable;
	/**更多分享Enable*/
	public static String more_Enable;
	/**微信在分享列表中的位置*/
	public static int weChatIndex;
	/**朋友圈在分享列表中的位置*/
	public static int wechatMomentsIndex;
	/**新浪微博在分享列表中的位置*/
	public static int sinaWeiboIndex;
	/**qq在分享列表中的位置*/
	public static int qQIndex;
	/**qq空间在分享列表中的位置*/
	public static int qZoneIndex;
	/**腾讯微博在分享列表中的位置*/
	public static int tencentWeiboIndex; 
	/**人人网在分享列表中的位置*/
	public static int renrenIndex;
	/**短信在分享列表中的位置*/
	public static int shortMessageIndex; 
	/**邮件在分享列表中的位置*/
	public static int emailIndex;
	/**更多分享分享列表中的位置*/
	public static int moreIndex;
	/**用于添加需要分享的平台*/
	public static ArrayList<String> enList = new ArrayList<String>();
	/**
	 * 从youtui_sdk.xml文件中解析开发者配置的平台信息
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
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHAT));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHATMOMENTS).equals(tag)) {
					// 设置朋友圈AppId和Enable
					wechatMoments_AppId=parser.getAttributeValue(null, "AppId");
					WechatMoments_Enable=parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHATMOMENTS));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_SINAWEIBO).equals(tag)) {
					// 设置新浪微博AppSecret,Enable,RedirectUrl
					sinaWeibo_AppKey=parser.getAttributeValue(null, "AppKey");
					sinaWeibo_AppSecret=parser.getAttributeValue(null, "AppSecret");
					sinaWeibo_Enable=parser.getAttributeValue(null, "Enable");
					sinaWeibo_RedirectUrl=parser.getAttributeValue(null, "RedirectUrl");
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_SINAWEIBO));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QQ).equals(tag)) {
					// 设置QQ AppId,AppKey,Enable
					qQ_AppId=parser.getAttributeValue(null, "AppId");
					qQ_AppKey=parser.getAttributeValue(null, "AppKey");
					qQ_Enable=parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QQ));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QZONE).equals(tag)) {
					// 设置QZone AppId,AppKey,Enable
					qZone_AppId=parser.getAttributeValue(null, "AppId");
					qZone_AppKey=parser.getAttributeValue(null, "AppKey");
					qZone_Enable=parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QZONE));
					}

				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_TENCENTWEIBO).equals(tag)) {
					// 设置TencentWeibo AppId,AppKey,Enable
					tencentWeibo_AppKey=parser.getAttributeValue(null, "AppKey");
					tencentWeibo_AppSecret=parser.getAttributeValue(null, "AppSecret");
					tencentWeibo_Enable=parser.getAttributeValue(null, "Enable");
					tencentWeibo_RedirectUrl=parser.getAttributeValue(null, "RedirectUrl");
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_TENCENTWEIBO));
					}

				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_RENN).equals(tag)) {
					// 设置人人AppId,AppKey,Enable
					renren_AppId=parser.getAttributeValue(null, "AppId");
					renren_AppKey=parser.getAttributeValue(null, "AppKey");;
					renren_Enable=parser.getAttributeValue(null, "Enable");
					renren_SecretKey=parser.getAttributeValue(null, "SecretKey");
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_RENN));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MESSAGE).equals(tag)) {
					// 短信Enable
					shortMessage_Enable=parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MESSAGE));
					}
				} else if (YtPlatform.getPlatfornName(YtPlatform.PLATFORM_EMAIL).equals(tag)) {
					// 邮箱Enable
					email_Enable=parser.getAttributeValue(null, "Enable");
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_EMAIL));
					}
				}else if(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MORE_SHARE).equals(tag)){
					if (parser.getAttributeValue(null, "Enable").contains("true")) {
						enList.add(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MORE_SHARE));
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
		setIndex();
	}

	/**
	 * 设置各个社交平台在enList中的位置
	 */
	private static void setIndex() {
		weChatIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHAT));
		if("true".equals(wechat_Enable)&&("".equals(wechat_AppId)||null==wechat_AppId)){
			YtLog.e("微信配置错误", "微信平台被激活但是微信信息未配置,请在youtui_sdk.xml中配置微信信息");
		}
		
		wechatMomentsIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_WECHATMOMENTS));
		if("true".equals(WechatMoments_Enable)&&("".equals(wechatMoments_AppId)||null==wechatMoments_AppId)){
			YtLog.e("微信朋友圈配置错误", "微信朋友圈被激活,但是微信朋友圈信息未配置,请在youtui_sdk.xml中配置微信朋友圈信息.");
		}
		
		sinaWeiboIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_SINAWEIBO));
		if("true".equals(sinaWeibo_Enable)&&("".equals(sinaWeibo_AppKey)||null==sinaWeibo_AppKey||"".equals(sinaWeibo_AppSecret)||null==sinaWeibo_AppSecret||"".equals(sinaWeibo_RedirectUrl)||null==sinaWeibo_RedirectUrl)){
			YtLog.e("新浪微博配置错误", "新浪微博被激活但是微信朋友圈信息未配置,请在youtui_sdk.xml中配置新浪微博信息");
		}
		
		qQIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QQ));
		if("true".equals(qQ_Enable)&&("".equals(qQ_AppId)||null==qQ_AppId||"".equals(qQ_AppKey)||null==qQ_AppKey)){
			YtLog.e("qq配置错误", "qq被激活但是微信朋友圈信息未配�?请在youtui_sdk.xml中配置qq信息.");
		}
		
		qZoneIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_QZONE));
		if("true".equals(qZone_Enable)&&("".equals(qZone_AppId)||null==qZone_AppId||"".equals(qZone_AppKey)||null==qZone_AppKey)){
			YtLog.e("qq空间配置错误", "qq空间被激活但是微信朋友圈信息未配置,请在youtui_sdk.xml中配置qq空间信息.");
		}
		
		tencentWeiboIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_TENCENTWEIBO));
		if("true".equals(tencentWeibo_Enable)&&("".equals(tencentWeibo_AppKey)||null==tencentWeibo_AppKey||"".equals(tencentWeibo_AppSecret)||null==tencentWeibo_AppSecret||"".equals(tencentWeibo_RedirectUrl)||null==tencentWeibo_RedirectUrl)){
			YtLog.e("腾讯微博配置错误", "腾讯微博被激活但是微信朋友圈信息未配息,请在youtui_sdk.xml中配置腾讯微博信息");
		}
		
		renrenIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_RENN));
		if("true".equals(renren_Enable)&&("".equals(renren_AppId)||null==renren_AppId||"".equals(renren_AppKey)||null==renren_AppKey||"".equals(renren_SecretKey)||null==renren_SecretKey)){
			YtLog.e("人人配置错误", "人人被激活但是微信朋友圈信息未配�?请在youtui_sdk.xml中配置人人信息");
		}
		
		shortMessageIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MESSAGE));
		emailIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_EMAIL));
		moreIndex = enList.indexOf(YtPlatform.getPlatfornName(YtPlatform.PLATFORM_MORE_SHARE));
	}

}
