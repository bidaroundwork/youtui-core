package cn.bidaround.ytcore.yxapi;

import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.api.YXImageMessageData;
import im.yixin.sdk.api.YXMessage;
import im.yixin.sdk.api.YXMusicMessageData;
import im.yixin.sdk.api.YXTextMessageData;
import im.yixin.sdk.api.YXVideoMessageData;
import im.yixin.sdk.api.YXWebPageMessageData;
import im.yixin.sdk.util.BitmapUtil;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;


//  没有判断是否分享到朋友圈
public class YixinShare {
	
	/** 待分享图片 */
	private Bitmap bitmap;
	
	private IYXAPI api;
	
	private ShareData shareData;
	
	private Activity act;
	
	/** 是否分享到朋友圈 */
	private boolean shareToFriends;
	
	public YixinShare(Activity activity, boolean shareToFriends, ShareData shareData, YtShareListener listener, String realUrl, String shortUrl, boolean isAppShare){
		
		act = activity;
		this.shareData = shareData;
		this.shareToFriends = shareToFriends;
		
		if(!shareToFriends){
			api = YXAPIFactory.createYXAPI(activity, YtPlatform.PLATFORM_YIXIN.getAppId());
		}else{
			api = YXAPIFactory.createYXAPI(activity, YtPlatform.PLATFORM_YIXINFRIENDS.getAppId());
		}
		
		api.registerApp();
		
		YXBaseActivity.listener = listener;
		YXBaseActivity.isShareApp = isAppShare;
		
		if(shareToFriends)
			YXBaseActivity.platform = YtPlatform.PLATFORM_YIXINFRIENDS;
		else
			YXBaseActivity.platform = YtPlatform.PLATFORM_YIXIN;
	}
	
	
	public void shareToYixin(){
		if(shareData==null){
			return;
		}
		
		YXMessage msg = new YXMessage();
		
		// 分享图文
		if (shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
			
			// 如果是本地图片
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			
			YXWebPageMessageData data = new YXWebPageMessageData();
			data.webPageUrl = shareData.getTargetUrl();
			msg.messageData = data;
			msg.title = shareData.getTitle();
			msg.description = shareData.getText();
			
			msg.thumbData = BitmapUtil.bmpToByteArray(getBmpThum(bitmap), false); 
		}
		//	分享图片，如果是网路图片，已先将图片下载下来了
		else if(shareData.getShareType() == ShareData.SHARETYPE_IMAGE){
			
			YXImageMessageData imgObj = new YXImageMessageData();
			imgObj.imagePath = shareData.getImagePath();
			msg.messageData = imgObj;
			
			bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			msg.thumbData = BitmapUtil.bmpToByteArray(getBmpThum(bitmap), true);
			
		}
		//分享文字
		else if(shareData.getShareType() == ShareData.SHARETYPE_TEXT){
			
			YXTextMessageData textObj = new YXTextMessageData();
			textObj.text = shareData.getText();

			msg.messageData = textObj;
			msg.description = shareData.getDescription();

		}
		//分享音乐
		else if(shareData.getShareType() == ShareData.SHARETYPE_MUSIC){
			YXMusicMessageData music = new YXMusicMessageData();
			music.musicUrl = shareData.getTargetUrl();
			music.musicDataUrl = shareData.getMusicUrl();
			
			msg.messageData = music;
			msg.title = shareData.getTitle();
			msg.description = shareData.getDescription();
			
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			msg.thumbData = BitmapUtil.bmpToByteArray(getBmpThum(bitmap), true);

		}
		//分享视频
		else if(shareData.getShareType() == ShareData.SHARETYPE_VIDEO){
			YXVideoMessageData video = new YXVideoMessageData();
			video.videoUrl = shareData.getVideoUrl();
			
			msg.messageData = video;
			msg.title = shareData.getTitle();
			msg.description = shareData.getDescription();
			
			if (shareData.getImagePath() != null) {
				bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
			}
			msg.thumbData = BitmapUtil.bmpToByteArray(getBmpThum(bitmap), true);
		}
		
		SendMessageToYX.Req req = new SendMessageToYX.Req();
		req.transaction = buildTransaction("youtui");
		req.message = msg;
		req.scene = shareToFriends ? SendMessageToYX.Req.YXSceneTimeline
				: SendMessageToYX.Req.YXSceneSession;
		api.sendRequest(req);
		
	}
	
	/**
	 *  获取缩略图  bitmap为空时分享会没有响应，所以要设置一个默认图片让用户知道
	 * @param bitmap
	 * @return
	 */
	private Bitmap getBmpThum(Bitmap bitmap){
		Bitmap bmpThum = null;
		if (bitmap != null) {
			bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150 * bitmap.getHeight() / bitmap.getWidth(), true);
			bitmap.recycle();
		} else {
			bmpThum = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(act.getResources(), YtCore.res.getIdentifier("yt_loadfail", "drawable", YtCore.packName)), 150, 150, true);
		}
		return bmpThum;
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
