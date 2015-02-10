package cn.bidaround.ytcore.activity;

import java.util.Locale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.kaixin.KaixinShare;
import cn.bidaround.ytcore.renn.RennShare;
import cn.bidaround.ytcore.sina.SinaHttpShare;
import cn.bidaround.ytcore.tencentwb.TencentWbShare;
import cn.bidaround.ytcore.util.Util;

/**
 * 自定义的分享界面
 * @author youtui
 * @since 2015/1/23
 */
public class ShareView extends LinearLayout{
	
	private final int BACK_ID = 140901;
	private final int SHAREBT_ID = 140902;
	
	private Activity activity;
	
	private ShareData shareData;
	
	private YtPlatform platform;
	
	private YtShareListener listener;
	
	private EditText editText;
	
	public ShareView(Activity activity, ShareData shareData, YtShareListener listener, YtPlatform platform) {
		super(activity);
		this.activity = activity;
		this.shareData = shareData;
		this.listener = listener;
		this.platform = platform;
		
		if(shareData == null){
			return;
		}
		init();
	}
	
	
	private void init(){
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundColor(0xFFE9ECFF);
		
		// 分享内容框
		LinearLayout bodyLayout = new LinearLayout(activity);
		bodyLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		bodyLayout.setLayoutParams(bodyParams);
		bodyLayout.setBackgroundColor(0xFFF4F4F4);
		bodyLayout.setHorizontalGravity(Gravity.LEFT);
		bodyLayout.setVerticalGravity(Gravity.TOP);
		

		bodyLayout.addView(createTextView());
		bodyLayout.addView(createImageView());

		addView(createHeadView());
		addView(bodyLayout, bodyParams);
	}
	
	class ShareListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			shareData.setText(editText.getText().toString());
			Util.showProgressDialog(activity, getStringRes("yt_shareing"), true);
			
			if(platform == YtPlatform.PLATFORM_SINAWEIBO)
				new SinaHttpShare(activity, shareData, listener).shareToSina();
			
			if(platform == YtPlatform.PLATFORM_RENREN)
				new RennShare(activity, listener, shareData).shareToRenn();
			
			if(platform == YtPlatform.PLATFORM_TENCENTWEIBO)
				new TencentWbShare(activity, listener, shareData).shareToTencentWb();
			
