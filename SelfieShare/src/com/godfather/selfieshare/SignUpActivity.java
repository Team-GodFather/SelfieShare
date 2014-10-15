package com.godfather.selfieshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.god.father.selfieshare.controllers.CurrentLocationListener;
import com.god.father.selfieshare.controllers.Message;
import com.god.father.selfieshare.controllers.SignUpValidator;
import com.god.father.selfieshare.data.QueryExecutor;
import com.god.father.selfieshare.models.SelfieUser;

import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class SignUpActivity extends BaseActivity {
	private static final String WRONG_PASSWORDS_INPUT = "The passwords don't match!";
	private static final String SHORT_PASSWORD = "Pasword is too short!";
	private static final String SHORT_USERNAME = "Username is too short!";
	private static final String REGISTRATION_SUCCESS = "You have been registered!";
	private static final String REGISTRATION_FILED = "Your registration is rejected!";
	private static final String SIGNIN_VALIDATION = "Please enter correct data in the fields!";
	private static final String AGE_NOT_IN_RANGE = "Age must be positive number!";
	private static final String PHONENUMBER_NOT_IN_RANGE = "Phone number must be positive!";
	private static final String WRONG_SEX_TYPE = "Unknown sex type!";

	private LocationManager locationManager;
	private CurrentLocationListener currentLocationListener;
	private Message message;
	private QueryExecutor queryExecutor;
	private ProgressDialog connectionProgressDialog;
	private String sex;
	private EditText signUpUsername;
	private EditText signUpPassword;
	private EditText signUpConfirmPassword;
	private EditText signUpAge;
	private EditText signUpPhoneNumber;

   @Override
    protected String getActivityTitle() {
        return this.getString(R.string.title_activity_sign_up);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_sign_up;
    }
	

	@Override
	protected void onCreate() {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		this.locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		this.currentLocationListener = CurrentLocationListener.getInstance();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, currentLocationListener);
		
		this.currentLocationListener.context = this;

		this.message = new Message(this);
		this.queryExecutor = QueryExecutor.getInstance();
		this.sex = "";
		this.connectionProgressDialog = new ProgressDialog(this);
		this.connectionProgressDialog.setMessage("Registering...");

		this.signUpUsername = (EditText) findViewById(R.id.signUpUsername);
		this.signUpPassword = (EditText) findViewById(R.id.signUpPassword);
		this.signUpConfirmPassword = (EditText) findViewById(R.id.signUpConfirmPassword);
		this.signUpAge = (EditText) findViewById(R.id.signUpAge);
		this.signUpPhoneNumber = (EditText) findViewById(R.id.signUpPhoneNumber);
	}

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
		case R.id.signUpSexMale:
			if (checked) {
				this.sex = "Male";
			}
			break;
		case R.id.signUpSexFemale:
			if (checked) {
				this.sex = "Female";
			}
			break;
		}
	}

	public void register(View view) {
		boolean correctData = true;
		SelfieUser user = new SelfieUser();
		String username = signUpUsername.getText().toString();
		String password = signUpPassword.getText().toString();
		String confirmPassword = signUpConfirmPassword.getText().toString();

		if (signUpAge.getText().toString().trim().equals("")) {
			signUpAge.setText("0");
		}
		if (signUpPhoneNumber.getText().toString().trim().equals("")) {
			signUpPhoneNumber.setText("0");
		}

		int age = Integer.parseInt(signUpAge.getText().toString());
		long phoneNumber = Long.parseLong(signUpPhoneNumber.getText()
				.toString());

		if (!SignUpValidator.validateRange(username, password, confirmPassword,
				age, sex, phoneNumber)) {
			message.print(SIGNIN_VALIDATION);
			correctData = false;
		}

		if (!SignUpValidator.validateUsername(username)) {
			message.print(SHORT_USERNAME);
			correctData = false;
		}

		if (!SignUpValidator.validatePassword(password)) {
			message.print(SHORT_PASSWORD);
			correctData = false;
		}

		if (!SignUpValidator.validatePasswords(password, confirmPassword)) {
			message.print(WRONG_PASSWORDS_INPUT);
			correctData = false;
		}

		if (!SignUpValidator.validateAge(age)) {
			message.print(AGE_NOT_IN_RANGE);
			correctData = false;
		}

		if (!SignUpValidator.validateSexType(sex)) {
			message.print(WRONG_SEX_TYPE);
			correctData = false;
		}

		if (!SignUpValidator.validatePhoneNumber(phoneNumber)) {
			message.print(PHONENUMBER_NOT_IN_RANGE);
			correctData = false;
		}

		if (correctData) {
			user.setUsername(username);
			user.setAge(age);
			user.setSex(sex);
			user.setPhoneNumber(phoneNumber);

			user.setLocation(currentLocationListener.getLocation());

			this.connectionProgressDialog.show();
			this.queryExecutor.registerUser(user, password, signUpThread());
		}
	}

	public RequestResultCallbackAction<?> signUpThread() {
		return new RequestResultCallbackAction<Object>() {
			@Override
			public void invoke(RequestResult<Object> requestResult) {
				final boolean hasErrors;

				if (requestResult.getSuccess()) {
					hasErrors = false;
				} else {
					hasErrors = true;
				}

				SignUpActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						connectionProgressDialog.dismiss();

						if (!hasErrors) {
							message.print(REGISTRATION_SUCCESS);

							Intent intent = new Intent(SignUpActivity.this,
									MainActivity.class);
							startActivity(intent);
						} else {
							message.print(REGISTRATION_FILED);
						}
					}
				});
			}
		};
	}
}
