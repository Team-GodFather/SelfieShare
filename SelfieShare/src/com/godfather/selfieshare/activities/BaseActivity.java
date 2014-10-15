package com.godfather.selfieshare.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.utils.ActivityUtils;

public abstract class BaseActivity extends Activity {
    protected final Context context;

    protected BaseActivity() {
        this.context = this;
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
