package com.service.chataround.dto.chat;

import com.service.chataround.dto.HasError;

public class BaseDto implements HasError {

	private String serverMessage;
	private String responseStatus;
	
	public String getServerMessage() {
		return serverMessage;
	}

	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

}
