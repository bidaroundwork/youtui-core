package cn.bidaround.ytcore.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.text.TextUtils;
import cn.bidaround.ytcore.util.Constant;
import cn.bidaround.ytcore.util.YtLog;

/**
 * 读取保存在youtui_sdk.xml下的保存的key信息以便于后续使用
 * 
 * @author youtui
 * @since 14/4/21
 */
public class KeyInfo {
	/** 友推AppKey */
	public static String youTui_AppKey;
	
	public static String youTui_AppSecret;
	
	public static Map<String, String> KeyInforMap = new HashMap<String, String>();
	
	public static ArrayList<String> enList = new ArrayList<String>();

	/**
	 * 从youtui_sdk.xml文件中解析开发者配置的平台信息
	 * 
	 * @param context
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static void parseXML(Context context) {
		try {
			enList.clear();
			InputStream in = context.getResources().getAssets().open(Constant.YOUTUI_SDK_XML);
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
						youTui_AppSecret = parser.getAttributeValue(null, "AppSecret");
					}
					for(YtPlatform p : YtPlatform.values()){
						String name = p.getName();
						if(name.equalsIgnoreCase(tag)){
							String AppId = parser.getAttributeValue(null, Constant.APPID);
							String AppKey = parser.getAttributeValue(null, Constant.APPKEY);
							String AppSecret = parser.getAttributeValue(null, Constant.APPSECRET);
							String RedirectUrl = parser.getAttributeValue(null, Constant.REDIRECTURL);
							String Enable = parser.getAttributeValue(null, Constant.ENABLE);
							if(!TextUtils.isEmpty(AppId))
								KeyInforMap.put(name + Constant.APPID, AppId);
							if(!TextUtils.isEmpty(AppKey))
								KeyInforMap.put(name + Constant.APPKEY, AppKey);
							if(!TextUtils.isEmpty(AppSecret))
								KeyInforMap.put(name + Constant.APPSECRET, AppSecret);
							if(!TextUtils.isEmpty(RedirectUrl))
								KeyInforMap.put(name + Constant.REDIRECTURL, RedirectUrl);
							if(!TextUtils.isEmpty(Enable) && "true".equalsIgnoreCase(Enable)){
								KeyInforMap.put(name + Constant.ENABLE, Enable);
								enList.add(name);
							}
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
		} catch (Exception e) {
			YtLog.e("YtCore:", "youtui_sdk.xml error");
			e.printStackTrace();
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
			in = context.getResources().getAssets().open(Constant.YOUTUI_SDK_XML);
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(in, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					String tag = parser.getName();
					if (paltform.getName().equalsIgnoreCase(tag)) {
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
