package cn.bidaround.youtui_template;

import java.io.File;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;
import cn.bidaround.youtui_template.ColorPickerView.OnColorSelectListener;
import cn.bidaround.youtui_template.ThicknessView.OnSelectListener;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.util.Util;

/**
 * 处理截屏图像
 * @author youtui
 * @since 14/7/10  2015/1/4优化
 * 
 */
public class ScreenCapEditActivity extends Activity implements OnClickListener, OnTouchListener, OnGlobalLayoutListener{
	
	private ImageView imageView;
	private Bitmap bitmap;
	private Bitmap drawBit;
	private Bitmap swapBit;
	
	float downx = 0, downy = 0;
	float rectx = 0, recty = 0;
	
	private Canvas canvas;
	private Paint paint = new Paint();
	private int width, height;
	
	// 画矩形为true， 画线为false
	private boolean drawRect = false;
	
	private Handler mHandler = new Handler();
	
	public static  String picPath = getSDCardPath() + "/youtui/yt_screen.png";
	
	// 默认画细线
	private int count = 0;
	
	// 画矩形、画线的图形控件
	private ImageView rectImage, lineImage;
	
	// YtTemplate的显示类型
	private int viewType;

	private Path path = new Path();
	
	private ShareData capdata;
	private ShareData shareData;
	
	// 当前的画笔颜色，默认为黑色
	public static int currentColor = Color.BLACK ;
	
	// 画笔颜色、粗细 控制的窗口
	private PopupWindow pop ;
	
	// 画笔粗细的控件
	private ThicknessView thicknessView;
	
	// 画笔颜色选择控件
	private ColorPickerView colorView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	@SuppressWarnings("deprecation")
	private void init(){
		setContentView(getIdentifier("yt_activity_screencapedit", "layout"));
		
		viewType = getIntent().getExtras().getInt("viewType");
		
		shareData = (ShareData) getIntent().getExtras().getSerializable("shareData");
		capdata = (ShareData) getIntent().getExtras().getSerializable("capdata");
		
		lineImage = (ImageView) findViewById(getId("yt_screencap_drawline_image"));
		rectImage = (ImageView) findViewById(getId("yt_screencap_drawrect_image"));
		imageView = (ImageView) findViewById(getId("yt_screencap_image"));
		
		imageView.getViewTreeObserver().addOnGlobalLayoutListener(this);
		bitmap = BitmapFactory.decodeFile(getSDCardPath() + "/youtui/yt_screen.png").copy(Bitmap.Config.ARGB_8888, true);
		imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
		
		lineImage.setBackgroundResource(getIdentifier("yt_screencap_pencil_on", "drawable"));
		
		paint.setColor(currentColor);
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);
		paint.setAntiAlias(true);
		
