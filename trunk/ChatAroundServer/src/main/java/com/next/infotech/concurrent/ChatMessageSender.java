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
	private CounterManager counterManager;
	public ChatMessageSender(ChatMessageInternalDto chatMessageInternalDto,CounterManager counterManager){
		this.chatMessageInternalDto = chatMessageInternalDto;
		this.counterManager = counterManager;
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
			counterManager.incrementCounter(CounterNames.CHAT_MESSAGE_SENT_TO_APP_ENGINE);
			
		}catch(Exception ex){
			counterManager.incrementCounter(CounterNames.CHAT_MESSAGE_FAILED_TOSENT_TO_APP_ENGINE);
			logger.error(ex.getMessage(),ex);
		}
	}

}
