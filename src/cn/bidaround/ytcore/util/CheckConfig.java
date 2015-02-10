package cn.bidaround.ytcore.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.wxapi.WXEntryActivity;
import cn.bidaround.ytcore.yxapi.YXBaseActivity;

/**
 * <h1>集成检测，检测配置文件是否丢失</h1>
 * <b>检测内容</b>
 * <li>检测资源文件是否丢失（youtui_sdk.xml/res资源）
 * <li>检测是否缺少权限（由于某些机型不支持无法检测权限，所以取消权限检测）
 * <li>检测AndroidManifest.xml配置
 * <li>检测是否配置友推appKey
 * <li>如果有配置微信、易信检测是否有创建集成需要的类，是否有在manifest中配置
 * <hr>
 * <li>检测过程中存在问题将以<b><font color=red>YouTui</font></b>为tag，警告或者错误的方式提示在LogCat，并会有相应的Toast提示
 * <hr>
 * @author youtui
 * @since 2014/11/11
 */
public class CheckConfig {
	
	private final static String TAG = "YouTui";
	
	private Context mContext;
	
	private List<String> mLayoutList;
	private List<String> mActivityList;
	private List<String> mDrawableList;
	private List<String> mAnimList;
	
	/** 缺少res下drawable/string/layout文件*/
	private final String MISS_RES = "miss resources in res directory!";
	
	/** 缺少assets/youtui_sdk.xml*/
	private final String MISS_ASSESTS = "miss youtui_sdk.xml in assets directory!";
	
	/** 未配置youtui_sdk.xml中友推appkey*/
	private final String MISS_YOUTUIKEY = "miss youtui appkey!";
	
	/** AndroidManifest缺少需要注册的Activity*/
	private final String MISS_ACTIVITY = "miss activity in AndroidManifest!";
	
	/** 检测微信、易信集成需要添加的类是否有创建*/
	private final String MISS_CLASS = "class not found!";
	
	public CheckConfig(Context context){
		mContext = context;
	}
	
	private void init(){
		mLayoutList = new ArrayList<String>();
		mActivityList = new ArrayList<String>();
		
		mLayoutList.add("yt_activity_dialog");
		mLayoutList.add("yt_activity_screencapedit");
		mLayoutList.add("yt_activity_share");
		mLayoutList.add("yt_activity_shareedit");
		mLayoutList.add("yt_authdialog");
		mLayoutList.add("yt_black_grid_item");
		mLayoutList.add("yt_dialog_shareedit");
		
		mLayoutList.add("yt_point_webview");
		mLayoutList.add("yt_popup_list");
		mLayoutList.add("yt_popup_viewpager");
		mLayoutList.add("yt_popup_whiteviewpager");
		mLayoutList.add("yt_share_pager");
		mLayoutList.add("yt_white_grid_item");
		mLayoutList.add("yt_white_list_item");
		
		mActivityList.add("cn.bidaround.ytcore.login.AuthActivity");
		mActivityList.add("cn.bidaround.point.PointActivity");
		mActivityList.add("cn.bidaround.youtui_template.ScreenCapEditActivity");
		
		mDrawableList = new ArrayList<String>();
		mDrawableList.add("yt_colorchoose_gray");
		mDrawableList.add("yt_button");
		mDrawableList.add("yt_btn_style_alert_dialog_cancel_normal");
		mDrawableList.add("yt_yixinfriends");
		mDrawableList.add("yt_yixin");
		mDrawableList.add("yt_sinaweibo");
		mDrawableList.add("yt_wechat");
		mDrawableList.add("yt_wechatfavorite");
		mDrawableList.add("yt_tencentweibo");
		mDrawableList.add("yt_side");
		mDrawableList.add("yt_sendbutton");
		mDrawableList.add("yt_screencap_save");
		mDrawableList.add("yt_screencap_rectangle_on");
		mDrawableList.add("yt_screencap_rectangle_off");
		mDrawableList.add("yt_screencap_pencil_on");
		mDrawableList.add("yt_screencap_pencil_off");
		mDrawableList.add("yt_screencap_circle_small_on");
		mDrawableList.add("yt_screencap_circle_small_off");
		mDrawableList.add("yt_screencap_circle_middle_on");
		mDrawableList.add("yt_screencap_circle_middle_off");
		mDrawableList.add("yt_screencap_cancel");
		mDrawableList.add("yt_renren");
		mDrawableList.add("yt_reddot");
		mDrawableList.add("yt_qq");
		mDrawableList.add("yt_wechatmoments");
		mDrawableList.add("yt_more");
		mDrawableList.add("yt_shortmessage");
		mDrawableList.add("yt_email");
		mDrawableList.add("yt_loadfail");
		mDrawableList.add("yt_list_newmessage");
		mDrawableList.add("yt_list_item_unselected_color_border");
		mDrawableList.add("yt_copylink");
		mDrawableList.add("yt_left_arrow");
		mDrawableList.add("yt_kaixin");
		mDrawableList.add("yt_guide_dot_white");
		mDrawableList.add("yt_guide_dot_black");
		
		mAnimList = new ArrayList<String>();
		mAnimList.add("yt_sharepopup_fade_in");
		mAnimList.add("yt_sharepopup_fade_out");
	}
	
