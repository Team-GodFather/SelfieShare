package com.godfather.selfieshare.controllers;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.SelfieUser;
import com.telerik.everlive.sdk.core.model.system.GeoPoint;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class CurrentLocationListener implements LocationListener {
    private static final long LOCATION_REFRESH_TIME = 60000;
    private static final float LOCATION_REFRESH_DISTANCE = 0;

    private static CurrentLocationListener currentLocationListener;

    private GeoPoint location = null;

    private CurrentLocationListener() {
    }


    public static CurrentLocationListener getInstance() {
        if (currentLocationListener == null) {
            currentLocationListener = new CurrentLocationListener();
        }

        return currentLocationListener;
    }

    public void setLocationManager(LocationManager locationManager) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, currentLocationListener);
        currentLocationListener.setLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
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
    }

    @Override
    public void onProviderDisabled(String provider) {
        this.location = null;
        currentLocationListener = null;
    }
}
