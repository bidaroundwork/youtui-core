package cn.bidaround.ytcore.social;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.util.Util;

/**
 * 该类实现邮件和短信分享
 * 
 * @author youtui
 * @since 14/6/19
 */
public class OtherShare {
	private Activity act;

	public OtherShare(Activity act) {
		this.act = act;
	}

	/**
	 * 分享到短信
	 * 
	 * @param sms_body
	 */
	public void sendSMS(ShareData data) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
		if(TextUtils.isEmpty(data.getTargetUrl()))
			sendIntent.putExtra("sms_body", data.getText()); 
		else
			sendIntent.putExtra("sms_body", data.getText() + data.getTargetUrl());
		sendIntent.setType("vnd.android-dir/mms-sms");
		act.startActivityForResult(sendIntent, 1002);
	}
	
	/**
	 * 分享到彩信
	 * @param sms_body
	 */
	public void sendMMS(ShareData data) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(data.getImagePath())));
			intent.putExtra("subject", data.getTitle());
			if(TextUtils.isEmpty(data.getTargetUrl()))
				intent.putExtra("sms_body", data.getText()); 
			else
				intent.putExtra("sms_body", data.getText() + data.getTargetUrl()); 
			intent.putExtra(Intent.EXTRA_TEXT, "it's EXTRA_TEXT");
			intent.setType("image/*");
			intent.setClassName("com.android.mms","com.android.mms.ui.ComposeMessageActivity");
			act.startActivityForResult(intent, 1002);
		} catch (Exception e) {
	        Util.openSystemShare(act, data);
		}
	}

	/**
	 * 分享到Email
	 * 
	 * @param emailBody
	 */
	public void sendMail(String emailBody) {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		 email.setType("plain/text");
		 String emailSubject = YtCore.res.getString(YtCore.res.getIdentifier("yt_share", "string", YtCore.packName));
		// 设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		// 设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		// 调用系统的邮件系统
		act.startActivityForResult(Intent.createChooser(email,YtCore.res.getString(YtCore.res.getIdentifier("yt_chooseemail", "string", YtCore.packName))), 1001);
	}

}
