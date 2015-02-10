package cn.bidaround.youtui_template;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bidaround.point.PointActivity;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.util.Constant;
import cn.bidaround.ytcore.util.Util;

/**
 * viewpager+gridview 黑色网格样式分享样式
 * 
 * @author youtui
 * @since 14/4/25
 */
@SuppressLint("ViewConstructor")
public class BlackGridTemplate extends YTBasePopupWindow implements OnClickListener, OnPageChangeListener {
	
	/** 判断该分享页面是否正在运行 */
	private GridView pagerOne_gridView, pagerTwo_gridView, pagerThird_gridView;
	private TemplateAdapter pagerOne_gridAdapter, pagerTwo_gridAdapter, pagerThird_gridAdapter;
	private View sharepopup_indicator_linelay;
	private ImageView zeroIamge, oneIamge, twoIamge;
	private ViewPager viewPager;
	private final int ITEM_AMOUNT = 6;
	private TextView yt_blackpopup_screencap_text;
	private Button cancelBt;

	public BlackGridTemplate(Activity act, boolean hasAct, YtTemplate template, ShareData shareData, ArrayList<String> enList) {
		super(act, hasAct, template, shareData, enList);
		instance = this;
	}

	/**
	 * 显示分享界面
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		View view = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("yt_popup_viewpager", "layout", YtCore.packName), null);
		initButton(view);
		initViewPager(view);
		// 设置popupwindow的属性
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());

		if (template.getPopwindowHeight() > 0) {
			setHeight(template.getPopwindowHeight());
		} else {
			if (enList.size() <= 3) {
				setHeight(Util.dip2px(act, 230));
			} else {
				setHeight(Util.dip2px(act, 335));
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
	 * 初始化积分按钮
	 * 
	 * @param view
	 */

	private void initButton(View view) {
		zeroIamge = (ImageView) view.findViewById(YtCore.res.getIdentifier("sharepopup_zero_iv", "id", YtCore.packName));
		oneIamge = (ImageView) view.findViewById(YtCore.res.getIdentifier("sharepopup_one_iv", "id", YtCore.packName));
		twoIamge = (ImageView) view.findViewById(YtCore.res.getIdentifier("sharepopup_two_iv", "id", YtCore.packName));

		if (enList.size() <= 12)
			twoIamge.setVisibility(View.GONE);

		cancelBt = (Button) view.findViewById(YtCore.res.getIdentifier("cancel_bt", "id", YtCore.packName));

		if (template.isOnAction() && YtTemplate.getActionName() != null&&hasAct) {
			cancelBt.setText(YtTemplate.getActionName());
		} else {
			String cancel = YtCore.res.getString(YtCore.res.getIdentifier("yt_cancel", "string", YtCore.packName));
			cancelBt.setText(cancel);
		}
		cancelBt.setOnClickListener(this);

		yt_blackpopup_screencap_text = (TextView) view.findViewById(YtCore.res.getIdentifier("yt_blackpopup_screencap_text", "id", YtCore.packName));
		yt_blackpopup_screencap_text.setOnClickListener(this);
		if (template.isScreencapVisible()) {
			yt_blackpopup_screencap_text.setVisibility(View.VISIBLE);
		} else {
			yt_blackpopup_screencap_text.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 初始化viewpager
	 */
	private void initViewPager(View view) {
		viewPager = (ViewPager) view.findViewById(YtCore.res.getIdentifier("share_viewpager", "id", YtCore.packName));
		sharepopup_indicator_linelay = view.findViewById(YtCore.res.getIdentifier("sharepopup_indicator_linelay", "id", YtCore.packName));
		ArrayList<View> pagerList = new ArrayList<View>();

		// 添加第一页
		if (enList.size() <= 6)
			pagerList.add(initOneView());
		// 添加第二页
		else if (enList.size() > 6 && enList.size() <= 12) {
			pagerList.add(initOneView());
			pagerList.add(initTwoView());
		}
		// 添加第三页
		else if (enList.size() > 12 && enList.size() <= 18) {
			pagerList.add(initOneView());
			pagerList.add(initTwoView());
			pagerList.add(initThirdView());
		}

		SharePagerAdapter pagerAdapter = new SharePagerAdapter(pagerList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(3);

		// 设置滑动下标
		if (enList.size() > 6) {
			viewPager.setOnPageChangeListener(this);
		} else if (enList.size() <= 6) {
			if (sharepopup_indicator_linelay != null) {
				sharepopup_indicator_linelay.setVisibility(View.INVISIBLE);
			}
		}
	}

	private View initOneView() {
		ArrayList<String> pagerOneList = new ArrayList<String>();

		int last = 6;
		if (enList.size() < last)
			last = enList.size();

		for (int i = 0; i < last; i++) {
			pagerOneList.add(enList.get(i));
		}

		View pagerOne = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("yt_share_pager", "layout", YtCore.packName), null);
		pagerOne_gridView = (GridView) pagerOne.findViewById(YtCore.res.getIdentifier("sharepager_grid", "id", YtCore.packName));
		// pagerOne_gridAdapter = new ShareGridAdapter(act, enList, showStyle);
		pagerOne_gridAdapter = new TemplateAdapter(act, pagerOneList, Constant.BLACK_GRID_LAYOUT_NAME, hasAct);
		pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
		pagerOne_gridView.setOnItemClickListener(this);
		return pagerOne;
	}

	private View initTwoView() {
		ArrayList<String> pagerTwoList = new ArrayList<String>();
		int last = 12;
		if (enList.size() < last)
			last = enList.size();
		for (int i = 6; i < last; i++) {
			pagerTwoList.add(enList.get(i));
		}
		// 初始化第二页
		View pagerTwo = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("yt_share_pager", "layout", YtCore.packName), null);
		pagerTwo_gridView = (GridView) pagerTwo.findViewById(YtCore.res.getIdentifier("sharepager_grid", "id", YtCore.packName));
		pagerTwo_gridAdapter = new TemplateAdapter(act, pagerTwoList, Constant.BLACK_GRID_LAYOUT_NAME, hasAct);
		pagerTwo_gridView.setAdapter(pagerTwo_gridAdapter);
		pagerTwo_gridView.setOnItemClickListener(this);
		return pagerTwo;
	}

	private View initThirdView() {
		ArrayList<String> pagerThirdList = new ArrayList<String>();
		int last = 18;
		if (enList.size() < last)
			last = enList.size();
		for (int i = 12; i < last; i++) {
			pagerThirdList.add(enList.get(i));
		}
		// 初始化第三页
		View pagerThird = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("yt_share_pager", "layout", YtCore.packName), null);
		pagerThird_gridView = (GridView) pagerThird.findViewById(YtCore.res.getIdentifier("sharepager_grid", "id", YtCore.packName));
		pagerThird_gridAdapter = new TemplateAdapter(act, pagerThirdList, Constant.BLACK_GRID_LAYOUT_NAME, hasAct);
		pagerThird_gridView.setAdapter(pagerThird_gridAdapter);
		pagerThird_gridView.setOnItemClickListener(this);
		return pagerThird;
	}

