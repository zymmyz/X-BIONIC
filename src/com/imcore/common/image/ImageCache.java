package com.imcore.common.image;

import java.io.File;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageCache {
	private LruCache<String, Bitmap> mCache;
	private static ImageCache instance;

	/*
	 * 构造函数，初始化缓存时要有分配的最大内存
	 */
	private ImageCache() {
		int maxSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
		mCache = new LruCache<String, Bitmap>(maxSize);
	}

	/*
	 * 单例模式，通过这个函数可以拿到这个类的唯一实例，由于是唯一实例，所以要对其加锁，防止线程的不安全性
	 */
	protected synchronized static ImageCache getInstance() {
		if (instance == null) {
			instance = new ImageCache();
		}
		return instance;
	}

	/*
	 * 判断图片是否有在内存缓存中，是返回真，否则再次判断是否存在本地，如果也没有在本地，则需要从网络中下载图片
	 */
	protected synchronized boolean isCached(String key) {
		return isExistsInMemory(key) ? true : isExistsInLocal(key);
	}

	/*
	 * 判断是否在内存缓存中
	 */
	private boolean isExistsInMemory(String key) {
		return (mCache.get(key) != null);
	}

	/*
	 * 判断是否在本地存储卡中
	 */
	private boolean isExistsInLocal(String key) {
		boolean isExit = true;
		String fileName = String.valueOf(key.hashCode());
		File file = new File(StorageHelper.APP_DIR_ROOT, fileName);
		if (!file.exists()) {
			isExit = false;
		}
		return isExit;
	}
	
	/**
	 * 从缓存中获取key 指定的图片对象
	 * 
	 * @param key
	 * @return
	 */
	protected Bitmap get(String key) {
		return this.get(key, 0, 0);
	}

	/*
	 * 从缓存中获取图片，并且制定缩放之后的尺寸
	 */
	protected Bitmap get(String key, int reqWidth, int reqHeight) {
		if (isExistsInMemory(key)) {
			return mCache.get(key);
		}
		if (reqWidth != 0 && reqWidth != 0) {
			return this.getBitmapFromLocal(key, reqWidth, reqHeight);
		}
		return this.getBitmapFromLocal(key);
	}
	/*
     * 从本地获取图片，无边界压缩
     */
	private Bitmap getBitmapFromLocal(String key) {
		return this.getBitmapFromLocal(key, 0, 0);
	}
    /*
     * 从本地获取图片，有边界压缩
     */
	private Bitmap getBitmapFromLocal(String key, int reqWidth, int reqHeight) {
		Bitmap bitmap = null;
		if (reqWidth != 0 && reqHeight != 0) {
			//StorageHelper.getBitmapFromLocal(String,int,int)有边界压缩，取得图片
			bitmap = StorageHelper.getBitmapFromLocal(String.valueOf(key.hashCode()),
					reqWidth, reqHeight);
		}else{
			//StorageHelper.getBitmapFromLocal(String)无边界压缩，取得图片
			bitmap = StorageHelper.getBitmapFromLocal(String.valueOf(key.hashCode()));
		}
		put(key, bitmap);
		return bitmap;
	}
	//从本地读取读片后存入内存缓存中
	private void put(String key,Bitmap bitmap){
		if(key!=null&&bitmap!=null){
			mCache.put(key, bitmap);
		}
	}

}
