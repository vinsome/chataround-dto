package com.next.infotech.persistance.domain;


/**
 * This interface contains all information of use which need to be store in database
 * @author Ravi
 *
 */
public interface UserDomain extends UserCacheDomain{

	String getEmail();
	void setEmail(String email);
	
	String getPassword();
	void setPassword(String password);
	
}
