package com.godfather.selfieshare.activities;

import android.app.ProgressDialog;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.data.QueryExecutor;

public class HomeActivity extends BaseActivity<HomeActivity> {

	private QueryExecutor queryExecutor;
	private ProgressDialog connectionProgressDialog;


	protected void create() {
		this.queryExecutor = QueryExecutor.getInstance();
		this.connectionProgressDialog = new ProgressDialog(this);
		this.connectionProgressDialog.setMessage("Logging out...");
	}

	protected String getActivityTitle() {
		return this.getString(R.string.title_activity_home);
	}

	protected int getActivityLayout() {
		return R.layout.activity_home;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		queryExecutor.logoutUser();
	}
}
