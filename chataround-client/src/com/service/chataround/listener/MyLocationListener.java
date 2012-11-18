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

public class MyLocationListener implements LocationListener {
	private EventBus eventBus;
	
	public static int PERMISSION_DENIED = 1;
	public static int POSITION_UNAVAILABLE = 2;
	public static int TIMEOUT = 3;
	public static String TAG="TAG";
	protected LocationManager locationManager;
	protected boolean running = false;
	private Context ctx;
	
	public MyLocationListener(LocationManager locationManager,Context ctx,EventBus eventBus) {
		this.locationManager = locationManager;
		this.ctx=ctx;
		this.eventBus=eventBus;
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
		BigDecimal latitude = new BigDecimal(location.getLatitude()).setScale(2, RoundingMode.HALF_UP);
		BigDecimal longitude = new BigDecimal(location.getLongitude()).setScale(2, RoundingMode.HALF_UP);
		LocationChangeEvent event = new LocationChangeEvent(latitude,longitude);
		eventBus.post(event);
		/*
		if(!isRegisteredToServer&& StringUtils.hasText(nickName)) {
			//register to server!
			RegisterUserRequestDto dto = new RegisterUserRequestDto();
				dto.setDeviceId(regId);
				dto.setEmail("email");
				dto.setLattitude(latitude.doubleValue());
				dto.setLongitude(longitude.doubleValue());
				dto.setNickName(nickName);
				dto.setPassword("");
				dto.setStatusMessage(mood);
				
			new ChatAroundTask(ctx,null).execute(dto,ChatConstants.REGISTER_SERVER_URL);
				
		}else{
			
		ChatAroundDto dto = new ChatAroundDto();
			dto.setDeviceId(regId);
			dto.setLattitude(String.valueOf(latitude));
			dto.setLongitude(String.valueOf(longitude));
			dto.setNickName(nickName);
			dto.setMood(mood);
		//param based : ("lat") Double lattitude, "long") Double longitude,@RequestParam("nn") String nickName,@RequestParam("uid") 
		new ChatAroundTask(ctx,null).execute(dto,ChatConstants.PING_LOCATION_SERVER_URL);
		
		}
		*/
	}

	public void start() {
		if (!this.running) {
			if (this.locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
				this.running = true;
				Log.d(TAG, "using gps");
				this.locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 100000, 10, this); ////1 * 60 * 1000 (1 minutes)  and X  metres
			} else {
				Log.d(TAG, "GPS provider is not available.");
			}
		}
		running=false;
		if (!this.running) {
			if (this.locationManager
					.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
				this.running = true;
				Log.d(TAG, "using network");
				this.locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 100000, 10, this);//1 * 60 * 1000 (1 minutes)  and X  metres
			} else {
				Log.d(TAG, "Network provider is not available.");
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
}
