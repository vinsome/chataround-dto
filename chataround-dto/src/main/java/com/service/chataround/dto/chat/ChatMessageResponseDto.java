package com.service.chataround.dto.chat;

import java.io.Serializable;

public class ChatMessageResponseDto implements Serializable{

	public enum MessageStatus {
		Sent("Sent"), Failed("Failed"),Pending("Pending");

        private String value;

        MessageStatus(String value) { this.value = value; }    

        public String getValue() { return value; }

        public static MessageStatus parse(String value) {
        	MessageStatus messageStatus = null; // Default
            for (MessageStatus item : MessageStatus.values()) {
                if (item.getValue().equals(value)) {
                	messageStatus = item;
                    break;
                }
            }
            return messageStatus;
        }

    };
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String recipientId;
	private String message;
	private MessageStatus status;
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
	public MessageStatus getStatus() {
		return status;
	}
	public void setStatus(MessageStatus status) {
		this.status = status;
	}
	
}
