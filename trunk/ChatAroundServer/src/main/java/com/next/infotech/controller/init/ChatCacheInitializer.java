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
import com.next.infotech.persistance.services.ChatAroundServices;

@Component
public class ChatCacheInitializer {

	@Autowired
	private UserLocationCache userLocationCache;
	@Autowired
	private ChatAroundServices chatAroundServices;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PostConstruct
	public void initCache(){
		try {
			logger.info("Rebuilding User Cache in Memory - Start");
			chatAroundServices.rebuildCache(userLocationCache);
			logger.info("Rebuilding User Cache in Memory - Done");
		} catch (AppException e) {
			logger.error("Unable to rebuild cache on server startup", e);
		}
	}
	
}
