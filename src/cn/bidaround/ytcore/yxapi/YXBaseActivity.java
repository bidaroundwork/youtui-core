/**
 * 
 */
package cn.bidaround.ytcore.yxapi;

import im.yixin.sdk.api.BaseReq;
import im.yixin.sdk.api.BaseResp;
import im.yixin.sdk.api.BaseYXEntryActivity;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.util.YixinConstants;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.YtPlatform;

/**
 * 易信分享到朋友圈会提示分享成功，但是分享到指定朋友时，却不会提示分享成功
 * @author youtui 
 */
public class YXBaseActivity extends BaseYXEntryActivity {

	/** 分享事件监听 */
	public static YtShareListener listener;

	public static YtPlatform platform;
	public static boolean isShareApp;

	/**
	 * 返回第三方app根据app id创建的IYXAPI，
	 * 
	 * @return
	 */
	protected IYXAPI getIYXAPI() {
		return YXAPIFactory.createYXAPI(this, platform.getAppId());
	}

	/**
	 * 易信响应第三方APP的请求时，易信调用第三方APP的此函数。第三方APP通过sendRequest分享内容到易信，
	 * 易信处理完毕后调用此函数。该函数由父类的onCreate或者onNewIntent进行调用
	 */
	@Override
	public void onResp(BaseResp resp) {
		switch (resp.getType()) {
		case YixinConstants.RESP_SEND_MESSAGE_TYPE:
			SendMessageToYX.Resp resp1 = (SendMessageToYX.Resp) resp;
			switch (resp1.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				if (listener != null)
					listener.onSuccess(platform, resp.errStr);
				YtShareListener.sharePoint(this, platform.getChannleId(), isShareApp);
				break;
			case BaseResp.ErrCode.ERR_COMM:
				if (listener != null)
					listener.onError(platform, resp.errStr);
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				if (listener != null)
					listener.onCancel(platform);
				break;
			case BaseResp.ErrCode.ERR_SENT_FAILED:
				if (listener != null)
					listener.onError(platform, resp.errStr);
				break;
			}
			break;
		}
		finish();
	}

	/**
	 * 易信主动发送请求到第三方APP时，易信调用第三方APP的此函数。该函数由父类BaseYXEntryActivity的onCreate或者
	 */
	@Override
	public void onReq(BaseReq req) {

	}
	
	@Override
	protected void onDestroy() {
		listener = null;
		platform = null;
		super.onDestroy();
	}
}
