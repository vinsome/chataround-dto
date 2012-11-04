package com.service.chataroundpush.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataroundpush.server.PushDatastore;
import com.service.chataroundpush.service.PushService;

@Controller
public class ChatAroundPushMessage {
	private final static Logger LOGGER = Logger.getLogger(ChatAroundPushMessage.class
			.getName());
	
	@Autowired
	private PushService pushService;
	
	@RequestMapping(value = "/chatAroundRegister.do", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public String chatAroundRegister(HttpEntity<String> requestEntity, HttpServletResponse response) {
		LOGGER.fine("chatAroundRegister.do begin");
		String jsonBody=requestEntity.getBody();
		Gson gson = new Gson();
		ChatAroundDto dto = gson.fromJson(jsonBody, ChatAroundDto.class);
		//Map<String, String> params = dto.getParams();
	    PushDatastore.register(dto.getDeviceId(),dto.getAppId(),dto.getTime());
	    dto.setResponse("push.server.operation.register.ok");
		return gson.toJson(dto);
	}
	
	@RequestMapping(value = "/chatAroundUnRegister.do", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public String chatAroundUnRegister(HttpEntity<String> requestEntity, HttpServletResponse response) {
		LOGGER.fine("chatAroundUnRegister begin");
		String jsonBody=requestEntity.getBody();
		Gson gson = new Gson();
		ChatAroundDto dto = gson.fromJson(jsonBody, ChatAroundDto.class);
		//Map<String, String> params = dto.getParams();
	    PushDatastore.unregister(dto.getDeviceId(),dto.getAppId());
	    dto.setResponse("push.server.operation.unregister.ok");
		return gson.toJson(dto);
	}	
	
	
	@RequestMapping(value = "/chatAroundSendMessage.do", method = { RequestMethod.POST,
			RequestMethod.GET })
	@ResponseBody
	public String chatAroundSendMessage(HttpEntity<String> requestEntity, HttpServletResponse response) {
		LOGGER.fine("chatAroundSendMessage begin");
		String jsonBody=requestEntity.getBody();
		Gson gson = new Gson();
		ChatAroundDto dto = gson.fromJson(jsonBody, ChatAroundDto.class);
		LOGGER.fine("chatAroundSendMessage Message get "+dto.getMessage());
		dto = pushService.pushMessage(dto);
		dto.setResponse("push.server.operation.sendmessage.ok");
		//dto = service.makePersistent(dto);
		//return gson.toJson(dto);
		return gson.toJson(dto);
	}
	
	@RequestMapping(value = "/chatAroundHelloTest.do")
	public void chatAroundHelloTest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		LOGGER.fine("chatAroundHelloTest - welcomeHandler Begin");
		response.getWriter().println(
				"InstaMeet setup and Running");
		LOGGER.fine("chatAroundHelloTest - welcomeHandler End");
	}	
}
