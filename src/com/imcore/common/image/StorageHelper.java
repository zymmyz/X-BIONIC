package com.imcore.common.image;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.util.Log;

public class StorageHelper {

	public final static String APP_DIR_ROOT = "YunMing";
	public final static String APP_DIR_IMAGE = "YunMing/images";
	private final static String LOG_DEBUG_TAG = "gh_storage";
    /*
     * 避免被创造实例
     */
	private StorageHelper() {

	}
	
	/*
	 * 判断外存储设备是否可写
	 */
	public static boolean isExternalStorageWritable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}
		return false;
	}
	
	/*
	 * 判断外存储设备是否可读
	 */
	public static boolean isExternalStorageReadable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)||Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			return true;
		}
		return false;
	}
	/*
	 * 获取app在外存储器的根路径
	 */
	public static File getAppDir(){
		File appDir = null;
		if(isExternalStorageWritable()){
			appDir = new File(Environment.getExternalStorageDirectory()+"/"+APP_DIR_ROOT);
			if(!appDir.exists()){
				appDir.mkdir();
			}else{
				Log.e(LOG_DEBUG_TAG, "外部存储器不可写入");
			}
		}
		return appDir;
	}
	
	/**
	 * 获取当前app存放图片文件的目录
	 * 
	 * @return 返回File对象实例，表示当前app存储图片的目录
	 */
	protected static File getAppImageDir() {
		File appDir = null;
		if (isExternalStorageWritable()) {
			appDir = new File(Environment.getExternalStorageDirectory() + "/"
					+ APP_DIR_IMAGE);
			if (!appDir.exists()) {
				appDir.mkdirs();
			}
		} else {
			Log.d(LOG_DEBUG_TAG, "外部存储器不可写入");
		}
		return appDir;
	}
	/*
	 * 根据图片路径获取本地图片，不做边界压缩
	 */
	protected static Bitmap getBitmapFromLocal(String imgName){
		
		if(!isExternalStorageReadable()){
			Log.d(LOG_DEBUG_TAG, "外部存储器不可读");
			return null;
		}
		
		File imgFile = new File(getAppImageDir(),imgName);
		Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	    return bitmap;
	}
	
	protected static Bitmap getBitmapFromLocal(String imgName,int reqWidth,int reqHeight){
		if(!isExternalStorageReadable()){
			Log.d(LOG_DEBUG_TAG, "外部存储器不可读");
			return null;
		}
		File imgFile = new File(getAppImageDir(),imgName);
		Options opts = new Options();
		//true表示只加载图片宽高
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(getAppImageDir() + "/" + imgName, opts);
		opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
		return bitmap;
	}
	
	
	/**
	 * 计算图片inSampleSize缩放比
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(Options options, int reqWidth,
			int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			final float totalPixels = width * height;
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

}
