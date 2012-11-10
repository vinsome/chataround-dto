package com.next.infotech.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.next.core.exception.AppException;
import com.next.core.exception.InternalAppException;
import com.next.infotech.cache.UserLocationCache;
import com.next.infotech.concurrent.QueueManager;
import com.next.infotech.persistance.domain.UserCacheDomain;
import com.next.infotech.persistance.domain.UserPublicDomain;
import com.next.infotech.persistance.helper.jpa.impl.UserHelper;
import com.next.infotech.persistance.jpa.impl.User;
import com.service.chataround.dto.chat.ChatMessageDto;
import com.service.chataround.dto.chat.ChatMessageInternalDto;
import com.service.chataround.dto.chat.ChatMessageResponseDto;
import com.service.chataround.dto.chat.UserPingRequestDto;
import com.service.chataround.dto.chat.UserStatusUpdateDto;
import com.service.chataround.dto.chat.ChatMessageResponseDto.MessageStatus;
import com.service.chataround.dto.register.RegisterUserRequestDto;

@Controller
public class ChatController extends BaseController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserLocationCache userLocationCache;
	@Autowired
	private UserHelper userHelper;
	@Autowired
	private QueueManager queueManager;

	@RequestMapping(value="/api/1.0/pinglocation", method = RequestMethod.GET)
    @ResponseBody
	public void pingUserLocation(@RequestParam("lat") Double lattitude,@RequestParam("long") Double longitude,@RequestParam("nn") String nickName,@RequestParam("uid") String userId) throws AppException{
		userLocationCache.updateUserLocation(userId, lattitude, longitude);
		
	}
	
	@RequestMapping(value="/api/1.0/pinglocationandgetuserold", method = RequestMethod.GET)
    @ResponseBody
	public List<UserPublicDomain> pingUserLocationAndGetUserList(@RequestParam("lat") Double lattitude,@RequestParam("long") Double longitude,@RequestParam("nn") String nickName,@RequestParam("uid") String userId) throws AppException{
		userLocationCache.updateUserLocation(userId, lattitude, longitude);
		return userLocationCache.getUsersNearMe(lattitude, longitude,userId);
	}
	@RequestMapping(value="/api/1.0/pinglocationandgetuser", method = RequestMethod.POST)
    @ResponseBody
	public List<UserPublicDomain> pingUserLocationAndGetUserListPost(@RequestBody UserPingRequestDto userPingRequest) throws AppException{
		userLocationCache.updateUserLocation(userPingRequest.getUserId(), userPingRequest.getLattitude(), userPingRequest.getLongitude());
		return userLocationCache.getUsersNearMe(userPingRequest.getLattitude(), userPingRequest.getLongitude(),userPingRequest.getUserId());
	}
	
	@RequestMapping(value="/api/1.0/registeruser", method = RequestMethod.POST)
    @ResponseBody
	public RegisterUserRequestDto registerUser(@RequestBody RegisterUserRequestDto registerUserRequest) throws AppException{
		User user = convertUser(registerUserRequest);
		user = userHelper.createUser(user);
		userLocationCache.registerUser(user);
		registerUserRequest.setUserId(user.getUserId());
		registerUserRequest.setPassword("*****");
		return registerUserRequest;
	}
	private User convertUser(RegisterUserRequestDto registerUserRequest) throws InternalAppException{
		User user = new User();
		try {
			BeanUtils.copyProperties(user,registerUserRequest);
		} catch (Exception e) {
			throw new InternalAppException(e);
		}
		return user;
	}
	@RequestMapping(value="/api/1.0/updateuserstatus", method = RequestMethod.POST)
    @ResponseBody
	public void updateUserStatus(@RequestBody UserStatusUpdateDto userStatusUpdateDto) throws AppException{
		userHelper.updateUserStatus(userStatusUpdateDto.getUserId(),userStatusUpdateDto.getStatus());
		userLocationCache.updateUserStatus(userStatusUpdateDto.getUserId(),userStatusUpdateDto.getStatus());
	}
	
	@RequestMapping(value="/api/1.0/ofline/{userId}", method = RequestMethod.POST)
    @ResponseBody
	public void offlineUser(@PathVariable Long userId) throws AppException{
		userLocationCache.offlineUser(userId);
	}
	
	@RequestMapping(value="/api/1.0/viewusermap", method = RequestMethod.GET)
	public String viewUserMap(HttpServletRequest request,Model model) throws AppException{
		Collection<UserCacheDomain> allUsers = userLocationCache.getAllUsers();
		Set<String> allLocationKeys = userLocationCache.getAllLocationKeys();
		List<GridRectangle> allRectangles = new ArrayList<ChatController.GridRectangle>(allLocationKeys.size());
		double lattitude;
		double longitude;
		GridRectangle oneGridRectangle;
		for(String oneKey:allLocationKeys){
			String[] dividedString = oneKey.split("X");
			lattitude = Double.parseDouble(dividedString[0]);
			longitude = Double.parseDouble(dividedString[1]);
			oneGridRectangle = new GridRectangle();
			oneGridRectangle.setTopLattitude(lattitude);
			oneGridRectangle.setTopLongitude(longitude);
			oneGridRectangle.setBottomLattitude(lattitude + 1.0);
			oneGridRectangle.setBottomLongitude(longitude + 1.0);
			allRectangles.add(oneGridRectangle);
		}
		model.addAttribute("AllUsers", allUsers);
		model.addAttribute("AllRectangles", allRectangles);
		return "showusermap";
	}
	
	@RequestMapping(value="/api/1.0/sendchatmessage", method = RequestMethod.POST)
    @ResponseBody
	public ChatMessageResponseDto sendChatMessage(@RequestBody ChatMessageDto chatMessageDto) throws AppException{
		ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto();
		UserCacheDomain receipientUser = userLocationCache.getUserByExternalId(chatMessageDto.getRecipientId());
		if(receipientUser == null){
			chatMessageResponseDto.setStatus(MessageStatus.Failed);
			chatMessageResponseDto.setMessage("User is offline");
		}else{
			chatMessageResponseDto.setStatus(MessageStatus.Pending);
			ChatMessageInternalDto chatMessageInternalDto = new ChatMessageInternalDto(chatMessageDto);
			chatMessageInternalDto.setReceipientDeviceId(receipientUser.getDeviceId());
			//and send this message to Luis's App Engine controller using thread pool in async way
			queueManager.addChatMessageToQueue(chatMessageInternalDto);
		}
		return chatMessageResponseDto;
	}
	
	
	
	public static class GridRectangle{
		private double topLongitude;
		private double topLattitude;
		private double bottomLongitude;
		private double bottomLattitude;
		
		public double getTopLongitude() {
			return topLongitude;
		}
		public void setTopLongitude(double topLongitude) {
			this.topLongitude = topLongitude;
		}
		public double getTopLattitude() {
			return topLattitude;
		}
		public void setTopLattitude(double topLattitude) {
			this.topLattitude = topLattitude;
		}
		public double getBottomLongitude() {
			return bottomLongitude;
		}
		public void setBottomLongitude(double bottomLongitude) {
			this.bottomLongitude = bottomLongitude;
		}
		public double getBottomLattitude() {
			return bottomLattitude;
		}
		public void setBottomLattitude(double bottomLattitude) {
			this.bottomLattitude = bottomLattitude;
		}


	}
	
}
