package cn.bidaround.ytcore;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import cn.bidaround.point.YoutuiConstants;
import cn.bidaround.point.YtLog;
import cn.bidaround.point.YtPoint;
import cn.bidaround.ytcore.activity.ShareActivity;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.AuthLogin;
import cn.bidaround.ytcore.login.AuthUserInfo;
import cn.bidaround.ytcore.login.SinaNoKeyShare;
import cn.bidaround.ytcore.social.OtherShare;
import cn.bidaround.ytcore.util.AccessTokenKeeper;
import cn.bidaround.ytcore.util.AppHelper;
import cn.bidaround.ytcore.util.CMyEncrypt;
import cn.bidaround.ytcore.util.DownloadImage;
import cn.bidaround.ytcore.util.Util;
import cn.bidaround.ytcore.wxapi.WXEntryActivity;

/**
 * 友推分享操作类
 * @author youtui
 * @since 14/6/11 
 */
public class YtCore {
	/**sim卡序列号*/
	public static String cardNum;
	/**android手机imei*/
	public static String imei;
	/**应用包名*/
	public static String packName;
	/**应用资源*/
	public static Resources res;
	/**应用AppContent*/
	public static Context appContext;
	/**实例*/
	public static YtCore yt;
	/** 获取应用分享分享信息成功 */
	private final int GET_APPSHAREDATA_SUCCESS = 0;
	/** 获取应用分享内容失败 */
	private final int GET_APPSHAREDATA_FAIL = 1;
	/** 获取内容分享信息成功 */
	private final int GET_CONTENTSHAREDATA_SUCCESS = 3;
	/** 获取内容分享内容失败 */
	private final int GET_CONTENTSHAREDATA_FAIL = 4;
	/**传入的activity*/
	private Activity act;
	/**分享平台*/
	private YtPlatform platform;
	/**分享监听*/
	private YtShareListener listener;
	/**邀请码*/
	private String appRecommenderId;
	/**活动id*/
	private String appActivityId;
	/**处理获取待分享信息后的操作,获取到待分享信息就进行分享,没有获取到待分享信息则提醒用户*/
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Util.dismissDialog();
			switch (msg.what) {
			//获取应用分享信息成功后进行分享操作
			case GET_APPSHAREDATA_SUCCESS:
				ShareData shareData = (ShareData) msg.obj;
				doShare(act, platform, listener,shareData);
				break;
				//获取应用分享信息失败,提醒用户
			case GET_APPSHAREDATA_FAIL:
				Toast.makeText(appContext, "获取分享内容失败,请检查分享的网络地址和网络连接情况...", Toast.LENGTH_SHORT).show();
				break;
				//获取内容分享信息成功,进行分享操作
			case GET_CONTENTSHAREDATA_SUCCESS:
				ShareData contentShareData = (ShareData) msg.obj;
				doShare(act, platform, listener,contentShareData);
				break;
				//获取内容分享信息失败,提醒用户
			case GET_CONTENTSHAREDATA_FAIL:
				Toast.makeText(appContext, "获取分享内容失败,请检查分享的网络地址和网络连接情况...", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};

	/** 获取友推sdk的实例 */
	public static YtCore getInstance() {
		if (yt == null) {
			yt = new YtCore();
		}
		return yt;
	}
	
	private YtCore() {
	}

	/** 分享到社交平台 */
	public void share(Activity act, YtPlatform platform, YtShareListener listener,final ShareData shareData) {
		// 分享前操作
		this.act = act;
		this.platform = platform;
		this.listener = listener;

		Util.showProgressDialog(act, "获取分享数据中...", false);
		if (listener != null) {
			listener.onPreShare();
		}
		// 获取分享信息
		new Thread() {
			public void run() {
				getShareData(shareData);
			};
		}.start();
	}

	/** 跳转到分享页面 */
	private void doShare(Activity act, final YtPlatform platform, final YtShareListener listener,final ShareData shareData) {
		String shortUrl = null ;
		final String realUrl = shareData.getTarget_url();
		if (!shareData.isAppShare && shareData.getTarget_url() != null && !shareData.getTarget_url().equals("")) {
			shortUrl = CMyEncrypt.shortUrl(shareData.getTarget_url())[0];
			sendUrl(KeyInfo.youTui_AppKey, platform.getChannleId(), shareData.getTarget_url(), !shareData.isAppShare, shortUrl);
		}

		dealWithUrl(platform.getChannleId(), shortUrl,shareData);
		if (platform == YtPlatform.PLATFORM_WECHAT || platform == YtPlatform.PLATFORM_WECHATMOMENTS) {
			// 微信和朋友圈
			if (AppHelper.isWeixinExisted(act)) {
				try {
					Intent it = new Intent(act, Class.forName(packName + ".wxapi.WXEntryActivity"));
					WXEntryActivity.listener = listener;
					it.putExtra("platform", platform);
					it.putExtra("fromShare", true);
					it.putExtra("shortUrl", shortUrl);
					it.putExtra("realUrl", realUrl);
					WXEntryActivity.shareData = shareData;
					act.startActivity(it);
				} catch (ClassNotFoundException e) {
					YtLog.e("at YouTui.doShare() when platform is wechat or wechatmoments", packName + ".wxapi.WXEntryActivity cann't be found");
					e.printStackTrace();
				}
			} else {
				Toast.makeText(act, "未安装微信。。。", Toast.LENGTH_SHORT).show();
			}

		} else if (platform == YtPlatform.PLATFORM_EMAIL) {
			//分享到Email
			if(shareData.getTarget_url()!=null){
				new OtherShare(act).sendMail(shareData.getText()+shareData.getTarget_url());
			}else{
				new OtherShare(act).sendMail(shareData.getText());
			}			
		} else if (platform == YtPlatform.PLATFORM_MESSAGE) {
			//分享到短信
			if(shareData.getTarget_url()!=null){
				new OtherShare(act).sendSMS(shareData.getText()+shareData.getTarget_url());
			}else{
				new OtherShare(act).sendSMS(shareData.getText());
			}		
		} else if (platform == YtPlatform.PLATFORM_MORE_SHARE) {
			//更多分享
			moreShare(shareData);
		} else if (platform == YtPlatform.PLATFORM_TENCENTWEIBO) {
			//finalShortUrl用于传递shortUrl
			final String finalShortUrl = shortUrl;
			//分享到腾讯微博
			if (AccessTokenKeeper.isTencentWbAuthExpired(act)) {
				//如果腾讯微博授权过期,先获取授权
				AuthLogin tencentWbLogin = new AuthLogin();
				AuthListener tencentWbListener = new AuthListener() {
					@Override
					public void onAuthSucess(Activity act, AuthUserInfo userInfo) {
						Intent qqWBIt = new Intent(act, ShareActivity.class);
						qqWBIt.putExtra("platform", platform);
						ShareActivity.shareData = shareData;
						WXEntryActivity.listener = listener;
						qqWBIt.putExtra("shortUrl", finalShortUrl);
						qqWBIt.putExtra("realUrl", realUrl);
						act.startActivityForResult(qqWBIt, KeyInfo.tencentWeiboIndex);
					}

					@Override
					public void onAuthFail(Activity act) {
						Toast.makeText(act, "授权失败...", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onAuthCancel(Activity act) {
						Toast.makeText(act, "授权取消...", Toast.LENGTH_SHORT).show();
					}
				};

				tencentWbLogin.tencentWbAuth(act, tencentWbListener);
			} else {
				//如果已授权,进行分享
				Intent it = new Intent(act, ShareActivity.class);
				ShareActivity.listener = listener;
				it.putExtra("platform", platform);
				it.putExtra("shortUrl", shortUrl);
				it.putExtra("realUrl", realUrl);
				ShareActivity.shareData = shareData;
				act.startActivity(it);
			}

		} else if (platform == YtPlatform.PLATFORM_QQ || platform == YtPlatform.PLATFORM_QZONE) {
			//分享到qq和qq空间 
			if(AppHelper.isTencentQQExisted(act)){
				Intent it = new Intent(act, ShareActivity.class);
				ShareActivity.listener = listener;
				it.putExtra("platform", platform);
				it.putExtra("shortUrl", shortUrl);
				it.putExtra("realUrl", realUrl);
				ShareActivity.shareData = shareData;
				act.startActivity(it);
			}else{
				Toast.makeText(act, "未安装QQ。。。", Toast.LENGTH_SHORT).show();
			}

		} else if (platform == YtPlatform.PLATFORM_SINAWEIBO) {
			//分享到新浪微博
			
			//如果选择了无key分享
			if("true".equals(KeyInfo.getKeyValue(appContext, YtPlatform.PLATFORM_SINAWEIBO, "IsNoKeyShare"))){
				if(AccessTokenKeeper.readAccessToken(appContext).isSessionValid()){
					Intent it = new Intent(act, ShareActivity.class);
					ShareActivity.listener = listener;
					it.putExtra("platform", platform);
					it.putExtra("sinaWeiboIsNoKeyShare", true);
					it.putExtra("shortUrl", shortUrl);
					it.putExtra("realUrl", realUrl);
					ShareActivity.shareData = shareData;
					act.startActivity(it);
				}else{
					new SinaNoKeyShare().sinaAuth(act,realUrl,shortUrl);
					ShareActivity.shareData = shareData;
					ShareActivity.listener = listener;
				}
			}else{
				if(AppHelper.isSinaWeiboExisted(act)){
					Intent it = new Intent(act, ShareActivity.class);
					ShareActivity.listener = listener;
					it.putExtra("platform", platform);
					it.putExtra("shortUrl", shortUrl);
					it.putExtra("realUrl", realUrl);
					ShareActivity.shareData = shareData;
					act.startActivity(it);
				}else{
					Toast.makeText(act, "未安装新浪微博。。。", Toast.LENGTH_SHORT).show();
				}
			}
		} else if (platform == YtPlatform.PLATFORM_RENN) {
			//分享到人人网
			if(AppHelper.isRenrenExisted(act)){
				Intent it = new Intent(act, ShareActivity.class);
				ShareActivity.listener = listener;
				it.putExtra("platform", platform);
				it.putExtra("shortUrl", shortUrl);
				it.putExtra("realUrl", realUrl);
				ShareActivity.shareData = shareData;
				act.startActivity(it);
			}else{
				Toast.makeText(act, "未安装人人网。。。", Toast.LENGTH_SHORT).show();
			}
		}else if(platform == YtPlatform.PLATFORM_COPYLINK){
			//复制链接 
			if(shareData.getTarget_url()!=null){
				
				Util.copyLink(mHandler, act, shareData.getTarget_url());
			}		
		}
	}

	/** 获取分享信息 */
	private void getShareData(ShareData shareData) {
		if (shareData.isAppShare) {
			//如果是应用分享
			getAppShareData(shareData);
		} else {
			// 如果是内容分享,设置了网络图片而没有设置本地图片，则下载到本地再进行分享
			if (shareData.getImageUrl() != null && shareData.getImagePath() == null) {
				String picPath = shareData.getImageUrl().substring(shareData.getImageUrl().lastIndexOf("/") + 1, shareData.getImageUrl().length());
				try {
					DownloadImage.down_file(shareData.getImageUrl(), YoutuiConstants.FILE_SAVE_PATH, picPath);
					shareData.setImagePath(Environment.getExternalStorageDirectory() + YoutuiConstants.FILE_SAVE_PATH + picPath);
					YtLog.i("YtCore:" + "网络图片保存到本地的路径", shareData.getImagePath());

				} catch (IOException e) {
					mHandler.sendEmptyMessage(GET_CONTENTSHAREDATA_FAIL);
					e.printStackTrace();
					return;
				}

			}
			if (shareData.getImageUrl() != null && !shareData.getTarget_url().equals("") && shareData.getImagePath() != null && !shareData.getImagePath().equals("")) {
				Message msg = Message.obtain(mHandler, GET_CONTENTSHAREDATA_SUCCESS, shareData);
				mHandler.sendMessage(msg);
			} else {
				mHandler.sendEmptyMessage(GET_CONTENTSHAREDATA_FAIL);
			}

		}
	}

	/** isAppShare为true时的分享内容,此时分享内容由开发者预先存放在友推服务器上 */
	private boolean getAppShareData(ShareData shareData) {

		HttpParams httpParam = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParam, 10000);
		HttpClient client = new DefaultHttpClient(httpParam);
		HttpPost post = new HttpPost(YoutuiConstants.YT_URL + "/activity/getAppInfo");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cardNum", cardNum));
		params.add(new BasicNameValuePair("imei", imei));
		params.add(new BasicNameValuePair("appId", KeyInfo.youTui_AppKey));
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String str = EntityUtils.toString(entity);
			YtLog.i("YtCore:" + "获取应用分享信息", str);
			JSONObject json = new JSONObject(str);

			JSONObject object = json.getJSONObject("object");

			// 初始化应用分享的参数

			shareData.setImageUrl(object.getString("logoURL"));
			shareData.setTarget_url(object.getString("downloadURLAD"));
			shareData.setText(object.getString("appDescription"));
			shareData.setTitle(object.getString("appName"));
			// 活动id和邀请号
			appRecommenderId = object.getString("recommenderId");
			appActivityId = object.getString("activityId");
			// 是否有正在进行的活动
			shareData.setIsInProgress(Boolean.parseBoolean(object.getString("hasProgressingPopAct")));

			YtLog.i("YtCore:" + "活动是否进行中", object.getString("hasProgressingPopAct"));
			// 下载Logo图片以便于后续分享
			if (shareData.getImageUrl() != null) {
				String picPath = shareData.getImageUrl().substring(shareData.getImageUrl().lastIndexOf("/") + 1,shareData.getImageUrl().length());
				DownloadImage.down_file(shareData.getImageUrl(), YoutuiConstants.FILE_SAVE_PATH, picPath);
				shareData.setImagePath(Environment.getExternalStorageDirectory() + YoutuiConstants.FILE_SAVE_PATH + picPath);
				YtLog.i("YtCore:" + "网络图片保存到本地的路径", shareData.getImagePath());
			}
		} catch (UnsupportedEncodingException e) {
			mHandler.sendEmptyMessage(GET_APPSHAREDATA_FAIL);
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			mHandler.sendEmptyMessage(GET_APPSHAREDATA_FAIL);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			mHandler.sendEmptyMessage(GET_APPSHAREDATA_FAIL);
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			mHandler.sendEmptyMessage(GET_APPSHAREDATA_FAIL);
			e.printStackTrace();
			return false;
		}
		Message msg = Message.obtain(mHandler,GET_APPSHAREDATA_SUCCESS,shareData);
		
		mHandler.sendMessage(msg);
		return true;
	}

	/** ytcore初始化操作 */
	public static void init(final Context context) {
		// 读取手机信息
		getPhoneInfo(context);

		// 读取youtui_sdk.xml配置
		try {
			KeyInfo.parseXML(context);
		} catch (IOException e) {
			YtLog.e("YtCore:", "解析youtui_sdk.xml错误,请正确填写");
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			YtLog.e("YtCore:", "解析youtui_sdk.xml错误,请正确填写");
			e.printStackTrace();
		}

		// 初始化积分组件
		new Thread() {
			@Override
			public void run() {
				YtPoint.init(context, KeyInfo.youTui_AppKey);
			}
		}.start();
		// 创建youtui对象
		if (yt == null) {
			yt = new YtCore();
		}
	}

	/** 设置是否输出YtLog信息,开发时输出有助于定位错误,正式打包时请关闭输出 */
	public static void showLog(boolean bool) {
		YtLog.showLog = bool;
	}

	/** 读取手机和应用信息 */
	private static void getPhoneInfo(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			cardNum = tm.getSimSerialNumber();
		}
		if (tm != null) {
			imei = tm.getDeviceId(); /* 获取imei号 */
		}

		packName = context.getPackageName();
		res = context.getResources();
		appContext = context.getApplicationContext();
	}

