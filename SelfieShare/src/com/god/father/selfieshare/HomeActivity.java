package com.god.father.selfieshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.god.father.selfieshare.controllers.Message;
import com.god.father.selfieshare.data.QueryExecutor;

public class HomeActivity extends Activity {

	private Message message;
	private QueryExecutor queryExecutor;
	private ProgressDialog connectionProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		this.message = new Message(this);
		this.queryExecutor = QueryExecutor.getInstance();
		this.connectionProgressDialog = new ProgressDialog(this);
		this.connectionProgressDialog.setMessage("Logging out...");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		queryExecutor.logoutUser();
		
//		Intent intent = new Intent(HomeActivity.this, MainActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//		startActivity(intent);
	}

}
