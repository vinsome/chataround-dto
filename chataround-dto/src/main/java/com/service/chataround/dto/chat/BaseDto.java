package com.service.chataround.dto.chat;

import com.service.chataround.dto.HasError;

public class BaseDto implements HasError {

	private String serverMessage;
	private String responseStatus;
	@Override
	public String getServerMessage() {
		return serverMessage;
	}

	@Override
	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
	}

	@Override
	public String getResponseStatus() {
		return responseStatus;
	}

	@Override
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

}
