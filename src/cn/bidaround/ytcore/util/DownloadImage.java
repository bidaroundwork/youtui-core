package cn.bidaround.ytcore.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 下载图片
 * @author youtui
 * @since 14/6/19
 */
public class DownloadImage {
	protected static int bufferSize = 32 * 1024;

	/**
	 * 加载系统本地图片
	 */
	@SuppressWarnings("unused")
	public static Bitmap loadImage(final String url, final String filename) {
		try {
			FileInputStream fis = new FileInputStream(url + filename);
			if (fis != null) {
				return BitmapFactory.decodeStream(fis);
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 下载文件功能
	 * @throws Exception 
	 * @throws NotFoundException 
	 */
	public static void down_file(String url, String path, String filename) throws NotFoundException, Exception {
		//YtLog.i(TAG, "start down shared image");
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		conn.setConnectTimeout(3000);
		InputStream is = conn.getInputStream();
		if (is == null) {
			throw new Exception("stream is null");
		}
		FileUtils util = new FileUtils();
		util.creatSDDir(path);
		File file = util.creatSDFile(path + filename);// 保存的文件名
		
		OutputStream os = new BufferedOutputStream(new FileOutputStream(file), bufferSize);
		
		byte bytes[] = new byte[bufferSize];
		int count;
		while ((count = is.read(bytes, 0, bufferSize)) != -1) {
			os.write(bytes, 0, count);
		}
		os.close();
		is.close();
	}

}
