package com.godfather.selfieshare.controllers;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.widget.Toast;
import com.godfather.selfieshare.data.QueryExecutor;
import com.telerik.everlive.sdk.core.model.system.GeoPoint;

public class CurrentLocationListener implements LocationListener {
    private static final long LOCATION_REFRESH_TIME = 60000;
    private static final float LOCATION_REFRESH_DISTANCE = 10;

    public static Context appContext;
    public static Boolean isRunning;
    private static LocationManager locationManager;
    private static CurrentLocationListener currentLocationListener;
    private static String currentProvider;

    private GeoPoint location = null;

    private CurrentLocationListener() {
        isRunning = false;
    }


    public static CurrentLocationListener getInstance() {
        if (currentLocationListener == null) {
            currentLocationListener = new CurrentLocationListener();
        }

        return currentLocationListener;
    }

    public void setLocationManager(LocationManager locationMan) {
        locationManager = locationMan;
        this.CheckProvider();
    }

    private void CheckProvider() {
        currentProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? LocationManager.NETWORK_PROVIDER : null;
        if (currentProvider == null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            currentProvider = LocationManager.GPS_PROVIDER;
        }

        if (currentProvider != null) {
            locationManager.requestLocationUpdates(currentProvider, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, currentLocationListener);
            currentLocationListener.setLocation(locationManager.getLastKnownLocation(currentProvider));

            isRunning = true;
        } else {
            String message = "Your gps/network is not turned off.\nYour geolocation will not be sent to server.";
            Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
        }
    }

    public GeoPoint getLocation() {
        return this.location;
    }

    private void setLocation(final GeoPoint location) {
        this.location = location;

        QueryExecutor queryExecutor = QueryExecutor.getInstance();
        queryExecutor.updateCurrentUserLocation(location);
    }

    private void setLocation(Location location) {
        GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());

        this.setLocation(point);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.setLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (currentProvider == null) {
            this.CheckProvider();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (currentProvider.equals(provider)) {
            isRunning = false;
            currentProvider = null;
        }
    }
}
