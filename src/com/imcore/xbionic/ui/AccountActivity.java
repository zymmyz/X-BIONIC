package com.imcore.xbionic.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.model.OrderCart;
import com.imcore.common.model.ProductColor;
import com.imcore.common.model.ProductDetail;
import com.imcore.common.model.ProductOrder;
import com.imcore.common.model.ProductSize;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class AccountActivity extends Activity implements OnClickListener{
    
	private float allPrice;
	private TextView tvOrder;
	private TextView tvWait;
	private Button btnGoShopping;
	private Button btnAccountOrder;
	private final static String SP_NAME = "userInfo";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		
		btnGoShopping = (Button)findViewById(R.id.btn_account_go_shopping);
		btnAccountOrder = (Button)findViewById(R.id.btn_account_order);
		btnGoShopping.setOnClickListener(this);
		btnAccountOrder.setOnClickListener(this);
		Intent intent = getIntent();
		allPrice = intent.getFloatExtra("allPrice", 100);
		tvOrder = (TextView)findViewById(R.id.tv_account_jine);
		tvWait = (TextView)findViewById(R.id.tv_account_wait_price_value);
		tvOrder.setText("订单金额 : "+allPrice+" 元");
		tvWait.setText(allPrice+"");
		
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.btn_account_go_shopping:
			Intent intent = new Intent(this,ProductActivity.class);
			startActivity(intent);
			break;
			
			
		case R.id.btn_account_order:
		    new OrderSummitTask().execute();
		break;
			
		}
		
	}
	
	
	private class OrderSummitTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			String url = "/order/commit.do";
			
			SharedPreferences sp = getSharedPreferences(SP_NAME,
					MODE_PRIVATE);
			String userId = sp.getString("userId", "");
			String token = sp.getString("token", "");
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("userId", userId);
			args.put("token", token);
			args.put("ProvinceId", 1);
			args.put("cityId", 1);
			args.put("Address", "厦门");
			args.put("zipCode",361100);
			args.put("Phone", 73611001);
			args.put("LinkMan", "testname");
			args.put("emailAccount", 46033008);
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
                ToastUtil.showToast(AccountActivity.this, "账户提交成功！");
				
				super.onPostExecute(result);
			}
		}

	}


}
