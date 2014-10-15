package com.godfather.selfieshare.data;

import java.util.ArrayList;

import com.godfather.selfieshare.models.SelfieUser;
import com.telerik.everlive.sdk.core.EverliveApp;
import com.telerik.everlive.sdk.core.query.definition.UserSecretInfo;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class QueryExecutor {
	private static QueryExecutor queryExecutor;

	private final String EVERLIVE_KEY = "vzK1tHn68Q4BHHux";
	
	private EverliveApp everlive;

	private QueryExecutor() {
		this.everlive = new EverliveApp(EVERLIVE_KEY);
	}

	public static QueryExecutor getInstance() {
		if (queryExecutor == null) {
			queryExecutor = new QueryExecutor();
		}

		return queryExecutor;
	}

	@SuppressWarnings("rawtypes")
	public void registerUser(SelfieUser user, String password,
			RequestResultCallbackAction callback) {
		UserSecretInfo userSecretInfo = new UserSecretInfo();
		userSecretInfo.setPassword(password);

		this.everlive.workWith().users(SelfieUser.class)
				.create(user, userSecretInfo).executeAsync(callback);
	}

	@SuppressWarnings("rawtypes")
	public void loginUser(String username, String password,
			RequestResultCallbackAction callback) {
		this.everlive.workWith().authentication().login(username, password)
				.executeAsync(callback);
	}
	
	public void logoutUser() {
		this.everlive.workWith().authentication().logout().executeAsync(null);
	}
	
	public void getAllUsernames(RequestResultCallbackAction<ArrayList<SelfieUser>> callback) {
		this.everlive.workWith().users(SelfieUser.class).getAll().executeAsync(callback);
	}
	

	// public void getMyUsername() {
	// User myUser =
	// (User)this.everlive.workWith().users().getMe().executeSync();
	// Log.i("App_name", "My Username: " + myUser.getUsername());
	// }
}
