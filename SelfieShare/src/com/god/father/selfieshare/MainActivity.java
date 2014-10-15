package com.god.father.selfieshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;

import com.god.father.selfieshare.controllers.LoginValidator;
import com.god.father.selfieshare.controllers.Message;
import com.god.father.selfieshare.data.QueryExecutor;
import com.god.father.selfieshare.tasks.MusicService;
import com.god.father.selfieshare.tasks.MusicService.LocalBinder;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class MainActivity extends Activity {
	private final String LOGIN_SUCCESSFUL = "Successful login!";
	private final String LOGIN_FAILED = "Incorrect username or password!";
	private final String LOGIN_VALIDATION = "Please enter correct data in the fields!";

	private Message message;
	private QueryExecutor queryExecutor;
	private ProgressDialog connectionProgressDialog;
	private MusicService mService;
	boolean mBound = false;

	private EditText loginUsername;
	private EditText loginPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.message = new Message(this);
		this.queryExecutor = QueryExecutor.getInstance();
		this.connectionProgressDialog = new ProgressDialog(this);
		this.connectionProgressDialog.setMessage("Logging in ...");

		this.loginUsername = (EditText) findViewById(R.id.loginUsername);
		this.loginPassword = (EditText) findViewById(R.id.loginPassword);

		Intent intent = new Intent(MainActivity.this, MusicService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			mService.loadSong(R.raw.selfie);
			mService.playSong();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	public void login(View view) {
		String username = loginUsername.getText().toString();
		String password = loginPassword.getText().toString();

		if (LoginValidator.validate(username, password)) {
			connectionProgressDialog.show();
			queryExecutor.loginUser(username, password, loginThread());
		} else {
			message.print(LOGIN_VALIDATION);
		}
	}

	public RequestResultCallbackAction<?> loginThread() {
		return new RequestResultCallbackAction<Object>() {
			@Override
			public void invoke(RequestResult<Object> requestResult) {
				final boolean hasErrors;

				if (requestResult.getSuccess()) {
					hasErrors = false;
				} else {
					hasErrors = true;
				}

				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						connectionProgressDialog.dismiss();

						if (!hasErrors) {
							message.print(LOGIN_SUCCESSFUL);

							Intent intent = new Intent(MainActivity.this,
									HomeActivity.class);
							//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent);
						} else {
							message.print(LOGIN_FAILED);
						}
					}
				});
			}
		};
	}

	public void register(View view) {
		Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
		startActivity(intent);
	}
}
