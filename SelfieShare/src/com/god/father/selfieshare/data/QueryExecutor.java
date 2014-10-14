package com.god.father.selfieshare.data;

import com.telerik.everlive.sdk.core.EverliveApp;
import com.telerik.everlive.sdk.core.model.system.User;
import com.telerik.everlive.sdk.core.query.definition.UserSecretInfo;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class QueryExecutor {
	private static QueryExecutor queryExecutor;
	
	private EverliveApp everlive;
	
	private QueryExecutor() {
		this.everlive = new EverliveApp("vzK1tHn68Q4BHHux");
	}
	
	public static QueryExecutor getInstance() {
		if (queryExecutor == null) {
			queryExecutor = new QueryExecutor();
		}
		
		return queryExecutor;
	}
	
	public void registerUser(String username, String password)
	{
	   User user = new User();
	   user.setUsername(username);
	   UserSecretInfo secretInfo = new UserSecretInfo();
	   secretInfo.setPassword(password); 
	   this.everlive.workWith().users().create(user, secretInfo).executeAsync();
	}
	
	@SuppressWarnings("rawtypes")
	public void loginUser(String username, String password,RequestResultCallbackAction callback)
	{
	    this.everlive.workWith().authentication().login(username, password)
	    .executeAsync(callback);
	}
	
//	public void getMyUsername() {
//	    User myUser = (User)this.everlive.workWith().users().getMe().executeSync();
//	    Log.i("App_name", "My Username: " + myUser.getUsername());
//	}
}
