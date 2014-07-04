package com.imcore.xbionic.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.ProductColor;
import com.imcore.common.model.ProductDetail;
import com.imcore.common.model.ProductImg;
import com.imcore.common.model.ProductSize;
import com.imcore.common.model.ProductSizeDetail;
import com.imcore.common.model.ProductStore;
import com.imcore.common.model.ProductTech;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;
import com.imcore.xbionic.ui.CustomScrollView.OnLoadListener;
import com.imcore.xbionic.ui.CustomScrollView.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

@SuppressLint("NewApi")
public class ProductDetailActivity extends SlidingFragmentActivity implements
		OnClickListener {
	private final static String SP_NAME = "userInfo";
	private List<ProductImg> list;
	private List<ProductColor> listColor;
	private List<ProductSize> listSize;
	private List<ProductSizeDetail> listSizeDetail;
	private List<ProductTech> listTech;
	private Button btnMore;
	private Button btnBuy;
	private Bundle mBundle = new Bundle();
	private ProductDetailRightFragment frg;
	private CustomScrollView scrollView = null;
	private Gallery gl;
	private int productId = -1;
	private int colorId = -1;
	private int sizeId = -1;
	private boolean loadFlag = true;
	private String colorName;
	private String sizeName;
	private String count = null;
	private String productQuantityId = null;
	private String qty = null;

	// //////
	private String productName;
	private float productPrice;
	private EditText etCount;
	private Button btnCommont;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);

		btnCommont = (Button) findViewById(R.id.btn_product_detail_commont);
		etCount = (EditText) findViewById(R.id.et_product_detail_edit_store);
		scrollView = (CustomScrollView) findViewById(R.id.custom_scroll_view);
		btnMore = (Button) findViewById(R.id.btn_product_detail_more);
		btnBuy = (Button) findViewById(R.id.btn_product_detail_add_shop_cart);
		btnMore.setOnClickListener(this);
		btnBuy.setOnClickListener(this);
		btnCommont.setOnClickListener(this);
		Intent intent = getIntent();
		productId = intent.getIntExtra("id", 267);
		// ///////
		initSlidingMenu(savedInstanceState);
		gl = (Gallery) findViewById(R.id.gl_product_detail);

		etCount.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				count = etCount.getText().toString();
				if (count != null & colorId != -1 & sizeId != -1) {
					mBundle.putString("count", count);
					frg = new ProductDetailRightFragment();
					frg.setArguments(mBundle);
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.menu_frame, frg).commit();
				}

			}
		});

		new ImageTask().execute(productId);
		new DetailTask().execute(productId);
		new SizeTask().execute(productId);
		new TechnologyTask().execute(productId);

		scrollView.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {

						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						scrollView.onRefreshComplete();
					}

				}.execute();
			}
		});

		scrollView.setOnLoadListener(new OnLoadListener() {
			public void onLoad() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {

						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}

					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@SuppressLint({ "ResourceAsColor", "NewApi" })
					@Override
					protected void onPostExecute(Void result) {

						if (loadFlag) {
							LinearLayout linearSize = (LinearLayout) findViewById(R.id.layout_linear_size_name);
							linearSize
									.setBackgroundResource(R.drawable.titleandsizebackground);
							TextView tvSize = new TextView(
									ProductDetailActivity.this);
							LinearLayout.LayoutParams tvlp = new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							tvlp.leftMargin = 10;
							tvlp.gravity = Gravity.CENTER_VERTICAL;
							tvSize.setText("产品尺码");
							tvSize.setTextColor(Color.WHITE);
							tvSize.setTextSize(20);
							tvSize.setLayoutParams(tvlp);
							linearSize.addView(tvSize);
							// Bitmap bm =
							// BitmapFactory.decodeResource(getResources(),
							// R.drawable.line);
							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inSampleSize = 10;
							Bitmap b = BitmapFactory.decodeResource(
									getResources(), R.drawable.line, opts);

							TableLayout table = (TableLayout) findViewById(R.id.tbl_size);
							table.setStretchAllColumns(true);
							table.setDividerDrawable(new BitmapDrawable(b));
							table.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE
									| LinearLayout.SHOW_DIVIDER_BEGINNING
									| LinearLayout.SHOW_DIVIDER_END);
							TableLayout.LayoutParams tableLparams = new TableLayout.LayoutParams(
									TableLayout.LayoutParams.WRAP_CONTENT,
									TableLayout.LayoutParams.WRAP_CONTENT);
							// tableLparams.setMargins(0, 0, 0, 0);
							// int inttemp = 0;
							for (int i = 0; i < listSizeDetail.size(); i++) {
								TableRow tablerow = new TableRow(
										ProductDetailActivity.this);
								tablerow.setDividerDrawable(new BitmapDrawable(
										b));
								tablerow.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING
										| LinearLayout.SHOW_DIVIDER_END
										| LinearLayout.SHOW_DIVIDER_MIDDLE);
								ProductSizeDetail product = listSizeDetail
										.get(i);

								tablerow.addView(addImageView(
										ProductDetailActivity.this,
										product.size));
								tablerow.addView(addImageView(
										ProductDetailActivity.this, product.p1));
								tablerow.addView(addImageView(
										ProductDetailActivity.this, product.p2));
								tablerow.addView(addImageView(
										ProductDetailActivity.this, product.p4));
								tablerow.addView(addImageView(
										ProductDetailActivity.this, product.p5));
								tablerow.addView(addImageView(
										ProductDetailActivity.this, product.p6));

								table.addView(tablerow, tableLparams);
							}

							LinearLayout linearTech = (LinearLayout) findViewById(R.id.layout_linear_tech);
							linearTech
									.setBackgroundResource(R.drawable.titleandsizebackground);
							TextView tvTech = new TextView(
									ProductDetailActivity.this);
							LinearLayout.LayoutParams tvTechLp = new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							tvTechLp.leftMargin = 10;
							tvTechLp.gravity = Gravity.CENTER_VERTICAL;
							tvTech.setText("科技点");
							tvTech.setTextColor(Color.WHITE);
							tvTech.setTextSize(20);
							tvTech.setLayoutParams(tvTechLp);
							linearTech.addView(tvTech);

							ListView listView = (ListView) findViewById(R.id.lv_product_tech);
							TechAdapter techAdapter = new TechAdapter();
							listView.setAdapter(techAdapter);
							setListViewHeight(listView, techAdapter);
						}

						// scrollView.addView(view);
						scrollView.invalidate();
						loadFlag = false;
						scrollView.onLoadComplete();
					}

				}.execute();
			}
		});

	}

	// //////////////////////////////
	@Override
	protected void onResume() {
		if (mBundle != null) {
			frg = new ProductDetailRightFragment();

			frg.setArguments(mBundle);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, frg).commit();
		}

		super.onResume();
	}

	public void setListViewHeight(ListView lv, TechAdapter techAdapter) {
		int count = lv.getCount();
		int h = 10;
		System.out.println(lv.getCount());
		for (int i = 0; i < count; i++) {
			View view = techAdapter.getView(i, null, lv);
			view.measure(View.MeasureSpec.UNSPECIFIED,
					View.MeasureSpec.UNSPECIFIED);
			h += view.getMeasuredHeight();
			System.out.println("h:" + h);
		}
		ViewGroup.LayoutParams params = lv.getLayoutParams();
		params.height = h;
		System.out.println("params h :" + params.height);
		lv.setLayoutParams(params);
	}

	private class TechAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listTech.size();
		}

		@Override
		public Object getItem(int position) {
			return listTech.get(position);
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
						R.layout.view_product_detail_tech_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view.findViewById(R.id.iv_tech);

				viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_tech);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			String imgPath = "http://www.bulo2bulo.com"
					+ listTech.get(position).imageUrl + "_S.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.img);
			viewHolder.tvTitle.setText(listTech.get(position).title);
			return view;

		}

		private class ViewHolder {
			public ImageView img;
			public TextView tvTitle;
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private TextView addImageView(Context context, String text) {
		TextView textView = new TextView(ProductDetailActivity.this);
		LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// ivLp.setMargins(1, 1, 1, 1);
		// textView.setLeft(10);
		// textView.setTop(10);
		// textView.setRight(10);
		// textView.setBottom(10);
		// textView.setLayoutParams(new android.widget.TableRow.LayoutParams(16,
		// 50));
		textView.setText(text);// 赋值
		textView.setSingleLine();
		textView.setGravity(Gravity.CENTER);// 居中
		textView.setTextSize(15);
		textView.setTextColor(Color.WHITE);
		textView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		textView.setBackgroundResource(R.color.black);
		// textView.setLayoutParams(ivLp);
		return textView;
	}

	private int mColor = 0;
	private int mSize = 0;

	public Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mColor == 0) {
				mColor = msg.getData().getInt("color");
			}
			if (mSize == 0) {
				mSize = msg.getData().getInt("size");
			}
			if (mColor == 1 && mSize == 1) {
				new StoreTask().execute(productId, colorId, sizeId);
				mBundle.putString("colorName", colorName);
				mBundle.putInt("productId", productId);
				mBundle.putString("sizeName", sizeName);
				// ///////////////////////
				mBundle.putString("productName", productName);
				mBundle.putFloat("productPrice", productPrice);
				frg = new ProductDetailRightFragment();

				frg.setArguments(mBundle);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.menu_frame, frg).commit();
			}
			super.handleMessage(msg);
		}
	};

	private class ImageTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];
			String url = "/product/images/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("id", id);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, args);
			String jsonResponse = null;
			try {
				// 得到请求的网络返回的结果
				jsonResponse = HttpHelper.execute(entity);
				Log.i("productDetail", jsonResponse);
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
				list = JsonUtil.toObjectList(jsonData, ProductImg.class);

				final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.product_detail_viewGroup);
				LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				for (int i = 0; i < list.size(); i++) {
					ImageView iv = new ImageView(ProductDetailActivity.this);
					// iv.setImageResource(R.drawable.no);
					LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					// ivLp.gravity = Gravity.CENTER;
					ivLp.leftMargin = 20;
					iv.setLayoutParams(ivLp);
					// iv.setScaleType(ScaleType.CENTER);// 居中显示
					/*
					 * LayoutParams lp = (LayoutParams) linearLayout
					 * .getLayoutParams(); LinearLayout.LayoutParams params =
					 * new LinearLayout.LayoutParams(
					 * LinearLayout.LayoutParams.MATCH_PARENT,
					 * LinearLayout.LayoutParams.WRAP_CONTENT); lp.gravity =
					 * Gravity.CENTER; linearLayout.setLayoutParams(params);
					 * linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
					 */
					linearLayout.addView(iv);
				}

				GalleryAdapter adapter = new GalleryAdapter();
				gl.setAdapter(adapter);
				gl.setSpacing(100);

				gl.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						for (int i = 0; i < linearLayout.getChildCount(); i++) {
							if (position == i) {
								linearLayout.getChildAt(i)
										.setBackgroundResource(R.drawable.yes);
							} else {
								linearLayout.getChildAt(i)
										.setBackgroundResource(R.drawable.no);
							}
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
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
				ProductDetail productDetail = JsonUtil.toObject(jsonData,
						ProductDetail.class);
				// /////////////
				productName = productDetail.name;
				productPrice = productDetail.price;
				String jsonColor = JsonUtil.getJsonValueByKey(jsonData,
						"sysColorList");
				listColor = JsonUtil
						.toObjectList(jsonColor, ProductColor.class);

				TextView tvName = (TextView) findViewById(R.id.tv_product_detail_name);
				TextView tvPrice = (TextView) findViewById(R.id.tv_product_detail_price);
				tvName.setText(productDetail.name);
				tvPrice.setText("￥" + productDetail.price);
				final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_linear_color);
				boolean flag = true;
				for (ProductColor pc : listColor) {
					ImageView iv = new ImageView(ProductDetailActivity.this);
					LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(
							50, 50);
					ivLp.gravity = Gravity.CENTER_VERTICAL;
					ivLp.leftMargin = 40;
					ivLp.topMargin = 3;
					String imgPath = "http://www.bulo2bulo.com" + pc.colorImage
							+ ".jpg";
					iv.setScaleType(ScaleType.CENTER_CROP);
					iv.setLayoutParams(ivLp);
					new ImageFetcher().fetch(imgPath, iv);
					if (flag) {
						iv.setBackgroundResource(R.drawable.imageview_background);
						colorId = pc.id;
						// ////////
						colorName = pc.color;
						Message msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putInt("color", 1);
						msg.setData(bundle);
						h.sendMessage(msg);
					}
					linearLayout.addView(iv);
					flag = false;
				}

				for (int i = 1; i < linearLayout.getChildCount(); i++) {
					final ImageView iv = (ImageView) linearLayout.getChildAt(i);
					final int m = i;
					iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {

							colorId = listColor.get(m - 1).id;
							// ///////
							if (colorId != -1 && sizeId != -1) {
								colorName = listColor.get(m - 1).color;
								mBundle.putString("colorName",
										listColor.get(m - 1).color);
								mBundle.putInt("productId", productId);
								frg = new ProductDetailRightFragment();

								frg.setArguments(mBundle);
								getSupportFragmentManager().beginTransaction()
										.replace(R.id.menu_frame, frg).commit();
							}
							for (int j = 1; j < linearLayout.getChildCount(); j++) {
								if (iv == linearLayout.getChildAt(j)) {
									iv.setBackgroundResource(R.drawable.imageview_background);
								} else {
									linearLayout
											.getChildAt(j)
											.setBackgroundResource(
													R.drawable.imageview_background_normal);
								}
							}
							if (productId != -1 && colorId != -1
									&& sizeId != -1) {
								new StoreTask().execute(productId, colorId,
										sizeId);
							}
						}
					});
				}

				super.onPostExecute(result);
			}
		}

	}

	private class SizeTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];
			String url = "/product/size/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("id", id);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, args);
			String jsonResponse = null;
			try {
				// 得到请求的网络返回的结果
				jsonResponse = HttpHelper.execute(entity);
				Log.i("productDetail", jsonResponse);
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

				String jsonSize = JsonUtil.getJsonValueByKey(jsonData,
						"sysSizeList");
				listSize = JsonUtil.toObjectList(jsonSize, ProductSize.class);

				String jsonSizeDetail = JsonUtil.getJsonValueByKey(jsonData,
						"sizeStandardDetailList");
				listSizeDetail = JsonUtil.toObjectList(jsonSizeDetail,
						ProductSizeDetail.class);

				boolean flag = true;
				final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_linear_size);
				for (ProductSize ps : listSize) {
					Button btn = new Button(ProductDetailActivity.this);
					LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					btnLp.gravity = Gravity.CENTER_VERTICAL;
					btnLp.leftMargin = 25;
					btnLp.topMargin = 8;
					// iv.setScaleType(ScaleType.CENTER_CROP);
					btn.setLayoutParams(btnLp);
					btn.setText(ps.size);
					if (flag) {
						btn.setBackgroundResource(R.drawable.sizeselectbuttondown);
						sizeId = ps.id;
						sizeName = ps.size;
						Message msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putInt("size", 1);
						msg.setData(bundle);
						h.sendMessage(msg);

					} else {
						btn.setBackgroundResource(R.drawable.sizeselectbuttonup);
					}

					linearLayout.addView(btn);
					flag = false;
				}

				for (int i = 1; i < linearLayout.getChildCount(); i++) {
					final Button btn = (Button) linearLayout.getChildAt(i);
					final int m = i;
					btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							sizeId = listSize.get(m - 1).id;
							// ////////
							if (colorId != -1 && sizeId != -1) {
								sizeName = listSize.get(m - 1).size;
								mBundle.putString("sizeName",
										listSize.get(m - 1).size);
								mBundle.putInt("productId", productId);
								frg = new ProductDetailRightFragment();
								frg.setArguments(mBundle);
								getSupportFragmentManager().beginTransaction()
										.replace(R.id.menu_frame, frg).commit();
							}
							for (int j = 1; j < linearLayout.getChildCount(); j++) {
								if (btn == linearLayout.getChildAt(j)) {
									btn.setBackgroundResource(R.drawable.sizeselectbuttondown);
								} else {
									linearLayout
											.getChildAt(j)
											.setBackgroundResource(
													R.drawable.sizeselectbuttonup);
								}
							}

							if (productId != -1 && colorId != -1
									&& sizeId != -1) {
								new StoreTask().execute(productId, colorId,
										sizeId);
							}

						}
					});
				}

				super.onPostExecute(result);
			}
		}

	}

	private class StoreTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];
			int colorId = params[1];
			int sizeId = params[2];
			String url = "/product/quantity/get.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("id", id);
			args.put("colorId", colorId);
			args.put("sizeId", sizeId);
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
				ProductStore productStore = JsonUtil.toObject(jsonData,
						ProductStore.class);

				productQuantityId = String.valueOf(productStore.id);
				qty = String.valueOf(productStore.qty);

				TextView tvStore = (TextView) findViewById(R.id.tv_product_detail_store_value);
				tvStore.setText("(库存" + productStore.qty + "件)");
				super.onPostExecute(result);
			}
		}

	}

	private class TechnologyTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];
			String url = "/product/labs/list.do";
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
				listTech = JsonUtil.toObjectList(jsonData, ProductTech.class);
				super.onPostExecute(result);
			}
		}

	}

	private class AddtoShopcartTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String userId = params[0];
			String token = params[1];
			String productQuantityId = params[2];
			String qty = params[3];
			// int productQuantityId = Integer.parseInt(params[2]);
			// int qty = Integer.parseInt(params[3]);

			String url = "/shoppingcart/update.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("productQuantityId", Integer.parseInt(productQuantityId));
			args.put("qty", Integer.parseInt(qty));

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
				ToastUtil
						.showToast(ProductDetailActivity.this, "您已成功将商品添加到购物车");
				super.onPostExecute(result);
			}
		}

	}

	private class GalleryAdapter extends BaseAdapter {

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
						R.layout.view_product_detail_gallery_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view
						.findViewById(R.id.iv_product_detail);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			String imgPath = "http://www.bulo2bulo.com"
					+ list.get(position).image + "_L.jpg";
			new ImageFetcher().fetch(imgPath, viewHolder.img);
			return view;
		}

		private class ViewHolder {
			public ImageView img;
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_product_detail_more:
			toggle();
			break;

		case R.id.btn_product_detail_commont:
			Intent intent = new Intent(this, ProductCommonActivity.class);
			intent.putExtra("productId", productId);
			startActivity(intent);
			break;

		case R.id.btn_product_detail_add_shop_cart:

			int q = Integer.parseInt(qty);
			String str = etCount.getText().toString();
			if (str == null || "".equals(str)) {
				ToastUtil
						.showToast(ProductDetailActivity.this, "对不起,购买数量不能为空！");
				return;
			}

			int m = Integer.parseInt(str);
			if (m < 0 || m > q) {
				ToastUtil.showToast(ProductDetailActivity.this, "对不起，库存不足");
				return;
			}

			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			new AddtoShopcartTask().execute(userId, token, productQuantityId,
					str);
			break;
		}

	}

	/**
	 * 初始化滑动菜单
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {
		// 设置滑动菜单的视图
		setBehindContentView(R.layout.menu_frame);
		// frg = new ProductDetailRightFragment();
		// Bundle bundle = new Bundle();
		// bundle.putInt("productId", productId);
		// frg.setArguments(mBundle);
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.menu_frame, frg).commit();
		// 实例化滑动菜单对象
		SlidingMenu sm = getSlidingMenu();
		// 设置滑动阴影的宽度
		sm.setShadowWidthRes(R.dimen.shadow_width_product_detail);
		// 设置滑动阴影的图像资源
		sm.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		sm.setFadeDegree(0.35f);
		// 设置触摸屏幕的模式
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		sm.setMode(SlidingMenu.RIGHT);
	}

}
