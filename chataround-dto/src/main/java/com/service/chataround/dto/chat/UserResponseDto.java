package com.service.chataround.dto.chat;

import com.service.chataround.dto.UserWebEntity;

public class UserResponseDto implements UserWebEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String nickName;
	private Double longitude;
	private Double lattitude;
	private String statusMessage;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLattitude() {
		return lattitude;
	}
	public void setLattitude(Double lattitude) {
		this.lattitude = lattitude;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
}
