package com.service.chataround.dto.chat;

import java.io.Serializable;
import java.util.Date;

public class ChatMessageDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id ;
	private String senderId;
	private String recipientId;
	private String message;
	private String appId;
	private String nickName;
	private boolean mine;
	private boolean sent;
	private Date time;
	
	
	public ChatMessageDto(ChatMessageDto chatMessageDto) {
		super();
		this.senderId = chatMessageDto.senderId;
		this.recipientId = chatMessageDto.recipientId;
		this.message = chatMessageDto.message;
		this.appId = chatMessageDto.appId;
		this.nickName = chatMessageDto.nickName;
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

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public boolean isMine() {
		return mine;
	}

	public void setMine(boolean mine) {
		this.mine = mine;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

}
