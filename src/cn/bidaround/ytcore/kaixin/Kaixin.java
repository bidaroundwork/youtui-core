/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bidaround.ytcore.kaixin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;


public class Kaixin {
	
	/**
	 * 组件申请时获得的api key，在调用接口时它代表该组件的唯一身份。
	 */
	public static final String API_KEY = YtPlatform.PLATFORM_KAIXIN.getAppId();
	
	/**
	 * 组件申请时获得的secret key
	 */
	public static final String SECRET_KEY = YtPlatform.PLATFORM_KAIXIN.getAppSecret(); 
	
	/**
	 * 组件申请时填写的网站地址
	 */
	public static String KX_AUTHORIZE_CALLBACK_URL = "http://localhost/";
	
	/**
	 * Kaixin授权地址
	 */
	public static final String KX_AUTHORIZE_URL = "http://api.kaixin001.com/oauth2/authorize";
	
	/**
	 * rest api接口地址
	 */
	public static String KX_REST_URL = "https://api.kaixin001.com";
	
	/**
	 * 常量字符串
	 */
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String ACCESS_DENIED = "access_denied";
	public static final String LOGIN_DENIED = "login_denied";

	/**
	 * 本地缓存字段
	 */
	private static final String KAIXIN_SDK_STORAGE = "kaixin_sdk_storage";
	private static final String KAIXIN_SDK_STORAGE_ACCESS_TOKEN = "kaixin_sdk_storage_access_token";
	private static final String KAIXIN_SDK_STORAGE_REFRESH_TOKEN = "kaixin_sdk_storage_refresh_token";
	private static final String KAIXIN_SDK_STORAGE_EXPIRES = "kaixin_sdk_storage_expires";

	/**
	 * Kaixin单实例
	 */
	private static Kaixin instance = null;
	
	public static synchronized Kaixin getInstance() {
		if (null == instance)
			instance = new Kaixin();
		return instance;
	}

