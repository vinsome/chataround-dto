package com.next.infotech.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatMessageRejectedExecutionHandler implements
		RejectedExecutionHandler {

	private final Logger logger = LoggerFactory.getLogger(ChatMessageRejectedExecutionHandler.class);
	public void rejectedExecution(Runnable r, ThreadPoolExecutor threadPoolExecutor) {
		// TODO Auto-generated method stub
		try {
			threadPoolExecutor.getQueue().put(r);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		} catch(Exception ex){
			logger.error(ex.getMessage(),ex);
		}

	}

}
