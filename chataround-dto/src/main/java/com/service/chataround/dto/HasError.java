package com.service.chataround.dto;

public interface HasError {

	public String getServerMessage();
	
	public void setServerMessage(String serverMessage);
	
	public String getResponseStatus();
	
	public void setResponseStatus(String responseStatus);
}
