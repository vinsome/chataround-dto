package com.service.chataround.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.content.Context;
import android.content.Intent;

public class ChatUtils {
	public static String TAG = ChatUtils.class.getName();
	/**
	 * Google API project id registered to use GCM.
	 */
	public static final String SENDER_ID = "794164299558";
	public static final String SERVER_URL = "http://chataround2012.appspot.com";

	public static final String PING_LOCATION_SERVER_URL = "http://chataround.sravi.com/ChatAroundServer/api/1.0/pinglocation";
	public static final String PING_LOCATION_AND_GET_USERS_SERVER_URL = "http://chataround.sravi.com/ChatAroundServer/api/1.0/pinglocationandgetuser";
	public static final String REGISTER_SERVER_URL = "http://chataround.sravi.com/ChatAroundServer/api/1.0/registeruser";
	public static final String SEND_MESSAGE_USER_SERVER_URL = "http://chataround.sravi.com/ChatAroundServer/api/1.0/sendchatmessage";

	// http://chataround.sravi.com/ChatAroundServer/api/1.0/viewusermap

	public static final String USER_NICKNAME = "nickname";
	public static final String USER_ID = "userId";
	public static final String USER_MOOD = "mood";
	public static final String USER_EMAIL = "emailUser";
	public static final String USER_PASSW = "userPssw";
	public static final String USER_SEX = "userSex";
	public static final String PREFS_NAME = "ChatAroundPreferences";
	public static final String USER_NOTIFICATIONS = "UserNotifications";
	public static final String USER_STAY_ONLINE = "StayOnline";
	public static final String USER_REGISTERED_ONLINE = "userRegisteredOnline";

	public static final String REGISTER_URL = "/chatAroundRegister.do";
	public static final String UNREGISTER_URL = "/chatAroundUnRegister.do";
	public static final String SENDMESSAGE_URL = "/chatAroundSendMessage.do";

	public static final String DISPLAY_MESSAGE_ACTION = "com.service.chataround.ChatAroundActivity";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message,
			String senderRegId) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(PushUtils.PARAMETER_MESSAGE, message);
		intent.putExtra(PushUtils.REG_ID_FROM_MESSANGER, senderRegId);
		context.sendBroadcast(intent);
	}

	public static float distanceFromUser(Double myLatitude, Double myLongitude,
			Double otherLatitude, Double otherLongitude) {
		float[] results = new float[3];
		android.location.Location.distanceBetween(myLatitude.doubleValue(),
				myLongitude.doubleValue(), otherLatitude.doubleValue(),
				otherLongitude.doubleValue(), results);
		float distance = results[0];// metres

		if (distance != 0.0) {
			return new BigDecimal(distance / 1000).setScale(2,
					RoundingMode.HALF_UP).floatValue();

		} else {
			return results[0];// in metres
		}
	}
	
	@Deprecated
	public static boolean isLocationChanged(Double myLatitude,
			Double myLongitude, Double otherLatitude, Double otherLongitude) {
		float distance = Math.abs(distanceFromUser(myLatitude, myLongitude,
				otherLatitude, otherLongitude));
		// distance greater or equal than 3km
		if (distance >= 3) {
			return true;
		} else {
			return false;
		}
	}

}
