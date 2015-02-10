package cn.bidaround.ytcore;

import android.content.Context;
import cn.bidaround.point.YtPoint;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * 分享事件监听，只对微信，新浪微博，qq，腾讯微博，人人等社交平台有效
 * 
 * @author youtui
 * @since 14/6/11
 */

public abstract class YtShareListener{
	
	/** 分享前操作 */
	public abstract void onPreShare(YtPlatform platform);

	/** 分享成功操作 */
	public abstract void onSuccess(YtPlatform platform, String result);

	/** 分享错误操作 */
	public abstract void onError(YtPlatform platform, String error);

	/** 分享取消操作 */
	public abstract void onCancel(YtPlatform platform);

	/** 友推积分操作 */
	public static final void sharePoint(Context context, int channelId, boolean isShareContent) {
		YtPoint.sharePoint(context, channelId, null, isShareContent, null);
	};
}
