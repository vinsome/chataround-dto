package com.next.infotech.persistance.domain;

import java.io.Serializable;
/*
 * This interface contains the information which need to be exposed to world 
 */
public interface UserPublicDomain extends Serializable{

	Long getId();
	void setId(Long id);
	
	String getNickName();
	void setNickName(String nickName);

	String getStatusMessage();
	void setStatusMessage(String statusMessage);

	Double getLongitude();
	void setLongitude(Double longitude);
	
	Double getLattitude();
	void setLattitude(Double lattitude);
	
}
