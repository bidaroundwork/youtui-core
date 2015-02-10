package cn.bidaround.youtui_template;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import cn.bidaround.ytcore.util.Util;

/**
 * 画笔粗细自定义控件
 * @author youtui
 * @since 2014/12/30
 */
public class ThicknessRectView extends View{
	
	private Paint paint;
	private int x, y; 
	
	// 画笔最大宽度
	private int max = 40;
	
	private int backColor = Color.WHITE;

	public ThicknessRectView(Context context, int y) {
		super(context);
		this.y = y;
		init();
	}
	
	private void init(){
		paint = new Paint();
		paint.setColor(ScreenCapEditActivity.currentColor);
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		float width = paint.getStrokeWidth();
		
		paint.setStrokeWidth(max);
		paint.setColor(backColor);
		
		if(x > 0){
			canvas.drawLine(x - getDensity(80), y, x + getDensity(80), y, paint);
			paint.setStrokeWidth(width < 5 ? 5 : width);
			paint.setColor(ScreenCapEditActivity.currentColor);
			canvas.drawLine(x - getDensity(80), y, x + getDensity(80), y, paint);
		}
	}
	
	public void refresh(int color, float width){
		paint.setColor(color);
		paint.setStrokeWidth(width);
		invalidate();
	}
	
	public int getCenterX(){
		return x;
	}
	
	public void setCenterX(int x){
		this.x = x;
	}
	
	private int getDensity(int value){
		return Util.getDensity(getContext(), value);
	}
}
