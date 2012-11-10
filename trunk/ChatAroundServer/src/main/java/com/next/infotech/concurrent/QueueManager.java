package com.next.infotech.concurrent;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.backportconcurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.service.chataround.dto.chat.ChatMessageInternalDto;

@Component
public class QueueManager {

	@Autowired
	ThreadPoolTaskExecutor sendChatMessageTaskExecutor;
	@PostConstruct
	public void initialize() {

	}

	public void addChatMessageToQueue(
			ChatMessageInternalDto chatMessageInternalDto) {
		ChatMessageSender chatMessageSender = new ChatMessageSender(chatMessageInternalDto);
		sendChatMessageTaskExecutor.execute(chatMessageSender);
	}
}
