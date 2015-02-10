package cn.bidaround.ytcore.activity;

import android.content.Intent;
import android.os.Bundle;
import cn.bidaround.ytcore.YtBaseActivity;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.sina.SinaSSOShare;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * 新浪客户端分享时才需要
 * @author youtui
 * @since 2015/1/23
 *
 */
public class SinaShareActivity extends YtBaseActivity implements IWeiboHandler.Response{
	
	/** 分享的平台 */
	protected YtPlatform platform;
	/** 待分享数据 */
	public static ShareData shareData;
	/** 分享监听 */
	public static YtShareListener listener;
	
	protected SinaSSOShare sinaSSOShare;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		platform = (YtPlatform) getIntent().getExtras().get("platform");
		sinaSSOShare = new SinaSSOShare(this, shareData, listener);
		sinaSSOShare.shareToSina();
	}
	
	
	/**
	 * 新浪微博分享完会调用该方法
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		if (sinaSSOShare != null) 
			sinaSSOShare.getIWeiboShareAPI().handleWeiboResponse(intent, this);
		super.onNewIntent(intent);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		// 分享成功
		case WBConstants.ErrorCode.ERR_OK:
			if (shareData != null) 
				YtShareListener.sharePoint(this, platform.getChannleId(), !shareData.isAppShare());
			
			if (listener != null) 
				listener.onSuccess(platform, baseResp.errMsg);
			break;
		// 分享取消
		case WBConstants.ErrorCode.ERR_CANCEL:
			if (listener != null) 
				listener.onCancel(platform);
			
			break;
		// 分享错误
		case WBConstants.ErrorCode.ERR_FAIL:
			if ("auth faild!!!!".equals(baseResp.errMsg)) {
				sinaSSOShare.getIWeiboShareAPI().registerApp();
			} else {
				if (listener != null) 
					listener.onError(platform, baseResp.errMsg);
			}
			break;

		default:
			break;
		}
		finish();
	}
}