	private X509TrustManager xtm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	private HostnameVerifier hnv = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	@SuppressLint("TrulyRandom")
	private Kaixin() {
		SSLContext sslContext = null;

		try {
			sslContext = SSLContext.getInstance("TLS");
			X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
			sslContext.init(null, xtmArray, new java.security.SecureRandom());
		} catch (GeneralSecurityException gse) {
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());
		}

		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}

	/**
	 * 完成登录并获取access_token(User-Agent Flow方式)
	 * 
	 * @param context
	 * @param listener
	 */
	public void authorize(final Context context,
			final AuthListener listener) {
		this.authorize(context, null, listener);
	}

	/**
	 * 完成登录并获取access_token(User-Agent Flow方式)
	 * 
	 * @param context
	 * @param permissions
	 * @param listener
	 */
	public void authorize(final Context context, String[] permissions,
			final AuthListener listener) {
		this.authorize(context, permissions, listener, KX_AUTHORIZE_CALLBACK_URL, "token");
	}

	/**
	 * 完成登录并获取access_token(User-Agent Flow方式)
	 * 
	 * @param context
	 * @param permissions
	 *            权限列表，参见http://wiki.open.kaixin001.com/index.php?id=OAuth%E6%96%
	 *            87%E6%A1%A3#REST%E6%
	 *            8E%A5%E5%8F%A3%E5%92%8COAuth%E6%9D%83%E9%99%90%E5%AF%B9%E7%85%
	 *            A 7%E8%A1%A8
	 * @param listener
	 * @param redirectUrl
	 * @param responseType
	 */
	private void authorize(final Context context, String[] permissions,
			final AuthListener listener, final String redirectUrl,
			String responseType) {
		CookieSyncManager.createInstance(context);

		Bundle params = new Bundle();
		params.putString("client_id", API_KEY);
		params.putString("response_type", responseType);
		params.putString("redirect_uri", redirectUrl);
		params.putString("state", "");
		params.putString("display", "page");
		params.putString("oauth_client", "1");

		 if (permissions != null && permissions.length > 0) {
			 String scope = TextUtils.join(" ", permissions);
			 params.putString("scope", scope);
		 }

		String url = KX_AUTHORIZE_URL + "?" + KaixinUtil.encodeUrl(params);
		
		new KaixinAuth(context, listener, url);
	}

	/**
	 * 判断会话是否有效
	 * 
	 * @return 会话是否有效 true : 有效  ； false : 无效
	 */
	public boolean isSessionValid(Context context) {
		return !TextUtils.isEmpty(getAccessToken(context))
				&& (getAccessExpires(context) != 0 
				&& (System.currentTimeMillis() < getAccessExpires(context)));
	}
	
	
	public void writeAccessToken(Context context, String accessToken, String refreshToken, String expiresIn){
		SharedPreferences pref = context.getSharedPreferences(KAIXIN_SDK_STORAGE, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
        editor.putString(KAIXIN_SDK_STORAGE_ACCESS_TOKEN, accessToken);
        editor.putString(KAIXIN_SDK_STORAGE_REFRESH_TOKEN, refreshToken);
        editor.putLong(KAIXIN_SDK_STORAGE_EXPIRES, System.currentTimeMillis() + Long.parseLong(expiresIn) * 1000);
        editor.commit();
	}

	public String getAccessToken(Context context) {
		SharedPreferences pref = context.getSharedPreferences(KAIXIN_SDK_STORAGE, Context.MODE_PRIVATE);
		return pref.getString(KAIXIN_SDK_STORAGE_ACCESS_TOKEN, null);
	}


	public String getRefreshToken(Context context) {
		SharedPreferences pref = context.getSharedPreferences(KAIXIN_SDK_STORAGE, Context.MODE_PRIVATE);
		return pref.getString(KAIXIN_SDK_STORAGE_REFRESH_TOKEN, null);
	}


	public long getAccessExpires(Context context) {
		SharedPreferences pref = context.getSharedPreferences(KAIXIN_SDK_STORAGE, Context.MODE_PRIVATE);
		return pref.getLong(KAIXIN_SDK_STORAGE_EXPIRES, 0);
	}

	/**
	 * 上传内容接口，采用multi-part post方式上传数据
	 * 
	 * @param params
	 *            参数列表
	 * @param photos
	 *            key-value形式的图像数据集， key为filename，
	 *            value为图像数据，参数类型可以是InputStream或byte[]
	 *            如果参数类型为InputStream，会在openUrl函数中将此流关闭
	 * @return 服务器返回的JSON串
	 * @throws FileNotFoundException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String uploadContent(Context context, String restInterface,
			Bundle params, Map<String, Object> photos)
			throws FileNotFoundException, MalformedURLException, IOException {

		if (params == null) {
			params = new Bundle();
		}
		params.putString("access_token", getAccessToken(context));
		return KaixinUtil.openUrl(context, KX_REST_URL + restInterface, "POST", params, photos);
	}
	
	/**
	 * 调用kaixin rest apis
	 * 
	 * @param context
	 *            应用环境
	 * @param restInterface
	 *            rest api接口
	 * @param params
	 *            key-value形式的参数集，key为参数名，value为参数值，数据类型可以是String或byte[]
	 * @param httpMethod
	 *            GET 或 POST
	 * @return 服务器返回的JSON串
	 * @throws FileNotFoundException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String request(Context context, String restInterface, Bundle params,
			String httpMethod) throws FileNotFoundException,
			MalformedURLException, IOException {

		if (params == null) {
			params = new Bundle();
		}
		params.putString("access_token", getAccessToken(context));
		return KaixinUtil.openUrl(context, KX_REST_URL + restInterface, httpMethod, params, null);
	}
	

//	/**
//	 * 读取本地缓存
//	 * 
//	 * @param context
//	 * @return 读取本地缓存是否成功
//	 */
//	public boolean loadStorage(Context context) {
//		SharedPreferences sp = context.getSharedPreferences(KAIXIN_SDK_STORAGE, Context.MODE_PRIVATE);
//		String accessToken = sp.getString(KAIXIN_SDK_STORAGE_ACCESS_TOKEN, null);
//		if (accessToken == null) {
//			return false;
//		}
//
//		String refreshToken = sp.getString(KAIXIN_SDK_STORAGE_REFRESH_TOKEN,
//				null);
//		if (refreshToken == null) {
//			return false;
//		}
//
//		long expires = sp.getLong(KAIXIN_SDK_STORAGE_EXPIRES, 0);
//		long currenct = System.currentTimeMillis();
//		if (expires < (currenct - ONE_HOUR)) {
//			clearStorage(context);
//			return false;
//		}
//		
//		writeAccessToken(context, accessToken, refreshToken, String.valueOf(expires));
//		return true;
//	}


//	/**
//	 * 清除本地缓存
//	 * 
//	 * @param context
//	 */
//	public void clearStorage(Context context) {
//		Editor editor = context.getSharedPreferences(KAIXIN_SDK_STORAGE,
//				Context.MODE_PRIVATE).edit();
//		editor.remove(KAIXIN_SDK_STORAGE_ACCESS_TOKEN);
//		editor.remove(KAIXIN_SDK_STORAGE_REFRESH_TOKEN);
//		editor.remove(KAIXIN_SDK_STORAGE_EXPIRES);
//		editor.commit();
//	}
}
