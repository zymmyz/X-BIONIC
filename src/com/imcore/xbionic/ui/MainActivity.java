package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.x_bionic.R;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private ViewPager viewpager = null;
	private List<View> list = null;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] mNaviItemText;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	// 侧拉式列表
	private ListView mDrawerList;

	private final static String NAVI_ITEM_TEXT = "text";
	private final static String NAVI_ITEM_ICON = "icon";

	private ImageView ivEvent;
	private ImageView ivIntroduce;
	private ImageView ivShoping;
	private ImageView ivStory;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewpager = (ViewPager) findViewById(R.id.viewpager_main);
		getActionBar().setIcon(R.drawable.ic_transpant);
		// getActionBar().setDisplayUseLogoEnabled(false);
		// getActionBar().setLogo(null);
		// getActionBar().setTitle("X");
		// 设置actionbar的图标和文字消失
		// getActionBar().setDisplayShowHomeEnabled(false);
		// getActionBar().setDisplayShowTitleEnabled(false);
		// getActionBar().setDisplayOptions(R.drawable.listbutton);
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		View viewEvent = getLayoutInflater().inflate(R.layout.view_event, null);
		View viewIntroduce = getLayoutInflater().inflate(
				R.layout.view_introduce, null);
		View viewShopping = getLayoutInflater().inflate(R.layout.view_shopping,
				null);
		View viewStory = getLayoutInflater().inflate(R.layout.view_story, null);

		ivEvent = (ImageView) viewEvent.findViewById(R.id.iv_event);
		ivIntroduce = (ImageView) viewIntroduce.findViewById(R.id.iv_indroduce);
		ivShoping = (ImageView) viewShopping.findViewById(R.id.iv_shopping);
		ivStory = (ImageView) viewStory.findViewById(R.id.iv_story);

		ivEvent.setOnClickListener(this);
		ivIntroduce.setOnClickListener(this);
		ivShoping.setOnClickListener(this);
		ivStory.setOnClickListener(this);

		list = new ArrayList<View>();
		list.add(viewEvent);
		list.add(viewIntroduce);
		list.add(viewShopping);
		list.add(viewStory);

		viewpager.setAdapter(new ViewPagerAdapter(list));
		viewpager.setOnPageChangeListener(new ViewPagerChangeListener());

		initDrawerLayout();
		selectNaviItem(0);

	}

	private void initDrawerLayout() {
		mNaviItemText = getResources().getStringArray(R.array.navi_items);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerTitle = getResources().getString(R.string.app_name);

		Map<String, Object> item1 = new HashMap<String, Object>();
		item1.put(NAVI_ITEM_TEXT, mNaviItemText[0]);
		item1.put(NAVI_ITEM_ICON, R.drawable.ic_xactivtypage);

		Map<String, Object> item2 = new HashMap<String, Object>();
		item2.put(NAVI_ITEM_TEXT, mNaviItemText[1]);
		item2.put(NAVI_ITEM_ICON, R.drawable.ic_xactivtypage2);

		Map<String, Object> item3 = new HashMap<String, Object>();
		item3.put(NAVI_ITEM_TEXT, mNaviItemText[2]);
		item3.put(NAVI_ITEM_ICON, R.drawable.ic_xactivtypage3);

		Map<String, Object> item4 = new HashMap<String, Object>();
		item4.put(NAVI_ITEM_TEXT, mNaviItemText[3]);
		item4.put(NAVI_ITEM_ICON, R.drawable.ic_xactivtypage4);

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(item1);
		data.add(item2);
		data.add(item3);
		data.add(item4);

		String[] from = new String[] { NAVI_ITEM_TEXT, NAVI_ITEM_ICON };
		int[] to = new int[] { R.id.tv_navi_item_text, R.id.iv_navi_item_icon };
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new SimpleAdapter(this, data,
				R.layout.view_navi_drawer_item, from, to));
		mDrawerList
				.setOnItemClickListener(new NaviDrawerListItemOnClickListner());
		initialDrawerListener();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	private void initialDrawerListener() {
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.listbutton, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				getSupportActionBar().setTitle(mTitle);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		//
		return super.onOptionsItemSelected(item);
	}

	private class NaviDrawerListItemOnClickListner implements
			OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectNaviItem(position);
		}
	}

	private void selectNaviItem(int position) {

		switch (position) {
		case 0:
			viewpager.setCurrentItem(0);
			break;
		case 1:
			viewpager.setCurrentItem(1);
			break;
		case 2:
			viewpager.setCurrentItem(2);
			break;
		case 3:
			viewpager.setCurrentItem(3);
			break;
		}

		mDrawerList.setItemChecked(position, true);
		setTitle(mNaviItemText[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private class ViewPagerAdapter extends PagerAdapter {

		private List<View> list;

		public ViewPagerAdapter(List<View> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position));
			return list.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}
	}

	private class ViewPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int page) {
			selectNaviItem(page);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_event:
			Intent intent1 = new Intent(this, EventActivity.class);
			startActivity(intent1);
			break;
			
		case R.id.iv_indroduce:
			Intent intent2 = new Intent(this, XIntroduceActivity.class);
			startActivity(intent2);
			break;
			
		case R.id.iv_shopping:
			Intent intent3 = new Intent(this, ProductActivity.class);
			startActivity(intent3);
			break;
			
		case R.id.iv_story:
			Intent intent4 = new Intent(this, ExpertActivity.class);
			startActivity(intent4);
			break;
		}
		
		

	}

}
