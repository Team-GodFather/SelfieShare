package com.godfather.selfieshare.activities;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.controllers.Message;
import com.godfather.selfieshare.controllers.SignUpValidator;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.SelfieUser;
import com.godfather.selfieshare.models.SexType;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class SignUpActivity extends BaseActivity<SignUpActivity> implements
		Button.OnClickListener {
	private static final String WRONG_PASSWORDS_INPUT = "The passwords don't match!";
	private static final String SHORT_PASSWORD = "Password is too short!";
	private static final String SHORT_USERNAME = "Username is too short!";
	private static final String REGISTRATION_SUCCESS = "You have been registered!";
	private static final String REGISTRATION_FILED = "Your registration is rejected!";
	private static final String AGE_NOT_IN_RANGE = "Age must be positive number!";
	private static final String PHONE_NUMBER_NOT_IN_RANGE = "Phone number must be positive!";

	private Message message;
	private QueryExecutor queryExecutor;
	private ProgressDialog connectionProgressDialog;
	private SexType sex;
	private EditText signUpUsername;
	private EditText signUpPassword;
	private EditText signUpConfirmPassword;
	private EditText signUpAge;
	private EditText signUpPhoneNumber;

	@Override
	protected int getActivityLayout() {
		return R.layout.activity_sign_up;
	}

	@Override
	protected void create() {
		this.message = new Message(this);
		this.queryExecutor = QueryExecutor.getInstance();
		this.sex = SexType.Male;
		this.connectionProgressDialog = new ProgressDialog(this);
		this.connectionProgressDialog.setMessage("Registering...");

		this.signUpUsername = (EditText) findViewById(R.id.signUpUsername);
		this.signUpPassword = (EditText) findViewById(R.id.signUpPassword);
		this.signUpConfirmPassword = (EditText) findViewById(R.id.signUpConfirmPassword);
		this.signUpAge = (EditText) findViewById(R.id.signUpAge);
		this.signUpPhoneNumber = (EditText) findViewById(R.id.signUpPhoneNumber);

		// Bind click listeners
		this.findViewById(R.id.signUpSexMale).setOnClickListener(this);
		this.findViewById(R.id.signUpSexFemale).setOnClickListener(this);
		this.findViewById(R.id.signUpBtn).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.signUpSexMale:
		case R.id.signUpSexFemale:
			this.chooseSex(view);
			break;
		case R.id.signUpBtn:
			this.register(view);
			break;
		}
	}

	public void chooseSex(View view) {
		switch (view.getId()) {
		case R.id.signUpSexMale:
			this.sex = SexType.Male;
			break;
		case R.id.signUpSexFemale:
			this.sex = SexType.Female;
			break;
		}
	}

	public void register(View view) {
		SelfieUser user = new SelfieUser();
		String username = this.signUpUsername.getText().toString();
		String password = this.signUpPassword.getText().toString();
		String confirmPassword = this.signUpConfirmPassword.getText()
				.toString();
		String age = this.signUpAge.getText().toString();
		String phoneNumber = this.signUpPhoneNumber.getText().toString();

		StringBuilder stringBuilder = new StringBuilder();

		if (!SignUpValidator.validateUsername(username)) {
			appendLine(stringBuilder, SHORT_USERNAME);
		}

		if (!SignUpValidator.validatePassword(password)) {
			appendLine(stringBuilder, SHORT_PASSWORD);
		}

		if (!SignUpValidator.validatePasswords(password, confirmPassword)) {
			appendLine(stringBuilder, WRONG_PASSWORDS_INPUT);
		}

		if (!SignUpValidator.validateAge(age)) {
			appendLine(stringBuilder, AGE_NOT_IN_RANGE);
		}

		if (!SignUpValidator.validatePhoneNumber(phoneNumber)) {
			appendLine(stringBuilder, PHONE_NUMBER_NOT_IN_RANGE);
		}

		if (stringBuilder.length() > 0) {
			stringBuilder.setLength(stringBuilder.length() - 1);
		}

		String errors = stringBuilder.toString();
		if (errors.equals("")) {
			user.setUsername(username);
			user.setAge(Integer.parseInt(age));
			user.setSex(sex.ordinal());
			user.setPhoneNumber(Long.parseLong(phoneNumber));
			user.setLocation(BaseActivity.currentLocationListener.getLocation());

			this.connectionProgressDialog.show();
			this.queryExecutor.registerUser(user, password, signUpThread());
		} else {
			this.message.print(errors);
		}
	}

	public RequestResultCallbackAction<?> signUpThread() {
		return new RequestResultCallbackAction<Object>() {
			@Override
			public void invoke(RequestResult<Object> requestResult) {
				final boolean hasErrors = !requestResult.getSuccess();

				SignUpActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.connectionProgressDialog.dismiss();

						if (!hasErrors) {
							activity.message.print(REGISTRATION_SUCCESS);

							activity.finish();
						} else {
							activity.message.print(REGISTRATION_FILED);
						}
					}
				});
			}
		};
	}

	private void appendLine(StringBuilder stringBuilder, String text) {
		stringBuilder.append(text).append('\n');
	}
}
