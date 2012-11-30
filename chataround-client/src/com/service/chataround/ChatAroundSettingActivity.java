package com.service.chataround;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.util.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.next.infotech.persistance.domain.UserPublicDomain.Gender;
import com.service.chataround.async.ChatAroundAsyncHtpp;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.chat.LoginDto;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.dto.chat.UserStatusUpdateDto;
import com.service.chataround.dto.chat.UserStatusUpdateResponseDto;
import com.service.chataround.dto.register.RegisterUserRequestDto;
import com.service.chataround.task.ChatAroundLoginTask;
import com.service.chataround.task.ChatAroundMoodTask;
import com.service.chataround.task.ChatAroundRegisterUserTask;
import com.service.chataround.util.ChatUtils;
import com.service.chataround.util.PushUtils;

public class ChatAroundSettingActivity extends Activity {
	public static String TAG = ChatAroundSettingActivity.class.getName();
	private EditText nickName;
	private EditText emailText;
	private EditText moodText;
	private EditText userPassw;
	private RadioGroup radioSex;
	private String regId;
	private String currentMood;
	private RegisterUserRequestDto temporalRegisteredUserDto;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsdialog);
		// only register if not yet registered
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isOnline()) {
			registerToCloud();
		}
		final SharedPreferences settings = getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);
		nickName = (EditText) findViewById(R.id.nicknameTextView);
		emailText = (EditText) findViewById(R.id.emailTextView);
		moodText = (EditText) findViewById(R.id.moodTextView);
		userPassw = (EditText) findViewById(R.id.passwordTextView);
		radioSex = (RadioGroup) findViewById(R.id.radioSexId);

		final String nick = settings.getString(ChatUtils.USER_NICKNAME, "");
		final String mood = settings.getString(ChatUtils.USER_MOOD, "");
		final String email = settings.getString(ChatUtils.USER_EMAIL, "");
		final String passw = settings.getString(ChatUtils.USER_PASSW, "");
		final String userId = settings.getString(ChatUtils.USER_ID, "");
		final int selectedId = settings.getInt(ChatUtils.USER_SEX,
				R.id.radioMaleId);
		boolean isRegistered = settings.getBoolean(
				ChatUtils.USER_REGISTERED_ONLINE, false);
		// dont modify mandatory fields
		if (isRegistered) {
			nickName.setEnabled(false);
			emailText.setEnabled(false);
			userPassw.setEnabled(false);
		}

		nickName.setText(nick);
		moodText.setText(mood);
		currentMood = mood;
		emailText.setText(email);
		userPassw.setText(passw);
		radioSex.check(selectedId);

		Switch switchButton = (Switch) findViewById(R.id.switchNotifId);
		Boolean isNotifications = settings.getBoolean(
				ChatUtils.USER_NOTIFICATIONS, true);
		switchButton.setChecked(isNotifications);
		switchButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Switch notif = (Switch) v;
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(ChatUtils.USER_NOTIFICATIONS,
						notif.isChecked());
				editor.commit();
			}
		});
		Switch switchButtonSound = (Switch) findViewById(R.id.switchNotifSoundId);
		Boolean isSound = settings.getBoolean(ChatUtils.USER_STAY_ONLINE, true);
		switchButtonSound.setChecked(isSound);
		switchButtonSound.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Switch sound = (Switch) v;
				if (userId != null && !"".equals(userId)) {
					if(!sound.isChecked()) {
						final SharedPreferences settings = getSharedPreferences(
								ChatUtils.PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean(ChatUtils.USER_STAY_ONLINE, sound.isChecked());
						editor.commit();						
						doGoOfline(userId);
					}
				} else {
					// listener will update status atutomatically
				}
			}
		});

		Button button = (Button) findViewById(R.id.saveSettings);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (StringUtils.hasText(nickName.getText().toString().trim())
						&& StringUtils.hasText(moodText.getText().toString()
								.trim())
						&& StringUtils.hasText(emailText.getText().toString()
								.trim())
						&& StringUtils.hasText(userPassw.getText().toString()
								.trim())) {
					if (isOnline()) {
						regId = GCMRegistrar
								.getRegistrationId(getApplicationContext());

						String nickname = nickName.getText().toString().trim();
						String mood = moodText.getText().toString().trim();
						String email = emailText.getText().toString().trim();
						String passw = userPassw.getText().toString().trim();
						int sex = radioSex.getCheckedRadioButtonId();
						// We need an Editor object to make preference changes.
						// All objects are from android.context.Context
						SharedPreferences.Editor editor = settings.edit();
						boolean isRegistered = settings.getBoolean(
								ChatUtils.USER_REGISTERED_ONLINE, false);
						String userId = settings.getString(ChatUtils.USER_ID,
								"");

						if (!isRegistered) {
							editor.putString(ChatUtils.USER_NICKNAME, nickname);
							editor.putString(ChatUtils.USER_MOOD, mood);
							editor.putString(ChatUtils.USER_EMAIL, email);
							editor.putString(ChatUtils.USER_PASSW, passw);
							editor.putInt(ChatUtils.USER_SEX, sex);
							editor.commit();
							// settingsDialog.hide();
							// finish();
							registerToServer(regId, email, nickname, passw,
									mood, sex);
						} else if (isRegistered && !"".equals(userId)) {
							// not registered, we only want user to change
							// mood...and notifications
							if (currentMood != null
									&& !currentMood.equals(moodText.getText()
											.toString().trim())) {
								// mood changed, call service to change mood
								changeMoodStatus(userId, moodText.getText()
										.toString().trim());

							}else{
								//no register or login or change mood, its a save so close it
								finish();
							}
						}else{
							//no register or login or change mood, its a save so close it
							finish();
						}

					}
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.app_offline), Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		Button closeButton = (Button) findViewById(R.id.closeSettings);
		closeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
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
			// settingsDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void registerToServer(String regId, String email, String nickName,
			String password, String mood, int sex) {
		RegisterUserRequestDto dto = new RegisterUserRequestDto();
		dto.setDeviceId(regId);
		dto.setEmail(email);// validating user
		dto.setLattitude(0.0d);
		dto.setLongitude(0.0d);
		dto.setNickName(nickName);// validating nickname
		dto.setPassword(password);
		dto.setStatusMessage(mood);
		if (sex == R.id.radioMaleId) {
			dto.setGender(Gender.Male.getValue());
		} else if (sex == R.id.radioFemaleId) {
			dto.setGender(Gender.Female.getValue());
		} else {
			dto.setGender(Gender.Other.getValue());
		}
		// in case user tries to login from existing one.
		temporalRegisteredUserDto = dto;
		// register to server and get users...
		new ChatAroundRegisterUserTask(this, null).execute(dto,
				ChatUtils.REGISTER_SERVER_URL);
	}

	private void changeMoodStatus(final String userId, final String mood) {
		UserStatusUpdateDto dto = new UserStatusUpdateDto();
			dto.setUserId(userId);
			dto.setStatus(mood);
		
		new ChatAroundMoodTask(this, null).execute(dto,ChatUtils.CHANGE_MOOD_SERVER_URL);
		
	}

	public void finishTaskRegisterUser(RegisterUserRequestDto dto) {
		if (dto != null
				&& StringUtils.hasText(dto.getUserId())
				&& (dto.getServerMessage() == null || "".equals(dto
						.getServerMessage()))) {
			savePreferences(dto.getUserId());
			finish();
		} else if (dto.getServerMessage() != null
				&& !"".equals(dto.getServerMessage()) ) {
			// some error: will try to login first then
			// Toast.makeText(getApplicationContext(), dto.getServerMessage(),
			// Toast.LENGTH_LONG).show();
			LoginDto loginDto = new LoginDto();
			loginDto.setEmail(temporalRegisteredUserDto.getEmail());
			loginDto.setNickname(temporalRegisteredUserDto.getNickName());
			loginDto.setPassword(temporalRegisteredUserDto.getPassword());
			doLogin(loginDto);

		}
	}

	private void doLogin(LoginDto dto) {

		new ChatAroundLoginTask(this, null).execute(dto,
				ChatUtils.LOGIN_SERVER_URL);

	}

	public void finishTaskUpdateUserStatus(UserStatusUpdateResponseDto dto) {
		if (dto != null && dto.getServerMessage() != null && !"".equals(dto.getServerMessage())) {
			// some error going on...
			Toast.makeText(getApplicationContext(),
					getString(R.string.some_error_server), Toast.LENGTH_LONG)
					.show();
			
		}else{
			final SharedPreferences settings = getSharedPreferences(
					ChatUtils.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(ChatUtils.USER_MOOD, moodText.getText().toString());
			editor.commit();
			Toast.makeText(getApplicationContext(),
					getString(R.string.status_updated_succesfully), Toast.LENGTH_LONG)
					.show();		
			finish();
		}
	}

	public void finishTaskLoginUser(UserPublicDto dto) {
		if (dto != null && dto.getServerMessage() != null && !"".equals(dto
						.getServerMessage())) {
			// some error going on...
			Toast.makeText(getApplicationContext(),
					dto.getServerMessage(), Toast.LENGTH_LONG)
					.show();
		} else {
			// user ok
			savePreferences(dto.getUserId());
			finish();
		}

	}

	private void savePreferences(String userId) {
		final SharedPreferences settings = getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ChatUtils.USER_ID, userId);
		editor.putBoolean(ChatUtils.USER_REGISTERED_ONLINE, true);
		editor.commit();
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
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
				// No need to display anything cause its background thingy
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

				// new ChatAroundTask(context,
				// null).execute(dto,ChatUtils.SERVER_URL +
				// ChatUtils.REGISTER_URL);
			}
		}
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}

	private void doGoOfline(String userId) {
		ChatAroundAsyncHtpp.post(
				ChatAroundAsyncHtpp.ChatAroundHttpEnum.OFFLINE.getUrl()
						+ userId, null, new JsonHttpResponseHandler() {

							@Override
							public void onSuccess(JSONArray arg0) {
								
								super.onSuccess(arg0);
							}

							@Override
							protected Object parseResponse(String arg0)
									throws JSONException {
								
								return super.parseResponse(arg0);
							}
						

				}

		);
	}

	private void doChangeMood(final String userId, final String status) {
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("status", status);

		ChatAroundAsyncHtpp.post(
				ChatAroundAsyncHtpp.ChatAroundHttpEnum.CHANGEMOOD.getUrl(),
				params, new JsonHttpResponseHandler() {

					public void onSuccess(UserStatusUpdateResponseDto obj) {
						final SharedPreferences settings = getSharedPreferences(
								ChatUtils.PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(ChatUtils.USER_MOOD, status);
						editor.commit();
					}

					@Override
					protected UserStatusUpdateResponseDto parseResponse(
							String arg0) throws JSONException {
						Gson gson = new Gson();
						UserStatusUpdateResponseDto response = gson.fromJson(
								arg0, UserStatusUpdateResponseDto.class);
						return (UserStatusUpdateResponseDto) response;
					}

				}

		);
	}
}
