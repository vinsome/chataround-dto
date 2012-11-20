package com.service.chataround.dto.chat;

import com.next.infotech.persistance.domain.UserPublicDomain;

public class UserPublicDto implements UserPublicDomain {

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
	private String IMAGES_SERVER = "http://staticchataround.sravi.com/ChatAroundServer/";

	public UserPublicDto(){
		
	}
	public UserPublicDto(UserPublicDomain user){
		this.nickName = user.getNickName();
		this.longitude = user.getLongitude();
		this.lattitude = user.getLattitude();
		this.statusMessage = user.getStatusMessage();
		this.userId = user.getUserId();
		if(user.getGender() != null){
			this.gender = user.getGender().getValue();	
		}
	}
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
	public String getSmallImageUrl() {
		return IMAGES_SERVER+"api/1.0/userthumbnail?userId="+userId+"&size=SMALL";
	}
	public void setSmallImageUrl(String smallImageUrl) {
		throw new RuntimeException("You cant set Image url");
	}
	public String getMediumImageUrl() {
		return IMAGES_SERVER+"api/1.0/userthumbnail?userId="+userId+"&size=MEDIUM";
	}
	public void setMediumImageUrl(String mediumImageUrl) {
		throw new RuntimeException("You cant set Image url");
	}
	public String getLargeImageUrl() {
		return IMAGES_SERVER+"api/1.0/userthumbnail?userId="+userId+"&size=LARGE";
	}
	public String getImageUrl(int size) {
		return IMAGES_SERVER+"api/1.0/userthumbnail?userId="+userId+"&size="+size;
	}
	public void setLargeImageUrl(String largeImageUrl) {
		throw new RuntimeException("You cant set Image url");
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserPublicDto [userId=");
		builder.append(userId);
		builder.append(", nickName=");
		builder.append(nickName);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", lattitude=");
		builder.append(lattitude);
		builder.append(", statusMessage=");
		builder.append(statusMessage);
		builder.append("]");
		return builder.toString();
	}
	

	
}
