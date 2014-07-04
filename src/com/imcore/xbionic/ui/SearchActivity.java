package com.imcore.xbionic.ui;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.EventInfo;
import com.imcore.common.model.ProductList;
import com.imcore.common.util.IatSettings;
import com.imcore.common.util.JsonParser;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class SearchActivity extends Activity {

	private final static String SP_NAME = "userInfo";

	private List<ProductList> list;
	private List<EventInfo> listEvent;

	private GridView gv;
	private ListView lv;

	private Button btnSearch;
	private EditText etKey;
	private ProgressDialog progressDialog;
	private ActionBar mActionBar;
	private Spinner sp;
	
	private Button btnSpeech;
	private RecognizerDialog iatDialog;
	private SharedPreferences mSharedPreferences;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mActionBar = getActionBar();
		mActionBar.hide();

		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		
		sp = (Spinner) findViewById(R.id.sp_search);
		String[] items = new String[] { "所有", "产品", "活动" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.preference_category, items);
		sp.setAdapter(adapter);

		gv = (GridView) findViewById(R.id.gv_search);
		lv = (ListView) findViewById(R.id.lv_search);
		
		btnSpeech = (Button)findViewById(R.id.btn_search_info);
		iatDialog = new RecognizerDialog(this,mInitListener);
		
		etKey = (EditText) findViewById(R.id.et_search);
		btnSearch = (Button) findViewById(R.id.btn_search);
		
		btnSpeech.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
				etKey.setText("");
				iatDialog.setListener(recognizerDialogListener);
				iatDialog.show();
				SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(
						SearchActivity.this, null);
				mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));
				mIat.setParameter(SpeechConstant.DOMAIN, "iat");
				mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
				mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

				mIat.startListening(recognizerListener);
				
			}
		});
		
		
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String keyword = etKey.getText().toString();

				if (keyword == null || "".equals(keyword)) {
					ToastUtil.showToast(SearchActivity.this, "请输入要搜索的关键字");
					return;
				}

				String type = String.valueOf(sp.getSelectedItemPosition());
				new DetailTask().execute(keyword, type);

			}
		});

	}
	
	
	
	/**
	 * ��д��������
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {

		}

		@Override
		public void onError(SpeechError error) {

		}

		@Override
		public void onEndOfSpeech() {

		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, String msg) {

		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.i("user", results.getResultString());
			
			String text = JsonParser.parseIatResult(results.getResultString());
			etKey.append(text);
			etKey.setSelection(etKey.length());
			if(isLast) {
				//Toast.makeText(SearchActivity.this,"complete",Toast.LENGTH_SHORT).show();
			}
			
		}

		@Override
		public void onVolumeChanged(int volume) {

		}

	};
	
	
	
	
	
	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d("user", "SpeechRecognizer init() code = " + code);
			if (code == ErrorCode.SUCCESS) {
				btnSpeech.setEnabled(true);
			}
		}
	};
	
	
	
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			etKey.append(text);
			etKey.setSelection(etKey.length());
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			//showTip(error.getPlainDescription(true));
		}

	};
	
	
	

	private class DetailTask extends AsyncTask<String, Void, String> {

		private int type;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SearchActivity.this,
					"loading...", "请稍后...", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String strkey = params[0];
			String strType = params[1];
			String keyword = URLEncoder.encode(strkey);
			type = Integer.parseInt(strType);
			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			String url = "/search/keyword.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("keyword", keyword);
			args.put("type", type);
			args.put("userId", "userId");
			args.put("token", token);

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

				if (type == 1) {
                    lv.setVisibility(View.GONE);
                    gv.setVisibility(View.VISIBLE);
					list = JsonUtil.toObjectList(jsonData, ProductList.class);
					GridAdapter adapter = new GridAdapter();
					gv.setAdapter(adapter);
					gv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							Intent intent = new Intent(SearchActivity.this,
									ProductDetailActivity.class);
							int productId = list.get(position).id;
							ToastUtil.showToast(SearchActivity.this, "id="
									+ productId);
							intent.putExtra("id", productId);
							startActivity(intent);
						}
					});

				}else if(type == 2){
					lv.setVisibility(View.VISIBLE);
					gv.setVisibility(View.GONE);
					listEvent = JsonUtil.toObjectList(jsonData, EventInfo.class);
					ListAdapter adapter = new ListAdapter();
					lv.setAdapter(adapter);
				}
						
						

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
				view = getLayoutInflater().inflate(
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

	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listEvent.size();
		}

		@Override
		public Object getItem(int position) {
			return listEvent.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolderList viewHolderList = null;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.view_search_list_item, null);
				viewHolderList = new ViewHolderList();

				viewHolderList.tvTitle = (TextView) view
						.findViewById(R.id.tv_search_event_title);
				viewHolderList.tvState = (TextView) view
						.findViewById(R.id.tv_search_event_state);
				viewHolderList.tvDate = (TextView) view
						.findViewById(R.id.tv_search_event_date);
				viewHolderList.imgPic = (ImageView) view
						.findViewById(R.id.iv_search_event_pic);

				view.setTag(viewHolderList);
			} else {
				viewHolderList = (ViewHolderList) view.getTag();
			}

			EventInfo event = listEvent.get(position);
			String imgPath = "http://www.bulo2bulo.com" + event.titleImageUrl
					+ ".jpg";
			new ImageFetcher().fetch(imgPath, viewHolderList.imgPic);

			viewHolderList.tvTitle.setText(event.title);
			viewHolderList.tvDate
					.setText(event.beginTime + "-" + event.endTime);

			return view;
		}

	}

	private class ViewHolderList {
		public TextView tvTitle;
		public TextView tvState;
		public TextView tvDate;
		public ImageView imgPic;
	}

}
