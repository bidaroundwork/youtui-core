package cn.bidaround.ytcore.login;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;

/**
 * 该类保存授权用户信息，用于第三方登录
 * 
 * @author youtui
 * @since 14/5/19
 */
public class AuthUserInfo {
	/** 新浪微博用户id */
	private String sinaUid;
	/** 新浪微博用户昵称 */
	private String sinaScreenname;
	/** 新浪微博用户头像url */
	private String sinaProfileImageUrl;
	/** 新浪微博用户性别 */
	private String sinaGender;
	/** 新浪微博用户名 */
	private String sinaName;

	private String sinaAccessToken;

	private String sinaUserInfoResponse;

	/** qq用户性别 */
	private String qqGender;
	/** qq用户头像url */
	private String qqImageUrl;
	/** qq用户名 */
	private String qqNickName;
	/** qq用户openid */
	private String qqOpenid;

	/** qq授权返回的字符串 */
	private String qqAuthResponse;

	/** qq获取用户信息返回字符串 */
	private String qqUserInfoResponse;

	/** 腾讯微博用户名 */
	private String tencentWbName;
	/** 腾讯微博用户昵称 */
	private String tencentWbNick;
	/** 腾讯微博用户openid */
	private String tencentWbOpenid;
	/** 腾讯微博用户头像url */
	private String tencentWbHead;
	/** 腾讯微博用户性别 */
	private String tencentWbGender;
	
	private String tencentWbBirthday;

	private String tencentUserInfoResponse;

	/** 开心网UID */
	private String kaixinUid;
	/** 开心网姓名 */
	private String kaixinName;
	/** 开心网性别 */
	private String kaixinGender;
	/** 开心网头像地址 */
	private String kaixinImageUrl;
	/** 开心网家乡 */
	private String KaixinHometown;
	/** 开心网城市 */
	private String kaixinCity;
	private String kaixinUserInfoResponse;

	/** 微信用户昵称 */
	private String wechatNickName;
	/** 微信用户所在的城市，显示拼音 */
	private String wechatCity;
	/** 微信用户所在的省份，显示拼音 */
	private String wechatProvince;
	/** 微信用户所在的国家 */
	private String wechatCountry;
	/** 微信用户头像地址 */
	private String wechatImageUrl;
	/** 微信用户性别 */
	private String wechatSex;
	/** 微信用户所用语言 */
	private String wechatLanguage;
	private String wechatOpenId;
	private String weChatUserInfoResponse;

	/***/
	public String getQqGender() {
		return qqGender;
	}

	/** 设置qq用户性别 */
	public void setQqGender(String qqGender) {
		this.qqGender = qqGender;
	}

	/** 获得qq用户头像url */
	public String getQqImageUrl() {
		return qqImageUrl;
	}

	/** 设置qq用户头像url */
	public void setQqImageUrl(String qqImageUrl) {
		this.qqImageUrl = qqImageUrl;
	}

	/** 获取qq用户昵称 */
	public String getQqNickName() {
		return qqNickName;
	}

	/** 设置qq用户昵称 */
	public void setQqNickName(String qqNickName) {
		this.qqNickName = qqNickName;
	}

	/** 获得qq openid */
	public String getQqOpenid() {
		return qqOpenid;
	}

	/** 设置qq openid */
	public void setQqOpenid(String qqOpenid) {
		this.qqOpenid = qqOpenid;
	}

	/** 获取腾讯微博用户名 */
	public String getTencentWbName() {
		return tencentWbName;
	}

	/** 设置腾讯微博用户名 */
	public void setTencentWbName(String tencentWbName) {
		this.tencentWbName = tencentWbName;
	}

	/** 获得腾讯微博用户昵称 */
	public String getTencentWbNick() {
		return tencentWbNick;
	}

	/** 设置腾讯微博用户昵称 */
	public void setTencentWbNick(String tencentWbNick) {
		this.tencentWbNick = tencentWbNick;
	}

	/** 获得腾讯微博openid */
	public String getTencentWbOpenid() {
		return tencentWbOpenid;
	}

	/** 设置腾讯微博openid */
	public void setTencentWbOpenid(String tencentWbOpenid) {
		this.tencentWbOpenid = tencentWbOpenid;
	}

	/** 获取腾讯微博用户头像url */
	public String getTencentWbHead() {
		return tencentWbHead;
	}

	/** 设置腾讯微博用户头像url */
	public void setTencentWbHead(String tencentWbHead) {
		this.tencentWbHead = tencentWbHead;
	}

	/** 获取新浪微博用户Id */
	public String getSinaUid() {
		return sinaUid;
	}

	/** 设置新浪微博用户id */
	public void setSinaUid(String sinaUid) {
		this.sinaUid = sinaUid;
	}

	public String getSinaUserInfoResponse() {
		return sinaUserInfoResponse;
	}

	public void setSinaUserInfoResponse(String sinaUserInfoResponse) {
		this.sinaUserInfoResponse = sinaUserInfoResponse;
	}

	public String getKaixinUserInfoResponse() {
		return kaixinUserInfoResponse;
	}

	public void setKaixinUserInfoResponse(String kaixinUserInfoResponse) {
		this.kaixinUserInfoResponse = kaixinUserInfoResponse;
	}

	/** 获得新浪微博用户昵称 */
	public String getSinaScreenname() {
		return sinaScreenname;
	}

	/** 设置新浪微博用户昵称 */
	public void setSinaScreenname(String sinaScreenname) {
		this.sinaScreenname = sinaScreenname;
	}

	/** 获得新浪微博用户头像url */
	public String getSinaProfileImageUrl() {
		return sinaProfileImageUrl;
	}

