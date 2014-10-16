package com.godfather.selfieshare.activities;

import com.godfather.selfieshare.R;

public class RequestDetailActivity extends BaseActivity<RequestDetailActivity> {

    @Override
    protected String getActivityTitle() {
        return this.getString(R.string.title_activity_request_detail);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_request_detail;
    }
}
