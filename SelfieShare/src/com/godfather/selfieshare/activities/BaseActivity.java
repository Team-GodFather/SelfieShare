package com.godfather.selfieshare.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.godfather.selfieshare.AppMain;
import com.godfather.selfieshare.R;
import com.godfather.selfieshare.controllers.CurrentLocationListener;
import com.godfather.selfieshare.utils.ActivityUtils;

public abstract class BaseActivity<TActivity> extends FragmentActivity  {
    protected AppMain appMain;

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

        appMain = (AppMain)this.getApplicationContext();

        String appName = this.getString(R.string.app_name);
        String activityTitle = ActivityUtils.getActivityTitle(appName, this.getActivityTitle());
        this.setTitle(activityTitle);

        this.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clearReferences();
        this.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        clearReferences();
        this.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        appMain.setCurrentActivity(this);
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

    private void clearReferences(){
        Activity currActivity = appMain.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            appMain.setCurrentActivity(null);
    }
}
