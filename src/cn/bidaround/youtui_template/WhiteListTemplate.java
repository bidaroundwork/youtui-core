package cn.bidaround.youtui_template;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bidaround.point.PointActivity;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.util.Constant;
import cn.bidaround.ytcore.util.Util;

/**
 * 白色列表样式的分享框
 * 
 * @author youtui
 * @since 14/5/4
 */
public class WhiteListTemplate extends YTBasePopupWindow implements OnClickListener {
	/** 用于判断分享页面是否正在运行 */
	private Button sharelist_knowaction_btn;
	private TextView yt_listpopup_screencap_text;
	// private ShareGridAdapter adapter;
	private TemplateAdapter adapter;
	private Handler uiHandler = new Handler();

	public WhiteListTemplate(Activity act, boolean hasAct, YtTemplate template, ShareData shareData, ArrayList<String> enList) {
		super(act, hasAct, template, shareData, enList);
		instance = this;
	}

	/**
	 * 显示分享列表窗口
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		View view = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("yt_popup_list", "layout", YtCore.packName), null);
		initListView(view);
		initButton(view);
		// 设置popupwindow的属
		setFocusable(true);
		setOutsideTouchable(true);
		//setBackgroundDrawable(YtCore.res.getDrawable(YtCore.res.getIdentifier("yt_side", "drawable", YtCore.packName)));
		setContentView(view);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());
		if (template.getPopwindowHeight() > 0) {
			setHeight(template.getPopwindowHeight());
		} else {
			if (enList.size() <= 6) {
				setHeight(Util.dip2px(act, 350));
			} else {
				setHeight(Util.dip2px(act, 350));
			}
		}

		if (template.getAnimationStyle() == YtTemplate.ANIMATIONSTYLE_DEFAULT) {
			setAnimationStyle(YtCore.res.getIdentifier("YtSharePopupAnim", "style", YtCore.packName));
		} else if (template.getAnimationStyle() == YtTemplate.ANIMATIONSTYLE_SYSTEM) {

		}

		showAtLocation(getContentView(), Gravity.BOTTOM, 0, 0);
		IntentFilter filter = new IntentFilter(Constant.BROADCAST_ISONACTION);
		act.registerReceiver(receiver, filter);
	}

	/**
	 * 初始化ListView
	 * 
	 * @param view
	 */
	private void initListView(View view) {
		ListView listView = (ListView) view.findViewById(YtCore.res.getIdentifier("sharelist_share_list", "id", YtCore.packName));
		// adapter = new ShareGridAdapter(act, enList, showStyle);
		adapter = new TemplateAdapter(act, enList, Constant.WHITE_LIST_LAYOUT_NAME, hasAct);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	/**
	 * 初始化查看和了解积分按钮
	 * 
	 * @param view
	 */
	private void initButton(View view) {
		// 如果么有活动显示取消，如果有活动显示了解活动规则
		sharelist_knowaction_btn = (Button) view.findViewById(YtCore.res.getIdentifier("sharelist_knowaction_btn", "id", YtCore.packName));
		if (template.isOnAction() && YtTemplate.getActionName() != null && hasAct) {
			sharelist_knowaction_btn.setText(YtTemplate.getActionName());
		} else {
			String cancel = YtCore.res.getString(YtCore.res.getIdentifier("yt_cancel", "string", YtCore.packName));
			sharelist_knowaction_btn.setText(cancel);
		}

		sharelist_knowaction_btn.setOnClickListener(this);

		yt_listpopup_screencap_text = (TextView) view.findViewById(YtCore.res.getIdentifier("yt_listpopup_screencap_text", "id", YtCore.packName));
		yt_listpopup_screencap_text.setOnClickListener(this);
		if (template.isScreencapVisible()) {
			yt_listpopup_screencap_text.setVisibility(View.VISIBLE);
		} else {
			yt_listpopup_screencap_text.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 查看和了解积分按钮监听
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == YtCore.res.getIdentifier("sharelist_knowaction_btn", "id", YtCore.packName)) {
			/** 有活动点击显示活动规则，没活动显示抽奖 */
			if (template.isOnAction() && YtTemplate.getActionName() != null && hasAct) {
				Intent it = new Intent(act, PointActivity.class);
				act.startActivity(it);
			} else {
				this.dismiss();
			}
		}
//		else if (v.getId() == YtCore.res.getIdentifier("sharelist_checkpoint_btn", "id", YtCore.packName)) {
//			Intent checkIt = new Intent(act, ShareActivity.class);
//			checkIt.putExtra("from", "check");
//			act.startActivity(checkIt);
//		} 
		else if (v.getId() == YtCore.res.getIdentifier("yt_listpopup_screencap_text", "id", YtCore.packName)) {
			// 截屏按钮
			TemplateUtil.GetandSaveCurrentImage(act, true);
			Intent it = new Intent(act, ScreenCapEditActivity.class);
			it.putExtra("viewType", template.getViewType());
			if (shareData.isAppShare()) {
				it.putExtra("target_url", YtCore.getInstance().getTargetUrl());
			} else {
				it.putExtra("target_url", shareData.getTargetUrl());
			}
			it.putExtra("capdata", template.getCapData());
			it.putExtra("shareData", shareData);
			act.startActivity(it);
			this.dismiss();
		}
	}

	@Override
	/**列表项点击事件*/
	public void onItemClick(final AdapterView<?> adapterView, final View arg1, int position, long arg3) {
		if (Util.isNetworkConnected(act)) {
			new YTShare(act).doListShare(position, template, shareData, instance);
			adapterView.setEnabled(false);
			uiHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					adapterView.setEnabled(true);
				}
			}, 500);
			if(template.isDismissAfterShare()){
				dismiss();
			}
		} else {
			String noNetwork = YtCore.res.getString(YtCore.res.getIdentifier("yt_nonetwork", "string", YtCore.packName));
			Toast.makeText(act, noNetwork, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 刷新积分
	 */
	@Override
	public void refresh() {
		adapter.notifyDataSetChanged();
	}

	/**
	 * 关闭分享界面
	 */
	@Override
	public void dismiss() {
		try {
			act.unregisterReceiver(receiver);
		} catch (Exception e) {
		}
		super.dismiss();
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent it) {
			if (Constant.BROADCAST_ISONACTION.equals(it.getAction()) && hasAct) {
				/** 如果读取到活动名,设置取消按钮为活动名 */
				if (sharelist_knowaction_btn != null) {
					sharelist_knowaction_btn.setText(YtTemplate.getActionName());
					refresh();
				}
			}
		}
	};

}
