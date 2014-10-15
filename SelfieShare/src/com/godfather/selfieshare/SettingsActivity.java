package com.godfather.selfieshare;


public class SettingsActivity extends BaseActivity {
	@Override
	protected String getActivityTitle() {
		return this.getString(R.string.title_activity_settings);
	}

	@Override
	protected int getActivityLayout() {
		return R.layout.activity_settings;
	}
}
