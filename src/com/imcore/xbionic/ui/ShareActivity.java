package com.imcore.xbionic.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x_bionic.R;
import com.example.x_bionic.R.id;
import com.example.x_bionic.R.layout;
import com.example.x_bionic.R.string;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.ProductList;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;
import com.imcore.common.weibo.Constants;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.exception.WeiboShareException;

public class ShareActivity extends Activity {

	private List<ProductList> list;
	private ListView lv;
	private ActionBar mActionBar;
	private ProgressDialog progressDialog;

	/** 微博分享的接口实例 */
	private IWeiboShareAPI mWeiboShareAPI;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		lv = (ListView) findViewById(R.id.lv_share);

		mActionBar = getActionBar();
		mActionBar.hide();
		new DetailTask().execute();

	}

	/**
	 * 初始化 UI 和微博接口实例 。
	 */
	private void initialize() {

		// 创建微博 SDK 接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);

		// 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
		boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
		int supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI();

		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!isInstalledWeibo) {
			mWeiboShareAPI
					.registerWeiboDownloadListener(new IWeiboDownloadListener() {
						@Override
						public void onCancel() {
							Toast.makeText(
									ShareActivity.this,
									R.string.weibosdk_demo_cancel_download_weibo,
									Toast.LENGTH_SHORT).show();
						}
					});
		}

		mWeiboShareAPI.registerApp();

	}

	private class DetailTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ShareActivity.this,
					"loading...", "请稍后...", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			int navId = 100001;
			int subNavId = 5;
			String url = "/category/products.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("navId", navId);
			args.put("subNavId", subNavId);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, args);
			String jsonResponse = null;
			try {
				// 得到请求的网络返回的结果
				jsonResponse = HttpHelper.execute(entity);
				// Log.i("xxx", jsonResponse);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonResponse;
		}

		@Override
		protected void onPostExecute(String result) {

			progressDialog.dismiss();

			if (TextUtil.isEmptyString(result)) {
				return;
			}
			// 将json纯文本拿到ResponseJsonEntity.fromJSON(result)进行解析，然后放入其实体里
			ResponseJsonEntity resEntity = ResponseJsonEntity.fromJSON(result);

			if (resEntity.getStatus() == 200) {
				String jsonData = resEntity.getData();
				list = JsonUtil.toObjectList(jsonData, ProductList.class);
				ListAdapter adapter = new ListAdapter();
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						Intent intent = new Intent(ShareActivity.this,
								ProductDetailActivity.class);
						int productId = list.get(position).id;
						intent.putExtra("id", productId);
						startActivity(intent);
					}
				});

				super.onPostExecute(result);
			}
		}

	}

	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = null;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.view_share_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view.findViewById(R.id.iv_share);
				viewHolder.tvName = (TextView) view
						.findViewById(R.id.tv_share_name);

				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.tv_share_price);

				viewHolder.btnWeibo = (Button) view
						.findViewById(R.id.btn_share_weibo);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			ProductList p = list.get(position);
			String imgPath = "http://www.bulo2bulo.com" + p.imageUrl + "_L.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.img);
			viewHolder.tvName.setText(p.name);
			viewHolder.tvPrice.setText("￥" + p.price);
			viewHolder.btnWeibo.setTag(viewHolder.img);

			viewHolder.btnWeibo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					initialize();
					try {
						// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
						if (mWeiboShareAPI.checkEnvironment(true)) {
							 WeiboMessage weiboMessage = new WeiboMessage();
							 weiboMessage.mediaObject = getImageObj((ImageView)arg0.getTag());
							 // 2. 初始化从第三方到微博的消息请求
						        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
						        // 用transaction唯一标识一个请求
						        request.transaction = String.valueOf(System.currentTimeMillis());
						        request.message = weiboMessage;
						        
						        // 3. 发送请求消息到微博，唤起微博分享界面
						        mWeiboShareAPI.sendRequest(request);
						}
					} catch (WeiboShareException e) {
						e.printStackTrace();
						Toast.makeText(ShareActivity.this, e.getMessage(),
								Toast.LENGTH_LONG).show();
					}

				}
			});

			return view;
		}

	}

	private class ViewHolder {
		public ImageView img;
		public TextView tvName;
		public TextView tvPrice;
		public Button btnWeibo;
	}
	
	
	/**
     * 创建图片消息对象。
     * 
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(ImageView img) {
        ImageObject imageObject = new ImageObject();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
        imageObject.setImageObject(bitmapDrawable.getBitmap());
        return imageObject;
    }
	

}
