package com.service.chataround;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gcm.GCMRegistrar;
import com.google.common.eventbus.EventBus;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.service.chataround.async.ChatAroundAsyncHtpp;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.chat.ChatMessageDto;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.fragment.ChatAroundListFragment;
import com.service.chataround.fragment.ChatFragment;
import com.service.chataround.listener.MyLocationListener;
import com.service.chataround.util.ChatUtils;
import com.service.chataround.util.PushUtils;

@SuppressLint("ParserError")
public class ChatAroundActivity extends Activity {
	public static String TAG = ChatAroundActivity.class.getName();

	private EventBus eventBus = new EventBus();
	private MyLocationListener locationListener;
	private String recipientId;
	private String nickNameRecipientId;
	private String fragmentPresent;
	private GoogleAnalyticsTracker tracker;
	private List<UserPublicDto> cacheList = new ArrayList<UserPublicDto>(0);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_around);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-36514546-1", 10, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		tracker.trackPageView("/" + TAG);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				ChatUtils.DISPLAY_MESSAGE_ACTION));

		doNavigateToFragment();

		final SharedPreferences settings = getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);

		String nick = settings.getString(ChatUtils.USER_NICKNAME, "");
		boolean registeredOnline = settings.getBoolean(
				ChatUtils.USER_REGISTERED_ONLINE, false);
		String userId = settings.getString(ChatUtils.USER_ID, "");
		String email = settings.getString(ChatUtils.USER_EMAIL, "");
		String passw = settings.getString(ChatUtils.USER_PASSW, "");
		//by default user wants to be online
		boolean userOnline = settings.getBoolean(
				ChatUtils.USER_STAY_ONLINE, true);
		
		if ("".equals(nick) || "".equals(email) || "".equals(passw)) {
			goToSettingActivity();
		}

		String regId = GCMRegistrar.getRegistrationId(this);

		if (regId != null && !"".equals(regId) && userId != null
				&& !"".equals(userId) && userOnline) {
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
		
		final ImageView imageArray = (ImageView) findViewById(R.id.chat_background);
		imageArray.setAdjustViewBounds(true);
		doRotation();
	}
	private void doRotation(){
        final int rotationRight = 360;
        final int rotationLeft = -20;
        final RotateAnimation rAnim;
        int degree;
            degree = rotationRight;
        
        final ImageView image = (ImageView) findViewById(R.id.chat_background);
        rAnim = new RotateAnimation(0f, degree, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rAnim.setStartOffset(0);
        rAnim.setDuration(2000);
        rAnim.setRepeatMode(Animation.INFINITE);
        rAnim.setRepeatCount(Animation.INFINITE);
        rAnim.setFillAfter(true);
        rAnim.setFillEnabled(true);
        image.startAnimation(rAnim);
    }
	private void doNavigateToFragment() {
		Intent intent = getIntent();
		// when reciving notification, comes here ...is the one that sends us
		// messages!
		String senderUserId = intent
				.getStringExtra(ChatUtils.NOTIFICATION_SENDER_USER_ID);
		if (senderUserId == null || "".equals(senderUserId)) {

			Fragment frg = Fragment.instantiate(this,
					ChatAroundListFragment.class.getName());
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.frameLayoutId, frg);
			ft.addToBackStack(null);
			ft.commit();

		} else {
			// means we are talking to someone and we clicked the notification
			// in mobile.
			setRecipientId(senderUserId);
			Fragment anotherFragment = Fragment.instantiate(this,
					ChatFragment.class.getName());
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.frameLayoutId, anotherFragment);
			ft.addToBackStack(null);
			ft.commit();
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
		tracker.stopSession();
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
		super.onPause();
		final SharedPreferences settings = getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);
		String userId = settings.getString(ChatUtils.USER_ID, "");
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId != null && !"".equals(regId) && userId != null
				&& !"".equals(userId)) {
			
			if(locationListener!=null)
				locationListener.setPaused(true);
			
		}
	}

	@Override
	public void onBackPressed() {
		if (getFragmentPresent() != null && !"".equals(getFragmentPresent())) {
			if (ChatFragment.class.getName().equals(getFragmentPresent())) {
				Fragment anotherFragment = Fragment.instantiate(this,
						ChatAroundListFragment.class.getName());
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.frameLayoutId, anotherFragment);
				ft.addToBackStack(null);
				ft.commit();
			} else {
				// where are in the list...probably we want to go out of app?
				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(getString(R.string.leave_app_title))
						.setMessage(getString(R.string.leave_app_message))
						.setPositiveButton(getString(R.string.leave_app_ok),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										final SharedPreferences settings = getSharedPreferences(
												ChatUtils.PREFS_NAME, 0);
										String userId = settings.getString(
												ChatUtils.USER_ID, "");
										doGoOfLine(userId);
									}
								})
						.setNegativeButton(getString(R.string.leave_app_ko),
								null).show();
			}
		}

	}
	private void doGoOfLine(String userId){
		ChatAroundAsyncHtpp
		.post(ChatAroundAsyncHtpp.ChatAroundHttpEnum.OFFLINE
				.getUrl() + userId,
				null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(
							JSONObject arg0) {

						super.onSuccess(arg0);
						finish();
					}

					@Override
					protected Object parseResponse(
							String arg0)
							throws JSONException {

						return super
								.parseResponse(arg0);
					}

				}

		);		
	}
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String senderUserId = intent.getExtras().getString(
					PushUtils.USER_ID_FROM_SENDER);
			ChatMessageDto dto = new ChatMessageDto();
			dto.setSenderId(senderUserId);
			eventBus.post(dto);
		}

	};

	public String getNickNameRecipientId() {
		return nickNameRecipientId;
	}

	public void setNickNameRecipientId(String nickNameRecipientId) {
		this.nickNameRecipientId = nickNameRecipientId;
	}

}
