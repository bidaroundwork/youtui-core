package cn.bidaround.ytcore.dao;

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

import cn.bidaround.point.YoutuiConstants;
import cn.bidaround.point.YtLog;
import cn.bidaround.ytcore.data.KeyInfo;

/**
 * YtCore网络操作
 * 
 * @author youtui
 * 
 */
public class YtCoreDao {
	private static final String GET_LINK_TYPE = "/app/infoById";

	/**
	 * 获取分享链接的类型
	 */
	public static String getLinkType() {
		String str = null;
		HttpParams httpParam = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParam, 10000);
		HttpClient client = new DefaultHttpClient(httpParam);
		HttpPost post = new HttpPost(YoutuiConstants.YT_URL + GET_LINK_TYPE);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appId", KeyInfo.youTui_AppKey));
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			str = EntityUtils.toString(entity);
			YtLog.d("getLinkType", str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
}
