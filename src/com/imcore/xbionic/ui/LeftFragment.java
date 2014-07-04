package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.x_bionic.R;

public class LeftFragment extends Fragment implements OnItemClickListener {
	private ListView lv;
	private List<String> list;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.view_left_frg, null);
		lv = (ListView) mView.findViewById(R.id.lv_tribe_point);
		list = new ArrayList<String>();
		list.add("您的订购");
		list.add("账户设置");
		list.add("达人申请");
		list.add("部落社区");
		list.add("购物车");
		list.add("订阅信息");
		list.add("微博分享");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.view_left_list_item, R.id.tv_product_item, list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);

		return mView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			Intent intent = new Intent(getActivity(), YouOrderActivity.class);
			startActivity(intent);
			break;
			
		case 1:
			Intent intent1 = new Intent(getActivity(),SetAccountActivity.class);
			startActivity(intent1);
			break;
		case 4:
			Intent intent2 = new Intent(getActivity(),ShoppingCartActivity.class);
			startActivity(intent2);
			break;
			
		case 6:
			Intent intent3 = new Intent(getActivity(),ShareActivity.class);
			startActivity(intent3);
			break;
			
		}

	}

}