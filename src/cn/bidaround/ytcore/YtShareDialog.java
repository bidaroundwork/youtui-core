package cn.bidaround.ytcore;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import cn.bidaround.ytcore.activity.ShareView;
import cn.bidaround.ytcore.activity.ShareView.OnBackListener;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * 编辑分享内容对话框
 * @author youtui
 */
public class YtShareDialog extends Dialog implements OnBackListener{
	private Activity activity;
	private ShareData data;
	private YtPlatform platform;
	private YtShareListener listener;
	
	public YtShareDialog(Activity act, ShareData data, YtPlatform platform, String shortUrl, String realUrl) {
		super(act);
		this.activity = act;
		this.data = data;
		this.platform = platform;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutParams lp = new LayoutParams();
		WindowManager manager = activity.getWindowManager();
		Display display = manager.getDefaultDisplay();
		lp.width = display.getWidth() * 7 / 8;
		setContentView(new ShareView(activity, data, listener, platform).setOnBackListener(this), lp);
	}

	public void setListener(YtShareListener listener) {
		this.listener = listener;
	}

	@Override
	public void onBack() {
		dismiss();
	}
}
