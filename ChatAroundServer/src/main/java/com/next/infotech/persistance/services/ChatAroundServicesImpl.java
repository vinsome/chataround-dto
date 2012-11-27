package com.next.infotech.persistance.services;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.next.core.db.page.PageResult;
import com.next.core.db.page.MapPageInfo.ORDER;
import com.next.core.exception.AppException;
import com.next.core.exception.InternalAppException;
import com.next.ext.core.db.page.HibernateMapPageInfo;
import com.next.infotech.cache.UserLocationCache;
import com.next.infotech.persistance.domain.UserDomain;
import com.next.infotech.persistance.helper.jpa.impl.UserHelper;
import com.next.infotech.persistance.jpa.impl.User;
import com.service.chataround.dto.chat.UserDto;

@Component
public class ChatAroundServicesImpl implements ChatAroundServices {

	@Autowired
	private UserHelper userHelper;

	public UserDomain createUser(UserDomain userDto) throws AppException {
		User user = convertUser(userDto);
		user = userHelper.createUser(user);
		userDto.setUserId(user.getUserId());
		return userDto;
	}

	public UserDomain updateUserStatus(String userExternalId,
			String statusMessage) throws AppException {
		User user = userHelper.getUserByUserId(userExternalId);
		if (user == null) {
			throw new AppException("No such user exists");
		}
		user.setStatusMessage(statusMessage);
		return convertUser(user);
	}

	public UserDomain getUserByExternalId(String userExternalId)
			throws AppException {
		User user = userHelper.getUserByUserId(userExternalId);
		return convertUser(user);
	}

	public UserDomain updateUserPhotoUrls(String userExternalId,
			String smallImageUrl, String mediumImageUrl, String largeImageUrl)
			throws AppException {
		User user = userHelper.getUserByUserId(userExternalId);
		if (user == null) {
			throw new AppException("No such user exists");
		}
		user.setSmallImageUrl(smallImageUrl);
		user.setMediumImageUrl(mediumImageUrl);
		user.setLargeImageUrl(largeImageUrl);
		user = userHelper.updateUser(user);
		return convertUser(user);
	}
	public void rebuildCache(UserLocationCache userLocationCache) throws AppException{
		int i=1;
		int pageSize = 100;
		userLocationCache.clearCache();
		while(true){
			HibernateMapPageInfo pageInfo = new HibernateMapPageInfo();
			pageInfo.setPageNo(i);
			pageInfo.setPageSize(pageSize);
			pageInfo.addOrderBy("User.id", ORDER.ASC);
			PageResult<User> users = userHelper.searchUsers(pageInfo);
			if(users.getResultList() == null || users.getResultList().size() == 0){
				break;
			}
			for(User oneUser:users.getResultList()){
				userLocationCache.registerUser(oneUser);
			}
			i++;
		}
	}

	private User convertUser(UserDomain userDto) throws InternalAppException {
		User user = new User();
		try {
			BeanUtils.copyProperties(user, userDto);
		} catch (Exception e) {
			throw new InternalAppException(e);
		}
		return user;
	}

	private UserDomain convertUser(User user) throws InternalAppException {
		if(user == null){
			return null;
		}
		UserDomain userDto = new UserDto();
		try {
			BeanUtils.copyProperties(userDto, user);
		} catch (Exception e) {
			throw new InternalAppException(e);
		}
		return user;
	}

	public UserDomain loginUser(String email, String nickName, String password)
			throws AppException {
		User user = userHelper.getUserByEmailId(email);
		if(user != null){
			if(!user.getNickName().equalsIgnoreCase(nickName) || user.getPassword().equals(password)){
				throw new AppException("Login Failed. Incorrect email/nickanme/password");
			}
		}else{
			throw new AppException("Login Failed. Incorrect email/nickanme/password");
		}
		return convertUser(user);
	}

}
