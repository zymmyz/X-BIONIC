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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.EventInfo;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;

public class EventActivity extends Activity {

	private final static String SP_NAME = "userInfo";
	private ListView lv;
	private List<EventInfo> listEvent;
	private ProgressDialog progressDialog;
	private ActionBar mActionBar;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);

		mActionBar = getActionBar();
		mActionBar.hide();
		lv = (ListView) findViewById(R.id.lv_event_main);
		new DetailTask().execute();
	}

	private class DetailTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(EventActivity.this,
					"loading...", "请稍后...", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			String url = "/search/keyword.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("type", 2);
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

				lv.setVisibility(View.VISIBLE);
				listEvent = JsonUtil.toObjectList(jsonData, EventInfo.class);
				ListAdapter adapter = new ListAdapter();
				lv.setAdapter(adapter);
			}

			super.onPostExecute(result);
		}
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
