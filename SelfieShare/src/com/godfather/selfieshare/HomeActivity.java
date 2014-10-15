package com.godfather.selfieshare;

import android.app.ProgressDialog;
import com.godfather.selfieshare.controllers.Message;
import com.godfather.selfieshare.data.QueryExecutor;

public class HomeActivity extends BaseActivity {
    @Override
    protected String getActivityTitle() {
        return this.getString(R.string.title_activity_home);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_home;
    }


    private Message message;
	private QueryExecutor queryExecutor;
	private ProgressDialog connectionProgressDialog;
	
	@Override
	protected void onCreate() {
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
	}
    
}
