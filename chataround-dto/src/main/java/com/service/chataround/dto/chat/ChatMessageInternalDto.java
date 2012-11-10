package com.service.chataround.dto.chat;


public class ChatMessageInternalDto extends ChatMessageDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String receipientDeviceId; 
	
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
