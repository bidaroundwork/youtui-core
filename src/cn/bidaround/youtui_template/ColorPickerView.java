package cn.bidaround.youtui_template;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bidaround.ytcore.util.Util;

/**
 * 颜色选择控件
 * @author youtui
 * @since 2014/12/30
 */
public class ColorPickerView extends LinearLayout implements OnTouchListener{
	
	private OnColorSelectListener selectListener;
	
	private ColorPicker picker;

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ColorPickerView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		setOrientation(VERTICAL);
		setGravity(Gravity.CENTER_HORIZONTAL);
		setBackgroundColor(Color.WHITE);
		
		initColorPicker();
		
		addView(getTitleView());
		addView(getColorPickerView());
		addView(getColorPickerSVBar());
	}
	
	private View getColorPickerView(){
		RelativeLayout view = new RelativeLayout(getContext());
		view.addView(picker);	
		view.addView(getCenterEmptyView());	
		return view;
	}
	
	private View getColorPickerSVBar(){
		ColorPickerSVBar sv = new ColorPickerSVBar(getContext());
		LayoutParams pa = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pa.bottomMargin = getDensity(20);
		sv.setLayoutParams(pa);
		
		sv.setColor(ScreenCapEditActivity.currentColor);
		
		picker.addSVBar(sv);
		return sv;
	}
		
	private void initColorPicker(){
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		picker = new ColorPicker(getContext());
		picker.setShowOldCenterColor(false);
		picker.setLayoutParams(params);
		picker.setNewCenterColor(ScreenCapEditActivity.currentColor);
	}
	
	/**
	 * 创建空白的控件，用户点击该按钮会显示圆环
	 */
	private View getCenterEmptyView(){
		View view = new View(getContext());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		view.setLayoutParams(params);
		view.setOnTouchListener(this);
		return view;
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
	
	private String getTitleText(){
		return getContext().getString(getContext().getResources().getIdentifier("yt_brush", "string", getContext().getPackageName())) +
		 getContext().getString(getContext().getResources().getIdentifier("yt_color", "string", getContext().getPackageName()));
	}
	
	public void setOnColorSelectListener(OnColorSelectListener listener){
		selectListener = listener;
	}
	
	public interface OnColorSelectListener{
		public void onSelectColor(int color);
	}
	
	private int getDensity(float value){
		return Util.getDensity(getContext(), value);
	}

	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			picker.setDrawCusView(true);
			break;
		case MotionEvent.ACTION_UP:
			picker.setDrawCusView(false);
			if(selectListener != null)
				selectListener.onSelectColor(picker.getColor());
			break;
		}
		return true;
	}
}
