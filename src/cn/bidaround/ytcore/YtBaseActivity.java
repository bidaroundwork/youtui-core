package cn.bidaround.ytcore;

import android.app.Activity;
import android.os.Bundle;
/**
 * 友推基础Activity,用于调试
 * @author youtui
 * @since  14/5/19
 */
public class YtBaseActivity extends Activity{
	/**标示activity*/
	protected  String TAG = getClass().toString();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.i(TAG, "onCreate");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		//Log.i(TAG, "onRestart");
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		//Log.i(TAG, "onPause");
	}
	
	@Override
	protected void onDestroy() {
		//Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//Log.i(TAG, "onStart");
	}
}
