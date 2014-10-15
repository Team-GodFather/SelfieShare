package com.godfather.selfieshare;


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
