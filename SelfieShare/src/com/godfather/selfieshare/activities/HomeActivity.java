package com.godfather.selfieshare.activities;

import android.app.ProgressDialog;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.controllers.Message;
import com.godfather.selfieshare.data.QueryExecutor;

public class HomeActivity extends BaseActivity<HomeActivity> {
	private Message message;
	private QueryExecutor queryExecutor;
	private ProgressDialog connectionProgressDialog;

	protected void create() {
		this.message = new Message(this);
		this.queryExecutor = QueryExecutor.getInstance();
		this.connectionProgressDialog = new ProgressDialog(this);
		this.connectionProgressDialog.setMessage("Logging out...");

//		Fragment fr;
//		fr = new MenuFragment();
//
//		FragmentManager fm = getFragmentManager();
//
//		FragmentTransaction fragmentTransaction = fm.beginTransaction();
//
//		fragmentTransaction.add(R.id.fragment_place, fr);
//
//		fragmentTransaction.commit();

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
