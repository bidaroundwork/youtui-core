package cn.bidaround.ytcore.login;


/**
 * 第三方登录回调需要实现该接口
 * @author youtui
 * @since 14/5/19
 */
public interface AuthListener {
	/**授权成功*/
	public abstract void onAuthSucess(AuthUserInfo userInfo);
/**授权失败*/
	public abstract void onAuthFail();
/**授权取消*/
	public abstract void onAuthCancel();
}