	/** 设置新浪微博用户头像url */
	public void setSinaProfileImageUrl(String sinaProfileImageUrl) {
		this.sinaProfileImageUrl = sinaProfileImageUrl;
	}

	/** 获得新浪微博用户性别 */
	public String getSinaGender() {
		return sinaGender;
	}

	/** 设置新浪微博用户性别 */
	public void setSinaGender(String sinaGender) {
		this.sinaGender = sinaGender;
	}

	/** 获得新浪微博用户名 */
	public String getSinaName() {
		return sinaName;
	}

	/** 设置新浪微博用户名 */
	public void setSinaName(String sinaName) {
		this.sinaName = sinaName;
	}

	/** 获得腾讯微博用户性别 */
	 public String getTencentWbGender() {
	 return tencentWbGender;
	 }
	/** 设置腾讯微博用户性别 */
	 public void setTencentWbGender(String tencentWbGender) {
	 this.tencentWbGender = tencentWbGender;
	 }

	public String getQqAuthResponse() {
		return qqAuthResponse;
	}

	public void setQqAuthResponse(String qqAuthResponse) {
		this.qqAuthResponse = qqAuthResponse;
	}

	public String getQqUserInfoResponse() {
		return qqUserInfoResponse;
	}

	public void setQqUserInfoResponse(String qqUserInfoResponse) {
		this.qqUserInfoResponse = qqUserInfoResponse;
	}

	public String getKaixinUid() {
		return kaixinUid;
	}

	public void setKaixinUid(String kaixinUid) {
		this.kaixinUid = kaixinUid;
	}

	public String getKaixinName() {
		return kaixinName;
	}

	public void setKaixinName(String kaixinName) {
		this.kaixinName = kaixinName;
	}

	public String getKaixinGender() {
		return kaixinGender;
	}

	public void setKaixinGender(String kaixinGender) {
		this.kaixinGender = kaixinGender;
	}

	public String getKaixinImageUrl() {
		return kaixinImageUrl;
	}

	public void setKaixinImageUrl(String kaixinImageUrl) {
		this.kaixinImageUrl = kaixinImageUrl;
	}

	public String getKaixinHometown() {
		return KaixinHometown;
	}

	public void setKaixinHometown(String kaixinHometown) {
		KaixinHometown = kaixinHometown;
	}

	public String getKaixinCity() {
		return kaixinCity;
	}

	public void setKaixinCity(String kaixinCity) {
		this.kaixinCity = kaixinCity;
	}

	public String getWechatNickName() {
		return wechatNickName;
	}

	public void setWechatNickName(String wechatNickName) {
		this.wechatNickName = wechatNickName;
	}

	public String getWechatCity() {
		return wechatCity;
	}

	public void setWechatCity(String wechatCity) {
		this.wechatCity = wechatCity;
	}

	public String getWechatProvince() {
		return wechatProvince;
	}

	public void setWechatProvince(String wechatProvince) {
		this.wechatProvince = wechatProvince;
	}

	public String getWechatCountry() {
		return wechatCountry;
	}

	public void setWechatCountry(String wechatCountry) {
		this.wechatCountry = wechatCountry;
	}

	public String getWechatImageUrl() {
		return wechatImageUrl;
	}

	public void setWechatImageUrl(String wechatImageUrl) {
		this.wechatImageUrl = wechatImageUrl;
	}

	public String getWechatSex() {
		return wechatSex;
	}

	public void setWechatSex(String wechatSex) {
		this.wechatSex = wechatSex;
	}

	public String getWechatLanguage() {
		return wechatLanguage;
	}

	public void setWechatLanguage(String wechatLanguage) {
		this.wechatLanguage = wechatLanguage;
	}

	public String getWechatOpenId() {
		return wechatOpenId;
	}

	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId;
	}

	public String getSinaAccessToken() {
		return sinaAccessToken;
	}

	public void setSinaAccessToken(String sinaAccessToken) {
		this.sinaAccessToken = sinaAccessToken;
	}

	public static String getSinaWbEx(Context context, AuthUserInfo info) {
		return getSinaWbEx(context, info.getSinaAccessToken(),
				info.getSinaUid());
	}

	public static String getSinaWbEx(Context context, String accesstoken,
			String uid) {
		HttpClient client = new DefaultHttpClient();
		String url = "https://api.weibo.com/2/users/show.json";
		url += "?" + "access_token=" + accesstoken;
		url += "&" + "uid=" + uid;
		String ret = null;
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse resp = client.execute(get);
			ret = EntityUtils.toString(resp.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String getWeChatUserInfoResponse() {
		return weChatUserInfoResponse;
	}

	public void setWeChatUserInfoResponse(String weChatUserInfoResponse) {
		this.weChatUserInfoResponse = weChatUserInfoResponse;
	}

	public String getTencentUserInfoResponse() {
		return tencentUserInfoResponse;
	}

	public String getTencentWbBirthday() {
		return tencentWbBirthday;
	}

	public void setTencentWbBirthday(String tencentWbBirthday) {
		this.tencentWbBirthday = tencentWbBirthday;
	}

	public void setTencentUserInfoResponse(String tencentUserInfoResponse) {
		this.tencentUserInfoResponse = tencentUserInfoResponse;
	}

	public boolean isQqPlatform() {
		return qqUserInfoResponse != null;
	}

	public boolean isTencentWbPlatform() {
		return tencentWbOpenid != null;
	}

	public boolean isSinaPlatform() {
		return sinaUserInfoResponse != null;
	}

	public boolean isWechatPlatform() {
		return weChatUserInfoResponse != null;
	}

	public boolean isKaixinPlatform() {
		return kaixinUserInfoResponse != null;
	}
}
