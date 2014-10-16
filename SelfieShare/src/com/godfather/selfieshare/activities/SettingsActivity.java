package com.godfather.selfieshare.activities;


import com.godfather.selfieshare.R;

public class SettingsActivity extends BaseActivity<SettingsActivity> {
	@Override
	protected String getActivityTitle() {
		return this.getString(R.string.title_activity_settings);
	}

	@Override
	protected int getActivityLayout() {
		return R.layout.activity_settings;
	}
}
