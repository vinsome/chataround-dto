package com.service.chataround;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ChatAroundSettingActivity extends Activity {
	public static String TAG = ChatAroundSettingActivity.class.getName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsdialog);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_chat_around, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_settings:
			//settingsDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
}
