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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.OrderCart;
import com.imcore.common.model.ProductColor;
import com.imcore.common.model.ProductDetail;
import com.imcore.common.model.ProductOrder;
import com.imcore.common.model.ProductSize;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class OrderDetailActivity extends Activity {

	private int orderId;
	private ListView lv;
	private TextView tvOrderNumber;
	private TextView tvOrderDate;
	private List<ProductOrder> list;
	private List<OrderCart> listCart;
	private List<ProductDetail> listProduct = new ArrayList<ProductDetail>();
	private List<ProductColor> listColor = new ArrayList<ProductColor>();
	private List<ProductSize> listSize = new ArrayList<ProductSize>();
	private final static String SP_NAME = "userInfo";
	private TextView tvOrderState;
	private TextView tvOrderAllPrice;
	private float allPrice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		lv = (ListView) findViewById(R.id.lv_order_detail);
		tvOrderNumber = (TextView) findViewById(R.id.tv_order_detail_order_number);
		tvOrderDate = (TextView) findViewById(R.id.tv_order_detail_order_time);
		tvOrderState = (TextView) findViewById(R.id.tv_order_detail_order_state);
		tvOrderAllPrice = (TextView)findViewById(R.id.tv_order_detail_order_all_price_value);
		
		Intent intent = getIntent();
		orderId = intent.getIntExtra("orderId", 1);
		new OrderDetailTask().execute();

	}

	private class OrderDetailTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String url = "/order/list.do";
			// /////////////////
			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
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

				for (ProductOrder p : list) {
					if (p.id == orderId) {
						tvOrderDate.setText("订单时间 : " + p.createDate);
						if (p.orderStatusId == 1) {
							tvOrderState.setText("订单状态 : 待付款");
						}
						listCart = JsonUtil.toObjectList(p.orderDetailList,
								OrderCart.class);
						for (OrderCart cart : listCart) {
							ProductDetail pd = JsonUtil.toObject(cart.product,
									ProductDetail.class);
							allPrice = allPrice+pd.price * cart.qty;
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
						tvOrderAllPrice.setText("￥"+allPrice);
					}
				}
				tvOrderNumber.setText("订单号 : " + orderId);
				ListViewAdapter adapter = new ListViewAdapter();
				lv.setAdapter(adapter);
                setListViewHeight(lv, adapter);
				super.onPostExecute(result);
			}
		}
	}
	
	
	public void setListViewHeight(ListView lv, ListViewAdapter adapter) {
		int count = lv.getCount();
		int h = 10;
		for (int i = 0; i < count; i++) {
			View view = adapter.getView(i, null, lv);
			view.measure(View.MeasureSpec.UNSPECIFIED,
					View.MeasureSpec.UNSPECIFIED);
			h += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = lv.getLayoutParams();
		params.height = h;
		lv.setLayoutParams(params);
	}
	
	
	

	private class ListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listProduct.size();
		}

		@Override
		public Object getItem(int position) {
			return listProduct.get(position);
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
						R.layout.view_order_detail_list_item, null);

				viewHolder = new ViewHolder();

				viewHolder.ivPic = (ImageView) view
						.findViewById(R.id.iv_order_detail_pic);

				viewHolder.tvName = (TextView) view
						.findViewById(R.id.tv_order_detail_order_name);

				viewHolder.tvColor = (TextView) view
						.findViewById(R.id.tv_order_detail_order_color);

				viewHolder.tvSize = (TextView) view
						.findViewById(R.id.tv_order_detail_order_size);

				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.tv_order_detail_order_price);

				viewHolder.tvCount = (TextView) view
						.findViewById(R.id.tv_order_detail_order_count);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			String imgPath = "http://bulo2bulo.com"
					+ listProduct.get(position).imageUrl + "_M.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.ivPic);

			viewHolder.tvName.setText(listProduct.get(position).name);
			viewHolder.tvColor.setText("颜色 : " + listColor.get(position).color);
			viewHolder.tvSize.setText("尺寸 : " + listSize.get(position).size);
			viewHolder.tvPrice.setText("单价 : ￥"
					+ listProduct.get(position).price);
			viewHolder.tvCount.setText("数量 : " + listCart.get(position).qty);

			return view;
		}

		private class ViewHolder {

			public ImageView ivPic;
			public TextView tvName;
			public TextView tvColor;
			public TextView tvSize;
			public TextView tvPrice;
			public TextView tvCount;
		}

	}

}
