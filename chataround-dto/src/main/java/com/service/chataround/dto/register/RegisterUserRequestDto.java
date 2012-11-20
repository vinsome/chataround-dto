package com.service.chataround.dto.register;

import com.service.chataround.dto.HasError;
import com.service.chataround.dto.chat.UserDto;

public class RegisterUserRequestDto extends UserDto implements HasError{
	
	private static final long serialVersionUID = 1L;
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
