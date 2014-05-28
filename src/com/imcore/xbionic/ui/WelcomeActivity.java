package com.imcore.xbionic.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.example.x_bionic.R;

public class WelcomeActivity extends Activity {

	private ProgressBar bar;

	public void init() {
		bar = ((ProgressBar) findViewById(R.id.progressbarid));
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (bar.getProgress() >= 100) {
						Intent intent = new Intent(WelcomeActivity.this,
								FlashActivity.class);
						WelcomeActivity.this.startActivity(intent);
						finish();
						return;
					}
					try {
						Thread.sleep(400L);
						bar.incrementProgressBy(10);
					} catch (InterruptedException localInterruptedException) {
						while (true)
							localInterruptedException.printStackTrace();
					}
				}
			}
		}).start();
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_welcome);
		init();
	}

}
