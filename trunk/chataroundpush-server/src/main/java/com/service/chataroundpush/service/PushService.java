package com.service.chataroundpush.service;

import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.dto.chat.ChatMessageInternalDto;

public interface PushService {
	public ChatAroundDto pushMessage(ChatAroundDto dto);
	public ChatMessageInternalDto pushMessage(ChatMessageInternalDto dto);
}