			else if(platform == YtPlatform.PLATFORM_KAIXIN){
				KaixinShare kaixinShare = new KaixinShare(activity, shareData, listener);
				if (kaixinShare.isAuthValid())
					kaixinShare.shareToKaixin();
				else 
					kaixinShare.doAuth();
			}
		}
	}
	
	private View createHeadView(){
		RelativeLayout headerLayout = new RelativeLayout(activity);
		RelativeLayout.LayoutParams headerLinearParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, Util.dip2px(activity, 50));
		headerLayout.setLayoutParams(headerLinearParams);
		headerLayout.setBackgroundColor(0xff66c0ff);

		// 返回键
		LinearLayout back = new LinearLayout(activity);
		RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(Util.dip2px(activity, 50),
				RelativeLayout.LayoutParams.MATCH_PARENT);
		backParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		back.setHorizontalGravity(Gravity.CENTER);
		back.setVerticalGravity(Gravity.CENTER);
		back.setId(BACK_ID);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCancel();
			}
		});
		
		// 返回键图片
		ImageView backImage = new ImageView(activity);
		LayoutParams backImageParams = new LayoutParams(Util.dip2px(activity, 20), Util.dip2px(activity, 20));
		backImage.setLayoutParams(backImageParams);
		backImage.setImageResource(activity.getResources().getIdentifier("yt_left_arrow", "drawable", activity.getPackageName()));
		back.addView(backImage);
		
		// 标题栏
		TextView title = new TextView(activity);
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		titleParams.addRule(RelativeLayout.RIGHT_OF, BACK_ID);
		titleParams.addRule(RelativeLayout.LEFT_OF, SHAREBT_ID);
		title.setGravity(Gravity.CENTER_VERTICAL);
		title.setText(getPlatformName(platform));
		title.setTextSize(16);
		title.setTextColor(0xffffffff);

		// 分享按钮
		TextView shareBt = new TextView(activity);
		shareBt.setId(SHAREBT_ID);
		RelativeLayout.LayoutParams shareBtParams = new RelativeLayout.LayoutParams(Util.dip2px(activity, 50),
				RelativeLayout.LayoutParams.MATCH_PARENT);
		shareBtParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		shareBt.setText(getStringRes("yt_share"));
		shareBt.setGravity(Gravity.CENTER_VERTICAL);
		shareBt.setTextColor(0xffffffff);

		shareBt.setOnClickListener(new ShareListener());

		headerLayout.addView(back, backParams);
		headerLayout.addView(title, titleParams);
		headerLayout.addView(shareBt, shareBtParams);
		return headerLayout;
	}
	
	private String getPlatformName(YtPlatform platform){
		return activity.getResources().getString(activity.getResources()
				.getIdentifier("yt_" + platform.getName().toLowerCase(Locale.US), "string", activity.getPackageName()));
	}
	
	@SuppressWarnings("deprecation")
	private EditText createTextView(){
		editText = new EditText(activity);
		LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		editParams.setMargins(8, 8, 8, 8);
		editText.setLayoutParams(editParams);
		
		if ((shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT || 
				shareData.getShareType() == ShareData.SHARETYPE_TEXT) && 
				shareData != null && shareData.getText() != null) {
			editText.setText(shareData.getText());
		}
		else
			shareData.setText("");
		
		editText.setGravity(Gravity.TOP);
		editText.setTextColor(0xffa1a1a1);
		editText.setTextSize(13);
		editText.setBackgroundDrawable(null);
		return editText;
	}
	
	private View createImageView(){
		
		LinearLayout layout = new LinearLayout(activity);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.HORIZONTAL);
		
		if(shareData.getShareType() == ShareData.SHARETYPE_IMAGE ||
				shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT ||
				shareData.getShareType() == ShareData.IMAGETYPE_APPRESOURE){
			ImageView shareImage = new ImageView(activity);
			LinearLayout.LayoutParams shareImageParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			shareImageParams.setMargins(15, 15, 25, 15);
			shareImageParams.gravity = Gravity.LEFT;
			shareImage.setLayoutParams(shareImageParams);
	
			if (shareData != null && shareData.getImagePath() != null) {
				Bitmap imageBit = BitmapFactory.decodeFile(shareData.getImagePath());
				Bitmap scaleBit = Bitmap.createScaledBitmap(imageBit, Util.dip2px(activity, 300), Util.dip2px(activity, 300)
						* imageBit.getHeight() / imageBit.getWidth(), true);
				@SuppressWarnings("deprecation")
				BitmapDrawable bitDraw = new BitmapDrawable(scaleBit);
	
				shareImage.setImageDrawable(bitDraw);
			}
			
			layout.addView(shareImage);
		}

		// empty view for the adaptive
		View view = new View(activity);
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 1, 1));
		
		layout.addView(view);
		return layout;
	}
	
	
	private String getStringRes(String res){
		return activity.getString(activity.getResources().getIdentifier(res, "string", activity.getPackageName()));
	}

	
	private void onCancel(){
	    if(listener != null)
	    	listener.onCancel(platform);
	    
//	    if(activity instanceof ShareActivity || activity instanceof SinaShareActivity)
//	    	activity.finish();
	    
	    if(onBackListener != null)
	    	onBackListener.onBack();
	}
	
	public View setOnBackListener(OnBackListener onBackListener) {
		this.onBackListener = onBackListener;
		return this;
	}

	private OnBackListener onBackListener;
	
	
	
	public interface OnBackListener{
		public void onBack();
	}
}
