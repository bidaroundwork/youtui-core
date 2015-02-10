package cn.bidaround.ytcore.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * 授权的基础类
 * 
 * @author youtui
 * @since 2015/1/22
 */
public class BaseAuth {

	protected Context context;

	protected AuthUserInfo userInfo = new AuthUserInfo();

	protected AuthListener listener;

	protected YtPlatform platform;

	private final static int SUC = 1;
	private final static int FAIL = 2;
	private final static int CANCEL = 3;

	public BaseAuth(Context context, AuthListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case SUC:
				if (listener != null)
					listener.onAuthSucess(userInfo);
				break;

			case FAIL:
				if (listener != null)
					listener.onAuthFail();
				break;

			case CANCEL:
				if (listener != null)
					listener.onAuthCancel();
				break;
			}

			cn.bidaround.ytcore.util.Util.dismissDialog();
			if (context instanceof AuthActivity)
				((AuthActivity) context).finish();
		};
	};

	protected void sendSuccess() {
		handler.sendEmptyMessage(SUC);
	}

	protected void sendFail() {
		handler.sendEmptyMessage(FAIL);
	}

	protected void sendCancel() {
		handler.sendEmptyMessage(CANCEL);
	}
}
