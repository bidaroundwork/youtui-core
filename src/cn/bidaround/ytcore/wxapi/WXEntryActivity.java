package cn.bidaround.ytcore.wxapi;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import cn.bidaround.ytcore.YtBaseActivity;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.util.Util;
import cn.bidaround.ytcore.util.YtLog;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信分享activity
 * 
 * 微信分享时生命周期为：  onCreate  -->  onResp  --> onDestory
 * 微信朋友圈分享时生命周期为：  onCreate  --> onDestory  --> onCreate --> onResp --> onDestory
 * 所以YtShareListener，AuthListener，YtPlatform，ShareData都设置成static，防止朋友圈分享后onDestory将对象销毁
 * @author youtui
 * @since 14/5/4
 */
public class WXEntryActivity extends YtBaseActivity implements IWXAPIEventHandler {
	
	/** 微信接口 */
	private IWXAPI mIWXAPI;
	
	/** 待分享图片 */
	private Bitmap bitmap;
	
	/** 分享图片的缩略图 */
	private Bitmap bmpThum;
	
	/** 微信是否为分享时打开 */
	private boolean fromShare;
	
	/** 分享事件监听 */
	public static YtShareListener listener;
	
	/** 微信授权登录监听事件 */
	public static AuthListener authListener;
	
	/** 显示授权进度窗口不能使用WXEntryActivity，这个Activity很快会被销毁 */
	public static Activity activity;
	
	/** 分享的平台,用于区别微信好友和微信朋友圈 */
	public static YtPlatform platform;
	
	/** 待分享数据 */
	public static ShareData shareData;

	private boolean isWechatAuth;
	
	/** 微信朋友圈第二次oncreate后才能回调*/
	private int destoryTime = 0 ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		fromShare = getIntent().getExtras().getBoolean("fromShare");
		isWechatAuth = getIntent().getExtras().getBoolean("isWechatAuth");

		
		if (platform == null)
			platform = YtPlatform.PLATFORM_WECHAT;

		mIWXAPI = WXAPIFactory.createWXAPI(WXEntryActivity.this, platform.getAppId(), false);
		mIWXAPI.registerApp(platform.getAppId());

		mIWXAPI.handleIntent(getIntent(), WXEntryActivity.this);

