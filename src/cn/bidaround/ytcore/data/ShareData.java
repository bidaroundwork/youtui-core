package cn.bidaround.ytcore.data;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.util.HttpUtils;
import cn.bidaround.ytcore.util.Util;

/**
 * 该类为分享数据类，有些平台有分享限制 友推sdk会过滤掉无法分享的内容，只分享能被平台接受的内容
 * 如果需要分享图片，需要设置imageUrl和imagePath中的一项 如果imageUrl和imagePath都被设置，则优先使用imagePath
 * 
 * @author youtui
 * @since 14/6/19
 */

public class ShareData implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * 如果为app分享设置为true，如果为content分享则设置为false app分享的内容由开发者预先保留在友推服务器上
	 * content分享的内容由开发者给ShareData实例的各个字段赋值
	 **/
	private boolean isAppShare = false;

	/** 分享的文字 */
	private String text = "";

	/** 分享的图片的本地路径 */
	private String imagePath;

	/** 分享的描述 */
	private String description;

	/** 分享的标题 */
	private String title;

	/** 分享的图片的网络url */
	private String imageUrl;

	/** 分享的网页链接 */
	private String targetUrl;

	private String musicUrl;

	private String videoUrl;

	private String image;
	
	/** 子页面统计在用户系统中的id，区分统计*/
	private String targetId;
	
	/** 发表时间*/
	private String publishTime;

	/** 是否有活动正在进行 */
	private boolean isInProgress = false;

	/** 图文分享，该分享类型为默认分享类型，如果开发者未设置，则使用默认分享类型 */
	public static final int SHARETYPE_IMAGEANDTEXT = 0;

	/** 纯图分享,qq空间不支持纯图分享 */
	public static final int SHARETYPE_IMAGE = 1;

	/** 纯文字分享,qq和qq空间不支持纯文字分享 */
	public static final int SHARETYPE_TEXT = 2;

	/** 分享音乐 */
	public static final int SHARETYPE_MUSIC = 3;

	/** 分享音乐 */
	public static final int SHARETYPE_VIDEO = 4;

	/** 用来判断分享的类型 */
	private int shareType = -1;

	private int imageType = 0;

	/** 分享图片类型为网络图片 */
	public static final int IMAGETYPE_INTERNET = 1;

	/** 分享图片类型为本地sd卡图片 */
	public static final int IMAGETYPE_SDCARD = 2;

	/** 分享图片类型为应用资源图片 */
	public static final int IMAGETYPE_APPRESOURE = 3;

	/** 设置分享图片为资源图片 */
	private int resourceImage;

	public void setIsInProgress(boolean isInProgress) {
		this.isInProgress = isInProgress;
	}

	/** 查看是否有活动正在进行中 */
	public boolean getIsInProgress() {
		return isInProgress;
	}

	public void setIsAppShare(boolean isAppShare) {
		this.setAppShare(isAppShare);
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	/**
	 * 网页链接地址
	 */
	public void setTargetUrl(String target_url) {
		this.targetUrl = target_url;
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
		HttpUtils.saveImage(this);
	}

	/** 获取分享的类型 */
	public int getShareType() {
		return shareType;
	}

	/** 设置分享的类型 */
	public void setShareType(int shareType) {
		this.shareType = shareType;
	}

	/**
	 * @param imageType
	 *            图片的类型 IMAGETYPE_NET = 1 网络图片 IMAGETYPE_SDCARD= 2 本地sd卡图片
	 *            IMAGETYPE_APPRESOURE=3 应用资源图片,选择该参数时第二个参数设为图片资源id(转为字符串)
	 */
	public void setImage(int imageType, final String image) {
		this.imageType = imageType;
		this.image = image;
		if (imageType == IMAGETYPE_INTERNET) {
			setImageUrl(image);
		} else if (imageType == IMAGETYPE_SDCARD) {
			setImagePath(image);
		} else if (imageType == IMAGETYPE_APPRESOURE) {
			final Bitmap bit = BitmapFactory.decodeResource(YtCore.res,
					Integer.valueOf(image));
			new Thread() {
				public void run() {
					Util.savePicToSd(bit, image, ShareData.this, "res");
				};
			}.start();
		}
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

	public boolean isAppShare() {
		return isAppShare;
	}

	public void setAppShare(boolean isAppShare) {
		this.isAppShare = isAppShare;
		if (isAppShare)
			HttpUtils.getAppShareData(this);
	}

	@Override
	public ShareData clone() throws CloneNotSupportedException {
		return (ShareData) super.clone();
	}

	public int getResourceImage() {
		return resourceImage;
	}

	public void setResourceImage(int resourceImage) {
		this.resourceImage = resourceImage;
		Bitmap bit = BitmapFactory.decodeResource(YtCore.res, resourceImage);
		Util.savePicToSd(bit, resourceImage + "", this, "res");
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
}
