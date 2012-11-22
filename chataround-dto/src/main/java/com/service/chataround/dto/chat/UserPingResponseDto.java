package com.service.chataround.dto.chat;

import java.io.Serializable;
import java.util.List;

public class UserPingResponseDto extends BaseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private Double longitude;
	private Double lattitude;
	private List<UserPublicDto> userList;
	
	public UserPingResponseDto(){
		
	}
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

}
