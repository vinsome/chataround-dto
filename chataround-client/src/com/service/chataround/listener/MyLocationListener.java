package com.service.chataround.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.service.chataround.ChatAroundActivity;
import com.service.chataround.R;
import com.service.chataround.event.LocationChangeEvent;
import com.service.chataround.util.ChatUtils;
import com.service.chataround.util.LocationCacheUtil;

public class MyLocationListener implements LocationListener {
	private EventBus eventBus;

	public static int PERMISSION_DENIED = 1;
	public static int POSITION_UNAVAILABLE = 2;
	public static int TIMEOUT = 3;
	public static String TAG = MyLocationListener.class.getName();
	protected LocationManager locationManager;
	protected boolean running = false;
	private Context ctx;
	private BigDecimal currentLatitude;
	private BigDecimal currentLongitude;
	private String currentGrid;
	private boolean registeredOnline;
	private String userId;
	private boolean paused;
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

	public void onProviderDisabled(String provider) {
		Log.d(TAG, "Location provider '" + provider + "' disabled.");
	}

	public void onProviderEnabled(String provider) {
		Log.d(TAG, "Location provider " + provider + " has been enabled");
	}

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

	public void onLocationChanged(Location location) {
		BigDecimal latitude = new BigDecimal(location.getLatitude()).setScale(
				2, RoundingMode.HALF_UP);
		BigDecimal longitude = new BigDecimal(location.getLongitude())
				.setScale(2, RoundingMode.HALF_UP);
		boolean pingAgain = true;
		//if at least we have a current position...check if we have moved from that one
		String newGrid = LocationCacheUtil.
		getGridKey(latitude.doubleValue(), longitude.doubleValue());
		
		if (currentLatitude != null && currentLongitude != null && StringUtils.hasText(currentGrid)) {

		}
		
		//update position values
		this.currentGrid=newGrid;
		this.currentLatitude = latitude;
		this.currentLongitude = longitude;
		//logic to ping again if we are already registered...
		if (pingAgain && userId!=null && !"".equals(userId)) {
			LocationChangeEvent event = new LocationChangeEvent(latitude,
					longitude);
			event.setRegisteredToServer(registeredOnline);
			event.setUserId(userId);
			eventBus.post(event);
		}

	}

	public void start() {
		if(!paused){
		if (!this.running) {
			if (this.locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
				this.running = true;
				Log.d(TAG, "using gps");
				this.locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 100000, 10, this); // //1
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
						LocationManager.NETWORK_PROVIDER, 100000, 10, this);// 1
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
	}

	public void doStart() {
		start();
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

	public BigDecimal getCurrentLongitude() {
		return currentLongitude;
	}

	public void setCurrentLongitude(BigDecimal currentLongitude) {
		this.currentLongitude = currentLongitude;
	}

	public BigDecimal getCurrentLatitude() {
		return currentLatitude;
	}

	public void setCurrentLatitude(BigDecimal currentLatitude) {
		this.currentLatitude = currentLatitude;
	}

	public String getCurrentGrid() {
		return currentGrid;
	}

	public void setCurrentGrid(String currentGrid) {
		this.currentGrid = currentGrid;
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
}
