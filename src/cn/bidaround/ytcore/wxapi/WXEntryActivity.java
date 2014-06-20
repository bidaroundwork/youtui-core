package cn.bidaround.ytcore.wxapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import cn.bidaround.point.ChannelId;
import cn.bidaround.ytcore.ErrorInfo;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.YtBaseActivity;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.util.Util;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * 微信分享activity
 * @author youtui
 * @since 14/5/4 
 */
public class WXEntryActivity extends YtBaseActivity implements IWXAPIEventHandler{
	/**微信接口*/
	private IWXAPI mIWXAPI;
	/**待分享图片*/
	private Bitmap bitmap;
	/**分享图片的缩略图*/
	private Bitmap bmpThum;
	/**短链接*/
	private String shortUrl;
	/**真实网址*/
	private String realUrl ;
	/**微信是否为分享时打开*/
	private boolean fromShare;
	/**分享事件监听*/
	public static YtShareListener listener;
	/**分享的平台,用于区别微信好友和微信朋友圈*/
	private YtPlatform platform;
	/**待分享数据*/
	public static ShareData shareData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// 判断是否为朋友圈
		platform = (YtPlatform) getIntent().getExtras().get("platform");
		fromShare = getIntent().getExtras().getBoolean("fromShare");
		shortUrl = getIntent().getExtras().getString("shortUrl");
		 realUrl = getIntent().getExtras().getString("realUrl");

		// 传入的pointArr不为null时
		if (platform == YtPlatform.PLATFORM_WECHATMOMENTS) {
			mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, KeyInfo.wechatMoments_AppId, false);
			mIWXAPI.registerApp(KeyInfo.wechatMoments_AppId);
		} else {
			mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, KeyInfo.wechat_AppId, false);
			mIWXAPI.registerApp(KeyInfo.wechat_AppId);
		}
		mIWXAPI.handleIntent(getIntent(), WXEntryActivity.this);
		shareToWx();
	}

	/**
	 * 分享到微信或朋友圈 当微信没有登陆时，分享会先进入登陆界面，登录后再次启动该activity，
	 * 导致通过Intent传入的ShareData.shareData和pointArr读取都为null 此时在shareToWx不需要做操作
	 */
	protected void shareToWx() {
		if (shareData != null) {

			WXMediaMessage msg = new WXMediaMessage();

			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			msg.title = shareData.getTitle();

			msg.description = shareData.getText();

			// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
			if (bitmap != null) {
				bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
			} else {
				bmpThum = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), YtCore.res.getIdentifier("loadfail", "drawable", YtCore.packName)), 150, 150, true);
			}
			msg.setThumbImage(bmpThum);
			WXWebpageObject pageObject = new WXWebpageObject();
			pageObject.webpageUrl = shareData.getTarget_url();
			msg.mediaObject = pageObject;
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("测试");
			req.message = msg;
			if (fromShare) {
				if (platform == YtPlatform.PLATFORM_WECHATMOMENTS) {
					req.scene = SendMessageToWX.Req.WXSceneTimeline;
				} else  {
					req.scene = SendMessageToWX.Req.WXSceneSession;
				}
				mIWXAPI.sendReq(req);
			}
		} else {
			
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handIntent(intent);
	}
	/**
	 * 微信监听分享结果
	 * @param intent
	 */
	public void handIntent(Intent intent) {
		setIntent(intent);
		// 监听分享后的返回结果
		mIWXAPI.handleIntent(intent, this);
	}

	/**
	 * 创建唯一标示
	 * @param type
	 * @return 唯一标示字符串
	 */
	protected String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	public void onReq(BaseReq req) {

	}
	
	@Override
	protected void onRestart() {
		Util.dismissDialog();
		finish();
		super.onRestart();
	}
	
	@Override
	protected void onDestroy() {
		shareData = null;
		listener = null;
		super.onDestroy();
	}


	@Override
	/**
	 * 微信分享监听
	 */
	public void onResp(BaseResp response) {
		switch (response.errCode) {
		case BaseResp.ErrCode.ERR_OK:		
			if (platform == YtPlatform.PLATFORM_WECHATMOMENTS) {
				YtShareListener.sharePoint(this, KeyInfo.youTui_AppKey, ChannelId.WECHATFRIEND, realUrl, !shareData.isAppShare, shortUrl);
				if(listener!=null){		
					ErrorInfo error = new ErrorInfo();
					String errorMessage = response.errStr;
					error.setErrorMessage(errorMessage);
					listener.onSuccess(error);
				}
			} else {
				YtShareListener.sharePoint(this, KeyInfo.youTui_AppKey, ChannelId.WECHAT, realUrl, !shareData.isAppShare, shortUrl);
				if(listener!= null){
					ErrorInfo error = new ErrorInfo();
					String errorMessage = response.errStr;
					error.setErrorMessage(errorMessage);
					listener.onSuccess(error);
				}
			}
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			if(listener!=null){
				ErrorInfo error = new ErrorInfo();
				String errorMessage = response.errStr;
				error.setErrorMessage(errorMessage);
				listener.onError(error);
			}
			break;
		case BaseResp.ErrCode.ERR_COMM:
			if(listener!=null){
				ErrorInfo error = new ErrorInfo();
				String errorMessage = response.errStr;
				error.setErrorMessage(errorMessage);
				listener.onError(error);
			}		
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			if(listener!=null){
				listener.onCancel();
			}
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			break;
		default:
			break;
		}
		finish();
	}
	
}
