package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.OrderCart;
import com.imcore.common.model.ProductColor;
import com.imcore.common.model.ProductCommon;
import com.imcore.common.model.ProductDetail;
import com.imcore.common.model.ProductOrder;
import com.imcore.common.model.ProductSize;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class ProductOrderFragment extends Fragment {

	private int item;
	private final static String SP_NAME = "userInfo";
	private ListView lv;
	private List<ProductOrder> list;
	private List<List<OrderCart>> listAllCart = new ArrayList<List<OrderCart>>();
	private List<List<ProductDetail>> listAllProductDetail = new ArrayList<List<ProductDetail>>();
	private List<List<ProductSize>> listAllProductSize = new ArrayList<List<ProductSize>>();
	private List<List<ProductColor>> listAllProductColor = new ArrayList<List<ProductColor>>();

	private List<ProductCommon> listCommon;
	private List<ProductDetail> listProduct = new ArrayList<ProductDetail>();

	private CommontAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		item = getArguments().getInt("item");
		View view = inflater.inflate(R.layout.view_you_order_list, null);
		lv = (ListView) view.findViewById(R.id.lv_you_order);
		if (item == 0) {
			new YouOrderTask().execute();
		} else {
			new CollectTask().execute();
		}
		return view;
	}

	private class YouOrderTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String url = "/order/list.do";
			// /////////////////
			SharedPreferences sp = getActivity().getSharedPreferences(SP_NAME,
					getActivity().MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("type", 1);
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

				list = JsonUtil.toObjectList(jsonData, ProductOrder.class);
				System.out.println("list size:" + list.size());
				for (ProductOrder p : list) {

					List<OrderCart> listCart = new ArrayList<OrderCart>();
					List<ProductDetail> listProduct = new ArrayList<ProductDetail>();
					List<ProductSize> listSize = new ArrayList<ProductSize>();
					List<ProductColor> listColor = new ArrayList<ProductColor>();

					listCart = JsonUtil.toObjectList(p.orderDetailList,
							OrderCart.class);
					Log.i("yyy", p.orderDetailList);
					System.out.println("size:" + listCart.size());
					for (OrderCart cart : listCart) {
						ProductDetail pd = JsonUtil.toObject(cart.product,
								ProductDetail.class);
						listProduct.add(pd);
						String jsonColor = JsonUtil.getJsonValueByKey(
								cart.product, "sysColor");
						ProductColor pc = JsonUtil.toObject(jsonColor,
								ProductColor.class);
						listColor.add(pc);
						String jsonSize = JsonUtil.getJsonValueByKey(
								cart.product, "sysSize");
						ProductSize ps = JsonUtil.toObject(jsonSize,
								ProductSize.class);
						listSize.add(ps);

					}
					listAllProductDetail.add(listProduct);
					List<ProductDetail> l = listAllProductDetail.get(0);
					listAllCart.add(listCart);
					listAllProductColor.add(listColor);
					listAllProductSize.add(listSize);

				}

				ListViewAdapter adapter = new ListViewAdapter();
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						int orderId = list.get(position).id;
						Intent intent = new Intent(getActivity(),
								OrderDetailActivity.class);
						intent.putExtra("orderId", orderId);
						startActivity(intent);
					}
				});

				super.onPostExecute(result);
			}
		}

	}

	private class CollectTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String url = "/user/favorite/list.do";
			SharedPreferences sp = getActivity().getSharedPreferences(SP_NAME,
					getActivity().MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("type", 1);
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
				listCommon = JsonUtil.toObjectList(jsonData,
						ProductCommon.class);
				for (int i = 0; i < listCommon.size(); i++) {
					ProductCommon p = listCommon.get(i);
					new DetailTask().execute(p.productId);

				}
				super.onPostExecute(result);
			}
		}

	}

	private class DetailTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];

			String url = "/product/get.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("id", id);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, args);
			String jsonResponse = null;
			try {
				// 得到请求的网络返回的结果
				jsonResponse = HttpHelper.execute(entity);
				Log.i("XXX", jsonResponse);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonResponse;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {

			if (TextUtil.isEmptyString(result)) {
				return;
			}
			// 将json纯文本拿到ResponseJsonEntity.fromJSON(result)进行解析，然后放入其实体里
			ResponseJsonEntity resEntity = ResponseJsonEntity.fromJSON(result);

			if (resEntity.getStatus() == 200) {
				String jsonData = resEntity.getData();
				ProductDetail p = JsonUtil.toObject(jsonData,
						ProductDetail.class);
				listProduct.add(p);
				if (listProduct.size() == listCommon.size()) {
					adapter = new CommontAdapter();
					lv.setAdapter(adapter);

				}

				super.onPostExecute(result);
			}
		}

	}

	private class DeleteTask extends AsyncTask<Integer, Void, String> {
		int position;

		@Override
		protected String doInBackground(Integer... params) {
			int id = params[0];
			position = params[1];
			String url = "/user/favorite/delete.do";
			SharedPreferences sp = getActivity().getSharedPreferences(SP_NAME,
					getActivity().MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("type", 1);
			args.put("id", id);
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
				listCommon.remove(position);
				adapter.notifyDataSetChanged();
				lv.setAdapter(adapter);
				// lv.setAdapter(adapter);
				ToastUtil.showToast(getActivity(), "删除成功!");

				super.onPostExecute(result);
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
				view = getActivity().getLayoutInflater().inflate(
						R.layout.view_you_order_list_item, null);

				viewHolder = new ViewHolder();
				viewHolder.tvOrderNumber = (TextView) view
						.findViewById(R.id.tv_you_order_order_number);

				viewHolder.tvOrderDate = (TextView) view
						.findViewById(R.id.tv_you_order_order_time);

				viewHolder.ivPic = (ImageView) view
						.findViewById(R.id.iv_you_order_pic);

				viewHolder.tvOrderName = (TextView) view
						.findViewById(R.id.tv_you_order_order_name);

				viewHolder.tvColor = (TextView) view
						.findViewById(R.id.tv_you_order_order_color);

				viewHolder.tvSize = (TextView) view
						.findViewById(R.id.tv_you_order_order_size);

				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.tv_you_order_order_price);

				viewHolder.tvCount = (TextView) view
						.findViewById(R.id.tv_you_order_order_count);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			String imgPath = "http://bulo2bulo.com"
					+ listAllProductDetail.get(position).get(0).imageUrl
					+ "_M.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.ivPic);

			viewHolder.tvOrderNumber.setText("订单号 : " + list.get(position).id);
			viewHolder.tvOrderDate.setText("订单时间 : "
					+ list.get(position).createDate);
			viewHolder.tvOrderName.setText(listAllProductDetail.get(position)
					.get(0).name);
			viewHolder.tvColor.setText("颜色 : "
					+ listAllProductColor.get(position).get(0).color);
			viewHolder.tvSize.setText("尺寸 : "
					+ listAllProductSize.get(position).get(0).size);
			viewHolder.tvPrice.setText("单价 : ￥"
					+ listAllProductDetail.get(position).get(0).price);
			viewHolder.tvCount.setText("数量 : "
					+ listAllCart.get(position).get(0).qty);

			return view;
		}

		private class ViewHolder {
			public TextView tvOrderNumber;
			public TextView tvOrderDate;
			public ImageView ivPic;
			public TextView tvOrderName;
			public TextView tvColor;
			public TextView tvSize;
			public TextView tvPrice;
			public TextView tvCount;
		}

	}

	private class CommontAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listCommon.size();
		}

		@Override
		public Object getItem(int position) {
			return listCommon.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = null;
			final int p = position;
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(
						R.layout.view_common_list_item, null);

				viewHolder = new ViewHolder();
				viewHolder.ivPic = (ImageView) view
						.findViewById(R.id.iv_common);
				viewHolder.tvName = (TextView) view
						.findViewById(R.id.tv_common_name);
				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.tv_common_price_value);
				viewHolder.tvPublicDate = (TextView) view
						.findViewById(R.id.tv_common_public_date);
				viewHolder.btnDelete = (Button) view
						.findViewById(R.id.btn_common_delete);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			ProductDetail pd = listProduct.get(position);

			String imgPath = "http://bulo2bulo.com"
					+ listProduct.get(position).imageUrl + "_M.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.ivPic);
			viewHolder.tvName.setText(pd.name);
			viewHolder.tvPrice.setText("￥" + pd.price);
			viewHolder.tvPublicDate.setText(listCommon.get(position).addDate);
			// ///////////////////////////////////////////////////////////
			viewHolder.btnDelete.setTag(listCommon.get(position).id);
			viewHolder.btnDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int id = (Integer) v.getTag();
					new DeleteTask().execute(id, p);

				}
			});

			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float xDown = 0;
					float yDown = 0;
					float xUp = 0;
					final ViewHolder holder = (ViewHolder) v.getTag();
					// 当按下时处理
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						xDown = event.getX();
						yDown = event.getY();
					}

					if (event.getAction() == MotionEvent.ACTION_UP) {
						xUp = event.getX();

						if ((xUp - xDown > 100)) {
							if (holder.btnDelete != null) {

								if (holder.btnDelete.getVisibility() == View.VISIBLE) {

									holder.btnDelete.setVisibility(View.GONE);
								} else {
									holder.btnDelete
											.setVisibility(View.VISIBLE);
								}

							}
						}

					}

					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						// float xMove = event.getX();
						float xMove = event.getX(event.getPointerCount() - 1);
						float yMove = event.getY(event.getPointerCount() - 1);

					}

					/*
					 * if (event.getAction() == MotionEvent.ACTION_UP) { xUp =
					 * event.getX(); }
					 */

					return true;
				}
			});

			return view;
		}

		private class ViewHolder {
			public ImageView ivPic;
			public TextView tvName;
			public TextView tvPrice;
			public TextView tvPublicDate;
			public Button btnDelete;
		}

	}

}
