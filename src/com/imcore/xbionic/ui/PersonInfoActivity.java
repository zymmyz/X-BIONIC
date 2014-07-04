package com.imcore.xbionic.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class PersonInfoActivity extends Activity implements OnClickListener{
   
	
	private final static String SP_NAME = "userInfo";
	private Button btnOk;
	private Button btnCancel;
	private EditText etUser;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etEmail;
	private EditText etDescript;
	private RadioGroup rg;
	private int sexId = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_info);
		
		btnOk = (Button) findViewById(R.id.btn_person_info_ok);
		btnCancel = (Button) findViewById(R.id.btn_person_info_cancel);
		etUser = (EditText)findViewById(R.id.et_person_info_user);
		etFirstName = (EditText)findViewById(R.id.et_person_info_first_name);
		etLastName = (EditText)findViewById(R.id.et_person_info_last_name);
		etEmail = (EditText)findViewById(R.id.et_person_info_email);
		etDescript = (EditText)findViewById(R.id.et_person_info_descript);
		rg = (RadioGroup)findViewById(R.id.rg_person_info);
		//rbMale = (RadioButton)findViewById(R.id.rb_person_info_sex_male);
		//rbFemale = (RadioButton)findViewById(R.id.rb_person_info_sex_female);
		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                 // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                 int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton)PersonInfoActivity.this.findViewById(radioButtonId);
                 //更新文本内容，以符合选中项
                String str = rb.getText().toString();
                
                if(str.equals("男")){
                	sexId = 1;
                }else{
                	sexId = 0;
                }
                
                
             }
         });

		
		
	}
	
	
	
	
	
	
	private class UpdateTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String url = "/user/update.do";
			SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");

			

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("displayName", etUser.getText().toString());
			args.put("FirstName", etFirstName.getText().toString());
			args.put("LastName", etLastName.getText().toString());
			args.put("email", etEmail.getText().toString());
			args.put("Gender", sexId);
			args.put("Desc", etDescript.getText().toString());

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
				ToastUtil.showToast(PersonInfoActivity.this, "个人资料更新成功!");
				finish();
				super.onPostExecute(result);
			}else{
				/////
			}
		}

	}






	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.btn_person_info_ok:
			String str = etUser.getText().toString();
			if(str==null||"".equals(str)){
				ToastUtil.showToast(PersonInfoActivity.this,"userName不能为空!");
				return;
			}
			new UpdateTask().execute();
			break;
			
		case R.id.btn_person_info_cancel:
		     finish();
		break;
		
		}
		
	}

	

}
