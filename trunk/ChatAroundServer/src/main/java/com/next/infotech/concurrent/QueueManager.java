package com.next.infotech.concurrent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.service.chataround.dto.chat.ChatMessageInternalDto;

@Component
public class QueueManager {

	@Autowired
	@Qualifier("sendChatMessageTaskExecutor")
	ThreadPoolTaskExecutor sendChatMessageTaskExecutor;
	
	@Autowired
	private CounterManager counterManager;
	
	public void addChatMessageToQueue(
			ChatMessageInternalDto chatMessageInternalDto) {
		ChatMessageSender chatMessageSender = new ChatMessageSender(chatMessageInternalDto,counterManager);
		sendChatMessageTaskExecutor.execute(chatMessageSender);
	}
}
