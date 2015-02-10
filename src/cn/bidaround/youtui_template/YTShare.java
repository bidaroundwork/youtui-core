package cn.bidaround.youtui_template;

import android.app.Activity;
import android.content.Intent;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * 具体的分享内容类
 * 
 * @author youtui
 * @since 14/6/19
 */
public class YTShare {
	private Activity act;

	public YTShare(Activity act) {
		this.act = act;
	}

	/**
	 * 分享到各个平台 判断平台所处的页面和在页面的位置设置点击分享事件 该方法用于黑色网格样式中
	 * 
	 * @param position
	 *            点击事件所在gridview上位置
	 * @param pageIndex
	 *            点击事件所在viewpager页面indext
	 * @param ShareData
	 *            .shareData
	 */
	public void doGridShare(int position, int pageIndex, YtTemplate template, ShareData shareData, int itemAmount, YTBasePopupWindow instance,int height) {
		
		YtPlatform platform = template.getPlatform(position, pageIndex, itemAmount);
		
		if(platform == null) return;
		
		if(!compareLasttime(platform))  return;
		
		doShare(template, platform, instance, shareData);
	}

	/**
	 * 分享到各个平台 判断平台所处的页面和在页面的位置设置点击分享事件 该方法用于黑色网格样式中
	 * 
	 * @param position
	 *            点击事件所在gridview上位置
	 * @param pageIndex
	 *            点击事件所在viewpager页面indext
	 * @param ShareData
	 *            .shareData
	 */
	public void doListShare(int position, YtTemplate template, ShareData shareData, YTBasePopupWindow instance) {
		YtPlatform platform = template.getPlatform(position);
		
		if(platform == null) return;
		
		if(!compareLasttime(platform))  return;
		
		doShare(template, platform, instance, shareData);
	}
	
	private void doShare(YtTemplate template, YtPlatform platform, YTBasePopupWindow instance, ShareData shareData){
		// 截屏分享
		if (platform == YtPlatform.PLATFORM_SCREENCAP) 
			shareScreencap(instance, template);
		// 其他平台分享
		else{
			ShareData data = template.getData(platform);
			YtCore.getInstance().share(act, platform, template.getListener(platform), data == null ? shareData : data);
		}
	}
	
	/**
	 * 截屏分享
	 */
	private void shareScreencap(YTBasePopupWindow instance, YtTemplate template){
		if (instance != null) {
			instance.dismiss();
		}
		TemplateUtil.GetandSaveCurrentImage(act, true);
		Intent it = new Intent(act, ScreenCapEditActivity.class);
		it.putExtra("viewType", template.getViewType());
		it.putExtra("target_url", template.getData(YtPlatform.PLATFORM_SCREENCAP).getTargetUrl());
		act.startActivity(it);
	}
	
	
	/**
	 * 比较最后一次的点击时间
	 * @param template
	 * @return true : 有效 ； false : 点击太快，无效
	 */
	private boolean compareLasttime(YtPlatform platform){
		return ShareRespUtil.getInstance().compareLastTime(platform.getName());
	}
}
