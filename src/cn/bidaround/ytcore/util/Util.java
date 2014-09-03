package cn.bidaround.ytcore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * 工具类
 * @author youtui
 * @since 14/5/4
 */
public class Util {
	private static final String TAG = "SDK_Sample.Util";
	private static ProgressDialog mProgressDialog;
	/**将一个数组的元素填充到另一个数组用来保存元素信息*/
	public static void addArr(int[] fromArr, int[] toArr) {
		for (int i = 0; i < toArr.length; i++) {
			toArr[i] = fromArr[i];
		}
	}

	/**
	 * 复制链接 复制链接 API 11之前用android.text.ClipboardManager; API
	 * 11之后用android.content.ClipboardManager
	 * 
	 * @param mHandler
	 * @param act
	 * @param message
	 */
	public static void copyLink(Handler mHandler, final Context act, final String message) {
		mHandler.post(new Runnable() {
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			public void run() {
				if (android.os.Build.VERSION.SDK_INT >= 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setPrimaryClip(android.content.ClipData.newPlainText("link", message));
					if (clip.hasPrimaryClip()) {
						//Toast.makeText(act, "复制成功", Toast.LENGTH_SHORT).show();
						Toast.makeText(act,act.getResources().getString(act.getResources().getIdentifier("yt_copysuccess", "string", act.getPackageName())), Toast.LENGTH_SHORT).show();
					} else {
						//Toast.makeText(act, "复制失败，请手动复制", Toast.LENGTH_SHORT).show();
						Toast.makeText(act,act.getResources().getString(act.getResources().getIdentifier("yt_copyfail", "string", act.getPackageName())), Toast.LENGTH_SHORT).show();
					}
				} else {
					android.text.ClipboardManager clip = (android.text.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setText(message);
					if (clip.hasText()) {
						//Toast.makeText(act, "复制成功", Toast.LENGTH_SHORT).show();
						Toast.makeText(act,act.getResources().getString(act.getResources().getIdentifier("yt_copysuccess", "string", act.getPackageName())), Toast.LENGTH_SHORT).show();
					} else {
						//Toast.makeText(act, "复制失败，请手动复制", Toast.LENGTH_SHORT).show();
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
	public static final void showProgressDialog(final Activity act, String message,final boolean isFinishActivity) {
		dismissDialog();
		mProgressDialog = new ProgressDialog(act);
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
					act.finish();
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
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri) {
		Log.v(TAG, "getbitmap:" + imageUri);
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

			Log.v(TAG, "image download finished." + imageUri);
		} catch (IOException e) {
			e.printStackTrace();
			Log.v(TAG, "getbitmap bmp fail---");
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 拼接下载链接
	 */
/*	public static String setDownloadUrl(int channelId) {
		return "http://youtui.mobi/i/" + AppShareData.appActivityId + "/" + KeyInfo.youTui_AppKey + "/" + AppShareData.appRecommenderId + channelId;
	}*/
		
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


}