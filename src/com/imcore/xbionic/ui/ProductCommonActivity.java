package com.imcore.xbionic.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.model.ProductCommon;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class ProductCommonActivity extends Activity implements OnClickListener {

	private Button btnOk;
	private Button btnCancel;
	private EditText etTitle;
	private EditText etCommont;
	private EditText etStrar;

	private int id;

	private final static String SP_NAME = "userInfo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_common);

		Intent intent = getIntent();
		id = intent.getIntExtra("productId", 1);

		btnOk = (Button) findViewById(R.id.btn_product_common_ok);
		btnCancel = (Button) findViewById(R.id.btn_product_common_cancel);
		etTitle = (EditText) findViewById(R.id.et_product_common_title);
		etCommont = (EditText) findViewById(R.id.et_product_common_suggest);
		etStrar = (EditText) findViewById(R.id.et_product_common_star);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_top_out);
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.anim_top_in,
					R.anim.anim_bottom_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class CommontTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String url = "/product/comments/add.do";
			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");

			String title = params[0];
			String comment = params[1];
			String star = params[2];

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("id", id);
			args.put("title", title);
			args.put("comment", comment);
			args.put("star", Integer.parseInt(star));

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
				ToastUtil.showToast(ProductCommonActivity.this, "评论成功!");
				finish();
				overridePendingTransition(R.anim.anim_top_in,
						R.anim.anim_bottom_out);

				super.onPostExecute(result);
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_product_common_ok:
			String title = etTitle.getText().toString();
			String comment = etCommont.getText().toString();
			String star = etStrar.getText().toString();

			if (title == null || "".equals(title)) {
				ToastUtil.showToast(this, "标题不能为空!");
				return;
			}

			if (comment == null || "".equals(comment)) {
				ToastUtil.showToast(this, "评论不能为空!");
				return;
			}

			if (star == null || "".equals(star)) {
				ToastUtil.showToast(this, "星不能为空!");
				return;
			}

			new CommontTask().execute(title, comment, star);

			break;

		case R.id.btn_product_common_cancel:
			finish();
			overridePendingTransition(R.anim.anim_top_in,
					R.anim.anim_bottom_out);

			break;
		}

	}

}
