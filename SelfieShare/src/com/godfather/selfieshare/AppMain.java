package com.godfather.selfieshare;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;

import android.os.IBinder;
import com.godfather.selfieshare.controllers.CurrentLocationListener;
import com.godfather.selfieshare.utils.MusicService;

public class AppMain extends Application {
    private MusicService mService;
    private boolean mBound = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup Location Service
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        CurrentLocationListener.getInstance().setLocationManager(locationManager);

        // Music Background Service
        Intent intent = new Intent(this, MusicService.class);
        this.bindService(intent, this.mConnection, this.BIND_AUTO_CREATE);
    }

    @Override
    public void onTerminate() {
        this.unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.loadSong(R.raw.selfie);
            mService.playSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
