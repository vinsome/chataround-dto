package com.next.infotech.cache;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.next.core.exception.AppException;
import com.next.infotech.persistance.domain.UserCacheDomain;
import com.next.infotech.web.dto.UserCacheDto;
import com.service.chataround.dto.chat.UserPublicDto;
import com.service.chataround.util.LocationCacheUtil;

@Component
public class UserLocationCache {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConcurrentHashMap<String, Set<UserCacheDomain>> userCacheByLocation = new ConcurrentHashMap<String, Set<UserCacheDomain>>(
			10000);
	private ConcurrentHashMap<String, UserCacheDomain> allLoggedInUsers = new ConcurrentHashMap<String, UserCacheDomain>(
			10000);
	// Number of degrees in each increment (box) in the grid
	static double GRID_BOX_SIZE_IN_DEGREE = LocationCacheUtil.GRID_BOX_SIZE_IN_DEGREE;

	public double getGridBoxSize() {
		return GRID_BOX_SIZE_IN_DEGREE;
	}
	public void setGridBoxSize(double boxSize) {
		GRID_BOX_SIZE_IN_DEGREE = boxSize; 
	}

	public double getGridBoxSizeInKm() {
		double baseLattitude = 40.0;
		double baseLongitude = 40.0;
		return distance(baseLattitude, baseLongitude, baseLattitude + GRID_BOX_SIZE_IN_DEGREE, baseLongitude+GRID_BOX_SIZE_IN_DEGREE, 'K');
	}
	public int getTotalUser(){
		return allLoggedInUsers.size();
	}
	public int getTotalLocaltionBoxes(){
		return userCacheByLocation.size();
	}

