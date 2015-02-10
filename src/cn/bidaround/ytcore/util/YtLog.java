package cn.bidaround.ytcore.util;

import android.util.Log;
/**
 * 系统Log在msg为null会崩溃，为""是不会输出,该类用于替代系统Log,避免输出信息为null时崩溃和为""时不输出
 * @author youtui
 * @since 14/6/19
 */
public class YtLog {
	/**该参数设置为true则显示Log输出，为flase则不显示Log输出，开发的时候可以打开查看部分输出,请在发行时关闭*/
	public static boolean showLog = true;
	/**取代系统Log.i*/
	public static void i(String tag,String msg){
		if(showLog){
			if(msg==null){
				Log.i(tag, "null");
			}else if(msg==""){
				Log.i(tag, "not null");
			}else{
				Log.i(tag, msg);
			}
		}
	}	
	/**取代系统Log.e*/
	public static void e(String tag,String msg){
		if(showLog){
			if(msg==null){
				Log.e(tag, "null");
			}else if(msg==""){
				Log.e(tag, "not null");
			}else{
				Log.e(tag, msg);
			}	
		}
	}
	
	/**取代系统Log.d*/
	public static void d(String tag,String msg){
		if(showLog){
			if(msg==null){
				Log.d(tag, "null");
			}else if(msg==""){
				Log.d(tag, "not null");
			}else{
				Log.d(tag, msg);
			}	
		}
	}
	
	/**取代系统Log.w*/
	public static void w(String tag,String msg){
		if(showLog){
			if(msg==null){
				Log.w(tag, "null");
			}else if(msg==""){
				Log.w(tag, "not null");
			}else{
				Log.w(tag, msg);
			}	
		}
	}
}
