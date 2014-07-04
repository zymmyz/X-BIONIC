package com.imcore.xbionic.ui;

import com.baidu.mapapi.SDKInitializer;
import com.example.x_bionic.R;
import com.example.x_bionic.R.string;
import com.iflytek.cloud.SpeechUtility;

import android.app.Application;

public class SpeechApp extends Application{

	@Override
	public void onCreate() {
		SpeechUtility.createUtility(SpeechApp.this, "appid="+getString(R.string.app_id));
		// 在使�?SDK 各组间之前初始化 context 信息，传�?ApplicationContext
				SDKInitializer.initialize(this);
		super.onCreate();
	}
	
}
