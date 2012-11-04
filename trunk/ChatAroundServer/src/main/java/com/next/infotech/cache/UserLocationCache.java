package com.next.infotech.cache;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.next.core.exception.AppException;
import com.next.infotech.persistance.domain.UserCacheDomain;
import com.next.infotech.persistance.domain.UserPublicDomain;
import com.next.infotech.persistance.helper.jpa.impl.UserHelper;
import com.next.infotech.web.dto.UserCacheDto;
import com.service.chataround.dto.chat.UserPublicDto;

@Component
public class UserLocationCache {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	private ConcurrentHashMap<String, Set<UserCacheDomain>> userCacheByLocation = new ConcurrentHashMap<String, Set<UserCacheDomain>>(10000);
	private ConcurrentHashMap<String, UserCacheDomain> allLoggedInUsers = new ConcurrentHashMap<String, UserCacheDomain>(10000);
	 // Number of degrees in each increment (box) in the grid
    static final double GRID_BOX_SIZE_IN_DEGREE = 0.5;
    
    AtomicLong id = new AtomicLong(1);
    
    private static final ThreadLocal<DecimalFormat> GRID_KEY_FORMATTER = new ThreadLocal<DecimalFormat>() {
        public DecimalFormat initialValue() {
            return new DecimalFormat("0.0");
        }
    };
    public Set<String> getAllLocationKeys(){
    	return userCacheByLocation.keySet();
    }
    public Collection<UserCacheDomain> getAllUsers(){
    	return allLoggedInUsers.values();
    }
    public void registerUser(UserCacheDomain userCacheDomain) throws AppException{
    	UserCacheDomain cachedUSer = new UserCacheDto(userCacheDomain);
    	cachedUSer.setLattitude(0.0);
    	cachedUSer.setLongitude(0.0);
    	allLoggedInUsers.put(cachedUSer.getUserId(), cachedUSer);
    	updateUserLocation(cachedUSer.getUserId(), userCacheDomain.getLattitude(), userCacheDomain.getLongitude());
    }
    public void updateUserStatus(String userId,String statusMessage) throws AppException{
		UserCacheDomain user = allLoggedInUsers.get(userId);
		if(user == null){
			throw new AppException("No user found [id="+ userId +"]");
		}
		user.setStatusMessage(statusMessage);
    }
    public void onlineUser(Long userId) throws AppException{
		UserCacheDomain user = allLoggedInUsers.get(userId);
		if(user == null){
			throw new AppException("No user found [id="+ userId +"]");
		}
		//Create Grid key
		String previosuLocationGridKey = getGridKey(user.getLattitude(), user.getLongitude());
		
		Set<UserCacheDomain> previousUserSet = getUserSetByLocation(previosuLocationGridKey,false);
		previousUserSet.remove(user);
    }
    public void offlineUser(Long userId) throws AppException{
		UserCacheDomain user = allLoggedInUsers.get(userId);
		if(user == null){
			throw new AppException("No user found [id="+ userId +"]");
		}
		//Create Grid key
		String previosuLocationGridKey = getGridKey(user.getLattitude(), user.getLongitude());
		
		Set<UserCacheDomain> previousUserSet = getUserSetByLocation(previosuLocationGridKey,false);
		previousUserSet.remove(user);
    }
	public void updateUserLocation(String userId,Double lattitude,Double longitude) throws AppException{
		UserCacheDomain user = allLoggedInUsers.get(userId);
		String previosuLocationGridKey = "";
		if(user == null){
			throw new AppException("No user found [id="+ userId +"]");
		}else{
			previosuLocationGridKey = getGridKey(user.getLattitude(), user.getLongitude());
			logger.info("previosuLocationGridKey={}",previosuLocationGridKey);
			//also update user's current Location
			user.setLattitude(lattitude);
			user.setLongitude(longitude);
		}
		//Create Grid key
		String currentLocationGridKey = getGridKey(lattitude, longitude);
		logger.info("currentLocationGridKey={}",currentLocationGridKey);
		
		if(currentLocationGridKey.equals(previosuLocationGridKey)){
			//User Location hasn't changed, so do nothing and return
			//i.e. user is still in the same boundary
			logger.info("");
			return;
		}
		//find out the previous location set for this User
		Set<UserCacheDomain> currentUserSetByLocation = getUserSetByLocation(currentLocationGridKey,true);
		Set<UserCacheDomain> previousUserSet = getUserSetByLocation(previosuLocationGridKey,false);
		if(previousUserSet != null){
			//Remove user from previous Location Set
			previousUserSet.remove(user);
			if(previousUserSet.isEmpty()){
				userCacheByLocation.remove(previosuLocationGridKey);
			}
		}
		//and add it to new one
		currentUserSetByLocation.add(user);
		
	}
	public List<UserPublicDomain> getUsersNearMe(Double latitude,Double longitude,String userId){
		//Create Grid key
		String gridKey = getGridKey(latitude, longitude);
		logger.info("gridKey = {}",gridKey);
		Set<UserCacheDomain> currentUserSetByLocation = userCacheByLocation.get(gridKey);
		logger.info("currentUserSetByLocation = {}",currentUserSetByLocation);
		logger.info("userCacheByLocation = {}",userCacheByLocation);
		if(currentUserSetByLocation == null){
			return Collections.EMPTY_LIST;
		}
		return convertUserList(currentUserSetByLocation,userId);
	}
	private List<UserPublicDomain> convertUserList(Collection<UserCacheDomain> userList,String userId){
		List<UserPublicDomain> convertedList = new ArrayList<UserPublicDomain>();
		for(UserCacheDomain oneUser:userList){
			if(oneUser.getUserId().equals(userId)){
				continue;
			}
			convertedList.add(new UserPublicDto(oneUser));
		}
		return convertedList;
	}

