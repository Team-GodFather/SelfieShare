package com.godfather.selfieshare;


public class RequestedActivity extends BaseActivity {
	@Override
	protected String getActivityTitle() {
		return this.getString(R.string.title_activity_requested);
	}

	@Override
	protected int getActivityLayout() {
		return R.layout.activity_requested;
	}
}
