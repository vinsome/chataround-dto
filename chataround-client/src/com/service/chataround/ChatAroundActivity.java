package com.service.chataround;

import org.springframework.util.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.service.chataround.fragment.ChatAroundListFragment;
import com.service.chataround.listener.MyLocationListener;
import com.service.chataround.util.Constants;

public class ChatAroundActivity extends Activity {
	private Dialog dialog;
	private EditText text;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_around);
        
        Fragment frg = Fragment.instantiate(this, ChatAroundListFragment.class.getName());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frameLayoutId, frg);
        ft.addToBackStack(null);
        ft.commit();
        
    }
    
    @Override
    public void onResume(){
    	super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener(locationManager,getApplicationContext());
        locationListener.start();    	
    	
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
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_settings:
    			settingsDialog();
    			return true;            	
        }
        return super.onOptionsItemSelected(item);
    }
    
	private void settingsDialog() {
		final SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		
		dialog = new Dialog(ChatAroundActivity.this);
		dialog.setContentView(R.layout.settingsdialog);
		dialog.setTitle(R.string.menu_settings);
		dialog.setCancelable(true);

		text = (EditText) dialog.findViewById(R.id.nicknameTextView);
		String nick = settings.getString(Constants.USER_NICKNAME, "");
		text.setText(nick);
		
		Switch switchButton = (Switch) dialog.findViewById(R.id.switchNotifId);
		Boolean isNotifications = settings.getBoolean(Constants.USER_NOTIFICATIONS, true);
		switchButton.setChecked(isNotifications);
		switchButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Switch notif = (Switch) v;
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(Constants.USER_NOTIFICATIONS, notif.isChecked());
				editor.commit();
			}
		});

		Switch switchButtonSound = (Switch) dialog
				.findViewById(R.id.switchNotifSoundId);
		Boolean isSound = settings.getBoolean(Constants.USER_STAY_ONLINE, true);
		switchButtonSound.setChecked(isSound);
		switchButtonSound.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Switch sound = (Switch) v;
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(Constants.USER_STAY_ONLINE, sound.isChecked());
				editor.commit();
			}
		});

		Button button = (Button) dialog.findViewById(R.id.saveSettings);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (StringUtils.hasText(text.getText().toString().trim())) {
					String nickname = text.getText().toString().trim();
					// We need an Editor object to make preference changes.
					// All objects are from android.context.Context
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(Constants.USER_NICKNAME, nickname);
					editor.commit();
					dialog.hide();
				} 
			}
		});
		
		Button closeButton = (Button) dialog.findViewById(R.id.closeSettings);
		closeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.hide();
			}
		});

		dialog.show();
	} 
    
}
