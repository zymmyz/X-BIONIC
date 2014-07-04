package com.imcore.xbionic.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.imcore.common.model.ProductDetail;
import com.imcore.common.model.ProductDetailAward;
import com.imcore.common.model.ProductDetailCommon;
import com.imcore.common.model.ProductDetailNews;
import com.imcore.common.model.ProductOrder;
import com.imcore.common.model.ProductSize;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class ProductDetailRightFragment extends Fragment implements OnClickListener{
	private ListView lv;
	private List<ProductDetailCommon> list;
	private List<ProductDetailNews> listNews;
	private List<ProductDetailAward> listAward;
	private int id;
	private String color;
	private String size;
	private String name;
	private float price;
	private String count;

	private Button btnCommon;
	private Button btnNews;
	private Button btnAward;
    
	private LinearLayout linear;
	private View view ;
	
	private Button btnShop;
	private Button btnCollect;
	private final static String SP_NAME = "userInfo";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle args = getArguments();
		id = args.getInt("productId");
		color = args.getString("colorName");
		size = args.getString("sizeName");
		//////////////
		name = args.getString("productName");
		price = args.getFloat("productPrice");
		count = args.getString("count");

		view = inflater.inflate(R.layout.view_product_detail_right_frag,
				null);
		linear = (LinearLayout)view.findViewById(R.id.layout_linear_frg);
		lv = (ListView) view.findViewById(R.id.lv_product_detail_right_fra);
		btnCommon = (Button) view.findViewById(R.id.btn_user_comment);
		btnNews = (Button) view.findViewById(R.id.btn_product_news);
		btnAward = (Button) view.findViewById(R.id.btn_award_winning);
		btnShop = (Button)view.findViewById(R.id.btn_shopping_cart);
		btnCollect = (Button)view.findViewById(R.id.btn_colloct);
		
		
		btnCommon.setOnClickListener(this);
		btnNews.setOnClickListener(this);
		btnAward.setOnClickListener(this);
		
		btnShop.setOnClickListener(this);
		btnCollect.setOnClickListener(this);
		
		new CommonTask().execute(id);
        
		return view;
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
						R.layout.view_product_detail_rigth_frg_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tvComment = (TextView) view
						.findViewById(R.id.tv_product_detail_frag_comment);

				viewHolder.tvAbout = (TextView) view
						.findViewById(R.id.tv_product_detail_frag_about);

				viewHolder.tvUserName = (TextView) view
						.findViewById(R.id.tv_product_detail_frag_user_name);

				viewHolder.tvTime = (TextView) view
						.findViewById(R.id.tv_product_detail_frag_time);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			ProductDetailCommon common = list.get(position);
			viewHolder.tvComment.setText(common.comment);
			viewHolder.tvAbout
					.setText("颜色分类 : " + color + ";" + "尺码 : " + size);
			viewHolder.tvUserName.setText(common.lastName + common.firstName);
			viewHolder.tvTime.setText(common.commentDate);
			return view;
		}

		private class ViewHolder {
			public TextView tvComment;
			public TextView tvAbout;
			public TextView tvUserName;
			public TextView tvTime;
		}

	}
	
	
	
	private class NewsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listNews.size();
		}

		@Override
		public Object getItem(int position) {
			return listNews.get(position);
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
						R.layout.view_product_detail_right_frg_news_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view
						.findViewById(R.id.iv_product_detail_frag_pic);

				viewHolder.tvTitle = (TextView) view
						.findViewById(R.id.tv_product_detail_frag_title);

				viewHolder.tvTime = (TextView) view
						.findViewById(R.id.tv_product_detail_frag_news_time);


				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			ProductDetailNews news = listNews.get(position);
			String imgPath = "http://bulo2bulo.com"+news.imageUrl+"_S.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.img);
			viewHolder.tvTitle.setText(news.title);
			viewHolder.tvTime.setText(news.updateDate);
			return view;
		}

		private class ViewHolder {
			public ImageView img;
			public TextView tvTitle;
			public TextView tvTime;
		}

	}
	
	
	private class AwardAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listAward.size();
		}

		@Override
		public Object getItem(int position) {
			return listAward.get(position);
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
						R.layout.view_product_detail_right_frg_award_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view
						.findViewById(R.id.iv_product_detail_frag_award);

				viewHolder.tvTitle = (TextView) view
						.findViewById(R.id.tv_product_detail_frag_award);

				view.setTag(viewHolder);
				
			} else {
				
				viewHolder = (ViewHolder) view.getTag();
			}

			ProductDetailAward award = listAward.get(position);
			String imgPath = "http://bulo2bulo.com"+award.imageUrl+"_S.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.img);
			viewHolder.tvTitle.setText(award.title);
			return view;
		}

		private class ViewHolder {
			public ImageView img;
			public TextView tvTitle;
		}

	}
	
	

	private class CommonTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];
			String url = "/product/comments/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("id", id);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, args);
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
				list = JsonUtil.toObjectList(jsonData,
						ProductDetailCommon.class);
				ListViewAdapter adapter = new ListViewAdapter();
				lv.setAdapter(adapter);
				view.invalidate();
				super.onPostExecute(result);
			}
		}

	}
	
	
	private class NewsTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];
			String url = "/news/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("id", id);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url,HttpMethod.GET, args);
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
				listNews = JsonUtil.toObjectList(jsonData,
						ProductDetailNews.class);
				NewsAdapter adapter = new NewsAdapter();
				lv.setAdapter(adapter);
				view.invalidate();
				super.onPostExecute(result);
			}
		}

	}
	
	
	
	private class AwardTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];
			String url = "/honor/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("id", id);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url,HttpMethod.GET, args);
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
				listAward = JsonUtil.toObjectList(jsonData,
						ProductDetailAward.class);
				AwardAdapter adapter = new AwardAdapter();
				lv.setAdapter(adapter);
				view.invalidate();
				super.onPostExecute(result);
			}
		}

	}
	
	
	private class ProductCollectTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String url = "/user/favorite/add.do";
			
			SharedPreferences sp = getActivity().getSharedPreferences(SP_NAME, getActivity().MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("type", 1);
			args.put("productId", id);
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
                ToastUtil.showToast(getActivity(), "收藏成功!");
				
				super.onPostExecute(result);
			}
		}
	}
   
	
	
   
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.btn_user_comment:
			btnCommon.setBackgroundResource(R.drawable.usercomment_select);
			btnNews.setBackgroundResource(R.drawable.productnews_nomal);
			btnAward.setBackgroundResource(R.drawable.awardwinning_nomal);
			new CommonTask().execute(id);
			break;
		case R.id.btn_product_news:
			btnNews.setBackgroundResource(R.drawable.productnews_select);
			btnCommon.setBackgroundResource(R.drawable.usercomment_nomal);
			btnAward.setBackgroundResource(R.drawable.awardwinning_nomal);
			new NewsTask().execute(id);
			break;
		case R.id.btn_award_winning:
			btnAward.setBackgroundResource(R.drawable.awardwinning_select);
			btnCommon.setBackgroundResource(R.drawable.usercomment_nomal);
			btnNews.setBackgroundResource(R.drawable.productnews_nomal);
			new AwardTask().execute(id);
			break;
		case R.id.btn_shopping_cart:
			Intent intent = new Intent(getActivity(),ShoppingCartActivity.class);
			startActivity(intent);
			break;
			
		case R.id.btn_colloct:
			new ProductCollectTask().execute();
			break;
		}
		
	}

}
