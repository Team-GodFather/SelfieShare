package com.godfather.selfieshare;

public class HomeActivity extends BaseActivity {
    @Override
    protected String getActivityTitle() {
        return this.getString(R.string.title_activity_home);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_home;
    }
}
