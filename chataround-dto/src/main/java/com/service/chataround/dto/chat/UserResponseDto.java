package com.service.chataround.dto.chat;

import com.next.infotech.persistance.domain.UserPublicDomain.Gender;
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
	private String gender;
	
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
	public Gender getGender() {
		return Gender.parse(gender);
	}
	public void setGender(String gender) {
		Gender gendreEnum = Gender.parse(gender);
		if(gendreEnum == null){
			throw new RuntimeException(gender + " is not a correct value for Gender");
		}
		this.gender = gender;
	}
	public void setGender(Gender gender) {
		if(gender == null){
			this.gender = null;
		}else{
			this.gender = gender.getValue();	
		}
	}
}
