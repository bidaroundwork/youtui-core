package cn.bidaround.youtui_template;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bidaround.point.YtPoint;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * 友推提供分享弹出界面的适配器
 * @author youtui
 * @since 2015/1/12
 */
public class TemplateAdapter extends BaseAdapter {
	protected Context context;
	protected ArrayList<String> list;
	protected boolean hasAct = false;
	protected String layoutName;
	
	public TemplateAdapter(Context context, ArrayList<String> list, String layoutName, boolean hasAct) {
		this.context = context;
		this.hasAct = hasAct;
		this.layoutName = layoutName;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		if(list != null && !list.isEmpty())
			return list.size();
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHoder hoder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(YtCore.res.getIdentifier(layoutName, "layout", YtCore.packName), null);
			hoder = new ViewHoder();
			hoder.platformLogo = (ImageView) convertView.findViewById(YtCore.res.getIdentifier("logo", "id", YtCore.packName));
			hoder.platformName = (TextView) convertView.findViewById(YtCore.res.getIdentifier("platform_name", "id", YtCore.packName));
			hoder.point = (TextView) convertView.findViewById(YtCore.res.getIdentifier("point", "id", YtCore.packName));
			hoder.sign = (TextView) convertView.findViewById(YtCore.res.getIdentifier("sign", "id", YtCore.packName));
			convertView.setTag(hoder);
		} else {
			hoder = (ViewHoder) convertView.getTag();
		}

		fillView(hoder, position);

		return convertView;
	}
	
	/**
	 * 填充子项
	 */
	private void fillView(ViewHoder hoder, int position) {
		hoder.platformLogo.setImageResource(ShareList.getLogo(list.get(position), context));
		hoder.platformName.setText(ShareList.getTitle(list.get(position), context));
		// 显示积分
		int id = YtPlatform.getPlatformByName(list.get(position)).getChannleId();
		if(id != -1)
			showPoint(hoder.sign, hoder.point, id);
		else{
			hoder.point.setVisibility(View.INVISIBLE);
			if(hoder.sign != null)
				hoder.sign.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 显示积分
	 */
	private void showPoint(View signView, TextView pointView, int channelId) {
		// 积分为0时不显示
		if(hasAct){
			int point = YtPoint.getPoint(channelId);
			if (point == 0) {
				if(signView != null)
					signView.setVisibility(View.INVISIBLE);
				pointView.setVisibility(View.INVISIBLE);
			} else {
				if(signView != null)
					signView.setVisibility(View.VISIBLE);
				pointView.setVisibility(View.VISIBLE);
				pointView.setText("分享+"+point+"积分");
			}
		}else{
			if(signView != null)
				signView.setVisibility(View.INVISIBLE);
			pointView.setVisibility(View.INVISIBLE);
		}
	}
	
	protected class ViewHoder {
		/** 平台的logo*/
		ImageView platformLogo;
		/** 平台显示的名字 */
		TextView platformName;
		/** 平台右上角的红色圆角控件*/
		View sign;
		/** 显示分享能获得的积分数*/
		TextView point;
	}
}
