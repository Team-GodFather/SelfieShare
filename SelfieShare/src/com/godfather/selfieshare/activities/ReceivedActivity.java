package com.godfather.selfieshare.activities;


import com.godfather.selfieshare.R;

public class ReceivedActivity extends BaseActivity {
	   @Override
	    protected String getActivityTitle() {
	        return this.getString(R.string.title_activity_received);
	    }

	    @Override
	    protected int getActivityLayout() {
	        return R.layout.activity_received;
	    }

}
