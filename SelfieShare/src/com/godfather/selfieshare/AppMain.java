package com.godfather.selfieshare;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;

import android.os.IBinder;
import com.godfather.selfieshare.controllers.CurrentLocationListener;
import com.godfather.selfieshare.controllers.NetworkManager;
import com.godfather.selfieshare.utils.MusicService;

public class AppMain extends Application {
    private MusicService mMusicService;
    private NetworkManager mNetworkManager;

    private boolean mBound = false;
    private Activity mCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup Location Service
        CurrentLocationListener.appContext = this;
        LocationManager locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        CurrentLocationListener.getInstance().setLocationManager(locationManager);

        // Network Service
        this.mNetworkManager = new NetworkManager();
        this.mNetworkManager.initialize(this);

        // Music Background Service
        Intent intent = new Intent(this, MusicService.class);
        this.bindService(intent, this.mConnection, this.BIND_AUTO_CREATE);
    }

    @Override
    public void onTerminate() {
        this.unbindService(mConnection);
        this.mNetworkManager.destroy();
    }

    public Activity getCurrentActivity(){
        return this.mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mMusicService = binder.getService();
            mBound = true;
            mMusicService.loadSong(R.raw.selfie);
            mMusicService.playSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
