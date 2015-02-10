package cn.bidaround.ytcore.data;

import android.app.Activity;
import cn.bidaround.ytcore.YtShareListener;

public class BaseShare {

	protected Activity activity;
	
	// 分享的数据
	protected ShareData shareData;
	
	protected YtShareListener listener;
	
	public BaseShare(Activity activity, ShareData shareData, YtShareListener listener) {
		this.activity = activity;
		this.shareData = shareData;
		this.listener = listener;
	}
}
