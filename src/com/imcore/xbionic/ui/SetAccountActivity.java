package com.imcore.xbionic.ui;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.example.x_bionic.R;
import com.example.x_bionic.R.id;
import com.example.x_bionic.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SetAccountActivity extends Activity implements OnItemClickListener{

	private ListView lv;
	private List<String> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_account);
		//URLEncoder.encode(keyword)
		lv = (ListView) findViewById(R.id.lv_set_account);
		list = new ArrayList<String>();
		
		list = new ArrayList<String>();
		list.add("我的信息查询");
		list.add("修改密码");
		list.add("个人资料更新");
		list.add("个人地址更新");
		list.add("查看我的地址");
		list.add("我的积分查询");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.view_set_account_list_item, R.id.tv_set_account, list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		switch (position) {
		case 0:
			break;
			
		case 1:
			Intent intent1 = new Intent(this,PwdModifyActivity.class);
			startActivity(intent1);
			break;
		case 2:
			Intent intent2 = new Intent(this,PersonInfoActivity.class);
			startActivity(intent2);
			break;
			
		case 3:
			Intent intent3 = new Intent(this,AddressModifyActivity.class);
			startActivity(intent3);
			break;
			
		}

	}


}
