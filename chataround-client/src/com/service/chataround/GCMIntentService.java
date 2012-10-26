/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.service.chataround;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.service.chataround.util.ChatConstants;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	public static final String USER_NOTIFICATIONS="notificationsUser";
	public static final String USER_SOUND_ENABLED="notificationsUserSound";
	
	public static final String PREFS_NAME = "EvangelioPrefsFileESP";

	public GCMIntentService() {
		super(ChatConstants.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// displayMessage(context, getString(R.string.gcm_registered));

		/*
		PushDto dto = new PushDto();
		dto.setRegId(registrationId);
		dto.setAppId("EVANGELIO_APP");
		Map<String, String> params = new HashMap<String, String>(0);
		params.put("languageId", "PT");
		dto.setParams(params);
		new EvangelioTask(context).execute(dto, SERVER_URL
				+ "/mymRegisterMessage.do");
		 */
		// ServerUtilities.register(context, registrationId);

	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		final String regId = GCMRegistrar.getRegistrationId(this);
		//displayMessage(context, getString(R.string.gcm_unregistered), regId);
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			/*
			PushDto dto = new PushDto();
			dto.setRegId(regId);
			dto.setAppId("EVANGELIO_APP");
			Map<String, String> params = new HashMap<String, String>(0);
			params.put("languageId", "PT");
			dto.setParams(params);
			new EvangelioTask(context).execute(dto, SERVER_URL
					+ "/mymUnRegisterMessage.do");
			*/
			GCMRegistrar.setRegisteredOnServer(context, false);
			// ServerUtilities.unregister(context, registrationId);

		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		// String message = getString(R.string.gcm_message);
		/*
		final String regId = GCMRegistrar.getRegistrationId(this);
		String message = intent.getExtras().getString(EXTRA_MESSAGE);
		
		String regIdFromMessanger = intent.getExtras().getString(
				REG_ID_FROM_MESSANGER);
		String language = intent.getExtras().getString(CommonUtilities.LANGUAGE_ID_FROM_MESSANGER);
		String nick = intent.getExtras().getString(CommonUtilities.NICK_ID_FROM_MESSANGER);
		
		//build dto
		PushDto dto = new PushDto();
		dto.setNick(nick);
		dto.setMessage(message);
		dto.setTime(Calendar.getInstance().getTime());
		dto.setSent(true);
		dto.setRead(false);
		dto.setMine(regId.equals(regIdFromMessanger));
		dto.getParams().put(CommonUtilities.LANGUAGE_ID_FROM_MESSANGER, language);
		
		//mines are already in it!
		if(!dto.isMine())
		RandomHelper.addMessageToDb(context,dto);
		
		displayMessage(context, message, regIdFromMessanger);
		// notifies user
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean isNotificaciones=settings.getBoolean(USER_NOTIFICATIONS,true);
		boolean isSound=settings.getBoolean(USER_SOUND_ENABLED,true);
		
		if (isNotificaciones&&!regId.equals(regIdFromMessanger))
			generateNotification(context, net.mym.evangelio.CommonUtilities.TAG
					+ ": " + nick+"@ "+message,isSound);
					*/
	}
	

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		/*
		String message = getString(R.string.gcm_deleted, total);
		final String regId = GCMRegistrar.getRegistrationId(this);
		displayMessage(context, message, regId);
		// notifies user
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean isNotificaciones=settings.getBoolean(USER_NOTIFICATIONS,true);
		boolean isSound=settings.getBoolean(USER_SOUND_ENABLED,true);
		
		if(isNotificaciones)
		generateNotification(context, message,isSound);
		*/
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		//displayMessage(context, getString(R.string.gcm_error, errorId), regId);
		
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		final String regId = GCMRegistrar.getRegistrationId(this);
		/*
		displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId), regId);
		*/
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message,boolean isSound) {
        long when = System.currentTimeMillis();
        /*
		
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, EvangelioTabActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        
        if(isSound)
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        notification.icon = R.drawable.icon;
        notification.ledARGB = Color.CYAN;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
        */
    }
}