	/**
	 * 发送短链接和长链接到服务器
	 * 
	 * @param appId 应用的友推key
	 * @param channelId 分享的频道
	 * @param url 长链接
	 * @param isShareContent 是否为内容分享
	 * @param uniqueCode  短链接
	 */
	public static void sendUrl(final String appId, final int channelId, final String url, final boolean isShareContent, final String uniqueCode) {
		// 发送连接地址
		if (isShareContent) {
			new Thread() {
				public void run() {

					HttpClient client = new DefaultHttpClient();

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					HttpPost post = new HttpPost(YoutuiConstants.YT_URL + "/activity/recordUrl");
					// 传入手机号，设备号，友推id,频道id
					params.add(new BasicNameValuePair("cardNum", cardNum));
					params.add(new BasicNameValuePair("imei", imei));
					params.add(new BasicNameValuePair("appId", appId));
					params.add(new BasicNameValuePair("channelId", String.valueOf(channelId)));
					// 用户分享的真实url
					params.add(new BasicNameValuePair("realUrl", url));
					// 传入唯一标示
					params.add(new BasicNameValuePair("virtualUrl", uniqueCode));
					// 是否为YOUTUI分享(可能为积分版本分享)
					params.add(new BasicNameValuePair("isYoutui", Boolean.toString(true)));
					try {
						post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						HttpResponse response = client.execute(post);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}

	}

	/**
	 * 处理链接
	 * 
	 * @param channelId
	 * @param shortUrl
	 */
	private void dealWithUrl(int channelId, String shortUrl,ShareData shareData) {
		if (shareData.isAppShare && !shareData.getIsInProgress()) {
			// 如果应用分享并且没有活动进行，使用拼接连接
			String url = "http://youtui.mobi/i/" + appActivityId + "/" + KeyInfo.youTui_AppKey + "/" + appRecommenderId + channelId;
			shareData.setTarget_url(url);
		} else if (!shareData.isAppShare) {
			// 如果是分享内容
			shareData.setTarget_url(YoutuiConstants.YOUTUI_LINK_URL + shortUrl);
		}
	}

	/** 系统分享 */
	private void moreShare(ShareData shareData) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("image/*");
		if (shareData.getImagePath() != null) {
			File file = new File(shareData.getImagePath());
			Uri uri = Uri.fromFile(file);
			it.putExtra(Intent.EXTRA_STREAM, uri);
		}
		it.putExtra(Intent.EXTRA_SUBJECT, shareData.getTitle());
		it.putExtra(Intent.EXTRA_TEXT, shareData.getText());
		it.putExtra(Intent.EXTRA_TITLE, shareData.getTitle());
		act.startActivity(Intent.createChooser(it,shareData.getTitle()));
	}

	/** 释放内存和统计应用使用情况 */
	public static void release(Context context) {
		YtPoint.release(context);
	}

}
