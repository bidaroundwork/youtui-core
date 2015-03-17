package cn.bidaround.ytcore.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import android.app.Activity;
import android.graphics.Bitmap;
import cn.bidaround.point.YtAcceptor;
import cn.bidaround.point.YtConstants;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.util.Request.Method;
import cn.bidaround.ytcore.util.Response.ErrorListener;
import cn.bidaround.ytcore.util.Response.Listener;

/**
 * 网络方法
 * 
 * @author youtui
 * @since 2015/1/13
 */
public class HttpUtils {

	public static void getAppShareData(ShareData data) {
		getAppShareData(null, data, null, null);
	}

	/**
	 * 获取应用在友推服务器配置的应用分享信息
	 */
	public static void getAppShareData(final Activity activity, final ShareData data, final YtShareListener listener, final YtPlatform platform) {
		RequestQueue queue = Volley.newRequestQueue(YtCore.getAppContext());
		Listener<String> jsonListener = new Listener<String>() {
			@Override
			public void onResponse(String str) {
				// YtLog.d("ShareData", str);
				try {
					JSONObject json = new JSONObject(str);
					boolean success = json.getBoolean("success");
					if (success) {
						JSONObject object = json.getJSONObject("object");
						data.setImageUrl(object.getString("sharePicUrl"));
						data.setTargetUrl(object.getString("shareLink"));
						data.setText(object.getString("shareDescription"));
						data.setTitle(object.getString("shareTitle"));
						data.setShareType(ShareData.SHARETYPE_IMAGEANDTEXT);
						if (activity == null && platform == null)
							saveImage(data);
						else
							saveImage(activity, data, listener, platform);
					} else {
						YtLog.e("YtCore", "该应用未配置分享信息");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		ErrorListener strError = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		};

		StringRequest strReq = new StringRequest(Method.POST, YtConstants.SHARE_CONTENT, jsonListener, strError) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("appId", KeyInfo.youTui_AppKey);
				map.put("sign", YtAcceptor.md5Encrypt(KeyInfo.youTui_AppKey, KeyInfo.youTui_AppSecret));
				map.put("imei", YtCore.imei);
				return map;
			}
		};
		queue.add(strReq);
	}

	/** 如果设置的是网络图片的话，需要保存网络图片到本地 */
	public static void saveImage(ShareData data) {
		saveImage(null, data, null, null);
	}

	/** 如果设置的是网络图片的话，需要保存网络图片到本地 */
	public static void saveImage(final Activity activity, final ShareData data, final YtShareListener listener, final YtPlatform platform) {
		RequestQueue queue = Volley.newRequestQueue(YtCore.getAppContext());
		Listener<Bitmap> imageListener = new Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				if (activity == null && platform == null)
					Util.savePicToSd(response, CMyEncrypt.shortUrl(data.getTargetUrl()), data, "url");
				else
					Util.savePicToSd(activity, response, CMyEncrypt.shortUrl(data.getTargetUrl()), data, "url", listener, platform);
			}
		};
		ErrorListener error = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (error != null)
					YtLog.e("YouTui", error.getMessage());
			}

		};
		ImageRequest req = new ImageRequest(data.getImageUrl(), imageListener, 800, 800, null, error);
		queue.add(req);
	}

	/**
	 * 发送短链接和长链接到服务器
	 * 
	 * @param appId
	 *            应用的友推key
	 * @param channelId
	 *            分享的频道
	 * @param url
	 *            长链接
	 * @param isShareContent
	 *            是否为内容分享
	 * @param uniqueCode
	 *            短链接
	 * @param publishTime
	 *            文章发布时间 格式为2014-02-02 15:30:30
	 * @param title
	 * @param description
	 * @param id
	 * @param imageUrl
	 */
	public static void sendUrl(final int channelId, final String url, final boolean isShareContent, final String uniqueCode, final String cardNum,
			final String imei, final String publishTime, final String title, final String description, final String id, final String imageUrl) {
		// 发送连接地址
		if (isShareContent) {
			new Thread() {
				public void run() {

					HttpClient client = new DefaultHttpClient();
					List<NameValuePair> params = buildBaseParams();
					HttpPost post = new HttpPost(YtConstants.RECORD_URL);
					// 传入手机号，设备号，友推id,频道id
					params.add(new BasicNameValuePair("cardNum", cardNum));
					params.add(new BasicNameValuePair("imei", imei));
					params.add(new BasicNameValuePair("channelId", String.valueOf(channelId)));
					// 用户分享的真实url
					params.add(new BasicNameValuePair("realUrl", url));
					// 传入唯一标示
					params.add(new BasicNameValuePair("virtualUrl", uniqueCode));
					// 是否为YOUTUI分享(可能为积分版本分享)
					params.add(new BasicNameValuePair("isYoutui", Boolean.toString(true)));

					// 文章发布时间
					params.add(new BasicNameValuePair("publicTime", publishTime));
					// 文章标题
					params.add(new BasicNameValuePair("title", title));
					// 文章文章描述
					params.add(new BasicNameValuePair("description", description));
					// 文章ID
					params.add(new BasicNameValuePair("artId", id));
					// 图片Url
					params.add(new BasicNameValuePair("imgUrl", imageUrl));

					try {
						post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						HttpResponse response = client.execute(post);
						try {
							JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
							if(!obj.has("success") || !obj.getBoolean("success"))
								YtLog.e("YouTui", "短链接设置失败");
						} catch (Exception e) {
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	public static void getStatisticsType() {
		new Thread() {
			@Override
			public void run() {
				try {
					String response = getLinkType();
					if (response != null) {
						JSONObject json = new JSONObject(response);
						JSONObject object = json.getJSONObject("object");
						int statisticsType = object.getInt("statisticsType");
						YtCore.getInstance().setStatisticsType(statisticsType);
						if (statisticsType == 2)
							YtCore.getInstance().setLinkUrl(object.getString("linkUrl"));
					}
				} catch (JSONException e) {
				}
			}
		}.start();
	}

	/**
	 * 获取分享链接的类型
	 */
	private static String getLinkType() {
		String str = null;
		HttpParams httpParam = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParam, 10000);
		HttpClient client = new DefaultHttpClient(httpParam);
		HttpPost post = new HttpPost(YtConstants.INFO_BY_ID);
		List<NameValuePair> params = buildBaseParams();
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			str = EntityUtils.toString(entity);
			// YtLog.d("getLinkType", str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 服务器请求，必须的基本参数
	 */
	public static List<NameValuePair> buildBaseParams() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appId", KeyInfo.youTui_AppKey));
		params.add(new BasicNameValuePair("sign", YtAcceptor.md5Encrypt(KeyInfo.youTui_AppKey, KeyInfo.youTui_AppSecret)));
		return params;
	}
}
