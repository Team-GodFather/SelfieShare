package com.godfather.selfieshare.data;

import java.util.ArrayList;
import java.util.UUID;

import com.godfather.selfieshare.models.SelfieUser;
import com.telerik.everlive.sdk.core.EverliveApp;
import com.telerik.everlive.sdk.core.model.system.GeoPoint;
import com.telerik.everlive.sdk.core.model.system.User;
import com.telerik.everlive.sdk.core.query.definition.UserSecretInfo;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class QueryExecutor {
    private static final String EVERLIVE_KEY = "vzK1tHn68Q4BHHux";
    private static EverliveApp app;

    private static QueryExecutor queryExecutor;
    private static User authentication;
    private static SelfieUser currentUser;

    private static RequestResultCallbackAction<User> userCallbackAction = new RequestResultCallbackAction<User>() {
        @Override
        public void invoke(RequestResult<User> requestResult) {
            final boolean hasErrors = !requestResult.getSuccess();
            if (!hasErrors) {
                authentication = requestResult.getValue();
                app.workWith().data(SelfieUser.class)
                        .getById(authentication.getId())
                        .executeAsync(selfieUserCallbackAction);
            }
        }
    };

    private static RequestResultCallbackAction<SelfieUser> selfieUserCallbackAction = new RequestResultCallbackAction<SelfieUser>() {
        @Override
        public void invoke(RequestResult<SelfieUser> requestResult) {
            final boolean hasErrors = !requestResult.getSuccess();
            if (!hasErrors) {
                currentUser = requestResult.getValue();
            }
        }
    };

    private QueryExecutor() {
        app = new EverliveApp(EVERLIVE_KEY);
    }

    public static QueryExecutor getInstance() {
        if (queryExecutor == null) {
            queryExecutor = new QueryExecutor();
        }

        return queryExecutor;
    }

    public SelfieUser getCurrentUser() {
        return this.getCurrentUser();
    }

    public void setUser(RequestResultCallbackAction<User> callbackAction) {
        app.workWith().users().getMe().executeAsync(callbackAction);
    }

    public void updateCurrentUserLocation(final GeoPoint location) {
        if (this.currentUser != null) {
            currentUser.setLocation(location);
            this.updateUser(currentUser, currentUser.getId());
        }
    }

    public void updateUser(SelfieUser updatedUser, UUID userId) {
        app.workWith().data(SelfieUser.class).updateById(userId, updatedUser).executeAsync();
    }

    @SuppressWarnings("rawtypes")
    public void registerUser(SelfieUser user, String password, RequestResultCallbackAction callback) {
        UserSecretInfo userSecretInfo = new UserSecretInfo();
        userSecretInfo.setPassword(password);

        this.app.workWith().users(SelfieUser.class)
                .create(user, userSecretInfo).executeAsync(callback);
    }

    @SuppressWarnings("rawtypes")
    public void loginUser(String username, String password, final RequestResultCallbackAction callback) {
        this.app.workWith().authentication().login(username, password)
                .executeAsync(new RequestResultCallbackAction<Object>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void invoke(RequestResult<Object> requestResult) {
                        final boolean hasErrors = !requestResult.getSuccess();
                        if (!hasErrors) {
                            setUser(userCallbackAction);
                        }

                        callback.invoke(requestResult);
                    }
                });
    }

    public void logoutUser() {
        this.app.workWith().authentication().logout().executeAsync(null);
    }

    public void getAllUserNames(RequestResultCallbackAction<ArrayList<SelfieUser>> callback) {
        this.app.workWith().users(SelfieUser.class).getAll().executeAsync(callback);
    }


    // public void getMyUsername() {
    // User myUser =
    // (User)this.app.workWith().users().getMe().executeSync();
    // Log.i("App_name", "My Username: " + myUser.getUsername());
    // }
}

