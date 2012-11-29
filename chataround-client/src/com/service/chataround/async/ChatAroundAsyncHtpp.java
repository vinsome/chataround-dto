package com.service.chataround.async;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ChatAroundAsyncHtpp {
	
	  private static final String BASE_URL = "http://chataround.sravi.com/ChatAroundServer/api/1.0/";
	  private static final String PING_LOCATION_SERVER_URL = "pinglocation";
	  private static final String PING_LOCATION_AND_GET_USERS_SERVER_URL = "pinglocationandgetuser";
	  private static final String REGISTER_SERVER_URL = "registeruser";
	  private static final String SEND_MESSAGE_USER_SERVER_URL = "sendchatmessage";
	  private static final String CHANGE_MOOD_SERVER_URL = "updateuserstatus";
	  private static final String LOGIN_SERVER_URL = "login";
	  private static final String OFFLINE_SERVER_URL = "ofline/";
	  

	  private static AsyncHttpClient client = new AsyncHttpClient();

	  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	  }
	  
	 public enum ChatAroundHttpEnum {
		 OFFLINE("OFFLINE",OFFLINE_SERVER_URL),
		 CHANGEMOOD("CHANGEMOOD",CHANGE_MOOD_SERVER_URL)
		 ;
		 
		 private String name;
		 private String url;
		 private ChatAroundHttpEnum(String name,String url){
			 this.name=name;
			 this.url=url;
		 }
		 public String getName(){
			 return name;
		 }
		 public String getUrl(){
			 return url;
		 }
		 
	 }
}
