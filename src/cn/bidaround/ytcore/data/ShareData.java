package cn.bidaround.ytcore.data;

import java.io.Serializable;
import cn.bidaround.ytcore.YtCore;


/**
 * 该类为分享数据类，有些平台有分享限制 友推sdk会过滤掉无法分享的内容，只分享能被平台接受的内容
 * 如果需要分享图片，需要设置imageUrl和imagePath中的一项 如果imageUrl和imagePath都被设置，则优先使用imagePath
 * @author youtui 
 * @since 14/6/19
 */

public class ShareData implements Serializable{
	
	public static ShareData instance;
	/**如果为app分享设置为true，如果为content分享则设置为false
	 * app分享的内容由开发者预先保留在友推服务器上
	 * content分享的内容由开发者给ShareData实例的各个字段赋值
	 **/
	public boolean isAppShare = false;
	/**分享的文字*/
	private String text = YtCore.res.getString(YtCore.res.getIdentifier("yt_getsharecontent_fail", "string", YtCore.packName));
	/**分享的图片的本地路径*/
	private String imagePath;
	/**分享的描述*/
	private String description = YtCore.res.getString(YtCore.res.getIdentifier("yt_description", "string", YtCore.packName));
	/**分享的标题*/
	private String title = YtCore.res.getString(YtCore.res.getIdentifier("yt_share", "string", YtCore.packName));
	/**分享的图片的网络url*/
	private String imageUrl;
	/**分享的网页链接*/
	private String target_url;
	/**是否有活动正在进行*/
	private boolean isInProgress = false;
	/**图文分享，该分享类型为默认分享类型，如果开发者未设置，则使用默认分享类型*/
	public static final int SHARETYPE_IMAGEANDTEXT = 0;
	/**纯图分享,qq空间不支持纯图分享*/
	public static final int SHARETYPE_IMAGE = 1;
	/**纯文字分享,qq和qq空间不支持纯文字分享*/
	public static final int SHARETYPE_TEXT = 2;
	/**分享音乐*/
	public static final int SHARETYPE_MUSIC = 3;
	/**分享音乐*/
	public static final int SHARETYPE_VIDEO = 4;
	
	/**用来判断分享的类型*/
	private int shareType = SHARETYPE_IMAGEANDTEXT;
	
	private int imageType = 0;
	/**分享图片类型为网络图片*/
	public static final int IMAGETYPE_INTERNET = 1;
	/**分享图片类型为本地sd卡图片*/
	public static final int IMAGETYPE_SDCARD= 2;
	/**分享图片类型为应用资源图片*/
	public static final int IMAGETYPE_APPRESOURE = 3;
	
	private String musicUrl;
	
	private String videoUrl;
	
	
	
	
	private String image;
	
	public ShareData(){
		instance = this;
	}
	
	public static ShareData getInstance(){
		return instance;
	}
	
	public void setIsInProgress(boolean isInProgress){
		this.isInProgress = isInProgress;
	}
	/**查看是否有活动正在进行中*/
	public boolean getIsInProgress(){
		return isInProgress;
	}
	public void setIsAppShare(boolean isAppShare) {
		this.isAppShare = isAppShare;
	}

	public String getTarget_url() {
		return target_url;
	}
	/**
	 * 网页链接地址
	 */
	public void setTarget_url(String target_url) {
		this.target_url = target_url;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 设置分享内容的描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getText() {
		return text;
	}

	/**
	 * 设置待分享的文字内容
	 */
	public void setText(String text) {
		this.text = text;
	}

	public String getImagePath() {
		return imagePath;
	}

	/**
	 * 待分享的本地图片路径
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * 待分享的内容标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * 设置分享的网络图片url
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**获取分享的类型*/
	public int getShareType() {
		return shareType;
	}
	/**设置分享的类型*/
	public void setShareType(int shareType) {
		this.shareType = shareType;
	}
	
	/**
	 * 
	 * @param imageType 图片的类型
	 * IMAGETYPE_NET = 1                                      网络图片
	 * IMAGETYPE_SDCARD= 2                               本地sd卡图片 
	 * IMAGETYPE_APPRESOURE=3                    应用资源图片
	 * 
	 * @param image 图片地址
	 * imageType=IMAGETYPE_NET        传入网络图片url
	 * imageType=IMAGETYPE_SDCARD     传入本地sd卡路径
	 * imageType=IMAGETYPE_APPRESOURE 传入应用图片资源id(转为字符串)
	 * 
	 */
	public void setImage(int imageType,String image){
		this.imageType = imageType;
		this.image = image;
	}

	public int getImageType() {
		return imageType;
	}

	public String getImage() {
		return image;
	}

	public String getMusicUrl() {
		return musicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}	
}
