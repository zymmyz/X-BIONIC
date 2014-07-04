package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
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
import android.util.Log;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.model.OrderCart;
import com.imcore.common.model.ProductColor;
import com.imcore.common.model.ProductDetail;
import com.imcore.common.model.ProductOrder;
import com.imcore.common.model.ProductSize;
import com.imcore.common.model.ShopCart;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;

public class YouOrderActivity extends ActionBarActivity {

	private ViewPager mVp;
	private FragmentAdapter frgmentAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_you_order);

		mVp = (ViewPager) findViewById(R.id.vp_you_order);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		ActionBar actionBar = getSupportActionBar();
		mVp.setPageMargin(3);

		frgmentAdapter = new FragmentAdapter();
		mVp.setAdapter(frgmentAdapter);
		mVp.setOnPageChangeListener(mViewPagerListener);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tab = actionBar.newTab();
		tab.setText("产品");
		tab.setTabListener(mTabListener);
		actionBar.addTab(tab);

		tab = actionBar.newTab();
		tab.setText("收藏");
		tab.setTabListener(mTabListener);
		actionBar.addTab(tab);

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

	private class FragmentAdapter extends FragmentStatePagerAdapter {

		public FragmentAdapter() {
			super(getSupportFragmentManager());
		}

		@Override
		public Fragment getItem(int arg0) {

			ProductOrderFragment productFragment = new ProductOrderFragment();
			Bundle args = new Bundle();
			if (arg0 == 0) {
				args.putInt("item", 0);
			}
			
			if (arg0 == 1) {
				args.putInt("item", 1);
			}
			productFragment.setArguments(args);
			return productFragment;

		}

		@Override
		public int getCount() {
			return 2;
		}

	}

}
