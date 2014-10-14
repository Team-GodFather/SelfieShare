package com.god.father.selfieshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.god.father.selfieshare.controllers.LoginValidator;
import com.god.father.selfieshare.controllers.Message;
import com.god.father.selfieshare.data.QueryExecutor;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class MainActivity extends Activity {
	private final String LOGIN_SUCCESSFUL = "Successful login!";
	private final String LOGIN_FAILED = "Incorrect username or password!";
	private final String LOGIN_VALIDATION = "Please enter correct data in the fields!";
	
	private Message message;
	private QueryExecutor queryExecutor;
	private ProgressDialog connectionProgressDialog;
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

	}

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
