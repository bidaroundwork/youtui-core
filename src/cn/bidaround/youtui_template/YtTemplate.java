package cn.bidaround.youtui_template;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import cn.bidaround.point.YtPoint;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.util.CheckConfig;
import cn.bidaround.ytcore.util.CheckShareData;
import cn.bidaround.ytcore.util.Constant;
import cn.bidaround.ytcore.util.Util;
import cn.bidaround.ytcore.util.YtToast;

/**
 * 友推模板,用于使用友推分享界面的开发者
 * 
 * @author youtui
 * @since 14/6/19
 * 
 */
public class YtTemplate {
	private Activity act;
	private int youTuiViewType;
	private HashMap<YtPlatform, YtShareListener> listenerMap = new HashMap<YtPlatform, YtShareListener>();
	private HashMap<YtPlatform, ShareData> shareDataMap = new HashMap<YtPlatform, ShareData>();
	private ShareData shareData;
	private ShareData capData;
	private ArrayList<String> enList = new ArrayList<String>();
	/**分享后分享框是否消失*/
	private boolean dismissAfterShare;
	private boolean screencapVisible = true;
	private boolean hasAct;
	private int popwindowHeight;
	private static boolean isOnAction = false;
	private int animationStyle = 0;
	private static String actionName = null;

	/** 友推默认的动画效果 */
	public static final int ANIMATIONSTYLE_DEFAULT = 0;
	/** 系统，无动画效果 */
	public static final int ANIMATIONSTYLE_SYSTEM = 1;

	public YtTemplate(Activity act, int youTuiViewType, boolean hasAct) {
		this.act = act;
		this.youTuiViewType = youTuiViewType;
		this.hasAct = hasAct;
		enList.addAll(KeyInfo.enList);
	}

	/**
	 * 为单独的平台添加分享数据
	 * 
	 * @param platform
	 * @param shareData
	 */
	public void addData(YtPlatform platform, ShareData shareData) {
		shareDataMap.put(platform, shareData);
	}

	/**
	 * 为所有的平台添加分享数据
	 * 
	 * @param platform
	 * @param shareData
	 */
	public void addDatas(ShareData shareData) {
		if (KeyInfo.enList != null && KeyInfo.enList.size() > 0) {
			for (String str : KeyInfo.enList) {
				shareDataMap.put(YtPlatform.getPlatformByName(str), shareData);
			}
		}
	}

	/**
	 * 获取该平台在分享框的位置
	 * 
	 * @param platform
	 * @return
	 */
	public int getIndex(YtPlatform platform) {
		return enList.indexOf(platform.getName());
	}

	public YtPlatform getPlatform(int index) {
		String formName = enList.get(index);
		for (YtPlatform form : YtPlatform.values()) {
			if (formName != null && formName.equals(form.getName()))
				return form;
		}
		return null;
	}

	public YtPlatform getPlatform(int index, int pageIndex, int itemAmount) {
		if (itemAmount == Integer.MAX_VALUE) {
			itemAmount = 0;
		}
		return getPlatform(index + pageIndex * itemAmount);
	}

	/**
	 * 获取指定平台的分享信息
	 * 
	 * @param platform
	 * @return 指定平台的分享信息
	 */
	public ShareData getData(YtPlatform platform) {
		return shareDataMap.get(platform);
	}

	/** 移除平台 */
	public void removePlatform(YtPlatform platform) {
		enList.remove(platform.getName());
	}

	/**
	 * 添加分享监听
	 * 
	 * @param platform
	 * @param listener
	 */
	public void addListener(YtPlatform platform, YtShareListener listener) {
		listenerMap.put(platform, listener);
	}

	/**
	 * 给所有平台添加分享监听
	 * 
	 * @param platform
	 * @param listener
	 */
	public void addListeners(YtShareListener listener) {
		if (KeyInfo.enList != null && KeyInfo.enList.size() > 0) {
			for (String str : KeyInfo.enList) {
				listenerMap.put(YtPlatform.getPlatformByName(str), listener);
			}
		}
	}

	/**
	 * 获得监听事件
	 */
	public YtShareListener getListener(YtPlatform platform) {
		return listenerMap.get(platform);
	}
	
