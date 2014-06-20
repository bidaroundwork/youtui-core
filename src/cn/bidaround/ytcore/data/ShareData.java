package cn.bidaround.ytcore.data;


/**
 * 该类为分享数据类，有些平台有分享限制 友推sdk会过滤掉无法分享的内容，只分享能被平台接受的内容
 * 如果需要分享图片，需要设置imageUrl和imagePath中的一项 如果imageUrl和imagePath都被设置，则优先使用imagePath
 * @author youtui 
 * @since 14/6/19
 */

public class ShareData  {

	//public static ShareData shareData;
	/**如果为app分享设置为true，如果为content分享则设置为false
	 * app分享的内容由开发者预先保留在友推服务器上
	 * content分享的内容由开发者给ShareData实例的各个字段赋值
	 **/
	public boolean isAppShare = true;
	/**分享的文字*/
	private String text = "加载分享内容失败,请查看网络连接情况...";
	/**分享的图片的本地路径*/
	private String imagePath;
	/**分享的描述*/
	private String description = "描述";
	/**分享的标题*/
	private String title = "分享";
	/**分享的图片的网络url*/
	private String imageUrl;
	/**分享的网页链接*/
	private String target_url;
	/**是否有活动正在进行*/
	private boolean isInProgress = false;
	
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

}
