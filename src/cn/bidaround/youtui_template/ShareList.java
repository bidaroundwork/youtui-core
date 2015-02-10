package cn.bidaround.youtui_template;

import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * 获取分享平台的logo和名字
 * @author youtui 
 * @since 2014/3/25
 */

public class ShareList {
	/**
	 * 获取分享平台的lolo
	 * @param name
	 * @param context
	 * @return
	 */
	public static int getLogo(String name,Context context){
		String packName = context.getPackageName();
		Resources res = context.getResources();
		
		for(YtPlatform p : YtPlatform.values()){
			String platName = p.getName();
			if(platName.equals(name))
				return res.getIdentifier("yt_" + name.toLowerCase(Locale.US), "drawable", packName);
		}
		return -1;
	}
	/**
	 * 获取分享平台的名字
	 * @param name
	 * @return 
	 */
	public static String getTitle(String name,Context context) {
		String packName = context.getPackageName();
		Resources res = context.getResources();
		
		for(YtPlatform p : YtPlatform.values()){
			String platName = p.getName();
			if(platName.equals(name))
				return res.getString(res.getIdentifier("yt_" + name.toLowerCase(Locale.US), "string", packName));
		}
		return "";
	}
}
