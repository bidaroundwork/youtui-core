package cn.bidaround.ytcore.kaixin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.BaseShare;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.AuthUserInfo;
import cn.bidaround.ytcore.util.Util;

/**
 *  开心网分享类, 必须带有文字，图片随意
 * @author youtui
 * @since 2014/9/29
 *
 */
public class KaixinShare extends BaseShare{
	
	private YtPlatform platform = YtPlatform.PLATFORM_KAIXIN;

	/** 开心网分享成功 */
	private final int KAIXIN_SHARE_SUCCESS = 4;
	
	/** 开心网分享失败 */
	private final int KAIXIN_SHARE_FAIL = 5;
	
	/** 开心网分享错误 */
	private final int KAIXIN_SHARE_ERROR = 6;
	
	/** 处理人人分享回调 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				/** 处理开心网分享成功 */
			case KAIXIN_SHARE_SUCCESS:
				// 开心网积分处理
				
				YtShareListener.sharePoint(activity, platform.getChannleId(), !shareData.isAppShare());
				if (listener != null) 
					listener.onSuccess(platform, (String) msg.obj);
				break;
				
				/** 处理开心网分享失败*/
			case KAIXIN_SHARE_FAIL:
			case KAIXIN_SHARE_ERROR:
				if (listener != null) 
					listener.onError(platform, (String) msg.obj);
				break;
			}
		};
	};
	
	
	/**
	 * 分享时的构造函数
	 */
	public KaixinShare(Activity activity, ShareData shareData, YtShareListener listener){
		super(activity, shareData, listener);
	}
	
	/**
	 * 分享到开心网，首先验证授权，如果授权有效，则直接分享；否则，则先授权;
	 */
	public void shareToKaixin(){
		doShare();
	}
	
	/**
	 * 授权会话是否有效，如果无效则授权
	 */
	public boolean isAuthValid(){
		Kaixin kaixin = Kaixin.getInstance();
		if (!kaixin.isSessionValid(activity)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 授权，第三方不能使用
	 */
	public void doAuth(){
		Kaixin kaixin = Kaixin.getInstance();
		String[] permissions = {"basic", "create_records"};
		kaixin.authorize(activity, permissions, authListener);
	}
	
	/**
	 * 授权，第三方使用
	 * @param authListener
	 */
	public void doAuth(final Activity activity, final AuthListener authListener){
		Kaixin kaixin = Kaixin.getInstance();
		String[] permissions = {"basic", "create_records"};
		kaixin.authorize(activity, permissions, authListener);
	}
	
	private AuthListener authListener = new AuthListener() {
		
		@Override
		public void onAuthSucess(AuthUserInfo userInfo) {
			doShare();
		}
		
		@Override
		public void onAuthFail() {
			Util.dismissDialog();
		}
		
		@Override
		public void onAuthCancel() {
			Util.dismissDialog();
		}
	};
	
	// 分享到开心网http请求
	private void doShare(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					// 开心网分享类, 必须带有文字，图片随意
					if(TextUtils.isEmpty(shareData.getText())){
						mHandler.sendEmptyMessage(KAIXIN_SHARE_FAIL);
						return;
					}
					
					Bundle bundle = new Bundle();
					bundle.putString("content", getShareDataText());
					
					Map<String, Object> photoes = new HashMap<String, Object>();
					setImageBundle(photoes, bundle);
					
					Kaixin kaixin = Kaixin.getInstance();
					String result = kaixin.uploadContent(activity, "/records/add.json", bundle, photoes);
					
					parseResult(result);
				} catch (IOException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(KAIXIN_SHARE_FAIL);
				}finally{
					Util.dismissDialog();
				}
			}
		}).start();
	}
	
	private String getShareDataText(){
		
		StringBuffer sb = new StringBuffer(shareData.getText());
		
		if(shareData.getShareType() == ShareData.SHARETYPE_VIDEO)
			sb.append("  " + shareData.getVideoUrl());
		else if(shareData.getShareType() == ShareData.SHARETYPE_MUSIC)
			sb.append("  " + shareData.getMusicUrl());
		
		sb.append("  " + getTargetUrl());
		return sb.toString();
	}
	
	private String getTargetUrl(){
		if(!TextUtils.isEmpty(shareData.getTargetUrl()))
			return shareData.getTargetUrl();
		else 
			return "";
	}
	
	private void setImageBundle(Map<String, Object> photoes, Bundle bundle) throws FileNotFoundException{
		// 分享SDcard中的图片或分享的网络图片已经被下载
		if(!TextUtils.isEmpty(shareData.getImagePath())){
			photoes.put("filename", new FileInputStream(new File(shareData.getImagePath())));
		}
		// 分享网络图片
		else if(!TextUtils.isEmpty(shareData.getImageUrl())){
			bundle.putString("picurl", shareData.getImageUrl());
		}
	}
	
	// 解析请求开心网提供的接口的返回结果
	private void parseResult(String result){
		KaixinError kaixinError = cn.bidaround.ytcore.kaixin.KaixinUtil.parseRequestError(result);
		if (kaixinError != null) {
			Message msg = Message.obtain();
			msg.what = KAIXIN_SHARE_ERROR;
			
			if(kaixinError.getErrorCode() == 40031){
				msg.obj = "上传照片出错";
			}
			else if(kaixinError.getErrorCode() == 40036){
				msg.obj = "提交内容超过140个汉字";
			}
			else if(kaixinError.getErrorCode() == 40043){
				msg.obj = "图片链接下载错误";
			}
			else{
				msg.obj = "分享失败";
			}
			mHandler.sendMessage(msg);
		} else {
			long rid = getRecordID(result);
			if (rid > 0) {
				mHandler.sendEmptyMessage(KAIXIN_SHARE_SUCCESS);
			} else {
				mHandler.sendEmptyMessage(KAIXIN_SHARE_FAIL);
			}
		}
	}
	
	/** 
	 *  人人分享成功会返回记录id
	 * @param jsonResult
	 * @return
	 * @throws JSONException
	 */
	private long getRecordID(String result) {
		try {
			JSONObject jsonObj = new JSONObject(result);
			long rid = jsonObj.optInt("rid");
			return rid;
		} catch (Exception e) {
			Log.i("", "kaixin share fail. the result do not have the rid key.");
		}
		return -1;
	}
}