	public void check() {
		
		if(!YtCore.getInstance().isCheckConfig()) return;
		
		if(Util.readCheckConfigTime() >= Constant.MAX_SUC_CHECKCONFIG_TIME){
			w("友推集成检测机制已连续运行" + Util.readCheckConfigTime() +"次，未检测出异常，已自动关闭.");
			return;
		}
		
		w("开始友推集成检测...");
		w("如需关闭集成检测机制，请调用YtTemplate.checkConfig(Boolean)");
		
		
		new Thread(new Runnable() {
			public void run() {
				init();
				
		        if(checkAssets())
	    			if(checkYoutuiKey())
	    				if(checkActivity() && checkOtherActivity())
	    					if(checkClass())
	    						checkRes();
	    					else
	    						showErrorToast(MISS_CLASS);
	    				else
	    					showErrorToast(MISS_ACTIVITY);
	    			else
	    				showErrorToast(MISS_YOUTUIKEY);
		        else
		        	showErrorToast(MISS_ASSESTS);
			}
			
		}).start();
    }  
	
	@SuppressWarnings("rawtypes")
	private void checkRes(){
		String packageName = mContext.getPackageName();  
        Class r = null;  
        try {  
            r = Class.forName(packageName + ".R");  
            Class[] classes = r.getClasses();  
            
            boolean success = true;
            for (int i = 0; i < classes.length; ++i) {  
            	String name = classes[i].getName().split("\\$")[1];
            		boolean b = true;
            	
                    if("layout".equals(name))
                    	if(!checkLayout(classes[i]))
                    		b = false;
                    
                    if("drawable".equals(name))
                    	if(!checkDrawable(classes[i]))
                    		b = false;
                   
                    if("string".equals(name))
                    	if(!checkValues(classes[i]))
                    		b = false;
                    
                    if("anim".equals(name))
                    	if(!checkAnim(classes[i]))
                    		b = false;
                    
                    if("style".equals(name))
                    	if(!checkStyle(classes[i]))
                    		b = false;
                    
                    if(!b) {
                    	showErrorToast(MISS_RES);
                    	if(success)
                    		success = false;
                    }
            }  
            
            if(success)
            	 Util.addCheckConfigTime();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
	private boolean checkAnim(Class<?> desireClass){
		Field[] fields = desireClass.getFields();
		
		if(fields != null && fields.length > 0 ){
			boolean b = true;
			List<String> list  = new ArrayList<String>();
			for(Field f : fields)
				list.add(f.getName());

			for(String str : mAnimList){
				if(!list.contains(str)){
					e("code:1022;>>>Miss "+str + ".xml in res/anim directory");
					b = false;
				}
			}
			return b;
		}
		return false;
	}
	
	private boolean checkStyle(Class<?> desireClass){
		Field[] fields = desireClass.getFields();
		
		if(fields != null && fields.length > 0 ){
			boolean b = true;
			List<String> list  = new ArrayList<String>();
			for(Field f : fields)
				list.add(f.getName());

			if(!list.contains("YtSharePopupAnim")){
				e("code:1023;>>>Miss yt_style.xml in res/values directory");
				b = false;
			}
			return b;
		}
		return false;
	}
	
	private boolean checkAssets(){
		try {
			mContext.getResources().getAssets().open("youtui_sdk.xml").close();
		} catch (IOException e) {
			e("code:1001;>>>Miss youtui_sdk.xml in the directory of assets");
			return false;
		}
		return true;
	}
	
	/**
	 * 微信、易信需在包名下创建一个类，判断该类是否有创建
	 * @return
	 */
	private boolean checkClass(){
		boolean b = true;
		// 微信、朋友圈、收藏，判断是否有创建WXEntryActivity类
		if("true".equals(YtPlatform.PLATFORM_WECHAT.getEnable()) || "true".equals(YtPlatform.PLATFORM_WECHATFAVORITE.getEnable())
				|| "true".equals(YtPlatform.PLATFORM_WECHATMOMENTS.getEnable())){
			boolean ad = checkClassExist(mContext.getPackageName()+".wxapi.WXEntryActivity", WXEntryActivity.class.getName());
			if(!ad) b = false;
		}
		
//		// 判断易信、易信朋友圈
		if("true".equals(YtPlatform.PLATFORM_YIXIN.getEnable()) || "true".equals(YtPlatform.PLATFORM_YIXINFRIENDS.getEnable())){
			boolean ad = checkClassExist(mContext.getPackageName()+".yxapi.YXEntryActivity", YXBaseActivity.class.getName());
			if(!ad) b = false;
		}
		return b;
	}
	
	private boolean checkOtherActivity(){
		boolean b = true;
		// 微信、朋友圈，判断是否有在menifest.xml中配置
		if("true".equals(YtPlatform.PLATFORM_WECHATFAVORITE.getEnable()) || "true".equals(YtPlatform.PLATFORM_WECHAT.getEnable())
				|| "true".equals(YtPlatform.PLATFORM_WECHATMOMENTS.getEnable())){
			boolean ac = haveActivity(mContext.getPackageName()+".wxapi.WXEntryActivity");
			if(!ac) b = false;
		}
		
		// 判断易信、易信朋友圈
		if("true".equals(YtPlatform.PLATFORM_YIXIN.getEnable()) || "true".equals(YtPlatform.PLATFORM_YIXINFRIENDS.getEnable())){
			boolean ac = haveActivity(mContext.getPackageName()+".yxapi.YXEntryActivity");
			if(!ac) b = false;
		}
		
		// 判断qq、qq空间
		if("true".equals(YtPlatform.PLATFORM_QQ.getEnable()) || "true".equals(YtPlatform.PLATFORM_QZONE.getEnable())){
			boolean assistActivity = haveActivity("com.tencent.connect.common.AssistActivity");
			boolean authActivity = haveActivity("com.tencent.tauth.AuthActivity");
			if(!assistActivity || !authActivity) b = false;
		}
		
		// 判断人人网
		if("true".equals(YtPlatform.PLATFORM_RENREN.getEnable())){
			boolean ac = haveActivity("com.renn.rennsdk.oauth.OAuthActivity");
			if(!ac) b = false;
		}
		
		// 判断新浪微博
		if("true".equals(YtPlatform.PLATFORM_SINAWEIBO.getEnable())){
			boolean ac = haveActivity("cn.bidaround.ytcore.activity.SinaShareActivity");
			if(!ac) b = false;
		}
		return b;
	}
	
	/**
	 * 指定类路径，判断是否存在，如果没有该类，则会进入异常处理块中
	 * @param clazz
	 * @return
	 */
	private boolean checkClassExist(String clazz, String parentClass){
		try {
			Class.forName(clazz);
			return true;
		} catch (Exception e) {
			e("code:1002;>>>Class not found "+ clazz +", please create the class extends " + parentClass);
			return false;
		}
	}
	
	private boolean checkYoutuiKey(){
		if(TextUtils.isEmpty(KeyInfo.youTui_AppKey)){
			e("code:1003;>>>Miss 'YouTui' label in youtui_sdk.xml");
			return false;
		}
		
		return true;
	}
	
	private boolean checkLayout(Class<?> desireClass){
		Field[] fields = desireClass.getFields();
		
		if(fields != null && fields.length > 0 ){
			boolean b = true;
			List<String> list  = new ArrayList<String>();
			for(Field f : fields)
				list.add(f.getName());

			for(String str : mLayoutList){
				if(!list.contains(str)){
					e("code:1004;>>>Miss "+str + ".xml文件");
					b = false;
				}
			}
			return b;
		}
		return false;
	}
	
	private boolean checkDrawable(Class<?> desireClass){
		Field[] fields = desireClass.getFields();
		
		if(fields != null && fields.length > 0 ){
			boolean b = true;
			List<String> list  = new ArrayList<String>();
			for(Field f : fields)
				list.add(f.getName());

			for(String str : mDrawableList){
				if(!list.contains(str)){
					e("code:1005;>>>Miss "+str + ".png in drawable directory");
					b = false;
				}
			}
			return b;
		}
		return false;
	}
	
	private boolean checkValues(Class<?> desireClass){
		Field[] fields = desireClass.getFields();
		
		if(fields != null && fields.length > 0 ){
			for(Field f : fields)
				if("youtui_appId".equals(f.getName()))
					return true;
			e("code:1006;>>>Miss yt_strings.xml in values directory");
			return false;
		}
		return false;
	}
	
	/**
	 * 检测必须配置的Activity
	 * @return
	 */
	private boolean checkActivity(){
		boolean b = true;
		for(String act : mActivityList){
			if(!haveActivity(act))
				b = false;
		}
		return b;
	}
	
	private boolean haveActivity(String name){
		try {
			// 如果没有配置Activity，怎会抛出异常
			mContext.getPackageManager().getActivityInfo(new ComponentName(mContext, name), PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			e("code:1008;>>>In AndoridManifest.xml do not found Activity with name "+name);
			return false;
		}
	}
	
	/** 出现Toast提示，说明检测过程中发现 错误*/
	private void showErrorToast(String msg){
		// 这种提示说明有检查出错误信息
		YtCore.getInstance().setCheckConfigHasError(msg);
		Util.clearCheckConfigTime();
		
		Message message = new Message();
		message.obj = msg;
		handler.sendMessage(message);
	}
	
	@SuppressLint("HandlerLeak") private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_LONG).show();
		};
	};
	
	private static void e(String msg){
		YtLog.e(TAG, "):>" + msg);
	}
	
	private static void w(String msg){
		YtLog.w(TAG, "):>" + msg);
	}
}
