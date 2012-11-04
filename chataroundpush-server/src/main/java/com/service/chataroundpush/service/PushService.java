package com.service.chataroundpush.service;

import com.service.chataround.dto.chat.ChatAroundDto;

public interface PushService {
	public ChatAroundDto pushMessage(ChatAroundDto dto);
}
