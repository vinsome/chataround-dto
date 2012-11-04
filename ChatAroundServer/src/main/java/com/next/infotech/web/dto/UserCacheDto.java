package com.next.infotech.web.dto;

import com.next.infotech.persistance.domain.UserCacheDomain;
import com.next.infotech.persistance.domain.UserPublicDomain;
import com.service.chataround.dto.chat.UserPublicDto;

public class UserCacheDto extends UserPublicDto implements UserCacheDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public UserCacheDto(){
		
	}
	public UserCacheDto(UserCacheDomain user){
		super((UserPublicDomain)user);
		this.deviceId = user.getDeviceId();
	}
	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserCacheDto [deviceId=");
		builder.append(deviceId);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
	
}