	final Set<UserCacheDomain> getUserSetByLocation(String gridKey,boolean createNewIfNotFound){
		Set<UserCacheDomain> currentUserSetByLocation = userCacheByLocation.get(gridKey);
		if(createNewIfNotFound){
			if(currentUserSetByLocation == null){
				currentUserSetByLocation = new LinkedHashSet<UserCacheDomain>();
				Set<UserCacheDomain> exisitnCurrentUserSetByLocation = userCacheByLocation.putIfAbsent(gridKey, currentUserSetByLocation);
				if(exisitnCurrentUserSetByLocation != null){
					currentUserSetByLocation = exisitnCurrentUserSetByLocation;
				}
			}
		}
		return currentUserSetByLocation;
	}
	static String getGridKey(double latitude, double longitude) {
        double keyLat = roundDownToNearestLeftTopCorner(latitude, GRID_BOX_SIZE_IN_DEGREE);
        double keyLon = roundDownToNearestLeftTopCorner(longitude, GRID_BOX_SIZE_IN_DEGREE);
        /*
        if (keyLat >= 90.0) {
            keyLat = 90.0 - GRID_BOX_SIZE_IN_DEGREE;
        } else if (keyLat < -90.0) {
            keyLat = -90.0;
        }
        if (keyLon >= 180.0) {
            keyLon = -180.0;
        }
        */
        
        DecimalFormat fmt = GRID_KEY_FORMATTER.get();
        return fmt.format(keyLat) + "X" + fmt.format(keyLon);
    }
	
	 static double roundDownToNearestLeftTopCorner(double value, double multiple) {
        double mod = value % multiple;
        if (mod == 0) {
            return value;
        } else if (value > 0.0) {
            return value - mod;
        } else {
            return value - mod - multiple;
        }
    }
	 
	 public static void main(String[] args){
		 System.out.println("1.2, 2.6 = "+getGridKey(1.2, 2.6));
		 System.out.println("1.8, 2.4 = "+getGridKey(1.8, 2.4));
		 System.out.println("91.2, 92.6 = "+getGridKey(91.2, 92.6));
		 System.out.println("91.8, 92.4 = "+getGridKey(91.8, 92.4));
		 System.out.println("181.2, 182.6 = "+getGridKey(181.2, 182.6));
		 System.out.println("181.8, 182.4 = "+getGridKey(181.8, 182.4));
		 System.out.println("-91.2, -92.6 = "+getGridKey(-91.2, -92.6));
		 System.out.println("-99.8, -92.6 = "+getGridKey(-99.8, -92.4));
		 System.out.println("-181.2, -182.6 = "+getGridKey(-181.2, -182.6));
		 System.out.println("-181.8, -182.4 = "+getGridKey(-181.8, -182.4));

	 }
}
