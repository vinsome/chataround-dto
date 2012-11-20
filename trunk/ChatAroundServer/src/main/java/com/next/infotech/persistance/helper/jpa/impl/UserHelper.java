package com.next.infotech.persistance.helper.jpa.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.next.core.db.page.PageInfo;
import com.next.core.db.page.PageResult;
import com.next.core.exception.AppException;
import com.next.ext.core.db.page.HibernateMapPageInfo;
import com.next.ext.core.persistance.helper.BasePersistanceHelper;
import com.next.infotech.persistance.jpa.impl.User;

/**
 * @author Ravi Sharma
 *
 */
@Component
public class UserHelper extends BasePersistanceHelper<User> {

	@Override
	protected void validateCreateObject(User user)
	throws AppException {
		checkIfStringMissing("Email", user.getEmail());
		checkIfStringMissing("NickName", user.getNickName());
		checkIfObjectMissing("Gender", user.getGender());
		checkUserUniqueAttributes(user);
	}

	@Override
	protected void validateUpdateObject(User user)
	throws AppException {
		checkIfStringMissing("Email", user.getEmail());
		checkIfStringMissing("NickName", user.getNickName());
		checkIfObjectMissing("Gender", user.getGender());
		checkUserUniqueAttributes(user);
	}
	public void updateUserStatus(String userId,String status) throws AppException{
		User existingUser = getUserByUserId(userId);
		if(existingUser == null){
			throw new AppException("No such user exists "+userId);
		}
	}
	private void checkUserUniqueAttributes(User user) throws AppException{
		User existingUser = getUserByEmailId(user.getEmail());
		if(existingUser != null && existingUser.getId() != user.getId()){
			throw new AppException("User already exists with email "+user.getEmail());
		}
		
		existingUser = getUserByNickname(user.getNickName());
		if(existingUser != null && existingUser.getId() != user.getId()){
			throw new AppException("User already exists with nickname "+user.getNickName());
		}
	}

	/**
	 * Creates a user in Database
	 * 
	 * @param user
	 * @return created user
	 * @throws AppException
	 */
	public User createUser(User user) throws AppException
	{
		user = super.createObject(user);
		UUID uniqueUserId = UUID.randomUUID();
		String userId = uniqueUserId.toString() +"-"+user.getId();
		user.setUserId(userId);
		return user;
	}

	/**
	 * Updates a user in Database
	 * 
	 * @param user
	 * @return updated user
	 * @throws AppException
	 */
	public User updateUser(User user) throws AppException
	{
		user = super.updateObject(user);
		return user;
	}

	/**
	 * return a User with given primary key/id
	 * 
	 * @param id
	 * @return User with PK as id(parameter)
	 * @throws AppException
	 */
	public User getUserById(Long id) throws AppException
	{
		User user = (User)super.getObjectByPK(User.class, id);
		return user;
	}

	/**
	 * @param pageInfo
	 * @return search result
	 * @throws AppException
	 */
	public PageResult<User> searchUsers(PageInfo pageInfo) throws AppException
	{
		return super.findObject(User.class, pageInfo);
	}

	/**
	 * @param pageInfo
	 * @return search result
	 * @throws AppException
	 * @throws AppException
	 */
	public User getUserByEmailId(String email) throws AppException {
		HibernateMapPageInfo pageInfo = new HibernateMapPageInfo();
		pageInfo.addCriteria("email", email);
		PageResult<User> pageResult = searchUsers(pageInfo);
		if ((pageResult == null) || (pageResult.getResultList() == null) || (pageResult.getResultList().size() <= 0)) {
			return null;
		}
		if (pageResult.getResultList().size() > 1) {
			throw new AppException("Got more then one user for same Email");
		}
		return pageResult.getResultList().get(0);
	}

	public User getUserByNickname(String nickname) throws AppException {
		HibernateMapPageInfo pageInfo = new HibernateMapPageInfo();
		pageInfo.addCriteria("nickName", nickname);
		PageResult<User> pageResult = searchUsers(pageInfo);
		if ((pageResult == null) || (pageResult.getResultList() == null) || (pageResult.getResultList().size() <= 0)) {
			return null;
		}
		if (pageResult.getResultList().size() > 1) {
			throw new AppException("Got more then one user for same Nickname");
		}
		return pageResult.getResultList().get(0);
	}
	
	public User getUserByUserId(String userId) throws AppException {
		HibernateMapPageInfo pageInfo = new HibernateMapPageInfo();
		pageInfo.addCriteria("userId", userId);
		PageResult<User> pageResult = searchUsers(pageInfo);
		if ((pageResult == null) || (pageResult.getResultList() == null) || (pageResult.getResultList().size() <= 0)) {
			return null;
		}
		if (pageResult.getResultList().size() > 1) {
			throw new AppException("Got more then one user for same Email");
		}
		return pageResult.getResultList().get(0);
	}


}
