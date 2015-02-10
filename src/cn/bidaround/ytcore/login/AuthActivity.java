package cn.bidaround.ytcore.login;

import android.content.Intent;
import android.os.Bundle;
import cn.bidaround.ytcore.YtBaseActivity;
import cn.bidaround.ytcore.qq.QQAuth;
import cn.bidaround.ytcore.sina.SinaWeiboAuth;
import cn.bidaround.ytcore.tencentwb.TencentWeiboAuth;
import cn.bidaround.ytcore.util.Constant;

/**
 * 授权登录Activity
 * 
 * @author youtui
 * @since 14/4/26
 */
public final class AuthActivity extends YtBaseActivity {
	
	public static AuthListener authListener;
	
	private SinaWeiboAuth sinaWeiboAuth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	/**
	 * 判断授权的平台
	 */
	private void initData() {
		String flag = getIntent().getExtras().getString(Constant.FLAG);
		
		if (Constant.FLAG_SINA.equals(flag)) 
			sinaWeiboAuth = new SinaWeiboAuth(this, authListener);
		
		else if (Constant.FLAG_TENCENTWEIBO.equals(flag)) 
			new TencentWeiboAuth(this, authListener);
		
		else if (Constant.FLAG_QQ.equals(flag)) 
			new QQAuth(this, authListener);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(sinaWeiboAuth != null)
			sinaWeiboAuth.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		authListener = null;
		super.onDestroy();
	}
}
