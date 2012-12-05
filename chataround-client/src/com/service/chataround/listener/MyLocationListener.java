package com.service.chataround.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.service.chataround.event.LocationChangeEvent;
import com.service.chataround.util.LocationCacheUtil;

public class MyLocationListener implements LocationListener {
	private EventBus eventBus;
	private static final int TWO_MINUTES = 1000 * 60 * 1;
	public static int PERMISSION_DENIED = 1;
	public static int POSITION_UNAVAILABLE = 2;
	public static int TIMEOUT = 3;
	public static String TAG = MyLocationListener.class.getName();
	protected LocationManager locationManager;
	protected boolean running = false;
	private Context ctx;
	private boolean registeredOnline;
	private String userId;
	private boolean paused;
	private Location currentBestLocation;
	
	public MyLocationListener(){
		
	}
	public MyLocationListener(LocationManager locationManager, Context ctx,
			EventBus eventBus,boolean registeredOnline,String userId) {
		this.locationManager = locationManager;
		this.ctx = ctx;
		this.eventBus = eventBus;
		this.userId = userId;
		this.registeredOnline = registeredOnline;
	}
	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "Location provider '" + provider + "' disabled.");
	}
	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "Location provider " + provider + " has been enabled");
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "The status of the provider " + provider + " has changed");
		if (status == 0) {
			Log.d(TAG, provider + " is OUT OF SERVICE");
		} else if (status == 1) {
			Log.d(TAG, provider + " is TEMPORARILY_UNAVAILABLE");
		} else {
			Log.d(TAG, provider + " is AVAILABLE");
		}
	}
	@Override
	public void onLocationChanged(Location location) {
		if(currentBestLocation!=null){
			if(isBetterLocation(location,currentBestLocation)){
				currentBestLocation=location;	
				notifyEvent(location);
			}
		}else{
			currentBestLocation=location;
			notifyEvent(location);
		}
	}
	
	
	private void notifyEvent(Location location){
		BigDecimal latitude = new BigDecimal(location.getLatitude()).setScale(
				2, RoundingMode.HALF_UP);
		BigDecimal longitude = new BigDecimal(location.getLongitude())
				.setScale(2, RoundingMode.HALF_UP);
		//if at least we have a current position...check if we have moved from that one
		
		//logic to ping again if we are already registered...
		if (userId!=null && !"".equals(userId)) {
			LocationChangeEvent event = new LocationChangeEvent(latitude,
					longitude);
			event.setRegisteredToServer(registeredOnline);
			event.setUserId(userId);
			eventBus.post(event);
		}		
	}
	
	public void doStart() {
		if (this.locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
			Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //<5>
		    if (location != null) {
		      Log.d(TAG, location.toString());
		      this.onLocationChanged(location); //
		    }
		    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TWO_MINUTES, 0, this);
		}
		if (this.locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //<5>
		    if (location != null) {
		      Log.d(TAG, location.toString());
		      this.onLocationChanged(location); //
		    }
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TWO_MINUTES, 0, this);
		}
		
		
	}
	
	public void startt() {
		//if(!paused){
		if (!this.running) {
			if (this.locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
				this.running = true;
				Log.d(TAG, "using gps");
				this.locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, TWO_MINUTES, 0, this); // //1
																			// *
																			// 60
																			// *
																			// 1000
																			// (1
																			// minutes)
																			// and
																			// X
																			// metres
			} else {
				Log.d(TAG, "GPS provider is not available.");
			}
		}
		running = false;
		if (!this.running) {
			if (this.locationManager
					.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
				this.running = true;
				Log.d(TAG, "using network");
				this.locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, TWO_MINUTES, 0, this);// 1
																			// *
																			// 60
																			// *
																			// 1000
																			// (1
																			// minutes)
																			// and
																			// X
																			// metres
			} else {
				Log.d(TAG, "Network provider is not available.");
			}
		}
		}
	//}

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	private void stop() {
		if (this.running) {
			this.locationManager.removeUpdates(this);
			this.running = false;
		}
	}

	/**
	 * Destroy listener.
	 */
	public void destroy() {
		this.stop();
	}

	public EventBus getEventBus() {
		return eventBus;
	}
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public Context getCtx() {
		return ctx;
	}
	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
	public boolean isRegisteredOnline() {
		return registeredOnline;
	}
	public void setRegisteredOnline(boolean registeredOnline) {
		this.registeredOnline = registeredOnline;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public LocationManager getLocationManager() {
		return locationManager;
	}
	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}
	public boolean isPaused() {
		return paused;
	}
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	public Location getCurrentBestLocation() {
		return currentBestLocation;
	}
	public void setCurrentBestLocation(Location currentBestLocation) {
		this.currentBestLocation = currentBestLocation;
	}
}
