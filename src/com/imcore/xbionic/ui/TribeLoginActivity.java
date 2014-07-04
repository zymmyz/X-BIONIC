package com.imcore.xbionic.ui;

import java.util.HashMap;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.util.ConnectivityUtil;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class TribeLoginActivity extends Activity {
	private EditText etUser;
	private EditText etPwd;
	private Button btnLogin;
	private ProgressDialog pd;
	private ActionBar mActionBar;
	private final static String SP_NAME = "userInfo";

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tribe_login);
		mActionBar = getActionBar();
		mActionBar.hide();
		
		initialWidget();
		btnLogin.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				doLogin();
			}
		});
	}

	private void initialWidget() {
		etUser = (EditText) findViewById(R.id.et_tribe_user);
		etPwd = (EditText) findViewById(R.id.et_tribe_pwd);
		btnLogin = (Button) findViewById(R.id.btn_enter_tribe);
	}

	private void doLogin() {
		if (!ConnectivityUtil.isOnline(this)) {
			return;
		}
		String userName = etUser.getText().toString();
		String password = etPwd.getText().toString();
		new LoginTask(userName, password).execute();
	}

	private class LoginTask extends AsyncTask<Void, Void, String> {
		private String mUserName;
		private String mPassword;

		public LoginTask(String userName, String password) {
			mUserName = userName;
			mPassword = password;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(TribeLoginActivity.this, "登录中", "请稍候......");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			String url = "/passport/login.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("phoneNumber", mUserName);
			args.put("password", mPassword);
			args.put("client", "android");
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
			pd.dismiss();
			if (TextUtil.isEmptyString(result)) {
				return;
			}
			// 将json纯文本拿到ResponseJsonEntity.fromJSON(result)进行解析，然后放入其实体里
			ResponseJsonEntity resEntity = ResponseJsonEntity.fromJSON(result);

			if (resEntity.getStatus() == 200) {
				String jsonData = resEntity.getData();
				//Log.i("student", jsonData);
				String userId = JsonUtil.getJsonValueByKey(jsonData, "id");
				String token = JsonUtil.getJsonValueByKey(jsonData, "token");
				SharedPreferences sp = getSharedPreferences(SP_NAME,
						MODE_PRIVATE);

				sp.edit().putString("userId", userId)
						.putString("token", token).commit();
				Intent intent = new Intent(TribeLoginActivity.this,
						MainActivity.class);
				ToastUtil.showToast(TribeLoginActivity.this, "userId="+userId);
				startActivity(intent);
				super.onPostExecute(result);
			} else {
				ToastUtil.showToast(TribeLoginActivity.this, resEntity.getMessage());
			}
		}

	}

}
