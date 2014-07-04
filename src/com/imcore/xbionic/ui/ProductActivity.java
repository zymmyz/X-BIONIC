package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.image.ImageFetcher;
import com.imcore.common.model.Category;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class ProductActivity extends SlidingFragmentActivity {
	private ExpandableListView eblv;
	private List<Category> groupList = new ArrayList<Category>();
	private List<List<Category>> childList = new ArrayList<List<Category>>();
	private Handler handler;
	private ImageFetcher imageFetcher;
	private Button imgbtn_top_left;
	private Button btnSearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product);
		imageFetcher = new ImageFetcher();
		groupList = new ArrayList<Category>();
		imgbtn_top_left = (Button) findViewById(R.id.btn_product_slide);
		eblv = (ExpandableListView) findViewById(R.id.eblv);
		eblv.setGroupIndicator(null);
		CategoryTask categoryTask = new CategoryTask();
		categoryTask.execute();
		
		
		btnSearch = (Button)findViewById(R.id.btn_product_search);
		
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(ProductActivity.this,SearchActivity.class);
				startActivity(intent);
				
			}
		});
		
		
		
		
		eblv.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub
				// View v = (View) eblv.getAdapter().getItem(groupPosition);
				// v.getTag();
				// ((BaseExpandableListAdapter)eblv.getAdapter()).notifyDataSetChanged();
			}
		});

		imgbtn_top_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				toggle();

			}
		});

		// 初始化滑动菜单
		initSlidingMenu(savedInstanceState);

	}

	/**
	 * 初始化滑动菜单
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {
		// 设置滑动菜单的视图
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new LeftFragment()).commit();
		// 实例化滑动菜单对象
		SlidingMenu sm = getSlidingMenu();
		// 设置滑动阴影的宽度
		sm.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动阴影的图像资源
		sm.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		sm.setFadeDegree(0.35f);
		// 设置触摸屏幕的模式
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	private class DetailTask extends AsyncTask<Integer, Void, String> {
		private int p;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int id = params[0];
			p = params[1];
			String url = "/category/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("navId", id);

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

			if (TextUtil.isEmptyString(result)) {
				return;
			}
			// 将json纯文本拿到ResponseJsonEntity.fromJSON(result)进行解析，然后放入其实体里
			ResponseJsonEntity resEntity = ResponseJsonEntity.fromJSON(result);

			if (resEntity.getStatus() == 200) {
				String jsonData = resEntity.getData();
				List<Category> list = JsonUtil.toObjectList(jsonData,
						Category.class);

				synchronized (childList) {
					childList.add(list);
				}
				Message msg = new Message();
				msg.what = p;
				handler.sendMessage(msg);

				super.onPostExecute(result);
			}
		}

	}

	private class CategoryTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			String url = "/category/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("navId", 0);

			// 将请求实体存到一个对象中
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, args);
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
				Log.i("student", jsonData);
				groupList = JsonUtil.toObjectList(jsonData, Category.class);

				for (int i = 0; i < groupList.size(); i++) {

					new DetailTask().execute(groupList.get(i).code, i);

					handler = new Handler() {
						public void handleMessage(Message msg) {
							if (msg.what == groupList.size() - 1) {
								ExpandAdapter adapter = new ExpandAdapter();
								eblv.setAdapter(adapter);
								for (List<Category> list : childList) {
									// ////////
									for (Category c : list) {
										Log.i("user", c.name);
									}
								}
								super.handleMessage(msg);
							}
						}
					};
				}
				super.onPostExecute(result);

			}

		}

	}

	private class ExpandAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childList.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = convertView;
			GroupViewHolder gvh = null;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.view_eblv_group_item, null);
				gvh = new GroupViewHolder();
				gvh.ivCategory = (ImageView) view.findViewById(R.id.iv_group);
				view.setTag(gvh);
			} else {
				gvh = (GroupViewHolder) view.getTag();
			}
			if (groupPosition == 0) {
				gvh.ivCategory.setBackgroundResource(R.drawable.upbackground);
			} else {
				gvh.ivCategory.setBackgroundResource(R.drawable.downbackground);
			}

			return view;
		}

		class GroupViewHolder {
			ImageView ivCategory;
		}
		
        //private int mGroupPosition;
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = convertView;
			ChildViewHolder gvh = null;
			//mGroupPosition = groupPosition;
			final int mGroupPosition = groupPosition;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.view_eblv_child_item, null);
				gvh = new ChildViewHolder();
				gvh.gvCategory = (GridListView) view
						.findViewById(R.id.gv_Gridview);
				view.setTag(gvh);
			} else {
				gvh = (ChildViewHolder) view.getTag();
			}

			GridAdapter gridAdapter = new GridAdapter(groupPosition);
			gvh.gvCategory.setAdapter(gridAdapter);
			setGridViewHeight(gvh.gvCategory, gridAdapter);
			gvh.gvCategory.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					int navId = groupList.get(mGroupPosition).code;
					int subNavId = childList.get(mGroupPosition).get(position).id;
					Intent intent = new Intent(ProductActivity.this,
							ProductListActivity.class);
					intent.putExtra("navId", navId);
					intent.putExtra("subNavId", subNavId);
					startActivity(intent);
				}
			});
			return view;
		}

		private class ChildViewHolder {
			GridListView gvCategory;
		}

		private class GridAdapter extends BaseAdapter {
			private int groupPosition;

			public GridAdapter(int groupPosition) {
				this.groupPosition = groupPosition;
			}

			@Override
			public int getCount() {
				return childList.get(groupPosition).size();
			}

			@Override
			public Object getItem(int position) {
				return childList.get(groupPosition).get(position);
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
					view = getLayoutInflater().inflate(R.layout.view_grid_item,
							null);
					viewHolder = new ViewHolder();
					viewHolder.imgCategory = (ImageView) view
							.findViewById(R.id.iv_category);
					viewHolder.tvCategory = (TextView) view
							.findViewById(R.id.tv_category);
					view.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) view.getTag();
				}

				Category category = childList.get(groupPosition).get(position);
				String imgPath = "http://bulo2bulo.com:8080/"
						+ category.imageUrl + "_L.png";
				/*
				 * ViewGroup.LayoutParams lp =
				 * viewHolder.imgCategory.getLayoutParams(); lp.height = 40;
				 * viewHolder.imgCategory.setLayoutParams(lp);
				 */
				viewHolder.imgCategory.setImageResource(R.drawable.hhhh);
				imageFetcher.fetch(imgPath, viewHolder.imgCategory);
				viewHolder.tvCategory.setText(category.name);
				System.out.println("fetch image");
				return view;
			}

		}

		public void setGridViewHeight(GridListView gv, GridAdapter gvAdapter) {
			int count = gv.getCount() % 4 == 0 ? gv.getCount() / 4 : (gv
					.getCount() / 4 + 1);
			int h = 10;
			System.out.println(gv.getCount());
			for (int i = 0; i < count; i++) {
				View view = gvAdapter.getView(i, null, gv);
				view.measure(View.MeasureSpec.UNSPECIFIED,
						View.MeasureSpec.UNSPECIFIED);
				h += view.getMeasuredHeight();
				System.out.println("h:" + h);
			}
			ViewGroup.LayoutParams params = gv.getLayoutParams();
			params.height = h;
			System.out.println("params h :" + params.height);
			gv.setLayoutParams(params);
		}

		private class ViewHolder {
			public ImageView imgCategory;
			public TextView tvCategory;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product, menu);
		return true;
	}

}
