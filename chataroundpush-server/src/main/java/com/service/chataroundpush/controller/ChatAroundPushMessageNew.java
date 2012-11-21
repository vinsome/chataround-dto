package com.service.chataroundpush.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.chat.ChatMessageInternalDto;
import com.service.chataroundpush.service.PushService;

@Controller
public class ChatAroundPushMessageNew {
	private final static Logger LOGGER = Logger.getLogger(ChatAroundPushMessageNew.class
			.getName());
	
	@Autowired
	private PushService pushService;
	
	Gson gson = new Gson();
	
	@RequestMapping(value = "/sendChatMessage.do", method = { RequestMethod.POST})
	@ResponseBody
	public String chatAroundSendMessage( 
				HttpEntity<String> requestEntity,
				HttpServletResponse response) {
		LOGGER.fine("chatAroundSendMessage begin");
		String jsonBody=requestEntity.getBody();
		LOGGER.fine("chatAroundSendMessage jsonBody "+jsonBody);
		Gson gson = new Gson();
		ChatMessageInternalDto chatMessageInternalDto = gson.fromJson(jsonBody, ChatMessageInternalDto.class);
		chatMessageInternalDto = pushService.pushMessage(chatMessageInternalDto);
		//dto.setResponse("push.server.operation.sendmessage.ok");
		//dto = service.makePersistent(dto);
		//return gson.toJson(dto);
		return gson.toJson(chatMessageInternalDto);
	}
}
