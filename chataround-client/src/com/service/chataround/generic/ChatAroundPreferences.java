package com.service.chataround.generic;

import android.content.Context;
import android.content.SharedPreferences;

import com.service.chataround.R;
import com.service.chataround.dto.PreferencesDto;
import com.service.chataround.util.ChatUtils;

public class ChatAroundPreferences extends ChatAroundGlobalPreferences {
	public static final String PREFS_NAME = "ChatAroundPreferences";
	private Context context;

	public ChatAroundPreferences(Context context) {
		this.context = context;
	}
	
	public void saveNotification(PreferencesDto dto){
		final SharedPreferences settings = context.getSharedPreferences(
				PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(ChatUtils.USER_NOTIFICATIONS,
				dto.isUserNotifications());
		editor.commit();
	}
	public void saveStayOnline(PreferencesDto dto){
		final SharedPreferences settings = context.getSharedPreferences(
				PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(ChatUtils.USER_STAY_ONLINE,
				dto.isStayOnline());
		editor.commit();
	}
	@Override
	public void savePreferences(PreferencesDto dto) {
		final SharedPreferences settings = context.getSharedPreferences(
				PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(ChatUtils.USER_NICKNAME, dto.getNickname());
		editor.putString(ChatUtils.USER_MOOD, dto.getMood());
		editor.putString(ChatUtils.USER_EMAIL, dto.getEmailUser());
		editor.putString(ChatUtils.USER_PASSW, dto.getUserPssw());
		editor.putString(ChatUtils.USER_ID, dto.getUserId());
		editor.putInt(ChatUtils.USER_SEX, dto.getUserSex());

		editor.putBoolean(ChatUtils.USER_STAY_ONLINE, dto.isStayOnline());
		editor.putBoolean(ChatUtils.USER_REGISTERED_ONLINE,
				dto.isUserRegisteredOnline());
		editor.putBoolean(ChatUtils.USER_NOTIFICATIONS,
				dto.isUserNotifications());
		editor.commit();
	}

	@Override
	public PreferencesDto getPreferences() {
		PreferencesDto dto = new PreferencesDto();
		dto = populateDto(dto);
		return dto;
	}

	private PreferencesDto populateDto(final PreferencesDto dto) {
		final SharedPreferences settings = context.getSharedPreferences(
				PREFS_NAME, 0);

		final String nick = settings.getString(ChatUtils.USER_NICKNAME, "");
		final String mood = settings.getString(ChatUtils.USER_MOOD, "");
		final String email = settings.getString(ChatUtils.USER_EMAIL, "");
		final String passw = settings.getString(ChatUtils.USER_PASSW, "");
		final String userId = settings.getString(ChatUtils.USER_ID, "");
		final int userSex = settings.getInt(ChatUtils.USER_SEX,
				R.id.radioMaleId);
		final boolean isRegistered = settings.getBoolean(
				ChatUtils.USER_REGISTERED_ONLINE, false);
		final boolean notifications = settings.getBoolean(
				ChatUtils.USER_NOTIFICATIONS, true);
		final boolean isOnline = settings.getBoolean(
				ChatUtils.USER_STAY_ONLINE, true);

		dto.setNickname(nick);
		dto.setMood(mood);
		dto.setEmailUser(email);
		dto.setUserPssw(passw);
		dto.setUserId(userId);
		dto.setUserSex(userSex);
		dto.setUserRegisteredOnline(isRegistered);
		dto.setUserNotifications(notifications);
		dto.setStayOnline(isOnline);

		return dto;
	}

}
