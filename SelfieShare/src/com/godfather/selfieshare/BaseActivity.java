package com.godfather.selfieshare;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.godfather.selfieshare.utils.ActivityUtils;
import com.godfather.selfieshare.utils.MusicService;
import com.godfather.selfieshare.utils.MusicService.LocalBinder;

public abstract class BaseActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getActivityLayout());

        String appName = this.getString(R.string.app_name);
        String activityTitle = ActivityUtils.getActivityTitle(appName, this.getActivityTitle());
        this.setTitle(activityTitle);

        this.onCreate();
    }

    // Abstract
    protected abstract String getActivityTitle();
    protected abstract int getActivityLayout();

    // Virtual
    protected void onCreate() {}
}
