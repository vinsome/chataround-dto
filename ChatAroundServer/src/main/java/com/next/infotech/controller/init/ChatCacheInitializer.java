package com.next.infotech.controller.init;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.next.core.db.page.MapPageInfo.ORDER;
import com.next.core.db.page.PageResult;
import com.next.core.exception.AppException;
import com.next.ext.core.db.page.HibernateMapPageInfo;
import com.next.infotech.cache.UserLocationCache;
import com.next.infotech.persistance.helper.jpa.impl.UserHelper;
import com.next.infotech.persistance.jpa.impl.User;

@Component
public class ChatCacheInitializer {

	@Autowired
	private UserLocationCache userLocationCache;
	@Autowired
	private UserHelper userHelper;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PostConstruct
	public void initCache(){
		try {
			logger.info("Rebuilding User Cache in Memory - Start");
			rebuildCache();
			logger.info("Rebuilding User Cache in Memory - Done");
		} catch (AppException e) {
			logger.error("Unable to rebuild cache on server startup", e);
		}
	}
	public void rebuildCache() throws AppException{
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
}
