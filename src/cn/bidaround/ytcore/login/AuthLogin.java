package cn.bidaround.ytcore.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.util.AppHelper;
import cn.bidaround.ytcore.util.Constant;
import cn.bidaround.ytcore.util.Util;
import cn.bidaround.ytcore.util.YtLog;
import cn.bidaround.ytcore.wxapi.WXEntryActivity;

/**
 * 授权登录
 * 
 * @author youtui
 * @since 14/6/19
 */
public class AuthLogin {
	/**
	 * 新浪授权登录
	 * 
	 * @param act
	 */
	public void sinaAuth(Activity act, AuthListener listener) {
		if (Util.isNetworkConnected(act)) {
			Intent sinaLogin = new Intent(act, AuthActivity.class);
			sinaLogin.putExtra(Constant.FLAG, Constant.FLAG_SINA);
			AuthActivity.authListener = listener;
			act.startActivity(sinaLogin);
		} else {
			Toast.makeText(act, YtCore.res.getString(YtCore.res.getIdentifier("yt_nonetwork", "string", YtCore.packName)), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * qq授权登录
	 * 
	 * @param act
	 */
	public void qqAuth(Activity act, AuthListener listener) {
		if (AppHelper.isTencentQQExisted(act)) {
			if (Util.isNetworkConnected(act)) {
				Intent qqLogin = new Intent(act, AuthActivity.class);
				qqLogin.putExtra(Constant.FLAG, Constant.FLAG_QQ);
				AuthActivity.authListener = listener;
				act.startActivity(qqLogin);
			} else {
				Toast.makeText(act, YtCore.res.getString(YtCore.res.getIdentifier("yt_nonetwork", "string", YtCore.packName)), Toast.LENGTH_SHORT).show();
			}
		} else {
			Resources res = act.getResources();
			String packName = act.getPackageName();
			Toast.makeText(act, res.getString(res.getIdentifier("yt_noqqclient", "string", packName)), Toast.LENGTH_SHORT).show();
		}
	}
	


	/**
	 * 腾讯微博授权登录
	 * 
	 * @param act
	 */

	public void tencentWbAuth(Context context, AuthListener listener) {
		if (Util.isNetworkConnected(context)) {
			Intent tencentWbLogin = new Intent(context, AuthActivity.class);
			tencentWbLogin.putExtra(Constant.FLAG, Constant.FLAG_TENCENTWEIBO);
			tencentWbLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			AuthActivity.authListener = listener;
			context.startActivity(tencentWbLogin);
		} else {
			Toast.makeText(context, YtCore.res.getString(YtCore.res.getIdentifier("yt_nonetwork", "string", YtCore.packName)), Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 微信授权登录
	 * 
	 * @param act
	 */

	public void wechatAuth(Activity act, AuthListener listener) {
		if (Util.isNetworkConnected(act)) {
			// 微信和朋友圈
			if (AppHelper.isWeixinExisted(act)) {
				try {
					Intent intent = new Intent(act, Class.forName(act.getPackageName() + ".wxapi.WXEntryActivity"));
					intent.putExtra("isWechatAuth", true);
					WXEntryActivity.authListener = listener;
					WXEntryActivity.activity = act;
					act.startActivity(intent);
				} catch (ClassNotFoundException e) {
					YtLog.e("at YouTui.doShare() when platform is wechat or wechatmoments", act.getPackageName() + ".wxapi.WXEntryActivity cann't be found");
					e.printStackTrace();
				}
			} else
				Toast.makeText(act, act.getResources().getString(act.getResources().getIdentifier("yt_nowechatclient", "string", act.getPackageName())), Toast.LENGTH_SHORT).show();
		} else
			Toast.makeText(act, YtCore.res.getString(YtCore.res.getIdentifier("yt_nonetwork", "string", YtCore.packName)), Toast.LENGTH_SHORT).show();
	}

}
