package com.next.infotech.persistance.domain;

import java.io.Serializable;
/*
 * This interface contains the information which need to be exposed to world 
 */
public interface UserPublicDomain extends Serializable{

	String getNickName();
	void setNickName(String nickName);

	String getStatusMessage();
	void setStatusMessage(String statusMessage);

	Double getLongitude();
	void setLongitude(Double longitude);
	
	Double getLattitude();
	void setLattitude(Double lattitude);
	
	String getUserId();
	void setUserId(String userId);
}
