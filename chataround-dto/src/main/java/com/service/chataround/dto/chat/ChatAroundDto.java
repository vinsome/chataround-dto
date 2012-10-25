package com.service.chataround.dto.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatAroundDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String deviceId;
	private String nickName;
	private String longitude;
	private String lattitude;
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

}
