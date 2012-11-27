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

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.chat.ChatMessageDto;
import com.service.chataround.util.ChatUtils;
import com.service.chataround.util.DatabaseUtils;
import com.service.chataround.util.PushUtils;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "ChatAround!";
	public static final String USER_NOTIFICATIONS="notificationsUser";
	public static final String USER_SOUND_ENABLED="notificationsUserSound";
	
	public GCMIntentService() {
		super(ChatUtils.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// displayMessage(context, getString(R.string.gcm_registered));
		
		ChatAroundDto dto = new ChatAroundDto();
		dto.setDeviceId(registrationId);
		dto.setAppId(PushUtils.APP_ID);
		dto.setTime(Calendar.getInstance().getTime());
		//no more need required
		//new ChatAroundTask(context,null).execute(dto,ChatUtils.SERVER_URL + ChatUtils.REGISTER_URL);
		GCMRegistrar.setRegisteredOnServer(context, true);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		final String regId = GCMRegistrar.getRegistrationId(this);
		//displayMessage(context, getString(R.string.gcm_unregistered), regId);
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ChatAroundDto dto = new ChatAroundDto();
			dto.setDeviceId(registrationId);
			dto.setAppId(PushUtils.APP_ID);
			
			//new ChatAroundTask(context,null).execute(dto,ChatUtils.SERVER_URL + ChatUtils.UNREGISTER_URL);
			
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
		
		final String regId = GCMRegistrar.getRegistrationId(this);
		String message = intent.getExtras().getString(PushUtils.PARAMETER_MESSAGE);
		
		String regIdFromMessanger = intent.getExtras().getString(
				PushUtils.REG_ID_FROM_MESSANGER);
		
		String nick = intent.getExtras().getString(PushUtils.NICK_ID_FROM_MESSANGER);
		String recipientId = intent.getExtras().getString(PushUtils.USER_ID_FROM_RECIPIENT);
		String senderId = intent.getExtras().getString(PushUtils.USER_ID_FROM_SENDER);
		//build dto
		ChatMessageDto dto = new ChatMessageDto();
		dto.setNickName(nick);
		dto.setMessage(message);
		dto.setTime(Calendar.getInstance().getTime());
		dto.setSent(true);
		dto.setMine(regId.equals(regIdFromMessanger));
		
		
		//mines are already in it!
		if(!dto.isMine()){
			//since its not mine, need to put here the senders cause my database looks for user_id from
			//which I«m talking to.
			dto.setRecipientId(senderId);	
			DatabaseUtils.addMessageToDb(context,dto);
			ChatUtils.displayMessage(context, message, senderId);
		}else{
			//its mine, so no need to add it to database
		}
		
		// notifies user
		SharedPreferences settings = getSharedPreferences(ChatUtils.PREFS_NAME, 0);
		boolean isNotificaciones=settings.getBoolean(USER_NOTIFICATIONS,true);
		boolean isSound=settings.getBoolean(USER_SOUND_ENABLED,true);
		
		if (isNotificaciones&&!regId.equals(regIdFromMessanger))
			generateNotification(context, TAG
					+ ": " + nick+"@ "+message,isSound,senderId);
			
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
	private static void generateNotification(Context context, String message,boolean isSound,String senderUserId) {
        long when = System.currentTimeMillis();
        
		
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, ChatAroundActivity.class);
        notificationIntent.putExtra(ChatUtils.NOTIFICATION_SENDER_USER_ID, senderUserId);
        
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        
        if(isSound)
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        notification.icon = R.drawable.ic_launcher;
        notification.ledARGB = Color.CYAN;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
        
    }
}
