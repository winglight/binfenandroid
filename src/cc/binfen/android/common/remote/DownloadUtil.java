package cc.binfen.android.common.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import cc.binfen.android.common.Constant;
import cc.binfen.android.model.CardsModel;

/**
 * @author Michael Intention：As a util to download image by url Description：By a
 *         handler and callback, this util could deal with the image by remote
 *         or local images and return a bitmap instance
 */
public class DownloadUtil {

	private static String LOGTAG = "DownloadUtil";

	// a http request to get the image file from the remote server
	private static void runSaveImageByUrl(final String url,
			final Handler handler) {

		final String path = convertUrl2ImgFileName(url);

		File tmp = new File(path);
		if (tmp.exists()) {
			return;
		}

		Runnable saveUrl = new Runnable() {
			public void run() {

				HttpEntity resEntity = null;

				HttpClient httpclient = new DefaultHttpClient();

				HttpGet httpget;

				HttpResponse response;

				try {

					httpget = new HttpGet(url);

					response = httpclient.execute(httpget);

					int status = response.getStatusLine().getStatusCode();

					if (status == HttpStatus.SC_OK) {
						resEntity = response.getEntity();

						// save to sdcard
						save2card(resEntity, path);

						Message msg = handler.obtainMessage(0, path);
						handler.sendMessage(msg);
					}

				} catch (OutOfMemoryError e) {
					Log.e(LOGTAG, "Out of memory error :(");
				} catch (Exception e) {
					// remove this image from list and update
					// location of post

				} finally {

					if (resEntity != null) {
						resEntity = null;
					}
				}

			}
		};
		new Thread(saveUrl).start();

	}

	// decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(String url, final ImageCallback callback) {
		// 1.deal with the image from internet
		if (url != null && url.startsWith("http://")) {

			String path = convertUrl2ImgFileName(url);

			File f = new File(path);

			// 1. check f is exist
			if (f.exists()) {

				return dealImg(path);
			} else {
				if (callback != null) {
					final Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							String tmp = (String) msg.obj;
							callback.setDrawable(dealImg(tmp));
						}
					};
					runSaveImageByUrl(url, handler);
				}
				return null;
			}
		} else {
			// 2.deal with the local image file
			return dealImg(url);
		}
	}

	public static void decodeFile(final List<CardsModel> list,
			final CoverflowCallback callback, final TextView titleTxt) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.setDrawable(list, titleTxt);
			}
		};

		HttpEntity resEntity = null;

		HttpClient httpclient = new DefaultHttpClient();

		HttpGet httpget;

		HttpResponse response;

		try {
			for (CardsModel card : list) {
				String url = card.getPhoto();
				final String path = convertUrl2ImgFileName(url);

				File tmp = new File(path);
				if (tmp.exists()) {
					continue;
				}

				try {
					httpget = new HttpGet(url);

					response = httpclient.execute(httpget);

					int status = response.getStatusLine().getStatusCode();

					if (status == HttpStatus.SC_OK) {
						resEntity = response.getEntity();

						// save to sdcard
						save2card(resEntity, path);
					}
				} catch (Exception e) {

				}

			}

		} catch (OutOfMemoryError e) {
			Log.e(LOGTAG, "Out of memory error :(");
		} catch (Exception e) {
			// remove this image from list and update
			// location of post

		} finally {

			if (resEntity != null) {
				resEntity = null;
			}
		}
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
	}

	// transfer a lcal image file to a bitmap instance, it'll add scale function
	// if needs.
	private static Bitmap dealImg(String path) {
		try {
			File f = new File(path);
			if(f.exists()){
			// Decode image size
			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
					null, null);

			return bitmap;
			}
		} catch (OutOfMemoryError e) {
			Log.e(LOGTAG, "Out of memory error :(");
		} catch (Exception e) {
			Log.e(LOGTAG, "dealImg error :" + e);
		}
		return null;
	}

	// a unique method to transfer a url to local path
	public static String convertUrl2ImgFileName(String imgUrl) {
		initDir(Constant.IMG_DIR);

		String path = Constant.IMG_DIR + parseFileNameByUrl(imgUrl);
		return path;
	}

	// parse a url into a unique file name
	private static String parseFileNameByUrl(String url) {
		int start = url.lastIndexOf("//");
		if (start < 0) {
			return "";
		} else {
			url = url.substring(start + 2);
		}
		start = url.indexOf("/");
		String fileName = url.substring(start + 1);
		start = fileName.lastIndexOf("?");
		fileName = fileName.substring(start + 1);
		fileName = fileName.replace("&", "_");
		fileName = fileName.replace("=", "_");
		fileName = fileName.replace("%", "_");
		fileName = fileName.replace("/", "_");
		fileName = fileName.replace(" ", "_");
		return fileName;

	}

	// ensure the directory exist for store images
	private static void initDir(String dir) {
		File sdDir = new File(dir);
		if (sdDir.exists() && sdDir.canWrite()) {

		} else {
			sdDir.mkdirs();

		}
	}

	// as named
	private static void save2card(HttpEntity resEntity, String path) {
		try {
			// save to sdcard
			FileOutputStream fos = new FileOutputStream(new File(path));
			resEntity.writeTo(fos);

			// release all instances
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
}