	private double distance(double lat1, double lon1, double lat2, double lon2,
			char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	public Set<String> getAllLocationKeys() {
		return userCacheByLocation.keySet();
	}

	public Collection<UserCacheDomain> getAllUsers() {
		return allLoggedInUsers.values();
	}

	public void registerUser(UserCacheDomain userCacheDomain)
			throws AppException {
		UserCacheDomain cachedUSer = new UserCacheDto(userCacheDomain);
		cachedUSer.setLattitude(0.0);
		cachedUSer.setLongitude(0.0);
		allLoggedInUsers.put(cachedUSer.getUserId(), cachedUSer);
		updateUserLocation(cachedUSer.getUserId(),
				userCacheDomain.getLattitude(), userCacheDomain.getLongitude());
	}

	public void updateUserStatus(String userId, String statusMessage)
			throws AppException {
		UserCacheDomain user = allLoggedInUsers.get(userId);
		if (user == null) {
			throw new AppException("No user found [id=" + userId + "]");
		}
		user.setStatusMessage(statusMessage);
	}

	public void onlineUser(Long userId) throws AppException {
		UserCacheDomain user = allLoggedInUsers.get(userId);
		if (user == null) {
			throw new AppException("No user found [id=" + userId + "]");
		}
		// Create Grid key
		String previosuLocationGridKey = LocationCacheUtil.getGridKey(user.getLattitude(),
				user.getLongitude());

		Set<UserCacheDomain> previousUserSet = getUserSetByLocation(
				previosuLocationGridKey, false);
		previousUserSet.remove(user);
	}

	public void offlineUser(Long userId) throws AppException {
		UserCacheDomain user = allLoggedInUsers.get(userId);
		if (user == null) {
			throw new AppException("No user found [id=" + userId + "]");
		}
		// Create Grid key
		String previosuLocationGridKey = LocationCacheUtil.getGridKey(user.getLattitude(),
				user.getLongitude());

		Set<UserCacheDomain> previousUserSet = getUserSetByLocation(
				previosuLocationGridKey, false);
		previousUserSet.remove(user);
	}

	public UserCacheDomain getUserByExternalId(String userId) {
		return allLoggedInUsers.get(userId);
	}

	public void updateUserLocation(String userId, Double lattitude,
			Double longitude) throws AppException {
		UserCacheDomain user = allLoggedInUsers.get(userId);
		String previosuLocationGridKey = "";
		if (user == null) {
			throw new AppException("No user found [id=" + userId + "]");
		} else {
			previosuLocationGridKey = LocationCacheUtil.getGridKey(user.getLattitude(),
					user.getLongitude());
			//logger.info("previosuLocationGridKey={}", previosuLocationGridKey);
			// also update user's current Location
			user.setLattitude(lattitude);
			user.setLongitude(longitude);
		}
		// Create Grid key
		String currentLocationGridKey = LocationCacheUtil.getGridKey(lattitude, longitude);
		//logger.info("currentLocationGridKey={}", currentLocationGridKey);

		if (currentLocationGridKey.equals(previosuLocationGridKey)) {
			// User Location hasn't changed, so do nothing and return
			// i.e. user is still in the same boundary
			logger.info("User location has not been updated but Client has pinged the location.");
			return;
		}
		// find out the previous location set for this User
		Set<UserCacheDomain> currentUserSetByLocation = getUserSetByLocation(
				currentLocationGridKey, true);
		Set<UserCacheDomain> previousUserSet = getUserSetByLocation(
				previosuLocationGridKey, false);
		if (previousUserSet != null) {
			// Remove user from previous Location Set
			previousUserSet.remove(user);
			if (previousUserSet.isEmpty()) {
				userCacheByLocation.remove(previosuLocationGridKey);
			}
		}
		// and add it to new one
		currentUserSetByLocation.add(user);

	}

	public List<UserPublicDto> getUsersNearMe(Double latitude,
			Double longitude, String userId) {
		// Create Grid key
		List<String> gridKeys = LocationCacheUtil.getNearestGridKeys(latitude, longitude,
				GRID_BOX_SIZE_IN_DEGREE);
		logger.info("gridKey = {}", gridKeys);

		Set<UserCacheDomain> allUsersNearmyLocation = new HashSet<UserCacheDomain>();
		Set<UserCacheDomain> currentUserSetByLocation;
		for (String gridKey : gridKeys) {
			currentUserSetByLocation = userCacheByLocation.get(gridKey);
			if (currentUserSetByLocation != null
					&& currentUserSetByLocation.size() > 0) {
				allUsersNearmyLocation.addAll(currentUserSetByLocation);
			}
		}

		return convertUserList(allUsersNearmyLocation, userId);
	}

	private List<UserPublicDto> convertUserList(
			Collection<UserCacheDomain> userList, String userId) {
		List<UserPublicDto> convertedList = new ArrayList<UserPublicDto>();
		for (UserCacheDomain oneUser : userList) {
			if (oneUser.getUserId().equals(userId)) {
				continue;
			}
			convertedList.add(new UserPublicDto(oneUser));
		}
		return convertedList;
	}

	final Set<UserCacheDomain> getUserSetByLocation(String gridKey,
			boolean createNewIfNotFound) {
		Set<UserCacheDomain> currentUserSetByLocation = userCacheByLocation
				.get(gridKey);
		if (createNewIfNotFound) {
			if (currentUserSetByLocation == null) {
				currentUserSetByLocation = new LinkedHashSet<UserCacheDomain>();
				Set<UserCacheDomain> exisitnCurrentUserSetByLocation = userCacheByLocation
						.putIfAbsent(gridKey, currentUserSetByLocation);
				if (exisitnCurrentUserSetByLocation != null) {
					currentUserSetByLocation = exisitnCurrentUserSetByLocation;
				}
			}
		}
		return currentUserSetByLocation;
	}

	

	
	public void clearCache(){
		userCacheByLocation.clear();
		allLoggedInUsers.clear();
	}
	/*
	public static void main(String[] args) {
		System.out.println("1.2, 2.6 = " + getGridKey(1.2, 2.6));
		System.out.println("1.8, 2.4 = " + getGridKey(1.8, 2.4));
		System.out.println("91.2, 92.6 = " + getGridKey(91.2, 92.6));
		System.out.println("91.8, 92.4 = " + getGridKey(91.8, 92.4));
		System.out.println("181.2, 182.6 = " + getGridKey(181.2, 182.6));
		System.out.println("181.8, 182.4 = " + getGridKey(181.8, 182.4));
		System.out.println("-91.2, -92.6 = " + getGridKey(-91.2, -92.6));
		System.out.println("-99.8, -92.6 = " + getGridKey(-99.8, -92.4));
		System.out.println("-181.2, -182.6 = " + getGridKey(-181.2, -182.6));
		System.out.println("-181.8, -182.4 = " + getGridKey(-181.8, -182.4));

	}
	*/
}
