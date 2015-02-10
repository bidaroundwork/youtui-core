package cn.bidaround.youtui_template;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import cn.bidaround.ytcore.data.KeyInfo;

/**
 * 处理重复点击分享平台
 * @author youtui
 *
 */
public class ShareRespUtil {
	
	private static ShareRespUtil instance;
	
	// 两次点击的间隔时间
	private final int INTERVAL_TIME = 1000;
	
	
	private static Map<String, Long> respMap = new HashMap<String, Long>();

	public static final synchronized ShareRespUtil getInstance() {
		if (instance == null) {
			instance = new ShareRespUtil();
			
			if(KeyInfo.enList != null && KeyInfo.enList.size() > 0){
				respMap.clear();
				for(String str : KeyInfo.enList)
					respMap.put(str, 0L);
			}
		}
		
		return instance;
	}
	
	/**
	 * 比较最后一次的点击时间
	 * @param template
	 * @return true : 有效 ； false : 点击太快，无效
	 */
	public boolean compareLastTime(String template){
		// 两次点击间隔时间大于规定的时间
		if(!TextUtils.isEmpty(template) 
				&& respMap.containsKey(template) 
				&& (respMap.get(template) == 0 || 
				respMap.get(template) + INTERVAL_TIME < System.currentTimeMillis())){
			respMap.put(template, System.currentTimeMillis());
			return true;
		}
		return false;
	}
}
