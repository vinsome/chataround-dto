package com.service.chataround.dto.chat;

import java.io.Serializable;
import java.util.List;

public class UserPingRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private Double longitude;
	private Double lattitude;
	private List<UserPublicDto> userList;
	private String serverMessage;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public List<UserPublicDto> getUserList() {
		return userList;
	}

	public void setUserList(List<UserPublicDto> userList) {
		this.userList = userList;
	}

	public String getServerMessage() {
		return serverMessage;
	}

	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
	}

}
