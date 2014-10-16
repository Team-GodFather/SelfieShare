package com.godfather.selfieshare.activities;


import com.godfather.selfieshare.R;

public class RequestedActivity extends BaseActivity<RequestedActivity> {
	@Override
	protected String getActivityTitle() {
		return this.getString(R.string.title_activity_requested);
	}

	@Override
	protected int getActivityLayout() {
		return R.layout.activity_requested;
	}
}
