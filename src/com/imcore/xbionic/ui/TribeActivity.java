

package com.imcore.xbionic.ui;

import com.example.x_bionic.R;
import com.example.x_bionic.R.layout;
import com.example.x_bionic.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TribeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tribe);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tribe, menu);
		return true;
	}

}
