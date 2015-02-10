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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * 封装一些实用方法的帮助器类
 */
public class KaixinUtil {
	/**
	 * log标签
	 */
	public static final String LOG_TAG = "KAIXIN_ANDROID_SDK";

	/**
	 * 对于InputStream类型的上传数据，上传缓冲区大小
	 */
	private static final int UPLOAD_BUFFER_SIZE = 1024;

	/**
	 * 将用&号链接的URL参数转换成key-value形式的参数集
	 * 
	 * @param s
	 *            将用&号链接的URL参数
	 * @return key-value形式的参数集
	 */
	@SuppressWarnings("deprecation")
	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
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

	/**
	 * 将URL中的查询串转换成key-value形式的参数集
	 * 
	 * @param url
	 *            待解析的url
	 * @return key-value形式的参数集
	 */
	public static Bundle parseUrl(String url) {
		url = url.replace("#", "?");
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			Bundle ref = decodeUrl(u.getRef());
			if (ref != null)
				b.putAll(ref);
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	/**
	 * 将key-value形式的参数集转换成用&号链接的URL查询参数形式。
	 * 
	 * @param parameters
	 *            key-value形式的参数集
	 * @return 用&号链接的URL查询参数
	 */
	@SuppressWarnings("deprecation")
	public static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			if(parameters.getString(key)!=null){
				sb.append(URLEncoder.encode(key) + "="
						+ URLEncoder.encode(parameters.getString(key)));	
			}
		}
		return sb.toString();
	}

	/**
	 * 发送http请求
	 * 
	 * @param context
	 *            应用环境
	 * @param requestURL
	 *            接口地址
	 * @param httpMethod
	 *            GET 或 POST
	 * @param params
	 *            key-value形式的参数集，key为参数名，value为参数值，数据类型可以是String或byte[]
	 * @param photos
	 *            key-value形式的图像数据集， key为filename，
	 *            value为图像数据，参数类型可以是InputStream或byte[]
	 *            如果参数类型为InputStream，会在openUrl函数中将此流关闭
	 * @return 服务器返回的JSON串
	 * @throws IOException
	 */
	public static String openUrl(Context context, String requestURL,
			String httpMethod, Bundle params, Map<String, Object> photos)
			throws IOException {

		OutputStream os;

		if (httpMethod.equals("GET")) {
			requestURL = requestURL + "?" + encodeUrl(params);
		}

		URL url = new URL(requestURL);
		HttpsURLConnection conn = (HttpsURLConnection) getConnection(context, url);

		conn.setRequestProperty("User-Agent", System.getProperties()
				.getProperty("http.agent")
				+ " KaixinAndroidSDK");
		
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("Charsert", "UTF-8");

		if (!httpMethod.equals("GET")) {
			Bundle dataparams = new Bundle();
//			for (String key : params.keySet()) {
//				if (params.getByteArray(key) != null) {
//					dataparams.putByteArray(key, params.getByteArray(key));
//				}
//			}

			String BOUNDARY = KaixinUtil.md5(String.valueOf(System
					.currentTimeMillis())); // 数据分隔线
			String endLine = "\r\n";

			conn.setRequestMethod("POST");
			
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + BOUNDARY);

			conn.connect();
			os = new BufferedOutputStream(conn.getOutputStream());

			os.write(("--" + BOUNDARY + endLine).getBytes());
			os.write((encodePostBody(params, BOUNDARY)).getBytes());
			os.write((endLine + "--" + BOUNDARY + endLine).getBytes());

			if (!dataparams.isEmpty()) {

				for (String key : dataparams.keySet()) {
					os.write(("Content-Disposition: form-data; name=\"" + key
							+ "\"" + endLine).getBytes());
					os
							.write(("Content-Type: content/unknown" + endLine + endLine)
									.getBytes());
					os.write(dataparams.getByteArray(key));
					os.write((endLine + "--" + BOUNDARY + endLine).getBytes());
				}
			}

			if (photos != null && !photos.isEmpty()) {

				for (String key : photos.keySet()) {

					Object obj = photos.get(key);
					if (obj instanceof InputStream) {
						InputStream is = (InputStream) obj;
						try {
							os
									.write(("Content-Disposition: form-data; name=\"pic\";filename=\""
											+ key + "\"" + endLine).getBytes());
							os
									.write(("Content-Type:application/octet-stream\r\n\r\n")
											.getBytes());
							byte[] data = new byte[UPLOAD_BUFFER_SIZE];
							int nReadLength = 0;
							while ((nReadLength = is.read(data)) != -1) {
								os.write(data, 0, nReadLength);
							}
							os.write((endLine + "--" + BOUNDARY + endLine)
									.getBytes());
						} finally {
							try {
								if (null != is) {
									is.close();
								}
							} catch (Exception e) {
								Log.e(LOG_TAG,
										"Exception on closing input stream", e);
							}
						}
					} else if (obj instanceof byte[]) {
						byte[] byteArray = (byte[]) obj;
						os
								.write(("Content-Disposition: form-data; name=\"pic\";filename=\""
										+ key + "\"" + endLine).getBytes());
						os
								.write(("Content-Type:application/octet-stream\r\n\r\n")
										.getBytes());
						os.write(byteArray);
						os.write((endLine + "--" + BOUNDARY + endLine)
								.getBytes());
					} else {
						Log.e(LOG_TAG, "无效的参数类型");
					}
				}
			}

			os.flush();
		}

		String response = "";
		try {
			response = read(conn.getInputStream());
		} catch (FileNotFoundException e) {
			response = read(conn.getErrorStream());
		}
		return response;
	}

	/**
	 * 封装multi-part方式上传的数据段
	 * 
	 * @param parameters
	 *            key-value形式的参数集
	 * @param boundary
	 *            数据分割线
	 * @return 用于multi-part方式上传的数据
	 */
	public static String encodePostBody(Bundle parameters, String boundary) {
		if (parameters == null)
			return "";
		StringBuilder sb = new StringBuilder();

		for (String key : parameters.keySet()) {
			
//			if (parameters.getByteArray(key) != null) {
//				continue;
//			}

			sb.append("Content-Disposition: form-data; name=\"" + key
					+ "\"\r\n\r\n" + parameters.getString(key));
			sb.append("\r\n" + "--" + boundary + "\r\n");
		}

		return sb.toString();
	}

	/**
	 * 读取http请求的网络数据流
	 * 
	 * @param in
	 *            网络数据流
	 * @return 读取的网络数据流
	 * @throws IOException
	 */
	public static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	/**
	 * 清除cookie
	 * 
	 * @param context
	 */
	public static void clearCookies(Context context) {
		@SuppressWarnings("unused")
		CookieSyncManager cookieSyncMngr = CookieSyncManager
				.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	/**
	 * 将服务器返回带有错误信息的JSON串，转化成KaixinError对象
	 * 
	 * @param response
	 *            服务器返回带有错误信息的JSON串
	 * @return KaixinError对象
	 */
	public static KaixinError parseRequestError(String response) {
		if (response.indexOf("error_code") < 0)
			return null;
		return parseJson(response);
	}

	private static KaixinError parseJson(String response) {
		try {
			JSONObject json = new JSONObject(response);
			return new KaixinError(json.getInt("error_code"), json.optString(
					"error", ""), json.optString("request", ""), response);
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * 显示信息提示框
	 */
	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}

	public static String md5(String input) {
		String result = input;
		if (input != null) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(input.getBytes());
				BigInteger hash = new BigInteger(1, md.digest());
				result = hash.toString(16);
				if ((result.length() % 2) != 0) {
					result = "0" + result;
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 建立Http连接，如果apn的设置中配置了代理服务器，那么建立连接时也需要做相关的处理
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static HttpURLConnection getConnection(Context context, URL url)
			throws IOException {

		// 说明：联网时优先选择WIFI联网，如果WIFI没开或不可用，则使用移动网络
		HttpsURLConnection httpsURLConn = null;

		// 获取当前可用网络信息
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		// 如果当前是WIFI连接
		if (null != netInfo
				&& ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
			httpsURLConn = (HttpsURLConnection) url.openConnection();
		} else {// 非WIFI联网
			String proxyHost = android.net.Proxy.getDefaultHost();

			if (null == proxyHost) { // 直连模式
				httpsURLConn = (HttpsURLConnection) url.openConnection();
			} else { // 代理模式
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP,
						new InetSocketAddress(android.net.Proxy
								.getDefaultHost(), android.net.Proxy
								.getDefaultPort()));
				httpsURLConn = (HttpsURLConnection) url.openConnection(p);
			}
		}
		return httpsURLConn;
	}

	/**
	 * 判断字符串中是否有中文字符
	 * 
	 * @param str
	 *            字符串
	 * @return boolean
	 */
	public static boolean isContainChinese(String str) {
		if (str == null || str.trim().length() <= 0) {
			return false;
		}

		int len = str.length();
		for (int i = 0; i < len; i++) {
			char word = str.charAt(i);
			if ((word >= 0x4e00) && (word <= 0x9fbb)) {
				return true;
			}
		}
		return false;
	}
}
