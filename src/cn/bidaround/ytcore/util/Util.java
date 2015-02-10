package cn.bidaround.ytcore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import cn.bidaround.point.YtConstants;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * 工具类
 * @author youtui
 * @since 14/5/4
 */
public class Util {
	private static ProgressDialog mProgressDialog;
	
	/**
	 * 复制链接 复制链接 API 11之前用android.text.ClipboardManager; API
	 * 11之后用android.content.ClipboardManager
	 * 
	 * @param mHandler
	 * @param act
	 * @param message
	 */
	public static void copyLink(final Context act, final String message, final int channelId, final boolean isShareContent) {
		new Handler().post(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				if (android.os.Build.VERSION.SDK_INT >= 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setPrimaryClip(android.content.ClipData.newPlainText("link", message));
					if (clip.hasPrimaryClip()) {
						Toast.makeText(act,act.getResources().getString(act.getResources().getIdentifier("yt_copysuccess", "string", act.getPackageName())), Toast.LENGTH_SHORT).show();
						YtShareListener.sharePoint(act, channelId, isShareContent);
					} else {
						Toast.makeText(act,act.getResources().getString(act.getResources().getIdentifier("yt_copyfail", "string", act.getPackageName())), Toast.LENGTH_SHORT).show();
					}
				} else {
					android.text.ClipboardManager clip = (android.text.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setText(message);
					if (clip.hasText()) {
						Toast.makeText(act,act.getResources().getString(act.getResources().getIdentifier("yt_copysuccess", "string", act.getPackageName())), Toast.LENGTH_SHORT).show();
						YtShareListener.sharePoint(act, channelId, isShareContent);
					} else {
						Toast.makeText(act,act.getResources().getString(act.getResources().getIdentifier("yt_copyfail", "string", act.getPackageName())), Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	/**
	 * 读取图片
	 */
	public static Bitmap readBitmap(final String path) {
		try {
			FileInputStream stream = new FileInputStream(new File(path + "test.jpg"));
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 8;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 显示ProgressDialog
	 */
	public static final void showProgressDialog(final Context context, String message,final boolean isFinishActivity) {
		dismissDialog();
		mProgressDialog = new ProgressDialog(context);
		// 设置进度条风格，风格为圆形，旋转的
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 提示信息
		mProgressDialog.setMessage(message);
		// 设置ProgressDialog 的进度条是否不明确
		mProgressDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if(isFinishActivity){
					if(context instanceof Activity)
						((Activity) context).finish();
				}				
			}
		});
		mProgressDialog.show();
	}
	/**
	 * dismiss ProgressDialog
	 */
	public static final void dismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isNetworkConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		if (info != null&&info.isAvailable()) {
			return true;
		}
		return false;
	}
	
	/**
	 * dp to px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px to dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
    
    /**
     * 将Key-value转换成用&号链接的URL查询参数形式。
     * 
     * @param parameters
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(key + "=" + URLEncoder.encode(parameters.getString(key)));
        }
        return sb.toString();
    }
    
    
    /**
     * 将用&号链接的URL参数转换成key-value形式。
     * 
     * @param s
     * @return
     */
    @SuppressWarnings("deprecation")
	public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            params.putString("url", s);
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                if (v.length > 1) {
                    params.putString(v[0], URLDecoder.decode(v[1]));
                }
            }
        }
        return params;
    }
    
    public static String md5(String string) {
        if (string == null || string.trim().length() < 1) {
            return null;
        }
        try {
            return getMD5(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private static String getMD5(byte[] source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            StringBuffer result = new StringBuffer();
            for (byte b : md5.digest(source)) {
                result.append(Integer.toHexString((b & 0xf0) >>> 4));
                result.append(Integer.toHexString(b & 0x0f));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
	
	/**
	 * 获取SDCard的目录路径功能
	 * 
	 * @return
	 */
	public static String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}
	
	/**保存图片到sd卡*/
	public static void savePicToSd(Activity activity, Bitmap bit, String name, ShareData shareData, String type,YtShareListener listener,YtPlatform platform) {
		
		savePicToSd(bit, name, shareData, type);
		
		
		YtCore.getInstance().doShare(activity, platform, listener, shareData);
	}
	
	/**保存图片到sd卡*/
	public static void savePicToSd(Bitmap bit, String name, ShareData shareData, String type) {
		String savePath = Util.getSDCardPath() + "/youtui";
		try {
			File path = new File(savePath);
			// 文件
			String filepath = savePath + "/" + name;
			if ("url".equals(type) && shareData.getImageUrl() != null) {
				if (shareData.getImageUrl().endsWith(".png")) {
					filepath += ".png";
				} else if (shareData.getImageUrl().endsWith(".jpg")) {
					filepath += ".jpg";
				} else if (shareData.getImageUrl().endsWith(".jpeg")) {
					filepath += ".jpeg";
				} else if (shareData.getImageUrl().endsWith(".gif")) {
					filepath += ".gif";
				} else {
					filepath += ".png";
				}
			} else {
				filepath += ".png";
			}
			File file = new File(filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				bit.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
			shareData.setImagePath(filepath);
		} catch (Exception e) {
			YtLog.w("YouTui", "图片缓存到SDcard, 失败!");
		}
	}
	
	/**
	 * 分享链接做短链接处理，便于后台数据统计
	 */
	public static void dealWithUrl(int channelId, String shortUrl, String linkUrl, int statisticsType, ShareData shareData) {

		if (shareData.getTargetUrl() != null && !"".equals(shareData.getTargetUrl())) {
			// 如果是分享内容
			if (statisticsType == 1 || statisticsType == 0) {
				shareData.setTargetUrl(YtConstants.YOUTUI_LINK_URL + shortUrl);
			} else if (statisticsType == 2) {
				if (linkUrl != null && !linkUrl.endsWith("/")) {
					shareData.setTargetUrl("http://" + linkUrl + "/link/" + shortUrl);
				} else if (linkUrl != null && linkUrl.endsWith("/")) {
					shareData.setTargetUrl("http://" + linkUrl + "link/" + shortUrl);
				}

			} else if (statisticsType == 3) {
				String url = shareData.getTargetUrl();
				if (url.contains("?")) {
					shareData.setTargetUrl(url + "&youtui=" + shortUrl);
				} else {
					shareData.setTargetUrl(url + "?youtui=" + shortUrl);
				}
			}

		}
	}
	
	/** 读取手机序列号*/
	public static String getSimNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) 
			return tm.getSimSerialNumber();
		return null;
	}
	
	/** 读取手机IMEI*/
	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) 
			return tm.getDeviceId(); /* 获取imei号 */
		return null;
	}
	
	/** 
	 * 打开系统分享界面
	 */
	public static void openSystemShare(Activity activity, ShareData shareData) {
        Intent intent = new Intent(Intent.ACTION_SEND);  
        if (shareData.getImagePath() == null || shareData.getImagePath().equals("")) {  
            intent.setType("text/plain"); // 纯文本  
        } else {  
            File f = new File(shareData.getImagePath());  
            if (f != null && f.exists() && f.isFile()) {  
                intent.setType("image/*");  
                Uri u = Uri.fromFile(f);  
                intent.putExtra(Intent.EXTRA_STREAM, u);  
            }  
        }  
        intent.putExtra(Intent.EXTRA_SUBJECT, shareData.getTitle());  
        if(shareData.getShareType()==ShareData.SHARETYPE_IMAGEANDTEXT){
            if(shareData.getTargetUrl()!=null){
            	intent.putExtra(Intent.EXTRA_TEXT, shareData.getText()+shareData.getTargetUrl());
            	intent.putExtra("sms_body", shareData.getText()+shareData.getTargetUrl());  
            }else{
            	intent.putExtra(Intent.EXTRA_TEXT, shareData.getText());
            	intent.putExtra("sms_body", shareData.getText());
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        activity.startActivity(Intent.createChooser(intent, "分享"));
	}
	
	
	
	/**
	 * 转换成DP
	 */
	public static int getDensity(Context context, float value) {
		return (int)(context.getResources().getDisplayMetrics().density * value);
	}
	
	private static int getIdentifier(String name, String type){
		return YtCore.res.getIdentifier(name, type, YtCore.packName);
	}
	
	public static int getLayoutIdenty(String name){
		return getIdentifier(name, "layout");
	}
	
	public static int getIdIdenty(String name){
		return getIdentifier(name, "id");
	}
	
	/**
	 * 集成检测如果连续3次检测未发现错误，将自动关闭检测机制
	 */
	public static int readCheckConfigTime(){
    	SharedPreferences sp = YtCore.getAppContext().getSharedPreferences("check_config", Context.MODE_PRIVATE);
		return sp.getInt("config_time", 0);
    }
    
	/**
	 * 检测次数累加
	 * @param context
	 * @param token
	 */
    public static void addCheckConfigTime(){
    	int time = readCheckConfigTime();
    	if(time < Constant.MAX_SUC_CHECKCONFIG_TIME){
    		YtLog.w("YouTui", "):>友推集成检测机制已连续运行" + time +"次，未检测出异常.");
    		SharedPreferences sp = YtCore.getAppContext().getSharedPreferences("check_config", Context.MODE_PRIVATE);
    		sp.edit().putInt("config_time", time + 1).commit();
    	}
    	else{
    		YtLog.w("YouTui", "):>友推集成检测机制已连续运行" + time +"次，未检测出异常，即将自动关闭.");
    		YtCore.checkConfig(false);
    	}
    }
    
    /**
	 * 集成检测失败后清零
	 * @param context
	 * @param token
	 */
    public static void clearCheckConfigTime(){
		SharedPreferences sp = YtCore.getAppContext().getSharedPreferences("config_time", Context.MODE_PRIVATE);
		sp.edit().putInt("config_time", 0).commit();
    }
    
    public static Bundle parseUrlQueryString(String queryString) {
		Bundle params = new Bundle();
		if (!isNullOrEmpty(queryString)) {
			String array[] = queryString.split("&");
			for (String parameter : array) {
				String keyValuePair[] = parameter.split("=");
				try {
					if (keyValuePair.length == 2) {
						params.putString(URLDecoder.decode(keyValuePair[0], "UTF-8"),
								URLDecoder.decode(keyValuePair[1], "UTF-8"));
					} else if (keyValuePair.length == 1) {
						params.putString(URLDecoder.decode(keyValuePair[0], "UTF-8"), "");
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return params;
	}
    
    private static boolean isNullOrEmpty(String s) {
		return (s == null) || (s.length() == 0);
	}
    
    /**
	 * 新浪是否通过客户端分享
	 */
	public static boolean isSinaClientShare(Context context) {
		return AppHelper.isSinaWeiboExisted(context) && !"true".equals(KeyInfo.getKeyValue(context, YtPlatform.PLATFORM_SINAWEIBO, "IsWebShare"));
	}

}
