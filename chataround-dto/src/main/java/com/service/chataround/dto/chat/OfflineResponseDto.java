package com.service.chataround.dto.chat;

import java.io.Serializable;

public class OfflineResponseDto extends BaseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String userOfflineStatus;


	/**
	 * @return the userOfflineStatus
	 */
	public String getUserOfflineStatus() {
		return userOfflineStatus;
	}


	/**
	 * @param userOfflineStatus the userOfflineStatus to set
	 */
	public void setUserOfflineStatus(String userOfflineStatus) {
		this.userOfflineStatus = userOfflineStatus;
	}
}
