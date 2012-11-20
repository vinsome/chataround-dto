package com.next.infotech.concurrent;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.service.chataround.dto.chat.ChatMessageInternalDto;

public class ChatMessageSender implements Runnable {

	private static final String SERVER_URL = "http://chataround2012.appspot.com/sendChatMessage.do";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Gson gson = new Gson();
	private ChatMessageInternalDto chatMessageInternalDto;
	public ChatMessageSender(ChatMessageInternalDto chatMessageInternalDto){
		this.chatMessageInternalDto = chatMessageInternalDto;
	}
	public void run() {
		try{
			//Create httpClient
			HttpClient httpClient = new DefaultHttpClient();
			
			//create Post request
			HttpPost httpost = new HttpPost(SERVER_URL);
			
			//Create Entity Data
			StringEntity httpEntity = new StringEntity(gson.toJson(chatMessageInternalDto));
			httpost.setEntity(httpEntity);
			
			httpClient.execute(httpost);
			
			
		}catch(Exception ex){
			logger.error(ex.getMessage(),ex);
		}
	}

}
