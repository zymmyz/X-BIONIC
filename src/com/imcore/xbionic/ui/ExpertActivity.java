package com.imcore.xbionic.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.example.x_bionic.R.id;
import com.example.x_bionic.R.layout;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.model.ExpertInfo;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;

public class ExpertActivity extends Activity {
    
	private final static String SP_NAME = "userInfo";
	private List<ExpertInfo> list;
	private ListView lv;
	private ProgressDialog progressDialog;
	private ActionBar mActionBar;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expert);
		
		lv = (ListView)findViewById(R.id.lv_expert);
		
		mActionBar = getActionBar();
		mActionBar.hide();

		
		new TestExpertTask().execute();
	}

	
	
	private class TestExpertTask extends AsyncTask<Integer, Void, String> {
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ExpertActivity.this,
					"loading...", "请稍后...", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {
              
			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			String url = "/testteam/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, args);
			String jsonResponse = null;
			try {
				// 得到请求的网络返回的结果
				jsonResponse = HttpHelper.execute(entity);
				Log.i("student", jsonResponse);
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
                list = JsonUtil.toObjectList(jsonData, ExpertInfo.class);
				ListViewAdapter adapter = new ListViewAdapter();
				lv.setAdapter(adapter);
                
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
				view = getLayoutInflater().inflate(
						R.layout.view_expert_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view
						.findViewById(R.id.iv_expert);
				
				viewHolder.tvTitle = (TextView)view.findViewById(R.id.tv_expert_title);
				viewHolder.tvDate = (TextView)view.findViewById(R.id.tv_expert_date);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			ExpertInfo expert = list.get(position);
			viewHolder.tvTitle.setText(expert.simpleDescrition);
			viewHolder.tvDate.setText(expert.updateDate);
			return view;
		}

	}

	private class ViewHolder {
		public ImageView img;
		public TextView tvTitle;
		public TextView tvDate;
	}
	
	
	
}
