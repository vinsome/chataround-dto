package com.next.infotech.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.next.core.exception.AppException;

public class BaseController {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public class ErrorMessage{
		private String responseStatus;
		private String message;
		public ErrorMessage(String responseStatus, String message) {
			super();
			this.responseStatus = responseStatus;
			this.message = message;
		}
		public String getResponseStatus() {
			return responseStatus;
		}
		public void setResponseStatus(String responseStatus) {
			this.responseStatus = responseStatus;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	@ExceptionHandler(AppException.class)
	@ResponseBody
	 public ErrorMessage handleAppException(AppException ex, HttpServletRequest request) {
     logger.error("Unable to server your request(App Exception)", ex);
	  return new ErrorMessage("Error",ex.getMessage());
	 }
	@ExceptionHandler(Exception.class)
	@ResponseBody
	 public ErrorMessage handleException(Exception ex, HttpServletRequest request) {
		logger.error("Unable to server your request(Internal Server Exception)", ex);
     	return new ErrorMessage("InternalError","Unable to server your request(Internal Server Exception)");
	 }
	
}
