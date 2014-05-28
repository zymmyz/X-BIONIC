package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.x_bionic.R;
import com.example.x_bionic.R.layout;
import com.example.x_bionic.R.menu;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class MainActivity extends ActionBarActivity{

	private ViewPager viewpager = null;
	private List<View> list = null;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewpager = (ViewPager) findViewById(R.id.viewpager_main);
		//getActionBar().setDisplayShowHomeEnabled(false); 
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayOptions(R.drawable.listbutton);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		list = new ArrayList<View>();
		list.add(getLayoutInflater().inflate(R.layout.view_event, null));
		list.add(getLayoutInflater().inflate(R.layout.view_introduce, null));
		list.add(getLayoutInflater().inflate(R.layout.view_shopping, null));
		list.add(getLayoutInflater().inflate(R.layout.view_story, null));

		viewpager.setAdapter(new ViewPagerAdapter(list));
		viewpager.setOnPageChangeListener(new ViewPagerChangeListener());
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
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
