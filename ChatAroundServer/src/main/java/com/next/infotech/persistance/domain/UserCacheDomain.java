package com.next.infotech.persistance.domain;


/*
 * This interface contains the information which need to be Cached in server 
 */
public interface UserCacheDomain extends UserPublicDomain{

	String getDeviceId();
	void setDeviceId(String deviceId);

}
