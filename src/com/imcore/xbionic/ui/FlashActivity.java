package com.imcore.xbionic.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.x_bionic.R;

public class FlashActivity extends Activity {
	private ViewPager viewpager = null;
	private List<View> list = null;
	private ImageView[] img = null;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			viewpager.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash);
		viewpager = (ViewPager) findViewById(R.id.viewpager);

		list = new ArrayList<View>();
		list.add(getLayoutInflater().inflate(R.layout.tab1, null));
		list.add(getLayoutInflater().inflate(R.layout.tab2, null));
		list.add(getLayoutInflater().inflate(R.layout.tab3, null));

		img = new ImageView[list.size()];

		LinearLayout layout = (LinearLayout) findViewById(R.id.viewGroup);
		for (int i = 0; i < list.size(); i++) {
			img[i] = (ImageView) layout.getChildAt(i);
			if (i == 0) {
				img[i].setBackgroundResource(R.drawable.yes);
			} else {
				img[i].setBackgroundResource(R.drawable.no);
			}
		}
		viewpager.setAdapter(new ViewPagerAdapter(list));
		viewpager.setOnPageChangeListener(new ViewPagerChangeListener());
		String args = getIntent().getStringExtra("help");
		if ("".equals(args)||args==null) {
			new Thread() {
				public void run() {
					for (int i = 0; i < list.size(); i++) {
						Message msg = new Message();
						msg.what = i;
						handler.sendMessage(msg);
						try {
							if (i == list.size() - 1) {
								try {
									Thread.sleep(1500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								Intent intent = new Intent(FlashActivity.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			}.start();
		}

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
			// 更新图标
			for (int i = 0; i < list.size(); i++) {
				if (page == i) {
					img[i].setBackgroundResource(R.drawable.yes);
				} else {
					img[i].setBackgroundResource(R.drawable.no);
				}
			}
		}

	}

}