		initThicknessView();
		initColorPickerView();
	}

	@Override
	public void onClick(final View view) {
		// 分享按钮
		if (view.getId() == getId("yt_screencap_share_bt")) {
			savePic(false);
			if(capdata == null){
				YtTemplate template = new YtTemplate(this, viewType, false);
				template.removePlatform(YtPlatform.PLATFORM_SHORTMESSAGE);
				template.removePlatform(YtPlatform.PLATFORM_EMAIL);
				shareData.setImagePath(picPath);
				template.setShareData(shareData);
				template.show();
			}
			else{
				YtTemplate template = new YtTemplate(this, viewType, false);
				template.removePlatform(YtPlatform.PLATFORM_SHORTMESSAGE);
				template.removePlatform(YtPlatform.PLATFORM_EMAIL);
				ShareData newData = capdata;
				newData.setImagePath( picPath);
				template.setCapData(capdata);
				template.setShareData(newData);
				template.show();
			}
			// 用户快速多次点击可能会调出多个页面，这里在点击后的短时间内设置禁止点击
			view.setClickable(false);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					view.setClickable(true);
				}
			}, 500);
		} 
		
		// 清除按钮
		else if (view.getId() == getId("yt_screencap_clear")) {
			if (imageView != null && drawBit != null) {
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();
				Matrix matrix = new Matrix();
				matrix.postScale((float) (width * 1.0 / w), (float) (height * 1.0 / h));
				drawBit = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true).copy(Bitmap.Config.ARGB_8888, true);
				imageView.setImageBitmap(drawBit);
				resetPaint();
				imageView.invalidate();
			}
		}
		
		// 选择画笔粗细
		else if (view.getId() == getId("yt_screencap_choose_paintwidth")) 
			showPop(view, thicknessView);
		
		// 选择画笔颜色
		else if (view.getId() == getId("yt_screencap_choose_color")) 
			showPop(view, colorView);
		
		// 保存图片
		else if (view.getId() == getId("yt_screencap_save"))
			savePic(true);
		
		// 点击画矩形按钮
		else if (view.getId() == getId("yt_screen_drawrect") ) 
			changeRectAndLindIcon(true);
		
		// 点击画线按钮
		else if (view.getId() == getId("yt_screen_drawline")) 
			changeRectAndLindIcon(false);
		
		// 点击返回按钮
		else if (view.getId() == getId("yt_screencap_back_linelay")) 
			finish();
	}
	
	private void changeRectAndLindIcon(boolean drawRect){
		this.drawRect = drawRect;
		if(drawRect){
			rectImage.setBackgroundResource(getIdentifier("yt_screencap_rectangle_on", "drawable"));
			lineImage.setBackgroundResource(getIdentifier("yt_screencap_pencil_off", "drawable"));
		}
		else{
			rectImage.setBackgroundResource(getIdentifier("yt_screencap_rectangle_off", "drawable"));
			lineImage.setBackgroundResource(getIdentifier("yt_screencap_pencil_on", "drawable"));
		}
		resetPaint();
	}
	
	private void initColorPickerView(){
		colorView = new ColorPickerView(this);
		colorView.setOnColorSelectListener(new OnColorSelectListener() {
			@Override
			public void onSelectColor(int color) {
				if(pop != null)
					pop.dismiss();
				currentColor = color;
			}
		});
	}
	
	private void initThicknessView(){
		thicknessView = new ThicknessView(this);
		thicknessView.setOnSelectListener(new OnSelectListener() {
			
			@Override
			public void onSelect(int width) {
//				paintWidth = width;
				if(paint != null)
					paint.setStrokeWidth(width);
				if(pop != null)
					pop.dismiss();
			}
		});
		thicknessView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, Util.getDensity(this, 200)));
	}
	
	@SuppressWarnings("deprecation")
	private void showPop(View parent, View content){
		
		changeWinAlpha(0.4f);
		
		if(pop == null){
			pop = new PopupWindow(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setOutsideTouchable(true);
		}
		pop.setContentView(content);
		pop.showAtLocation(parent, Gravity.CENTER, 0, 0);
		pop.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				changeWinAlpha(1.0f);
			}
		});
	}
	
	/**
	 * 改变窗口透明度
	 * @param alpha 初始为1
	 */
	private void changeWinAlpha(float alpha){
		WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
	}
	

	/** 重置画笔 */
	private void resetPaint() {
		canvas = new Canvas(drawBit);
		paint.setColor(currentColor);
		
		// 画笔大小至少5
		if(paint.getStrokeWidth() < 5)
			paint.setStrokeWidth(5);
	}

	/** 保存涂鸦图片 */
	private  void savePic(boolean showMsg) {
		// 图片存储路径
		String SavePath = getSDCardPath() + "/youtui";
		// 保存Bitmap
		try {
			File path = new File(SavePath);
			// 文件
			picPath = SavePath + "/yt_" + System.currentTimeMillis() + ".png";
			File file = new File(picPath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				drawBit.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
				if (showMsg) {
					String saveCap = getString(getIdentifier("yt_savecap", "string"));
					Toast.makeText(this, saveCap, Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int getId(String name){
		return getIdentifier(name, "id");
	}
	
	private int getIdentifier(String name, String tag){
		return getResources().getIdentifier(name, tag, getPackageName());
	}
	
	@Override
	public void onBackPressed() {
		if(pop != null && pop.isShowing())
			pop.dismiss();
		else
			super.onBackPressed();
	}

	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		float upx = 0, upy = 0;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			rectx = event.getX();
			recty = event.getY();
			downx = event.getX();
			downy = event.getY();
			path.moveTo(downx, downy);

			swapBit = Bitmap.createBitmap(drawBit);

			break;
		case MotionEvent.ACTION_MOVE:
			upx = event.getX();
			upy = event.getY();
			if (canvas != null && paint != null) {
				if (!drawRect) {
					drawBit = Bitmap.createBitmap(swapBit);
					imageView.setImageBitmap(drawBit);
					resetPaint();

					path.lineTo(upx, upy);
					canvas.drawPath(path, paint);
				} else {
					drawBit = Bitmap.createBitmap(swapBit);
					imageView.setImageBitmap(drawBit);
					resetPaint();
					canvas.drawLine(rectx, recty, rectx, upy, paint);
					canvas.drawLine(rectx, recty, upx, recty, paint);
					canvas.drawLine(rectx, upy, upx, upy, paint);
					canvas.drawLine(upx, recty, upx, upy, paint);
				}
			}
			downx = upx;
			downy = upy;
			imageView.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			upx = event.getX();
			upy = event.getY();

			if (drawRect) {
				canvas.drawLine(rectx, recty, rectx, upy, paint);
				canvas.drawLine(rectx, recty, upx, recty, paint);
				canvas.drawLine(rectx, upy, upx, upy, paint);
				canvas.drawLine(upx, recty, upx, upy, paint);
				imageView.invalidate();
			} else {
				path.reset();
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onGlobalLayout() {
		if (count == 0) {
			count++;
			width = imageView.getMeasuredWidth();
			height = imageView.getMeasuredHeight();
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.postScale((float) (width * 1.0 / w), (float) (height * 1.0 / h));
			drawBit = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true).copy(Bitmap.Config.ARGB_8888, true);
			resetPaint();
			imageView.setImageBitmap(drawBit);
			// 设置触摸事件进行绘图
			imageView.setOnTouchListener(this);
		}
	}
	
	private static String getSDCardPath() {
		File sdcardDir = null;
		boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}
}
