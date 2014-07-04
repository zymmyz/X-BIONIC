package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.x_bionic.R;
import com.example.x_bionic.R.layout;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.ProductList;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class XIntroduceActivity extends Activity {

	private ListView lv;
	private List<Integer> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xintroduce);
		
		lv = (ListView) findViewById(R.id.lv_x_introduce);
		list = new ArrayList<Integer>();
		list.add(R.drawable.histroy);
		list.add(R.drawable.awareimg);
		list.add(R.drawable.bionicprototype);
		list.add(R.drawable.productionbase);
		list.add(R.drawable.designanddevelopemt);
		ListViewAdapter adapter = new ListViewAdapter();
		lv.setAdapter(adapter);
		
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
						R.layout.view_x_introduce_list_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view
						.findViewById(R.id.iv_x_introduce);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.img.setBackgroundResource(list.get(position));
			return view;
		}

	}

	private class ViewHolder {
		public ImageView img;
	}
	
	


}
