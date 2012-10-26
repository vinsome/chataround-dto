package com.next.infotech.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import com.next.infotech.cache.UserLocationCache;
import com.next.infotech.persistance.domain.UserCacheDomain;
import com.next.infotech.web.dto.impl.RegisterUserRequestDto;
import com.next.infotech.web.dto.impl.UserPingRequestDto;
import com.next.infotech.web.dto.impl.UserStatusUpdateDto;

@Controller
public class ChatController extends BaseController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserLocationCache userLocationCache;
	
	@RequestMapping(value="/api/1.0/pinglocation", method = RequestMethod.GET)
    @ResponseBody
	public void pingUserLocation(@RequestParam("lat") Double lattitude,@RequestParam("long") Double longitude,@RequestParam("nn") String nickName,@RequestParam("uid") long userId) throws AppException{
		userLocationCache.updateUserLocation(userId, lattitude, longitude);
		
	}
	
	
	@RequestMapping(value="/api/1.0/pinglocationandgetuserold", method = RequestMethod.GET)
    @ResponseBody
	public List<UserCacheDomain> pingUserLocationAndGetUserList(@RequestParam("lat") Double lattitude,@RequestParam("long") Double longitude,@RequestParam("nn") String nickName,@RequestParam("uid") long userId) throws AppException{
		userLocationCache.updateUserLocation(userId, lattitude, longitude);
		return userLocationCache.getUsersNearMe(lattitude, longitude);
	}
	
	@RequestMapping(value="/api/1.0/pinglocationandgetuser", method = RequestMethod.POST)
    @ResponseBody
	public List<UserCacheDomain> pingUserLocationAndGetUserListPost(@RequestBody UserPingRequestDto userPingRequest) throws AppException{
		userLocationCache.updateUserLocation(userPingRequest.getId(), userPingRequest.getLattitude(), userPingRequest.getLongitude());
		return userLocationCache.getUsersNearMe(userPingRequest.getLattitude(), userPingRequest.getLongitude());
	}
	
	@RequestMapping(value="/api/1.0/registeruser", method = RequestMethod.POST)
    @ResponseBody
	public RegisterUserRequestDto registerUser(@RequestBody RegisterUserRequestDto registerUserRequest) throws AppException{
		userLocationCache.registerUser(registerUserRequest);
		return registerUserRequest;
	}
	@RequestMapping(value="/api/1.0/updateuserstatus", method = RequestMethod.POST)
    @ResponseBody
	public void updateUserStatus(@RequestBody UserStatusUpdateDto userStatusUpdateDto) throws AppException{
		userLocationCache.updateUserStatus(userStatusUpdateDto.getId(),userStatusUpdateDto.getStatus());
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
