package com.service.chataround;

import java.util.Calendar;

import org.springframework.util.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gcm.GCMRegistrar;
import com.google.common.eventbus.EventBus;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.chat.ChatMessageDto;
import com.service.chataround.fragment.ChatAroundListFragment;
import com.service.chataround.listener.MyLocationListener;
import com.service.chataround.task.ChatAroundTask;
import com.service.chataround.util.ChatUtils;
import com.service.chataround.util.PushUtils;

public class ChatAroundActivity extends Activity {
	public static String TAG = ChatAroundActivity.class.getName();
	private Dialog settingsDialog;

	private EventBus eventBus = new EventBus();
	private MyLocationListener locationListener;
	private String recipientId;
	private String fragmentPresent;
	private GoogleAnalyticsTracker tracker;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_around);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start("UA-36514546-1",10, this);
		
		Fragment frg = Fragment.instantiate(this,
				ChatAroundListFragment.class.getName());
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.frameLayoutId, frg);
		ft.addToBackStack(null);
		ft.commit();

		if (isOnline()) {
			registerToCloud();
		}
		final SharedPreferences settings = getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);

		String nick = settings.getString(ChatUtils.USER_NICKNAME, "");
		String mood = settings.getString(ChatUtils.USER_MOOD, "");
		String email = settings.getString(ChatUtils.USER_EMAIL, "");
		String passw = settings.getString(ChatUtils.USER_PASSW, "");

		if (!StringUtils.hasText(nick) || !StringUtils.hasText(email)
				|| !StringUtils.hasText(passw)) {
			goToSettingActivity();
		}

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				ChatUtils.DISPLAY_MESSAGE_ACTION));
	}

	@Override
	public void onResume() {
		super.onResume();
		tracker.trackPageView("/"+TAG);
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener(locationManager,
				getApplicationContext(), eventBus);
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
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_settings:
			goToSettingActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void goToSettingActivity(){
	    Intent intent = new Intent(this, ChatAroundSettingActivity.class);
	    startActivity(intent);
	}

	public void settingsDialogDeprecated() {


		settingsDialog = new Dialog(ChatAroundActivity.this);
		settingsDialog.setContentView(R.layout.settingsdialog);
		settingsDialog.setTitle(R.string.menu_settings);
		settingsDialog.setCancelable(true);




		/*
		if (selectedId == R.id.radioMaleId) {
			//RadioButton ra = (RadioButton)settingsDialog.findViewById(R.id.radioMaleId);ra.setSelected(true);
			
		} else if (selectedId == R.id.radioFemaleId) {
			//RadioButton ra = (RadioButton)settingsDialog.findViewById(R.id.radioFemaleId);ra.setSelected(true);
		} else {
			//RadioButton ra = (RadioButton)settingsDialog.findViewById(R.id.radioOtherId);ra.setSelected(true);
		}
		 */




		settingsDialog.show();
	}

	private void registerToCloud() {
		checkNotNull(ChatUtils.SERVER_URL, "SERVER_URL");
		checkNotNull(ChatUtils.SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, ChatUtils.SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				// No need to display anything cause it«s background thingy
				// mDisplay.append(getString(R.string.already_registered) +
				// "\n");
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				ChatAroundDto dto = new ChatAroundDto();
				dto.setDeviceId(regId);
				dto.setAppId(PushUtils.APP_ID);
				dto.setTime(Calendar.getInstance().getTime());

				new ChatAroundTask(context, null).execute(dto,
						ChatUtils.SERVER_URL + ChatUtils.REGISTER_URL);
			}
		}
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}

	public void finishTask(ChatAroundDto result) {
		if (result != null
				&& result.getResponse().equals(
						"push.server.operation.unregister.ok")) {
			GCMRegistrar.setRegisteredOnServer(getApplicationContext(), false);

		} else if (result != null
				&& result.getResponse().equals(
						"push.server.operation.register.ok")) {
			GCMRegistrar.setRegisteredOnServer(this, true);
			Log.d("HttpCall",
					"Call to google made!!!!! call2WebApp="
							+ result.getResponse() + "]");
		} else if (result != null
				&& result.getResponse().equals(
						"push.server.operation.register.ok")) {
			GCMRegistrar.setRegisteredOnServer(this, true);
			Log.d("HttpCall",
					"Call to google made!!!!! call2WebApp="
							+ result.getResponse() + "]");

		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public MyLocationListener getLocationListener() {
		return locationListener;
	}

	public void setLocationListener(MyLocationListener locationListener) {
		this.locationListener = locationListener;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	public String getFragmentPresent() {
		return fragmentPresent;
	}

	public void setFragmentPresent(String fragmentPresent) {
		this.fragmentPresent = fragmentPresent;
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ChatMessageDto dto = new ChatMessageDto();
			eventBus.post(dto);
		}

	};

	@Override
	protected void onDestroy() {
		tracker.stop();
		super.onDestroy();
	}
}
