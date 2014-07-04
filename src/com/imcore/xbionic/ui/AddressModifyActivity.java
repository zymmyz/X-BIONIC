package com.imcore.xbionic.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.ListCityWheelAdapter;
import kankan.wheel.widget.adapters.ListZoneWheelAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.x_bionic.R;
import com.imcore.common.http.HttpHelper;
import com.imcore.common.http.HttpMethod;
import com.imcore.common.http.RequestEntity;
import com.imcore.common.http.ResponseJsonEntity;
import com.imcore.common.model.City;
import com.imcore.common.model.Province;
import com.imcore.common.model.Zone;
import com.imcore.common.util.JsonUtil;
import com.imcore.common.util.TextUtil;

public class AddressModifyActivity extends Activity {

	private Button btnDialog;

	private Button btnMap;

	private EditText etPcz;
	private Spinner sp;

	private List<Province> listProvince;
	private Map<Integer, List<City>> mapCity = new HashMap<Integer, List<City>>();
	private Map<Integer, List<Zone>> mapZone = new HashMap<Integer, List<Zone>>();

	private ActionBar mActionBar;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_modify);
		mActionBar = getActionBar();
		mActionBar.hide();
		new ProvinceTask().execute();

		etPcz = (EditText) findViewById(R.id.et_address_modify_province);
		sp = (Spinner)findViewById(R.id.sp_address_modify);
		
		String[] items = new String[] { "男","女" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.preference_category, items);
		sp.setAdapter(adapter);
		

		btnMap = (Button) findViewById(R.id.btn_map);
		btnMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AddressModifyActivity.this,
						MapActivity.class);
				startActivity(intent);
			}
		});
	}

	private int count = 0;
	private int count1 = 0;
	private int count2 = 0;
	public Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				count++;
				if (count == listProvince.size()) {
					Log.i("view2", "listProvince size=" + listProvince.size());

					for (Integer key : mapCity.keySet()) {

						List<City> lc = mapCity.get(key);
						for (City c : lc) {
							count2++;
							new ZoneTask().execute(c.id);
						}

					}

				}
			}

			if (msg.what == 2) {
				count1++;
				Log.i("view1", "count1=" + count1);
				Log.i("view1", "count2=" + count2);
				if (count1 == count2) {
					
					
					etPcz.setOnFocusChangeListener(new OnFocusChangeListener() {
						
						Button btnSure;
						Dialog dialog;
						
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if(hasFocus){
								
								View view = getLayoutInflater().inflate(
										R.layout.view_address_modify_dialog, null);

								btnSure = (Button) view
										.findViewById(R.id.btn_address_modify_sure);
								btnSure.setTag(view);

								final WheelView wlProvince = (WheelView) view
										.findViewById(R.id.wl_province);
								final WheelView wlCity = (WheelView) view
										.findViewById(R.id.wl_city);
								final WheelView wlZone = (WheelView) view
										.findViewById(R.id.wl_zone);
								
								btnSure.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										
										int provinceId = listProvince.get(wlProvince.getCurrentItem()).id;
										String strProvince = listProvince.get(wlProvince.getCurrentItem()).province;
										String strCity = mapCity.get(provinceId).get(wlCity.getCurrentItem()).city;
										int cityId = mapCity.get(provinceId).get(wlCity.getCurrentItem()).id;
										String strZone = mapZone.get(cityId).get(wlZone.getCurrentItem()).area;
										etPcz.setText(strProvince+strCity+strZone);
										
										dialog.dismiss();
										
									}
								});

								OnWheelChangedListener listener = new OnWheelChangedListener() {
									public void onChanged(WheelView wheel,
											int oldValue, int newValue) {
									}
								};

								ArrayWheelAdapter<String> provinceAdapter = new ArrayWheelAdapter<String>(
										AddressModifyActivity.this, listProvince);
								wlProvince.setViewAdapter(provinceAdapter);
								wlProvince
										.addChangingListener(new OnWheelChangedListener() {
											@Override
											public void onChanged(WheelView wheel,
													int oldValue, int newValue) {
												wlCity.setViewAdapter(new ListCityWheelAdapter(
														getApplicationContext(),
														mapCity.get(listProvince
																.get(newValue).id)));
												wlCity.setTag(newValue);

												wlCity.setCurrentItem(0);

												int proId = listProvince
														.get(newValue).id;
												int cityId = mapCity.get(proId)
														.get(0).id;

												wlZone.setViewAdapter(new ListZoneWheelAdapter(
														getApplicationContext(),
														mapZone.get(cityId)));

												wlZone.setCurrentItem(0);
											}
										});

								wlCity.addChangingListener(new OnWheelChangedListener() {

									@Override
									public void onChanged(WheelView wheel,
											int oldValue, int newValue) {

										int value = (Integer) wlCity.getTag();
										int proId = listProvince.get(value).id;
										int cityId = mapCity.get(proId).get(
												newValue).id;

										wlZone.setViewAdapter(new ListZoneWheelAdapter(
												getApplicationContext(), mapZone
														.get(cityId)));

									}
								});

								wlCity.setViewAdapter(new ListCityWheelAdapter(
										getApplicationContext(), mapCity
												.get(listProvince.get(0).id)));

								int proId = listProvince.get(0).id;
								int cityId = ((List<City>) mapCity.get(proId))
										.get(0).id;

								wlZone.setViewAdapter(new ListZoneWheelAdapter(
										getApplicationContext(),
										(List<Zone>) mapZone.get(cityId)));

								// ///////////////////////////////
								dialog = new Dialog(
										AddressModifyActivity.this,
										R.style.transparentFrameWindowStyle);
								dialog.setContentView(view, new LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.WRAP_CONTENT));
								Window window = dialog.getWindow();
								// 设置显示动画
								window.setWindowAnimations(R.style.main_menu_animstyle);
								WindowManager.LayoutParams wl = window
										.getAttributes();
								wl.x = LayoutParams.MATCH_PARENT;
								wl.y = getWindowManager().getDefaultDisplay()
										.getHeight();
								// 设置显示位置
								dialog.onWindowAttributesChanged(wl);
								// 设置点击外围解散
								dialog.setCanceledOnTouchOutside(true);
								dialog.show();
							}
						}
						
						
					});


				}
			}

			super.handleMessage(msg);
		}
	};

	private class ProvinceTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			String url = "/province/list.do";
			Map<String, Object> args = new HashMap<String, Object>();

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
				listProvince = JsonUtil.toObjectList(jsonData, Province.class);

				for (Province p : listProvince) {
					new CityTask().execute(p.id);
				}

				super.onPostExecute(result);
			}
		}

	}

	private class CityTask extends AsyncTask<Integer, Void, String> {

		private int provinecId;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			provinecId = params[0];
			String url = "/city/list.do";
			Map<String, Object> args = new HashMap<String, Object>();

			args.put("provinceId", provinecId);

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

				List<City> lCity = JsonUtil.toObjectList(jsonData, City.class);
				mapCity.put(provinecId, lCity);
				// listCity.add(map);

				Message msg = new Message();
				msg.what = 1;
				h.sendMessage(msg);

				super.onPostExecute(result);
			}
		}

	}

	private class ZoneTask extends AsyncTask<Integer, Void, String> {

		private int cityId;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {

			cityId = params[0];
			String url = "/area/list.do";
			Map<String, Object> args = new HashMap<String, Object>();

			args.put("cityId", cityId);

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
				List<Zone> lZone = JsonUtil.toObjectList(jsonData, Zone.class);
				mapZone.put(cityId, lZone);
				// listZone.add(map);

				Message msg = new Message();
				msg.what = 2;
				h.sendMessage(msg);

				super.onPostExecute(result);
			}
		}

	}

}
