package com.next.infotech.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.next.core.exception.AppException;
import com.next.infotech.concurrent.CounterManager;
import com.next.infotech.concurrent.CounterNames;
import com.service.chataround.dto.HasError;

public class BaseController {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected CounterManager counterManager;

	public class ErrorMessage implements HasError{
		private String responseStatus;
		private String serverMessage;
		public ErrorMessage(String responseStatus, String message) {
			super();
			this.responseStatus = responseStatus;
			this.serverMessage = message;
		}
		public String getResponseStatus() {
			return responseStatus;
		}
		public void setResponseStatus(String responseStatus) {
			this.responseStatus = responseStatus;
		}
		public String getServerMessage() {
			return serverMessage;
		}
		public void setServerMessage(String serverMessage) {
			this.serverMessage = serverMessage;
		}
		
	}
	
	@ExceptionHandler(AppException.class)
	@ResponseBody
	 public ErrorMessage handleAppException(AppException ex, HttpServletRequest request) {
     logger.error("Unable to server your request(App Exception)", ex);
     counterManager.incrementCounter(CounterNames.USER_REQUEST_FAILED_APP);
	  return new ErrorMessage("Error",ex.getMessage());
	 }
	@ExceptionHandler(Exception.class)
	@ResponseBody
	 public ErrorMessage handleException(Exception ex, HttpServletRequest request) {
		logger.error("Unable to server your request(Internal Server Exception)", ex);
		counterManager.incrementCounter(CounterNames.USER_REQUEST_FAILED_INTERNAL);
     	return new ErrorMessage("InternalError","Unable to server your request(Internal Server Exception)");
	 }
	
}
