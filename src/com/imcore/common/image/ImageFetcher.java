package com.imcore.common.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.imcore.common.http.HttpHelper;

public class ImageFetcher {
	private ImageCache mImageCache;
	private final static String IMAGE_FETCHER_DEBUG_TAG = "ImageFetcher";

	public ImageFetcher() {
		this.mImageCache = ImageCache.getInstance();
	}

	public void fetch(String url, ImageView imageView) {
		@SuppressWarnings("unchecked")
		WeakReference<ImageWorkerTask> weakTask = (WeakReference<ImageWorkerTask>) imageView
				.getTag();
        if(weakTask!=null){
        	imageView.setImageBitmap(null);
        }
        ImageWorkerTask task = new ImageWorkerTask(url, imageView);
        imageView.setTag(task);
        task.execute();
	}

	/*
	 * 从网络上异步下载图片
	 */
	private class ImageWorkerTask extends AsyncTask<Void, Void, Boolean> {
		private String url;
		private WeakReference<ImageView> weakImageView;
		private int reqWidth;
		private int reqHeight;

		protected ImageWorkerTask(String url, ImageView imageView) {
			this.url = url;
			weakImageView = new WeakReference<ImageView>(imageView);
			this.reqWidth = DisplayUtil.px2Dip(imageView.getContext(),
					imageView.getLayoutParams().width);

			this.reqHeight = DisplayUtil.px2Dip(imageView.getContext(),
					imageView.getLayoutParams().height);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (!mImageCache.isCached(url)) {
				boolean isSucc = downLoadImage(url);
				return isSucc;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Bitmap bitmap = null;
				if (this.reqWidth != 0 && this.reqHeight != 0) {
					bitmap = mImageCache.get(url, reqWidth, reqHeight);
				} else {
					bitmap = mImageCache.get(url);
				}
				if (weakImageView.get() != null) {
					ImageView imageView = weakImageView.get();
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(bitmap);
					}
				}
			}
		}

	}

	/*
	 * 从网络上下载图片到本地存储
	 */
	public boolean downLoadImage(String url) {
		File imgFile = new File(StorageHelper.getAppImageDir(),
				String.valueOf(url.hashCode()));
		InputStream is = null;
		FileOutputStream fos = null;
		boolean isSucc = false;

		try {

			if (!imgFile.exists()) {
				imgFile.createNewFile();
			}
			fos = new FileOutputStream(imgFile);
			is = HttpHelper.getInputStream(url);

			if (is != null) {
				byte[] b = new byte[128];
				int len = 0;
				while ((len = is.read(b)) != -1) {
					fos.write(b, 0, len);
				}
				fos.flush();
				isSucc = true;
			}

		} catch (FileNotFoundException e) {
			Log.e(IMAGE_FETCHER_DEBUG_TAG, "文件未找到");
		} catch (IOException e) {
			Log.e(IMAGE_FETCHER_DEBUG_TAG, e.getLocalizedMessage());
		} finally {
			HttpHelper.closeStream(fos);
			HttpHelper.closeStream(is);
		}
		return isSucc;
	}

}
