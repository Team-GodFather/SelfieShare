package com.godfather.selfieshare;

import android.app.Activity;
import android.os.Bundle;

import com.godfather.selfieshare.utils.ActivityUtils;

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
