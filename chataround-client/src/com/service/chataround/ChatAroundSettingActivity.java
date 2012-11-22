package com.service.chataround;

import org.springframework.util.StringUtils;

import com.service.chataround.util.ChatUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

public class ChatAroundSettingActivity extends Activity {
	public static String TAG = ChatAroundSettingActivity.class.getName();
	private EditText nickName;
	private EditText emailText;
	private EditText moodText;
	private EditText userPassw;
	private RadioGroup radioSex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsdialog);
	}

	@Override
	public void onResume() {
		super.onResume();
		final SharedPreferences settings = getSharedPreferences(
				ChatUtils.PREFS_NAME, 0);
		nickName = (EditText) findViewById(R.id.nicknameTextView);
		emailText = (EditText) findViewById(R.id.emailTextView);
		moodText = (EditText) findViewById(R.id.moodTextView);
		userPassw = (EditText) findViewById(R.id.passwordTextView);
		radioSex = (RadioGroup) findViewById(R.id.radioSexId);

		String nick = settings.getString(ChatUtils.USER_NICKNAME, "");
		String mood = settings.getString(ChatUtils.USER_MOOD, "");
		String email = settings.getString(ChatUtils.USER_EMAIL, "");
		String passw = settings.getString(ChatUtils.USER_PASSW, "");
		int selectedId = settings.getInt(ChatUtils.USER_SEX, R.id.radioMaleId);

		nickName.setText(nick);
		moodText.setText(mood);
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
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(ChatUtils.USER_STAY_ONLINE, sound.isChecked());
				editor.commit();
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

					String nickname = nickName.getText().toString().trim();
					String mood = moodText.getText().toString().trim();
					String email = emailText.getText().toString().trim();
					String passw = userPassw.getText().toString().trim();
					int selectedId = radioSex.getCheckedRadioButtonId();
					// We need an Editor object to make preference changes.
					// All objects are from android.context.Context
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(ChatUtils.USER_NICKNAME, nickname);
					editor.putString(ChatUtils.USER_MOOD, mood);
					editor.putString(ChatUtils.USER_EMAIL, email);
					editor.putString(ChatUtils.USER_PASSW, passw);
					editor.putInt(ChatUtils.USER_SEX, selectedId);

					editor.commit();
					// settingsDialog.hide();
				}
			}
		});

		Button closeButton = (Button) findViewById(R.id.closeSettings);
		closeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

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
}
