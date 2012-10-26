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

import org.springframework.stereotype.Component;

import com.next.core.exception.AppException;
import com.next.infotech.persistance.domain.UserCacheDomain;

@Component
public class UserLocationCache {

	private ConcurrentHashMap<String, Set<UserCacheDomain>> userCacheByLocation = new ConcurrentHashMap<String, Set<UserCacheDomain>>(10000);
	private ConcurrentHashMap<Long, UserCacheDomain> allLoggedInUsers = new ConcurrentHashMap<Long, UserCacheDomain>(10000);
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
    	for(UserCacheDomain oneUserCacheDomain:allLoggedInUsers.values()){
    		if(oneUserCacheDomain.getNickName().equalsIgnoreCase(userCacheDomain.getNickName())){
    			throw new AppException("Nickname "+ userCacheDomain.getNickName() +" is alreadu used by some one else");
    		}
    	}
    	userCacheDomain.setId(id.addAndGet(1));
    	allLoggedInUsers.put(userCacheDomain.getId(), userCacheDomain);
    	updateUserLocation(userCacheDomain.getId(), userCacheDomain.getLattitude(), userCacheDomain.getLongitude());
    }
    public void updateUserStatus(Long userId,String statusMessage) throws AppException{
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
	public void updateUserLocation(Long userId,Double lattitude,Double longitude) throws AppException{
		UserCacheDomain user = allLoggedInUsers.get(userId);
		String previosuLocationGridKey = "";
		if(user == null){
			//If no user found then create new one or get from Database
			/*
			user = new User();
			user.setId(userId);
			user.setNickName(nickName);
			user.setLattitude(lattitude);
			user.setLongitude(longitude);
			allLoggedInUsers.put(userId, user);
			*/
			throw new AppException("No user found [id="+ userId +"]");
		}else{
			previosuLocationGridKey = getGridKey(user.getLattitude(), user.getLongitude());
			//also update user's current Location
			user.setLattitude(lattitude);
			user.setLongitude(longitude);
		}
		//Create Grid key
		String currentLocationGridKey = getGridKey(lattitude, longitude);
		
		if(currentLocationGridKey.equals(previosuLocationGridKey)){
			//User Location hasn't changed, so do nothing and return
			//i.e. user is still in the same boundary
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
	public List<UserCacheDomain> getUsersNearMe(Double latitude,Double longitude){
		//Create Grid key
		String gridKey = getGridKey(latitude, longitude);
		Set<UserCacheDomain> currentUserSetByLocation = userCacheByLocation.get(gridKey);
		if(currentUserSetByLocation == null){
			return Collections.EMPTY_LIST;
		}
		return new ArrayList<UserCacheDomain>(currentUserSetByLocation);
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
