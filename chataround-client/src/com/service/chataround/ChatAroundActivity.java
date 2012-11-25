package com.service.chataround;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gcm.GCMRegistrar;
import com.google.common.eventbus.EventBus;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.chat.ChatMessageDto;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.fragment.ChatAroundListFragment;
import com.service.chataround.listener.MyLocationListener;
import com.service.chataround.util.ChatUtils;

public class ChatAroundActivity extends Activity {
	public static String TAG = ChatAroundActivity.class.getName();

	private EventBus eventBus = new EventBus();
	private MyLocationListener locationListener;
	private String recipientId;
	private String fragmentPresent;
	private GoogleAnalyticsTracker tracker;
	private List<UserPublicDto> cacheList = new ArrayList<UserPublicDto>(0);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_around);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start("UA-36514546-1", 10, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		tracker.trackPageView("/" + TAG);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				ChatUtils.DISPLAY_MESSAGE_ACTION));

		Fragment frg = Fragment.instantiate(this,
				ChatAroundListFragment.class.getName());
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.frameLayoutId, frg);
		ft.addToBackStack(null);
		ft.commit();

		final SharedPreferences settings = getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);

		String nick = settings.getString(ChatUtils.USER_NICKNAME, "");
		boolean registeredOnline = settings.getBoolean(
				ChatUtils.USER_REGISTERED_ONLINE, false);
		String userId = settings.getString(ChatUtils.USER_ID, "");
		String email = settings.getString(ChatUtils.USER_EMAIL, "");
		String passw = settings.getString(ChatUtils.USER_PASSW, "");

		if ("".equals(nick) || "".equals(email) || "".equals(passw)) {
			goToSettingActivity();
		}
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId != null && !"".equals(regId) && userId != null
				&& !"".equals(userId)) {
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (locationListener == null) {
				locationListener = new MyLocationListener();
				locationListener.setLocationManager(locationManager);
				locationListener.setCtx(this);
			}
			locationListener.setRegisteredOnline(registeredOnline);
			locationListener.setUserId(userId);
			locationListener.setPaused(false);
			locationListener.setEventBus(eventBus);
			locationListener.start();
		}
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

	public void goToSettingActivity() {
		Intent intent = new Intent(this, ChatAroundSettingActivity.class);
		startActivity(intent);
	}

	@Deprecated
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

	@Override
	protected void onDestroy() {
		tracker.stop();
		super.onDestroy();
	}

	public List<UserPublicDto> getCacheList() {
		return cacheList;
	}

	public void setCacheList(List<UserPublicDto> cacheList) {
		this.cacheList = cacheList;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		final SharedPreferences settings = getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);
		String userId = settings.getString(ChatUtils.USER_ID, "");
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId != null && !"".equals(regId) && userId != null
				&& !"".equals(userId)) {
			locationListener.setPaused(true);
		}
	}

	@Override
	public void onBackPressed() {
		/*
		 * new AlertDialog.Builder(this)
		 * .setIcon(android.R.drawable.ic_dialog_alert) .setTitle("Deixar")
		 * .setMessage( "Tem certeza de que quer deixar o aplicativo?")
		 * .setPositiveButton("Confirmar", new DialogInterface.OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * Stop the activity
		 * 
		 * final String regId = GCMRegistrar
		 * .getRegistrationId(getApplicationContext()); PushDto dto = new
		 * PushDto(); SharedPreferences settings =
		 * getSharedPreferences(PREFS_NAME, 0); nick =
		 * settings.getString(USER_NICKNAME, ""); dto.setNick(nick);
		 * dto.setOperation(Operation.UNREGISTER); dto.setRegId(regId);
		 * dto.setMine(true); dto.setAppId("EVANGELIO_APP");
		 * 
		 * Map<String, String> params = new HashMap<String, String>( 0);
		 * params.put("languageId", "BR"); dto.setParams(params);
		 * 
		 * new EvangelioTask(MessageActivity.this) .execute(dto, SERVER_URL +
		 * "/mymUnRegisterMessage.do");
		 * 
		 * GCMRegistrar.setRegisteredOnServer( getApplicationContext(), false);
		 * tracker.trackEvent("MessageActivity", "backPressed", "", 10);
		 * finish();
		 * 
		 * }
		 * 
		 * }).setNegativeButton("Cancelar", null).show();
		 */

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ChatMessageDto dto = new ChatMessageDto();
			eventBus.post(dto);
		}

	};
}
