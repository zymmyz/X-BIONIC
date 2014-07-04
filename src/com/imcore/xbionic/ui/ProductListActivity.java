package com.imcore.xbionic.ui;

import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.model.CategoryList;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;
import com.imcore.common.util.ToastUtil;

public class ProductListActivity extends ActionBarActivity {
	private List<CategoryList> list;
	private ViewPager mVp;
	private Spinner sp;
	private Spinner spOrder;
	private int navId;
	private int subNavId;
	private int mPosition = 0;
	private FragmentAdapter frgmentAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_list);
		Intent intent = getIntent();
		navId = intent.getIntExtra("navId", 100001);
		subNavId = intent.getIntExtra("subNavId", 4);
		// ToastUtil.showToast(this, "navId="+navId+" "+"subNavId="+subNavId);
		list = new ArrayList<CategoryList>();
		mVp = (ViewPager) findViewById(R.id.vp);
		// 使自定义的普通View能在title栏显示, actionBar.setCustomView能起作用.
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		// 显示虚拟按键
		// displayVirtualMenuKey();
		initialSpinner();
		new DetailTask().execute(navId, subNavId);
	}

	private class DetailTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			int navId = params[0];
			int subNavId = params[1];

			String url = "/category/list.do";
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("navId", navId);
			args.put("subNavId", subNavId);

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

			if (TextUtil.isEmptyString(result)) {
				return;
			}
			// 将json纯文本拿到ResponseJsonEntity.fromJSON(result)进行解析，然后放入其实体里
			ResponseJsonEntity resEntity = ResponseJsonEntity.fromJSON(result);

			if (resEntity.getStatus() == 200) {
				String jsonData = resEntity.getData();
				list = JsonUtil.toObjectList(jsonData, CategoryList.class);
				ActionBar actionBar = getSupportActionBar();
				mVp.setPageMargin(3);

				frgmentAdapter = new FragmentAdapter();
				mVp.setAdapter(frgmentAdapter);
				mVp.setOnPageChangeListener(mViewPagerListener);
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				for (CategoryList c : list) {
					Tab tab = actionBar.newTab();
					tab.setText(c.categoryName);
					tab.setTabListener(mTabListener);
					actionBar.addTab(tab);
				}

				super.onPostExecute(result);
			}
		}

	}

	private OnPageChangeListener mViewPagerListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			getSupportActionBar().setSelectedNavigationItem(position);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int position) {

		}
	};

	private TabListener mTabListener = new TabListener() {

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			int position = tab.getPosition();
			mVp.setCurrentItem(position);
		}

		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

		}
	};
	private ProductListFragment productListFragment;

	private class FragmentAdapter extends FragmentStatePagerAdapter {

		public FragmentAdapter() {
			super(getSupportFragmentManager());
		}

		@Override
		public Fragment getItem(int arg0) {
			productListFragment = new ProductListFragment();
			Bundle args = new Bundle();
			args.putInt("navId", navId);
			args.putInt("subNavId", subNavId);
			args.putInt("desc", mPosition);
			productListFragment.setArguments(args);

			return productListFragment;

		}

		@Override
		public int getCount() {
			return list.size();
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product, menu);
		return true;
	}

	public void initialSpinner() {
		View actionbarLayout = LayoutInflater.from(this).inflate(
				R.layout.view_spinner, null);
		sp = (Spinner) actionbarLayout.findViewById(R.id.sp);
		String[] mItems = getResources().getStringArray(R.array.navi_category);
		ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this,
				R.layout.support_simple_spinner_dropdown_item, mItems);
		sp.setAdapter(spAdapter);

		// 事件监听
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//String str = parent.getItemAtPosition(position).toString();
				//ToastUtil.showToast(ProductListActivity.this, "你点击的是:" + str);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spOrder = (Spinner) actionbarLayout.findViewById(R.id.sp_order);
		mItems = getResources().getStringArray(R.array.navi_order);
		spAdapter = new ArrayAdapter<String>(this,
				R.layout.support_simple_spinner_dropdown_item, mItems);
		spOrder.setAdapter(spAdapter);

		// 事件监听
		spOrder.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mPosition = position;

				productListFragment = new ProductListFragment();
				Bundle args = new Bundle();
				args.putInt("navId", navId);
				args.putInt("subNavId", subNavId);
				args.putInt("desc", mPosition);
				productListFragment.setArguments(args);
				if (frgmentAdapter != null) {
					frgmentAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		// 在Bar上显示定制view
		getSupportActionBar().setCustomView(actionbarLayout);
	}

}
