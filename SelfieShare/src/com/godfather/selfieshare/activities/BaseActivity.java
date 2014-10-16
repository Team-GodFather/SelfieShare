package com.godfather.selfieshare.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.controllers.CurrentLocationListener;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.utils.ActivityUtils;
import com.telerik.everlive.sdk.core.model.system.GeoPoint;

public abstract class BaseActivity<TActivity> extends FragmentActivity  {

    protected static final CurrentLocationListener currentLocationListener = CurrentLocationListener.getInstance();

    protected final TActivity activity;
    
    @SuppressWarnings("unchecked")
    protected BaseActivity() {
        this.activity = (TActivity) this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getActivityLayout());

        String appName = this.getString(R.string.app_name);
        String activityTitle = ActivityUtils.getActivityTitle(appName, this.getActivityTitle());
        this.setTitle(activityTitle);

        this.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get user position
        GeoPoint currentLocation = BaseActivity.currentLocationListener.getLocation();
        QueryExecutor queryExecutor = QueryExecutor.getInstance();
        queryExecutor.updateCurrentUserLocation(currentLocation);

        this.resume();
    }

    // Abstract
    protected abstract String getActivityTitle();

    protected abstract int getActivityLayout();

    // Virtual
    protected void create() {
    }

    protected void resume() {
    }

    protected void pause() {
    }

    protected void destroy() {
    }
}
