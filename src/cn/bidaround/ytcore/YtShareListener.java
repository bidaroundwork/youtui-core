package cn.bidaround.ytcore;

import android.content.Context;
import cn.bidaround.point.YtPoint;
/** 
 * 分享事件监听，只对微信，新浪微博，qq，腾讯微博，人人等社交平台有效
 * @author youtui
 * @since 14/6/11
 */

public abstract class YtShareListener {
	/**分享前操作*/
	public abstract void onPreShare();
	/**分享成功操作*/
	public abstract void onSuccess(ErrorInfo error);
	/**分享错误操作*/
	public abstract void onError(ErrorInfo error);
	/**分享取消操作*/
	public abstract void onCancel();
    /**友推积分操作*/
	public static final void sharePoint(Context context,String appId,int channelId,String url,Boolean isShareContent,String uniqueCode) {
		YtPoint.sharePoint(context, appId, channelId, url, isShareContent, uniqueCode);	
	};

}
