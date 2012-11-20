package com.next.infotech.persistance.services;

import java.util.List;

import com.next.core.exception.AppException;
import com.next.infotech.cache.UserLocationCache;
import com.next.infotech.persistance.domain.UserDomain;

public interface ChatAroundServices {

	UserDomain createUser(UserDomain userDto) throws AppException;
	
	UserDomain updateUserStatus(String userExternalId,String status) throws AppException;
	
	UserDomain updateUserPhotoUrls(String userExternalId,String smallPhotoUrl,String mediumImageUrl,String largeImageUrl) throws AppException;
	
	UserDomain getUserByExternalId(String userExternalId) throws AppException;
	
	void rebuildCache(UserLocationCache userLocationCache) throws AppException;
}
