package com.service.chataround.util;


public class ChatConstants {
    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "794164299558";
    public static final String SERVER_URL = "http://chataround2012.appspot.com";
    
    public static final String PING_LOCATION_SERVER_URL="http://chataround.sravi.com/ChatAroundServer/api/1.0/pinglocation";
    public static final String PING_LOCATION_AND_GET_USERS_SERVER_URL="http://chataround.sravi.com/ChatAroundServer/api/1.0/pinglocationandgetuser";
    public static final String REGISTER_SERVER_URL="http://chataround.sravi.com/ChatAroundServer/api/1.0/registeruser";
    public static final String SEND_MESSAGE_USER_SERVER_URL="http://chataround.sravi.com/ChatAroundServer/api/1.0/sendchatmessage";
    
    //http://chataround.sravi.com/ChatAroundServer/api/1.0/viewusermap
    	
	public static final String USER_NICKNAME = "nickname";
	public static final String USER_ID = "userId";
	public static final String USER_MOOD = "mood";
	public static final String USER_EMAIL = "emailUser";
	public static final String USER_PASSW = "userPssw";
	public static final String PREFS_NAME = "ChatAroundPreferences";
	public static final String USER_NOTIFICATIONS = "UserNotifications";
	public static final String USER_STAY_ONLINE = "StayOnline";
	public static final String USER_REGISTERED_ONLINE = "userRegisteredOnline";
	
	public static final String REGISTER_URL="/chatAroundRegister.do";
	public static final String UNREGISTER_URL="/chatAroundUnRegister.do";
	public static final String SENDMESSAGE_URL="/chatAroundSendMessage.do";

}
