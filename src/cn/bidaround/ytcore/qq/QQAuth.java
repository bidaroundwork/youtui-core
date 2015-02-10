package cn.bidaround.ytcore.qq;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.BaseAuth;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * QQ授权处理类
 * @author youtui
 * @since 2015/1/22
 */
public class QQAuth extends BaseAuth{
	
	/** 腾讯授权qq类 */
	private Tencent mTencent;
	
	private String qqAuthResponse;
	
	/**
	 * qq授权监听
	 */
	private IUiListener iuiListener;

	/**
	 * 获取QQ用户信息监听
	 */
	private IUiListener getInfoListener;
	
	public QQAuth(Activity activity, AuthListener listener){
		super(activity, listener);
		initQQ();
	}
	
	/**
	 * qq授权
	 */
	private void initQQ() {
		mTencent = Tencent.createInstance(YtPlatform.PLATFORM_QQ.getAppId(), context);
		mTencent.logout(context);
		iuiListener = new IUiListener() {

			@Override
			public void onCancel() {
				sendCancel();
			}

			@Override
			public void onComplete(Object obj) {
				UserInfo info = new UserInfo(context, mTencent.getQQToken());
				getInfoListener = new IUiListener() {

					@Override
					public void onCancel() {
						sendCancel();
					}

					@Override
					public void onComplete(Object obj) {
						
						JSONObject json = (JSONObject) obj;
						userInfo.setQqOpenid(mTencent.getQQToken().getOpenId());
						userInfo.setQqAuthResponse(qqAuthResponse);
						try {
							userInfo.setQqUserInfoResponse(json.toString());
							userInfo.setQqNickName(json.getString("nickname"));
							userInfo.setQqImageUrl(json.getString("figureurl_qq_1"));
							userInfo.setQqGender(json.getString("gender"));

							sendSuccess();
						} catch (JSONException e) {
							sendFail();
						}
					}

					@Override
					public void onError(UiError arg0) {
						sendFail();
					}

				};
				info.getUserInfo(getInfoListener);
			}

			@Override
			public void onError(UiError arg0) {
				sendFail();
			}
		};
		mTencent.login((Activity) context, "all", iuiListener);
	}
}
