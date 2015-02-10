package cn.bidaround.ytcore.util;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.text.TextUtils;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * ShareData数据检测
 * 
 * <h1>检测项目</h1>
 * <li>除纯文字分享外，其他平台检测图片路径、url，本地图片是否存在，是否可正常打开，网络图片路径是否可正常访问
 * <li>文字分享检测文字、图片分享检测图片设置、音乐分享检测音乐的URL是否有效、视频分享检测视频URL是否有效
 * <li>qq、微信、易信分享检测target url（必须设置）
 * <hr>
 * <li>检测过程中存在问题将以<b><font color=red>YouTui</font></b>为tag，警告或者错误的方式提示在LogCat
 * <hr>
 * 
 * <li>检测分享平台是否可以接受该分享类型
 * @author youtui
 */
public class CheckShareData {
	
	private static final String TAG = "YouTui";

	public static void check(final ShareData shareData){
		
		if(!YtCore.getInstance().isCheckConfig()) return;
		
		YtLog.w("YouTui", "):>开始友推数据检测...");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(checkShareData(shareData) && checkShareDataIsAppShare(shareData)){
					
					boolean result = true;
					// 除纯文字分享外，其他分享都要设置图片
					boolean checkImage = true;
					
					if(shareData.getShareType() == ShareData.SHARETYPE_TEXT){
						checkImage = false;
						if(!checkShareDataText(shareData)) 
							result = false;
					}
					
					else if(shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT){
						if(!checkShareDataText(shareData)) 
							result = false;
					}
					
					else if(shareData.getShareType() == ShareData.SHARETYPE_MUSIC){
						if(!checkShareDataMusic(shareData)) 
							result = false;
					}
					
					else if(shareData.getShareType() == ShareData.SHARETYPE_VIDEO){
						if(!checkShareDataVideo(shareData)) 
							result = false;
					}
					
					if(checkImage && !checkShareDataImage(shareData)) 
						result = false;
						
					//qq、qq空间、微信、微信好友、微信收藏、易信、易信朋友圈分享时，必须指定target url
					if(TextUtils.isEmpty(shareData.getTargetUrl()))
						YtLog.e(TAG, "code:1020;Find the target url is null, can't share to template(Wechat、WechatCircle、" +
								"WechatFavorite、QQ、QQZone、Yixin、YixinCircle、QRCode)");
					else
						if(!checkUrl(shareData.getTargetUrl()))
							YtLog.e(TAG, "code:1021;The target url is invalid");
					
					if(result)
						YtLog.i(TAG, "Check ShareData not found error");
				}
			}
		}).start();
	}
	
	private static boolean checkFile(String path){
		File file = new File(path);
		return file.exists() && file.canRead();
	}
	
	private static boolean checkShareDataIsAppShare(ShareData mShareData){
		if(mShareData.isAppShare()){
			YtLog.e(TAG, "code:1009;Check error because the sharedata object isAppShare");
			return false;
		}
		return true;
	}
	
	private static boolean checkShareData(ShareData mShareData){
		if(mShareData == null){
			YtLog.e(TAG, "code:1010;The sharedata object is null");
			return false;
		}
		return true;
	}
	
	private static boolean checkShareDataVideo(ShareData mShareData){
		if(TextUtils.isEmpty(mShareData.getVideoUrl())){
			YtLog.e(TAG, "code:1011;ShareData the 'videoUrl' value is null, please call ShareData.setVideoUrl(String)");
			return false;
		}
		if(!checkUrl(mShareData.getVideoUrl())){
			YtLog.w(TAG, "code:1012;The video url is invalid");
			return false;
		}
		return true;
	}
	
	private static boolean checkShareDataMusic(ShareData mShareData){
		if(TextUtils.isEmpty(mShareData.getMusicUrl())){
			YtLog.e(TAG, "code:1013;ShareData the 'musicUrl' value is null, please call ShareData.setMusicUrl(String)");
			return false;
		}
		if(!checkUrl(mShareData.getMusicUrl())){
			YtLog.w(TAG, "code:1014;The music url is invalid");
			return false;
		}
		return true;
	}
	
	private static boolean checkShareDataText(ShareData mShareData){
		if(TextUtils.isEmpty(mShareData.getText())){
			YtLog.e(TAG, "code:1015;ShareData the 'text' value is null, please call ShareData.setText(String)");
			return false;
		}
		return true;
	}
	
	private static boolean checkShareDataImage(ShareData mShareData){
		if(TextUtils.isEmpty(mShareData.getImagePath()) && TextUtils.isEmpty(mShareData.getImageUrl())){
			YtLog.e(TAG, "code:1016;The 'imagePath/imageUrl' value is null," +
					" please call ShareData.setImagePath(String) or ShareData.setImageUrl(String)");
		}
		else if(!TextUtils.isEmpty(mShareData.getImagePath())){
			if(!checkFile(mShareData.getImagePath()))
					YtLog.e(TAG, "code:1018;The image path("+mShareData.getImagePath()+") is not exist or can not read");
			else
				return true;
		}
		else if(!TextUtils.isEmpty(mShareData.getImageUrl())){
			if(!checkUrl(mShareData.getImageUrl())){
				YtLog.w(TAG, "code:1019;The image url("+mShareData.getImageUrl()+") is invalid");
			}
			else
				return true;
		}
		return false;
	}
	
	private static boolean checkUrl(String url){
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            return conn.getResponseCode() == 200 ;
        } catch (Exception e) {
        	e.printStackTrace();
       	 return false;
        }
	}
	
	/**
	 * 判断该分享平台是否支持该分享的类型
	 * @param platform
	 * @param shareType ShareData.getShareType()
	 * @return
	 */
	public static void checkYtPlatForm(Context context, YtPlatform platform , int shareType){
		
		// QQ分享平台不支持视频分享
		if(platform == YtPlatform.PLATFORM_QQ && shareType == ShareData.SHARETYPE_VIDEO){
			showLog("QQ", "video");
		}
		
		// QQ空间分享平台不支持视频、音乐分享
		else if(platform == YtPlatform.PLATFORM_QZONE && (shareType == ShareData.SHARETYPE_VIDEO || shareType == ShareData.SHARETYPE_MUSIC)){
			showLog("QZone", "video and music");
		}
		
		// 新浪微博分享平台不支持的平台
		else if(platform == YtPlatform.PLATFORM_SINAWEIBO ){
			
			// 页面分享不支持纯图片、视频、音乐分享
			if("true".equals(KeyInfo.getKeyValue(context, YtPlatform.PLATFORM_SINAWEIBO, "IsWebShare"))){
				if(shareType == ShareData.SHARETYPE_VIDEO || shareType == ShareData.SHARETYPE_MUSIC || shareType == ShareData.SHARETYPE_IMAGE){
					showLog("SinaWB", "pure image、video and music");
				}
			}
			// 客户端分享不支持纯图片
			else{
				if(shareType == ShareData.SHARETYPE_IMAGE)
					showLog("SinaWB", "pure image");
			}
		}
		
		// 腾讯微博分享平台只支持纯文字、图文分享
		else if(platform == YtPlatform.PLATFORM_TENCENTWEIBO && shareType != ShareData.SHARETYPE_TEXT && shareType != ShareData.SHARETYPE_IMAGEANDTEXT){
			showLog("TencentWeibo", "pure image、video and music");
		}
		
		// 人人网分享平台只支持纯文字、图文分享
		else if(platform == YtPlatform.PLATFORM_RENREN && shareType != ShareData.SHARETYPE_TEXT && shareType != ShareData.SHARETYPE_IMAGEANDTEXT){
			showLog("Renren", "pure image、video and music");
		}
	}
	
	private static void showLog(String platform , String type){
		YtLog.e(TAG, "code:1024; The Platform of " + platform + " do not support sharing type of "+type);
	}
}