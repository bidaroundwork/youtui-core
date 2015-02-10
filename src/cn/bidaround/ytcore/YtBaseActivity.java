package cn.bidaround.ytcore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * 友推基础Activity,用于调试
 * 
 * @author youtui
 * @since 14/5/19
 */
public class YtBaseActivity extends Activity {
	/** 标示activity */
	protected String TAG = getClass().toString();
	protected Handler mBgHandler;
	private HandlerThread bgThread;

	@SuppressLint("HandlerLeak")
	protected Handler mUiHandler = new Handler() {
		public void handleMessage(Message msg) {
			handUiMsg(msg);
		};
	};

	/**
	 * 
	 * @param msg
	 */
	protected void handUiMsg(Message msg) {
	}

	/**
	 * 
	 * @param msg
	 */
	protected void handBgMsg(Message msg) {
	}

	@Override
	protected void onDestroy() {
		if (mBgHandler != null) {
			mBgHandler.getLooper().quit();
		}
		super.onDestroy();
	}

	/**
	 * 后台线程类
	 */
	public class BackgroundHandler extends Handler {

		public BackgroundHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handBgMsg(msg);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bgThread = new HandlerThread("bgThread");
		bgThread.start();
		mBgHandler = new BackgroundHandler(bgThread.getLooper());
		// Log.i(TAG, "onCreate");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// Log.i(TAG, "onRestart");
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Log.i(TAG, "onPause");
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Log.i(TAG, "onStart");
	}
}
