package com.easemob.helpdesk.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.hyphenate.kefusdk.utils.PathUtil;

import java.io.File;
import java.util.List;



@SuppressWarnings("UnnecessaryLocalVariable")
public class CommonUtils {

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}

	public static boolean isAppRunningForeground(Context ctx) {
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        return ctx.getPackageName().equalsIgnoreCase(tasks.get(0).baseActivity.getPackageName());
    }


	// 转换dip为px
	public static int convertDip2Px(Context context, int dip) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}


	public static String getFilePath(String remoteUrl, String fileName) {
		String filePath = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path = PathUtil.getInstance().getFilePath() + "/" + filePath;
		if (!TextUtils.isEmpty(fileName) && fileName.contains(".")) {
			path = path + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		return path;
	}

	public static String convertStringByMessageText(String messageText){
		return messageText.replaceAll("&lt;", "<").replaceAll("&#39;", "'").replaceAll("&amp;","&");
	}


    @SuppressLint("NewApi")
	public static Bitmap getScaleBitmap(Context ctx, String filePath){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);

		int bmpWidth = opt.outWidth;
		int bmpHeight = opt.outHeight;

		WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		int screenWidth;
		int screenHeight;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;
		} else {
			screenWidth = display.getWidth();
			screenHeight = display.getHeight();
		}
		opt.inSampleSize = 1;
		if(bmpWidth > bmpHeight){
			if(bmpWidth>screenWidth){
				opt.inSampleSize = bmpWidth / screenWidth;
			}
		}else{
			if(bmpHeight > screenHeight){
				opt.inSampleSize = bmpHeight / screenHeight;
			}
		}
		opt.inJustDecodeBounds = false;
		try{
			bmp = BitmapFactory.decodeFile(filePath, opt);
		}catch (OutOfMemoryError error){
			bmp = BitmapFactory.decodeFile(filePath);
		}

		try{
			Bitmap rotateBmp = rotateImage(new File(filePath), bmp);
			return rotateBmp;
		}catch (Exception e){
			return bmp;
		}
	}

	private static Bitmap rotateImage(final File file, Bitmap b){
		try {
			ExifInterface ei = new ExifInterface(file.getPath());
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			Matrix matrix = new Matrix();
			switch (orientation){
				case ExifInterface.ORIENTATION_ROTATE_90:
					matrix.postRotate(90);
					b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					matrix.postRotate(180);
					b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					matrix.postRotate(270);
					b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
					break;
				default:
					b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
					break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return b;
	}


	/**
	 * 获取apk的版本名称 currentVersionName
	 * @param context
	 * @return
	 */
	public static String getAppVersionNameFromApp(Context context) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 文字copy
	 * @param context
	 * @param content copy的文字
	 */
	public static void copyText(Context context, String content) {
		if (Build.VERSION.SDK_INT >= 11) {
			android.text.ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setText(content);
		} else {
			android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));
		}

	}

}



