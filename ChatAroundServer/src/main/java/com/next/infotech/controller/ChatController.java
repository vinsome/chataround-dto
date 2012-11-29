package com.next.infotech.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.next.core.exception.AppException;
import com.next.infotech.cache.UserLocationCache;
import com.next.infotech.concurrent.CounterNames;
import com.next.infotech.concurrent.QueueManager;
import com.next.infotech.persistance.domain.UserCacheDomain;
import com.next.infotech.persistance.domain.UserDomain;
import com.next.infotech.persistance.services.ChatAroundServices;
import com.service.chataround.dto.chat.ChatMessageDto;
import com.service.chataround.dto.chat.ChatMessageInternalDto;
import com.service.chataround.dto.chat.ChatMessageResponseDto;
import com.service.chataround.dto.chat.ChatMessageResponseDto.MessageStatus;
import com.service.chataround.dto.chat.LoginDto;
import com.service.chataround.dto.chat.OfflineResponseDto;
import com.service.chataround.dto.chat.UserPingRequestDto;
import com.service.chataround.dto.chat.UserPingResponseDto;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.dto.chat.UserStatusUpdateDto;
import com.service.chataround.dto.chat.UserStatusUpdateResponseDto;
import com.service.chataround.dto.register.RegisterUserRequestDto;