	/**如果用户设置有积分，刷新积分项*/
	public void refreshPoint(){
		if(hasAct){
			new Thread(){
				public void run() {
					YtPoint.refresh(act);
				};
			}.start();
		}
	}
	/** 调出分享界面 */
	public void show() {
		if(YtCore.getInstance().isCheckConfig()){
			String checkConfigError = YtCore.getInstance().getCheckConfigError();
			if(checkConfigError != null){
				Toast.makeText(act, checkConfigError, Toast.LENGTH_LONG).show();
				return;
			}
		}
		refreshPoint();
		// 判断该应用是否设置了活动
		if (!isOnAction()) 
			getActionInfo(act);
		
		if (youTuiViewType == YouTuiViewType.BLACK_POPUP) 
			new BlackGridTemplate(act, hasAct, this, shareData, enList).show();
		else if (youTuiViewType == YouTuiViewType.WHITE_LIST)
			new WhiteListTemplate(act, hasAct, this, shareData, enList).show();
		else if (youTuiViewType == YouTuiViewType.WHITE_GRID) 
			new WhiteGridTemplate(act, hasAct, this, shareData, enList).show();
		else if(youTuiViewType == YouTuiViewType.WHITE_GRID_CENTER)
			new WhiteGridCenterTemplate(act, hasAct, this, shareData, enList).show();
	}

	/** 调出截屏分享界面 */
	public void showScreenCap() {
		refreshPoint();
		// 判断该应用是否设置了活动
		if (!isOnAction()) {
			getActionInfo(act);
		}
		TemplateUtil.GetandSaveCurrentImage(act, false);
		Intent it = new Intent(act, ScreenCapEditActivity.class);
		it.putExtra("viewType", getViewType());
		if (shareData != null) {
			if (shareData.isAppShare()) {
				it.putExtra("target_url", YtCore.getInstance().getTargetUrl());
			} else {
				it.putExtra("target_url", shareData.getTargetUrl());
			}
		}
		it.putExtra("capdata", getCapData());
		it.putExtra("shareData", shareData);
		act.startActivity(it);
	}

	/** 调出分享界面,分享的图片为截屏(替换分享内容中的图片) */
	public void showScreenCapShare() {
		refreshPoint();
		// 判断该应用是否设置了活动
		if (!isOnAction()) {
			getActionInfo(act);
		}
		if (youTuiViewType == YouTuiViewType.BLACK_POPUP) {
			new BlackGridTemplate(act, hasAct, this, shareData, enList).show();
		} else if (youTuiViewType == YouTuiViewType.WHITE_LIST) {
			new WhiteListTemplate(act,hasAct, this, shareData, enList).show();
		} else if (youTuiViewType == YouTuiViewType.WHITE_GRID) {
			new WhiteGridTemplate(act, hasAct, this, shareData, enList).show();
		}
	}

	public String getScreenCapPath() {
		return TemplateUtil.getSDCardPath() + "/youtui/yt_screen.png";
	}

	/**
	 * 获取分享模板的类型
	 * 
	 * @return
	 */
	public int getViewType() {
		return youTuiViewType;
	}

	/**
	 * 关闭主分享界面
	 */
	public static void dismiss() {
		if(YTBasePopupWindow.getInstance()!=null){
			YTBasePopupWindow.getInstance().dismiss();
		}
	}
	
	/**
	 * YtTemplate初始化,传入用户id,开发者应该在程序的入口调用,初始化后后续操作才能正常进行
	 * 
	 * @param act
	 */
	public static void init(final Activity actvity, String appUserId) {
		YtCore.init(actvity, appUserId);
		YtPoint.setDefaultListener();
		// 判断是否有活动进行
		getActionInfo(actvity);
	}

	/**
	 * YtTemplate初始化,开发者应该在程序的入口调用,初始化后后续操作才能正常进行
	 * @param act
	 */
	public static void init(final Activity actvity) {
		init(actvity, null);
	}