	/**
	 * 活动按钮事件
	 */
	@Override
	public void onClick(View v) {

		if (v.getId() == YtCore.res.getIdentifier("cancel_bt", "id", YtCore.packName)) {
			/** 有活动点击显示活动规则 */
			if (template.isOnAction() && YtTemplate.getActionName() != null&&hasAct) {
				Intent it = new Intent(act, PointActivity.class);
				act.startActivity(it);
			} else {
				this.dismiss();
			}

		} else if (v.getId() == YtCore.res.getIdentifier("share_popup_knowtv", "id", YtCore.packName)) {

		} else if (v.getId() == YtCore.res.getIdentifier("share_popup_checktv", "id", YtCore.packName)) {

		} else if (v.getId() == YtCore.res.getIdentifier("yt_blackpopup_screencap_text", "id", YtCore.packName)) {
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

	/**
	 * 分享按钮点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
		if (Util.isNetworkConnected(act)) {
			if (adapterView == pagerOne_gridView) {
				new YTShare(act).doGridShare(position, 0, template, shareData, ITEM_AMOUNT, instance, instance.getHeight());
			} else if (adapterView == pagerTwo_gridView) {
				new YTShare(act).doGridShare(position, 1, template, shareData, ITEM_AMOUNT, instance, instance.getHeight());
			} else if (adapterView == pagerThird_gridView) {
				new YTShare(act).doGridShare(position, 2, template, shareData, ITEM_AMOUNT, instance, instance.getHeight());
			}
			if(template.isDismissAfterShare()){
				dismiss();
			}
		} else {
			String noNetwork = YtCore.res.getString(YtCore.res.getIdentifier("yt_nonetwork", "string", YtCore.packName));
			Toast.makeText(act, noNetwork, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 刷新显示积分
	 */
	@Override
	public void refresh() {
		if (pagerOne_gridAdapter != null) {
			pagerOne_gridAdapter.notifyDataSetChanged();
		}
		if (pagerTwo_gridAdapter != null) {
			pagerTwo_gridAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * viewpager状态变化监听
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	/**
	 * viewpager滑动监听
	 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	/**
	 * 页面选择监听，这里用来显示viewpager下标
	 */
	@Override
	public void onPageSelected(int index) {
		// viewpager下标
		switch (index) {
		case 0:
			zeroIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_white", "drawable", YtCore.packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_black", "drawable", YtCore.packName)));
			twoIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_black", "drawable", YtCore.packName)));

			break;
		case 1:
			twoIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_black", "drawable", YtCore.packName)));
			zeroIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_black", "drawable", YtCore.packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_white", "drawable", YtCore.packName)));
			break;
		case 2:
			zeroIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_black", "drawable", YtCore.packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_black", "drawable", YtCore.packName)));
			twoIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("yt_guide_dot_white", "drawable", YtCore.packName)));
			break;

		default:
			break;
		}

	}

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
			if (Constant.BROADCAST_ISONACTION.equals(it.getAction())&&hasAct) {
				/** 如果读取到活动名,设置取消按钮为活动名 */
				if (cancelBt != null) {
					cancelBt.setText(YtTemplate.getActionName());
					refresh();
				}
			}
		}
	};
	
}
