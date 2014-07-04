package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.ProductList;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class ProductListFragment extends Fragment {
	private List<ProductList> list;
	private GridView gv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		int navId = args.getInt("navId");
		int subNavId = args.getInt("subNavId");
		int mDesc = args.getInt("desc");
		View view = inflater.inflate(R.layout.view_product_list_grid, null);
		list = new ArrayList<ProductList>();
		gv = (GridView) view.findViewById(R.id.gv_product_list);
		new DetailTask().execute(navId, subNavId,mDesc);
		return view;
	}

	private class DetailTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int navId = params[0];
			int subNavId = params[1];
			int desc = params[2];
			String url = "/category/products.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("navId", navId);
			args.put("subNavId", subNavId);
			args.put("orderBy", "price");
	        args.put("desc",desc );

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

			if (TextUtil.isEmptyString(result)) {
				return;
			}
			// 将json纯文本拿到ResponseJsonEntity.fromJSON(result)进行解析，然后放入其实体里
			ResponseJsonEntity resEntity = ResponseJsonEntity.fromJSON(result);

			if (resEntity.getStatus() == 200) {
				String jsonData = resEntity.getData();
				list = JsonUtil.toObjectList(jsonData, ProductList.class);
				GridAdapter adapter = new GridAdapter();
				gv.setAdapter(adapter);
				gv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						Intent intent = new Intent(getActivity(),
								ProductDetailActivity.class);
						int productId = list.get(position).id;
						ToastUtil.showToast(getActivity(), "id="+productId);
						intent.putExtra("id", productId);
						startActivity(intent);
					}
				});

				super.onPostExecute(result);
			}
		}

	}

	private class GridAdapter extends BaseAdapter {

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
						R.layout.view_frg_product_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view
						.findViewById(R.id.iv_product_list);
				viewHolder.tvName = (TextView) view
						.findViewById(R.id.tv_product_list_name);

				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.tv_product_list_price);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			ProductList p = list.get(position);
			String imgPath = "http://www.bulo2bulo.com" + p.imageUrl + "_L.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.img);
			viewHolder.tvName.setText(p.name);
			viewHolder.tvPrice.setText("￥" + p.price);
			return view;
		}

	}

	private class ViewHolder {
		public ImageView img;
		public TextView tvName;
		public TextView tvPrice;
	}

}
