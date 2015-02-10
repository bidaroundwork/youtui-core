package cn.bidaround.youtui_template;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import cn.bidaround.point.YtPoint;
import cn.bidaround.ytcore.data.ShareData;

/**
 * 友推基础分享框，其他的分享popupwindow继承于此类
 * @author youtui
 * @since 14/5/4 
 */
public abstract class YTBasePopupWindow extends PopupWindow implements OnItemClickListener {
	
	protected YtTemplate template;
	protected ShareData shareData;
	protected ArrayList<String> enList;
	
	// 总共有多少页
	protected int pagerSize;
	// 每一页的容量
	protected int pagerVolume;
	
	
	/** 主分享界面实例 */
	protected  static YTBasePopupWindow instance;
	public static YTBasePopupWindow getInstance() {
		return instance;
	}

	/** 主分享界面样式 */
	/**获取积分信息*/
	public static final int GET_POINT = 0;
	/**分享后获得积分成功*/
	public static final int SHARED_HAS_POINT = 1;
	/**分享后获取积分失败*/
	public static final int SHARE_POINT_FAIL = 2;
	
	/**传入的activity*/
	protected  Activity act;
	/**是否有积分活动*/
	protected  boolean hasAct;
	/**监听分享完后的回调，刷新积分显示和告诉用户获得积分*/
	private static final String BROADCAST_REFRESHPOINT = "cn.bidaround.point.BROADCAST_REFRESHPOINT";
	
	public YTBasePopupWindow(Context context, boolean hasAct, YtTemplate template, ShareData shareData, ArrayList<String> enList) {
		super(context);	
		act = (Activity) context;
		this.hasAct = hasAct;
		initData();
		if(hasAct){
			IntentFilter filter = new IntentFilter(BROADCAST_REFRESHPOINT);
			context.registerReceiver(pointReceiver, filter);
		}
		this.shareData = shareData;
		this.template = template;
		this.enList = enList;
	}
	/**
	 * 刷新积分，将网络图片下载到本地SD卡
	 */
	@SuppressLint("HandlerLeak")
	protected void initData() {
		// 没有活动则将
		if (hasAct) {
			new Thread() {
				@Override
				public void run() {
					YtPoint.refresh(act);
				}
			}.start();
		} else 
			YtPoint.pointMap.clear();
	}
	
	/**
	 * 刷新积分，在子类实现
	 * 
	 * @param arr
	 */
	public abstract void refresh();

	/** 释放instance */
	@Override
	public void dismiss() {
		instance = null;
		try {
			act.unregisterReceiver(pointReceiver);
		} catch (Exception e) {
		}
		super.dismiss();
	}
	
	BroadcastReceiver pointReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
				refresh();
		}
	};
}
