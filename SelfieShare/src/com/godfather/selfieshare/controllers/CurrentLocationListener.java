package com.godfather.selfieshare.controllers;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.telerik.everlive.sdk.core.model.system.GeoPoint;

public class CurrentLocationListener  implements LocationListener{
	private static CurrentLocationListener currentLocationListener;
	public Context context;
	
	private GeoPoint location = null;
	
	private CurrentLocationListener() {
	}
	
	
	public static CurrentLocationListener getInstance() {
		if(currentLocationListener == null) {
			currentLocationListener = new CurrentLocationListener();
		}
		
		return currentLocationListener;
	}
	
	public GeoPoint getLocation() {
		return location;
	}


	private void setLocation(GeoPoint location) {
		this.location = location;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		 int lat = (int) (location.getLatitude() * 1E6);
	     int lng = (int) (location.getLongitude() * 1E6);
	     GeoPoint point = new GeoPoint(lat, lng);
	     Toast.makeText(context, lat + " " + lng, Toast.LENGTH_LONG)
			.show();
	     
		this.setLocation(point);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		this.location = null;
		currentLocationListener = null;
	}
}