	private static void getActionInfo(final Activity act) {
		new Thread() {
			public void run() {
				String response = YtPoint.isOnAction();
				if (response != null) {
					try {
						JSONObject json = new JSONObject(response);
						isOnAction = json.getBoolean("success");
						if (isOnAction) {
							JSONObject object = json.getJSONObject("object");
							setActionName(object.getString("activityName"));
							Intent it = new Intent(Constant.BROADCAST_ISONACTION);
							act.sendBroadcast(it);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	/**
	 * 在应用出口调用，释放内存
	 */
	public static void release(Context context) {
		YtCore.release(context);
	}

	/**
	 * 设置是否显示分享成功，分享失败，分享取消的提示
	 * 
	 * @param visible
	 */
	public void setToastVisible(boolean visible) {
		YtToast.visible = visible;
	}

	/**
	 * 的待分享数据,如果开发者没有使用addData(YtPlatform platform, ShareData
	 * shareData)方法为特定平台设置待分享数据,则平台分享的内容为此处设置的内容
	 * 
	 * @param shareD
	 *            该方法用于设置所有平台ata
	 */
	public void setShareData(ShareData shareData) {
		this.shareData = shareData;
		if(YtCore.getInstance().isCheckConfig())
		CheckShareData.check(shareData);
	}

	/**
	 * 获得分享数据
	 * 
	 * @return
	 */
	public ShareData getCapData() {
		return capData;
	}

	/**
	 * 设置分享数据
	 * 
	 * @param capData
	 */
	public void setCapData(ShareData capData) {
		this.capData = capData;
	}

	/**
	 * 是否显示截屏
	 * 
	 * @return
	 */
	public boolean isScreencapVisible() {
		return screencapVisible;
	}

	/**
	 * 设置截屏按钮是否可见
	 * 
	 * @param screencapVisible
	 */
	public void setScreencapVisible(boolean screencapVisible) {
		this.screencapVisible = screencapVisible;
	}

	/**
	 * 判断是否有活动
	 * 
	 * @return
	 */
	public boolean isHasAct() {
		return hasAct;
	}

	/**
	 * 设置是否有活动
	 * 
	 * @param hasAct
	 */
	public void setHasAct(boolean hasAct) {
		this.hasAct = hasAct;
	}

	/**
	 * 获取分享框高度
	 * 
	 * @return
	 */
	public int getPopwindowHeight() {
		return popwindowHeight;
	}

	/**
	 * 设置弹出分享框的高度
	 * 
	 * @param popwindowHeight
	 */
	public void setPopwindowHeight(int popwindowHeight) {
		this.popwindowHeight = popwindowHeight;
	}

	public boolean isOnAction() {
		return isOnAction;
	}

	public void setOnAction(boolean isOnAction) {
		YtTemplate.isOnAction = isOnAction;
	}

	/**
	 * 获得动画效果设置
	 * 
	 * @return
	 */
	public int getAnimationStyle() {
		return animationStyle;
	}

	/**
	 * 设置动画效果 ANIMATIONSTYLE_DEFAULT=0;ANIMATIONSTYLE_SYSTEM=1
	 * 
	 * @param animationStyle
	 */
	public void setAnimationStyle(int animationStyle) {
		this.animationStyle = animationStyle;
	}

	public static String getActionName() {
		return actionName;
	}

	public static void setActionName(String actionName) {
		YtTemplate.actionName = actionName;
	}

	/**
	 * 微信朋友圈分享是否将内容追加到标题；微信分享链接时，只会显示图片和title中的文字描述，文字只显示标题会显得内容很乏味
	 */
	public void setWxCircleTitleAppendText(boolean isWxTextAsTitle) {
		YtCore.isWxCircleTextAsTitle = isWxTextAsTitle;
	}

	public boolean isDismissAfterShare() {
		return dismissAfterShare;
	}

	public void setDismissAfterShare(boolean dismissAfterShare) {
		this.dismissAfterShare = dismissAfterShare;
	}
	
	public ArrayList<String> getEnList() {
		return enList;
	}

	public void setEnList(ArrayList<String> enList) {
		this.enList = enList;
	}
	
	public static void checkConfig(boolean isCheckConfig) {
		YtCore.checkConfig(isCheckConfig);
		
		if(isCheckConfig){
			if(YtCore.getInstance().isCheckConfig())
				new CheckConfig(YtCore.getAppContext()).check();
		}
		else
			Util.clearCheckConfigTime();
	}

	public void setTemplateType(int youTuiViewType){
		this.youTuiViewType = youTuiViewType;
	}
}