@Controller
public class ChatController extends BaseController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ChatAroundServices chatAroundServices;
	@Autowired
	private UserLocationCache userLocationCache;
	@Autowired
	private QueueManager queueManager;

	@RequestMapping(value="/api/1.0/pinglocationandgetuser", method = RequestMethod.POST)
    @ResponseBody
	public UserPingResponseDto pingUserLocationAndGetUserListPost(@RequestBody UserPingRequestDto userPingRequest) throws AppException{
		counterManager.incrementCounter(CounterNames.PING_REQUEST);
		userLocationCache.updateUserLocation(userPingRequest.getUserId(), userPingRequest.getLattitude(), userPingRequest.getLongitude());
		UserPingResponseDto userPingResponseDto = new UserPingResponseDto();
		userPingResponseDto.setLattitude(userPingRequest.getLattitude());
		userPingResponseDto.setLongitude(userPingRequest.getLongitude());
		userPingResponseDto.setUserId(userPingRequest.getUserId());
		userPingResponseDto.setUserList(userLocationCache.getUsersNearMe(userPingRequest.getLattitude(), userPingRequest.getLongitude(),userPingRequest.getUserId()));
		return userPingResponseDto;
	}
	
	
	@RequestMapping(value="/api/1.0/registeruser", method = RequestMethod.POST)
    @ResponseBody
	public RegisterUserRequestDto registerUser(@RequestBody RegisterUserRequestDto registerUserRequestDto) throws AppException{
		counterManager.incrementCounter(CounterNames.REGISTER_USER_REQUEST);
		UserDomain user = chatAroundServices.createUser(registerUserRequestDto);
		userLocationCache.registerUser(user);
		registerUserRequestDto.setUserId(user.getUserId());
		//mask the password
		registerUserRequestDto.setPassword("*****");
		return registerUserRequestDto;
		 
		 
	}
	
	@RequestMapping(value="/api/1.0/updateuserstatus", method = RequestMethod.POST)
    @ResponseBody
	public UserStatusUpdateResponseDto updateUserStatus(@RequestBody UserStatusUpdateDto userStatusUpdateDto) throws AppException{
		counterManager.incrementCounter(CounterNames.UPDATE_USER_STATUS_REQUEST);
		chatAroundServices.updateUserStatus(userStatusUpdateDto.getUserId(), userStatusUpdateDto.getStatus());
		userLocationCache.updateUserStatus(userStatusUpdateDto.getUserId(),userStatusUpdateDto.getStatus());
		UserStatusUpdateResponseDto userStatusUpdateResponseDto =  new UserStatusUpdateResponseDto();
		userStatusUpdateResponseDto.setStatus(userStatusUpdateDto.getStatus());
		userStatusUpdateResponseDto.setUserId(userStatusUpdateDto.getUserId());
		return userStatusUpdateResponseDto;
	}
	
	@RequestMapping(value="/api/1.0/ofline/{userId}", method = RequestMethod.POST)
    @ResponseBody
	public OfflineResponseDto offlineUser(@PathVariable String userId) throws AppException{
		counterManager.incrementCounter(CounterNames.OFFLINE_USER_REQUEST);
		userLocationCache.offlineUser(userId);
		OfflineResponseDto offlineResponseDto = new OfflineResponseDto();
		offlineResponseDto.setUserOfflineStatus("Success");
		return offlineResponseDto;
	}
	
	@RequestMapping(value="/api/1.0/login", method = RequestMethod.POST)
    @ResponseBody
	public UserPublicDto loginUser(@RequestBody LoginDto loginDto) throws AppException{
		counterManager.incrementCounter(CounterNames.LOGIN_USER_REQUEST);
		UserDomain user = chatAroundServices.loginUser(loginDto.getEmail(), loginDto.getNickname(), loginDto.getPassword());
		userLocationCache.registerUser(user);
		return new UserPublicDto(user);
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
		String systemVariable = System.getenv("VCAP_SERVICES");
		System.out.println(systemVariable);
		logger.info(systemVariable);
		return "showusermap";
	}
	
	@RequestMapping(value="/api/1.0/viewinfo", method = RequestMethod.GET)
	public String viewInfo(HttpServletRequest request,Model model) throws AppException{
		String bs = request.getParameter("bs");
		if(bs != null && !bs.trim().equals("")){
			try{
				Double boxSize = Double.parseDouble(bs);
				userLocationCache.setGridBoxSize(boxSize);
				chatAroundServices.rebuildCache(userLocationCache);
			}catch(Exception ex){
				model.addAttribute("Error", "Unable to parse BoxSize(bs)");		
			}
		}

		model.addAttribute("BoxSizeInDegree", userLocationCache.getGridBoxSize());
		model.addAttribute("BoxSizeInKm", userLocationCache.getGridBoxSizeInKm());
		model.addAttribute("TotalUsers", userLocationCache.getTotalUser());
		model.addAttribute("TotalLocations", userLocationCache.getTotalLocaltionBoxes());
		Map<String, Long> counterValues = counterManager.getCounters();
		model.addAttribute("counterMap", counterValues);
		
		return "showinfo";
	}
	
	@RequestMapping(value="/api/1.0/sendchatmessage", method = RequestMethod.POST)
    @ResponseBody
	public ChatMessageResponseDto sendChatMessage(@RequestBody ChatMessageDto chatMessageDto) throws AppException{
		counterManager.incrementCounter(CounterNames.CHAT_MESSAGE_REQUEST);
		ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto();
		UserCacheDomain receipientUser = userLocationCache.getUserByExternalId(chatMessageDto.getRecipientId());
		chatMessageResponseDto.setId(chatMessageDto.getId());
		if(receipientUser == null){
			counterManager.incrementCounter(CounterNames.CHAT_MESSAGE_FAILED);
			chatMessageResponseDto.setStatus(MessageStatus.Failed);
			chatMessageResponseDto.setMessage("User is offline");
		}else{
			UserCacheDomain senderUser = userLocationCache.getUserByExternalId(chatMessageDto.getSenderId());
			if(senderUser == null){
				//This should not happen, even if it happens try top find this user from DB and save it in cache
				UserDomain user = chatAroundServices.getUserByExternalId(chatMessageDto.getSenderId());
				if(user != null){
					userLocationCache.registerUser(user);
					senderUser = user;
				}
			}
			//If sender not found in cache and DB then raise the error
			if(senderUser == null){
				counterManager.incrementCounter(CounterNames.CHAT_MESSAGE_FAILED);
				chatMessageResponseDto.setStatus(MessageStatus.Failed);
				chatMessageResponseDto.setMessage("Invalid User");
			}else{
				//else send the message
				chatMessageResponseDto.setStatus(MessageStatus.Pending);
				ChatMessageInternalDto chatMessageInternalDto = new ChatMessageInternalDto(chatMessageDto);
				chatMessageInternalDto.setReceipientDeviceId(receipientUser.getDeviceId());
				chatMessageInternalDto.setSenderDeviceId(senderUser.getDeviceId());
				//and send this message to Luis's App Engine controller using thread pool in async way
				queueManager.addChatMessageToQueue(chatMessageInternalDto);
			}
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
