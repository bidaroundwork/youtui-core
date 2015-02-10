package cn.bidaround.youtui_template;

import cn.bidaround.ytcore.util.Util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 画笔粗细控件
 * @author youtui
 * @since 2014/12/30
 */
public class ThicknessView extends LinearLayout implements View.OnTouchListener, OnSeekBarChangeListener{
	// 自定义的画布绘制矩形控件，根据进度条的大小绘制相应高度的矩形
	private ThicknessRectView thicknessRectView;
	
	// 选择进度条
	private SeekBar bar;
	 
	public ThicknessView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOrientation(VERTICAL);
		setBackgroundColor(Color.WHITE);
		setGravity(Gravity.CENTER);
		
		addView(getTitleView());
		addView(getSeekBar());
		addView(getThicknessView());
	}
	
	// 创建标题控件
	private View getTitleView(){
		TextView text = new TextView(getContext());
		text.setText(getTitleText());
		text.setTextSize(20);
		text.setTextColor(Color.BLACK);
		
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		param.gravity = Gravity.CENTER;
		
		text.setLayoutParams(param);
		text.setPadding(0, getDensity(30), 0, getDensity(30));
		text.setGravity(Gravity.CENTER);
		return text;
	}
	
	// 创建进度条控件
	private View getSeekBar(){
		bar = new SeekBar(getContext());
		bar.setPadding(getDensity(30), getDensity(30), getDensity(30), 0);
		bar.setOnSeekBarChangeListener(this);
		
		bar.setOnTouchListener(this);
		
		int[] mColors = new int[]{Color.rgb(48, 172, 218), Color.rgb(48, 172, 218)};
		GradientDrawable mgDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, mColors);
		mgDrawable.setCornerRadius(50);
		mgDrawable.setStroke(10, -1);
		bar.setProgressDrawable(mgDrawable);
		 
		int[] location = new int[2];
		bar.getLocationOnScreen(location);
		
		return bar;
	}
	
	// 创建自定义绘制控件
	private View getThicknessView(){
		int[] location = new int[2];
		bar.getLocationOnScreen(location);
		int y = location[1] + getDensity(60);
		
		thicknessRectView = new ThicknessRectView(getContext(), y);
		
		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT,getDensity(160));
		param.gravity = Gravity.CENTER;
		thicknessRectView.setLayoutParams(param);
		thicknessRectView.setPadding(0, getDensity(20), 0, 0);
		
		return thicknessRectView;
	}
	
	private String getTitleText(){
		return getContext().getString(getContext().getResources().getIdentifier("yt_brush", "string", getContext().getPackageName())) +
		 getContext().getString(getContext().getResources().getIdentifier("yt_thickness", "string", getContext().getPackageName()));
	}
	
	private OnSelectListener onSelectListener;
	
	public void setOnSelectListener(OnSelectListener listener){
		onSelectListener = listener;
	}
	
	public interface OnSelectListener{
		public void onSelect(int width);
	}
	
	private int getDensity(float value){
		return Util.getDensity(getContext(), value);
	}

	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(onSelectListener != null)
				onSelectListener.onSelect(bar.getProgress());
		}
		return false;
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		
	}
	
	@Override
	public void onProgressChanged(SeekBar arg0, int value, boolean statue) {
		if(thicknessRectView.getCenterX() == 0)
			thicknessRectView.setCenterX(getWidth() / 2) ;
		
		if(thicknessRectView != null){
			thicknessRectView.refresh(Color.RED, value);
		}
	}
}