		if (isWechatAuth) {
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "wechat_sdk_demo_test";
			mIWXAPI.sendReq(req);
		} else
			shareToWx();
	}

	/**
	 * 分享到微信或朋友圈 当微信没有登陆时，分享会先进入登陆界面，登录后再次启动该activity，
	 * 导致通过Intent传入的ShareData.shareData和pointArr读取都为null 此时在shareToWx不需要做操作
	 */
	protected void shareToWx() {
		WXMediaMessage msg = new WXMediaMessage();
		if (shareData == null) {
			return;
		}

		// 微信分享将内容设置成标题
		if (YtCore.isWxCircleTextAsTitle && platform == YtPlatform.PLATFORM_WECHATMOMENTS)
			msg.title = shareData.getTitle() + "  " + shareData.getText();

		else
			msg.title = shareData.getTitle();

		msg.description = shareData.getText();

		if (shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
			// 如果是图文分享
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}

			// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
			if (bitmap != null) {
				bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
			} else {
				bmpThum = Bitmap.createScaledBitmap(
						BitmapFactory.decodeResource(getResources(), YtCore.res.getIdentifier("yt_loadfail", "drawable", YtCore.packName)), 150, 150,
						true);
			}
			msg.setThumbImage(bmpThum);
			WXWebpageObject pageObject = new WXWebpageObject();
			pageObject.webpageUrl = shareData.getTargetUrl();
			msg.mediaObject = pageObject;
		} else if (shareData.getShareType() == ShareData.SHARETYPE_IMAGE) {
			// 如果是纯图分享
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			if (bitmap != null) {
				bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150 * bitmap.getHeight() / bitmap.getWidth(), true);
				bitmap.recycle();
			} else {
				bmpThum = Bitmap.createScaledBitmap(
						BitmapFactory.decodeResource(getResources(), YtCore.res.getIdentifier("yt_loadfail", "drawable", YtCore.packName)), 150, 150,
						true);
			}
			msg.setThumbImage(bmpThum);
			WXImageObject image = new WXImageObject();
			image.imagePath = shareData.getImagePath();
			msg.mediaObject = image;
			// YtLog.w("SHARETYPE_IMAGE", shareData.getImageUrl());
		} else if (shareData.getShareType() == ShareData.SHARETYPE_TEXT) {
			// 纯文字分享
			WXTextObject text = new WXTextObject();
			text.text = shareData.getText();
			msg.mediaObject = text;
		} else if (shareData.getShareType() == ShareData.SHARETYPE_MUSIC) {
			// 音乐分享
			WXMusicObject music = new WXMusicObject();
			music.musicUrl = shareData.getMusicUrl();
			msg.mediaObject = music;
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
			if (bitmap != null) {
				bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
			} else {
				bmpThum = Bitmap.createScaledBitmap(
						BitmapFactory.decodeResource(getResources(), YtCore.res.getIdentifier("yt_loadfail", "drawable", YtCore.packName)), 150, 150,
						true);
			}
			msg.setThumbImage(bmpThum);
		} else if (shareData.getShareType() == ShareData.SHARETYPE_VIDEO) {
			// 视频分享
			WXVideoObject video = new WXVideoObject();
			video.videoUrl = shareData.getVideoUrl();
			msg.mediaObject = video;
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
			if (bitmap != null) {
				bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
			} else {
				bmpThum = Bitmap.createScaledBitmap(
						BitmapFactory.decodeResource(getResources(), YtCore.res.getIdentifier("yt_loadfail", "drawable", YtCore.packName)), 150, 150,
						true);
			}
			msg.setThumbImage(bmpThum);
			YtLog.w("SHARETYPE_MUSIC", shareData.getMusicUrl());
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("youtui");
		req.message = msg;
		
		if (fromShare) {
			if (platform == YtPlatform.PLATFORM_WECHATMOMENTS) 
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
			
			else if (platform == YtPlatform.PLATFORM_WECHAT)
				req.scene = SendMessageToWX.Req.WXSceneSession;
			
			else if (platform == YtPlatform.PLATFORM_WECHATFAVORITE) 
				req.scene = SendMessageToWX.Req.WXSceneFavorite;
			
			mIWXAPI.sendReq(req);
		}
	}

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handIntent(intent);
		super.onNewIntent(intent);
	}

	/**
	 * 微信监听分享结果
	 * 
	 * @param intent
	 */
	public void handIntent(Intent intent) {
		setIntent(intent);
		// 监听分享后的返回结果
		mIWXAPI.handleIntent(intent, this);
	}

	/**
	 * 创建唯一标示
	 * 
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
		super.onDestroy();
		
		if(platform != null){
			if(platform == YtPlatform.PLATFORM_WECHATMOMENTS){
				if(destoryTime >= 1)
					destoryObject();
				destoryTime ++;
			}
			else
				destoryObject();
		}
	}
	
	private void destoryObject(){
		authListener = null;
		listener = null;
		platform = null;
		shareData = null;
	}

	@Override
	/**
	 * 微信分享监听
	 */
	public void onResp(BaseResp response) {
		switch (response.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			// 微信授权登录
			if (isWechatAuth) {
				Bundle bundle = new Bundle();
				response.toBundle(bundle);
				new WechatAuthHelper(activity, authListener).httpForUserinfor(bundle);
			} else {
				
				if(shareData != null)
					YtShareListener.sharePoint(this, platform.getChannleId(), !shareData.isAppShare());

				if (listener != null)
					listener.onSuccess(platform, response.errStr);
			}
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			if (listener != null)
				listener.onError(platform, response.errStr);
			break;
		case BaseResp.ErrCode.ERR_COMM:
			if (listener != null)
				listener.onError(platform, response.errStr);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			if (listener != null)
				listener.onCancel(platform);

			if (authListener != null)
				authListener.onAuthCancel();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			break;
		default:
			break;
		}
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 微信签名检测
		if (YtCore.getInstance().isCheckConfig())
			WechatAuthHelper.checkWeChatSign();
	}
}
