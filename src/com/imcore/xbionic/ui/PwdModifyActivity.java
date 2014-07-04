package com.imcore.xbionic.ui;

import java.util.HashMap;
import java.util.Map;

import com.example.x_bionic.R;
import com.example.x_bionic.R.id;
import com.example.x_bionic.R.layout;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PwdModifyActivity extends Activity implements OnClickListener {

	private final static String SP_NAME = "userInfo";
	private Button btnOk;
	private Button btnCancel;
	private EditText etPhone;
	private EditText etOld;
	private EditText etNew;
	private String phone;
	private String oldPwd;
	private String newPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pwd_modify);

		btnOk = (Button) findViewById(R.id.btn_pwd_modify_ok);
		btnCancel = (Button) findViewById(R.id.btn_pwd_modify_cancel);
		etPhone = (EditText) findViewById(R.id.et_pwd_modify_phone);
		etOld = (EditText) findViewById(R.id.et_pwd_modify_old_pwd);
		etNew = (EditText) findViewById(R.id.et_pwd_modify_new_pwd);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		
	}

	private class UpdateTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String url = "/passport/password/change.do";
			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("phoneNumber", phone);
			args.put("oldPassword", oldPwd);
			args.put("newPassword", newPwd);

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
				ToastUtil.showToast(PwdModifyActivity.this, "密码修改成功!");
				finish();
				super.onPostExecute(result);
			} else {
				ToastUtil.showToast(PwdModifyActivity.this, "密码修改失败!");
			}
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_pwd_modify_ok:
			phone = etPhone.getText().toString();
			oldPwd = etOld.getText().toString();
			newPwd = etNew.getText().toString();
			if (phone == null || "".equals(phone)) {
				ToastUtil.showToast(PwdModifyActivity.this, "电话不能为空");
				return;
			}

			if (oldPwd == null || "".equals(oldPwd)) {
				ToastUtil.showToast(PwdModifyActivity.this, "旧密码不能为空");
				return;
			}

			if (newPwd == null || "".equals(newPwd)) {
				ToastUtil.showToast(PwdModifyActivity.this, "新密码不能为空");
				return;
			}

			new UpdateTask().execute();
			
			break;

		case R.id.btn_pwd_modify_cancel:
			finish();
			break;
		}
	}

}
