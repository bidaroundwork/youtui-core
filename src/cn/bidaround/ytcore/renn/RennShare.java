package cn.bidaround.ytcore.renn;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpException;

import android.app.Activity;
import cn.bidaround.point.YtConstants;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.BaseShare;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.util.Util;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutBlogParam;
import com.renn.rennsdk.param.UploadPhotoParam;

/**
 * 人人分享和回调
 * 
 * @author youtui
 * @since 14/6/19
 */
public class RennShare extends BaseShare {
	/** 人人分享接口 */
	private RennClient client;

	private YtPlatform platform = YtPlatform.PLATFORM_RENREN;

	public RennShare(Activity act, YtShareListener listener, ShareData shareData) {
		super(act, shareData, listener);
	}

	/**
	 * 分享到人人
	 * 
	 * @throws IOException
	 * @throws HttpException
	 */

	public void shareToRenn() {
		client = RennClient.getInstance(activity);
		client.init(platform.getAppId(), platform.getAppKey(), platform.getAppSecret());

		client.setScope(YtConstants.RENREN_SCOPE);
		client.setLoginListener(new RennLoginListener());

		if (!client.isLogin()) {
			client.login(activity);
			return;
		}
		doShare();
	}

	/**
	 * 分享到人人,分享文字过多时进行剪裁
	 */
	private void doShare() {
		String text = "";

		if (shareData != null && shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
			text = shareData.getText();
			if (text.length() > 110) {
				text = text.substring(0, 109);
				text += "...";
			}
			if (shareData.getTargetUrl() != null && !"".equals(shareData.getTargetUrl()) && !"null".equals(shareData.getTargetUrl())
					&& shareData.getShareType() != ShareData.SHARETYPE_IMAGE) {
				text += shareData.getTargetUrl();
			}
		} else if (shareData != null && shareData.getShareType() == ShareData.SHARETYPE_IMAGE) {
			text = "";
		}

		if (shareData.getShareType() == ShareData.SHARETYPE_IMAGE || shareData.getShareType() == ShareData.SHARETYPE_IMAGEANDTEXT) {
			try {
				doRennShare(text, client);
			} catch (RennException e) {
				e.printStackTrace();
			}
		} else {
			try {
				doRennShare_text(text, client);
			} catch (RennException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 分享图片到人人操作
	 * 
	 * @param text
	 * @param client
	 * @throws RennException
	 */
	private void doRennShare(final String text, final RennClient client) throws RennException {
		UploadPhotoParam raram = new UploadPhotoParam();
		raram.setDescription(text);
		File file = new File(shareData.getImagePath());
		raram.setFile(file);
		client.getRennService().sendAsynRequest(raram, callback);
	}

	/**
	 * 分享纯文字
	 * 
	 * @param text
	 * @param client
	 * @throws RennException
	 */
	private void doRennShare_text(final String text, final RennClient client) throws RennException {
		PutBlogParam param = new PutBlogParam();
		param.setTitle(shareData.getTitle());
		param.setContent(shareData.getText());
		client.getRennService().sendAsynRequest(param, callback);
	}

	CallBack callback = new CallBack() {

		@Override
		public void onFailed(String arg0, String arg1) {
			if (listener != null)
				listener.onError(platform, arg0 + " : " + arg1);

			Util.dismissDialog();
		}

		@Override
		public void onSuccess(RennResponse response) {
			YtShareListener.sharePoint(activity, platform.getChannleId(), !shareData.isAppShare());

			if (listener != null)
				listener.onError(platform, response.toString());

			Util.dismissDialog();
		}

	};

	/**
	 * 登录回调
	 * 
	 */
	class RennLoginListener implements LoginListener {
		@Override
		public void onLoginCanceled() {
			
		}

		@Override
		public void onLoginSuccess() {
			shareToRenn();
		}
	}

}
