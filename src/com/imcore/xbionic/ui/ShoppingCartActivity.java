package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.ProductColor;
import com.imcore.common.model.ProductDetail;
import com.imcore.common.model.ProductSize;
import com.imcore.common.model.ShopCart;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class ShoppingCartActivity extends Activity {
	private List<ShopCart> list;
	private List<ProductDetail> listProduct = new ArrayList<ProductDetail>();
	private List<ProductSize> listSize = new ArrayList<ProductSize>();
	private List<ProductColor> listColor = new ArrayList<ProductColor>();
	private final static String SP_NAME = "userInfo";
	private ListView lv;
	private Button btnEdit;
	private Button btnAccount;
	private TextView tvPrice;
	private float mAllPrice = 0;

	private String mUserId;
	private String mToken;

	private ListViewAdapter adapter = null;
	private ListCopyAdapter cAdapter = null;
	private boolean flag = false;

	// private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_cart);

		lv = (ListView) findViewById(R.id.lv_shopping_cart);
		Intent intent = getIntent();
		btnEdit = (Button) findViewById(R.id.btn_shopping_cart_more);
		tvPrice = (TextView) findViewById(R.id.tv_shop_cart_all_price_value);
		btnAccount = (Button) findViewById(R.id.btn_shop_cart_acounts);
		// 结算
		btnAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mAllPrice == 0) {
					return;
				}

				Intent intent = new Intent(ShoppingCartActivity.this,
						AccountActivity.class);
				intent.putExtra("allPrice", mAllPrice);
				startActivity(intent);
			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (flag) {

					if (cAdapter == null) {
						cAdapter = new ListCopyAdapter();
					}
					cAdapter.notifyDataSetChanged();
					lv.setAdapter(cAdapter);
					flag = false;
				} else {
					if (adapter == null) {
						adapter = new ListViewAdapter();
					}
					adapter.notifyDataSetChanged();
					lv.setAdapter(adapter);
					flag = true;
				}

			}
		});

		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		mUserId = sp.getString("userId", "");
		mToken = sp.getString("token", "");
		new ShoppingTask().execute();

	}

	private class ShoppingTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String url = "/shoppingcart/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", mUserId);
			args.put("token", mToken);
			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, args);
			String jsonResponse = null;
			try {
				// 得到请求的网络返回的结果
				jsonResponse = HttpHelper.execute(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonResponse;

		}

		@Override
		protected void onPostExecute(String result) {
			if (TextUtil.isEmptyString(result)) {
				return;
			}
			// 将json纯文本拿到ResponseJsonEntity.fromJSON(result)进行解析，然后放入其实体里
			ResponseJsonEntity resEntity = ResponseJsonEntity.fromJSON(result);

			if (resEntity.getStatus() == 200) {

				String jsonData = resEntity.getData();
				// List<String> listTest = JsonUtil.toJsonStrList(jsonData);
				list = JsonUtil.toObjectList(jsonData, ShopCart.class);

				for (ShopCart c : list) {
					ProductDetail pd = JsonUtil.toObject(c.product,
							ProductDetail.class);
					mAllPrice += pd.price * c.qty;
					listProduct.add(pd);

					String jsonColor = JsonUtil.getJsonValueByKey(c.product,
							"sysColor");
					ProductColor pc = JsonUtil.toObject(jsonColor,
							ProductColor.class);

					listColor.add(pc);

					String jsonSize = JsonUtil.getJsonValueByKey(c.product,
							"sysSize");
					ProductSize ps = JsonUtil.toObject(jsonSize,
							ProductSize.class);
					listSize.add(ps);
				}
				tvPrice.setText("￥" + mAllPrice);
				adapter = new ListViewAdapter();
				lv.setAdapter(adapter);
				flag = true;

				super.onPostExecute(result);
			}
		}

	}

	private class DeleteTask extends AsyncTask<Integer, Void, String> {
		private int mPosition;

		@Override
		protected String doInBackground(Integer... params) {

			int cartId = params[0];
			mPosition = params[1];
			String url = "/shoppingcart/delete.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", mUserId);
			args.put("token", mToken);
			args.put("cartId", cartId);
			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, args);
			String jsonResponse = null;
			try {
				// 得到请求的网络返回的结果
				jsonResponse = HttpHelper.execute(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonResponse;

		}

		@Override
		protected void onPostExecute(String result) {
			if (TextUtil.isEmptyString(result)) {
				return;
			}
			// 将json纯文本拿到ResponseJsonEntity.fromJSON(result)进行解析，然后放入其实体里
			ResponseJsonEntity resEntity = ResponseJsonEntity.fromJSON(result);

			if (resEntity.getStatus() == 200) {

				mAllPrice -= list.get(mPosition).qty
						* listProduct.get(mPosition).price;
				tvPrice.setText("￥" + mAllPrice);

				list.remove(mPosition);
				listProduct.remove(mPosition);
				listColor.remove(mPosition);
				listSize.remove(mPosition);
				cAdapter.notifyDataSetChanged();

				ToastUtil.showToast(ShoppingCartActivity.this, "您已成功删除！");

			}

		}
	}

	private class ListViewAdapter extends BaseAdapter {

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
						R.layout.view_shop_cart_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.ivpic = (ImageView) view
						.findViewById(R.id.iv_shop_cart);

				viewHolder.tvName = (TextView) view
						.findViewById(R.id.tv_shop_cart_name);

				viewHolder.tvColor = (TextView) view
						.findViewById(R.id.tv_shop_cart_color);

				viewHolder.tvSize = (TextView) view
						.findViewById(R.id.tv_shop_cart_size);

				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.tv_shop_cart_price_value);

				viewHolder.tvCount = (TextView) view
						.findViewById(R.id.tv_shop_cart_count);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			String imgPath = "http://bulo2bulo.com"
					+ listProduct.get(position).imageUrl + "_L.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.ivpic);
			viewHolder.tvName.setText(listProduct.get(position).name);
			viewHolder.tvColor.setText("颜色 : " + listColor.get(position).color);
			viewHolder.tvSize.setText("尺码 : " + listSize.get(position).size);
			viewHolder.tvPrice.setText("￥" + listProduct.get(position).price);
			viewHolder.tvCount.setText(list.get(position).qty + "");

			return view;
		}

		private class ViewHolder {
			public ImageView ivpic;
			public TextView tvName;
			public TextView tvColor;
			public TextView tvSize;
			public TextView tvPrice;
			public TextView tvCount;
		}

	}

	private class ListCopyAdapter extends BaseAdapter {

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
						R.layout.view_shop_cart_list_item, null);
				viewHolder = new ViewHolder();

				viewHolder.ivRemove = (ImageView) view
						.findViewById(R.id.iv_shop_cart_remove);

				viewHolder.ivpic = (ImageView) view
						.findViewById(R.id.iv_shop_cart);

				viewHolder.tvName = (TextView) view
						.findViewById(R.id.tv_shop_cart_name);

				viewHolder.tvColor = (TextView) view
						.findViewById(R.id.tv_shop_cart_color);

				viewHolder.tvSize = (TextView) view
						.findViewById(R.id.tv_shop_cart_size);

				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.tv_shop_cart_price_value);

				viewHolder.tvCount = (TextView) view
						.findViewById(R.id.tv_shop_cart_count);

				viewHolder.ivSub = (ImageView) view
						.findViewById(R.id.iv_shop_cart_sub);

				viewHolder.ivPlub = (ImageView) view
						.findViewById(R.id.iv_shop_cart_plub);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			String imgPath = "http://bulo2bulo.com"
					+ listProduct.get(position).imageUrl + "_L.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.ivpic);
			viewHolder.ivRemove.setBackgroundResource(R.drawable.remove_icon);
			viewHolder.tvName.setText(listProduct.get(position).name);
			viewHolder.tvColor.setText("颜色 : " + listColor.get(position).color);
			viewHolder.tvSize.setText("尺码 : " + listSize.get(position).size);
			viewHolder.tvPrice.setText("￥" + listProduct.get(position).price);
			viewHolder.tvCount.setText(list.get(position).qty + "");
			viewHolder.ivSub.setVisibility(View.VISIBLE);
			viewHolder.ivPlub.setVisibility(View.VISIBLE);
			viewHolder.ivSub.setTag(viewHolder.tvCount);
			viewHolder.ivPlub.setTag(viewHolder.tvCount);

			final int mPosition = position;
			viewHolder.ivRemove.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new DeleteTask().execute(list.get(mPosition).id, mPosition);

				}
			});

			viewHolder.ivPlub.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					TextView tv = (TextView) v.getTag();
					int cou = Integer.parseInt(tv.getText().toString());
					cou = cou + 1;
					tv.setText(String.valueOf(cou));
				}
			});

			viewHolder.ivSub.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					TextView tv = (TextView) v.getTag();
					int cou = Integer.parseInt(tv.getText().toString());
					cou = cou - 1;
					if (cou < 1) {
						return;
					}
					tv.setText(String.valueOf(cou));
				}
			});

			return view;
		}

		private class ViewHolder {
			public ImageView ivRemove;
			public ImageView ivpic;
			public TextView tvName;
			public TextView tvColor;
			public TextView tvSize;
			public TextView tvPrice;
			public TextView tvCount;
			public ImageView ivSub;
			public ImageView ivPlub;
		}

	}

}
