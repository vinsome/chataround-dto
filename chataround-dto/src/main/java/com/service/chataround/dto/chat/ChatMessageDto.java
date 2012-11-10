package com.service.chataround.dto.chat;

import java.io.Serializable;

public class ChatMessageDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String senderId;
	private String recipientId;
	private String message;
	
	public ChatMessageDto(ChatMessageDto chatMessageDto) {
		super();
		this.senderId = chatMessageDto.senderId;
		this.recipientId = chatMessageDto.recipientId;
		this.message = chatMessageDto.message;
	}
	
	public ChatMessageDto() {
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
