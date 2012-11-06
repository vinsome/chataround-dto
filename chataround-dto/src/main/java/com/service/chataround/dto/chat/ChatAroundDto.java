package com.service.chataround.dto.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAroundDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Id in database to change messages status
	private long id;
	private String deviceId;
	private String nickName;
	private String longitude;
	private String lattitude;
	private String message;
	private Date time;
	private boolean mine;
	private boolean sent;
	private String response;
	private String appId;
	private String mood;
	private List<ChatAroundDto> chatsAround = new ArrayList<ChatAroundDto>(0);

	public List<ChatAroundDto> getChatsAround() {
		return chatsAround;
	}

	public void setChatsAround(List<ChatAroundDto> chatsAround) {
		this.chatsAround = chatsAround;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLattitude() {
		return lattitude;
	}

	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public boolean isMine() {
		return mine;
	}

	public void setMine(boolean mine) {
		this.mine = mine;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

}
