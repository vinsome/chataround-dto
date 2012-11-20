package com.service.chataround.dto.chat;


public class ChatMessageInternalDto extends ChatMessageDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String senderDeviceId; 
	private String receipientDeviceId;
	
	public String getSenderDeviceId() {
		return senderDeviceId;
	}
	public void setSenderDeviceId(String senderDeviceId) {
		this.senderDeviceId = senderDeviceId;
	}
	public ChatMessageInternalDto(ChatMessageDto chatMessageDto){
		super(chatMessageDto);
	}
	public String getReceipientDeviceId() {
		return receipientDeviceId;
	}
	public void setReceipientDeviceId(String receipientDeviceId) {
		this.receipientDeviceId = receipientDeviceId;
	}

}
