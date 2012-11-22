package com.next.infotech.concurrent;

public interface CounterNames {

	public static final String PING_REQUEST = "PingRequest";
	public static final String CHAT_MESSAGE_REQUEST = "ChatMessageRequest";
	public static final String REGISTER_USER_REQUEST = "RegisterUserRequest";
	public static final String UPDATE_USER_STATUS_REQUEST = "UpdateUserStatusRequest";
	public static final String OFFLINE_USER_REQUEST = "OfflineUserRequest";
	public static final String CHAT_MESSAGE_FAILED = "ChatMessageFailed";
	public static final String CHAT_MESSAGE_SENT_TO_APP_ENGINE = "ChatMessageSentToAppEngine";
	public static final String CHAT_MESSAGE_FAILED_TOSENT_TO_APP_ENGINE = "ChatMessageFailedToSentToAppEngine";
	
	public static final String USER_REQUEST_FAILED_APP = "UserRequestFailedAppError";
	public static final String USER_REQUEST_FAILED_INTERNAL = "UserRequestFailedInternalError";
	public static final String TOTAL_UPLOAD_IMAGE_REQUEST = "TotalUploadImageRequest";
	public static final String TOTAL_FAILED_UPLOAD_IMAGE_REQUEST = "TotalFailedUploadImageRequest";
	public static final String TOTAL_SUCCESS_UPLOAD_IMAGE_REQUEST = "TotalSuccessUploadImageRequest";
	public static final String TOTAL_USER_THUMBNAIL_REQUEST = "TotalUserThumbNailRequest";
}

