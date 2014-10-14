package com.god.father.selfieshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.god.father.selfieshare.data.QueryExecutor;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class MainActivity extends Activity {
	private QueryExecutor queryExecutor = QueryExecutor.getInstance();
	private EditText loginUsername;
	private EditText loginPassword;
	private ProgressDialog connectionProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loginUsername = (EditText) findViewById(R.id.loginUsername);
		loginPassword = (EditText) findViewById(R.id.loginPassword);
		connectionProgressDialog = new ProgressDialog(this);
		connectionProgressDialog.setMessage("Logging in ...");
	}

	@SuppressWarnings("rawtypes")
	public void login(View view) {
		String username = loginUsername.getText().toString();
		String password = loginPassword.getText().toString();
		final Context context = getApplicationContext();

		connectionProgressDialog.show();
		queryExecutor.loginUser(username, password,
				new RequestResultCallbackAction() {
					@Override
					public void invoke(RequestResult requestResult) {
						final boolean hasErrors;
						final int duration = Toast.LENGTH_SHORT;

						if (requestResult.getSuccess()) {

//							CharSequence text = "Successfull login!";
//
//							Toast toast = Toast.makeText(context, text,
//									duration);
//							toast.show();
							hasErrors = false;
						} else {
							hasErrors = true;
						}
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								connectionProgressDialog.dismiss();
								
								if (!hasErrors) {
									CharSequence text = "Success login!";

									Toast toast = Toast.makeText(context, text,
											duration);
									toast.show();
									Intent intent = new Intent(
											MainActivity.this,
											HomeActivity.class);
									startActivity(intent);
								} else {
									CharSequence text = "Pesho login!";

									Toast toast = Toast.makeText(context, text,
											duration);
									toast.show();
								}
							}
						});
					}
				});
	}

	public void register(View view) {
		Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
		startActivity(intent);
	}
}
