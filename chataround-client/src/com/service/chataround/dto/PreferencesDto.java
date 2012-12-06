package com.service.chataround.dto;

import java.io.Serializable;

public class PreferencesDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private  String nickname;
	private  String userId;
	private  String mood;
	private  String emailUser;
	private  String userPssw;
	private  int userSex;
	private  boolean userNotifications;
	private  boolean stayOnline;
	private  boolean userRegisteredOnline;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public String getEmailUser() {
		return emailUser;
	}
	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}
	public String getUserPssw() {
		return userPssw;
	}
	public void setUserPssw(String userPssw) {
		this.userPssw = userPssw;
	}
	public int getUserSex() {
		return userSex;
	}
	public void setUserSex(int userSex) {
		this.userSex = userSex;
	}
	public boolean isUserNotifications() {
		return userNotifications;
	}
	public void setUserNotifications(boolean userNotifications) {
		this.userNotifications = userNotifications;
	}
	public boolean isStayOnline() {
		return stayOnline;
	}
	public void setStayOnline(boolean stayOnline) {
		this.stayOnline = stayOnline;
	}
	public boolean isUserRegisteredOnline() {
		return userRegisteredOnline;
	}
	public void setUserRegisteredOnline(boolean userRegisteredOnline) {
		this.userRegisteredOnline = userRegisteredOnline;
	}
}
