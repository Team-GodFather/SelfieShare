package com.godfather.selfieshare.data;

import java.util.ArrayList;
import java.util.UUID;

import com.godfather.selfieshare.controllers.CurrentLocationListener;
import com.godfather.selfieshare.models.SelfieUser;
import com.godfather.selfieshare.utils.GlobalCallbacks;
import com.telerik.everlive.sdk.core.EverliveApp;
import com.telerik.everlive.sdk.core.model.base.DynamicData;
import com.telerik.everlive.sdk.core.model.system.GeoPoint;
import com.telerik.everlive.sdk.core.model.system.User;
import com.telerik.everlive.sdk.core.query.definition.FieldsDefinition;
import com.telerik.everlive.sdk.core.query.definition.UserSecretInfo;
import com.telerik.everlive.sdk.core.query.definition.filtering.simple.ValueCondition;
import com.telerik.everlive.sdk.core.query.definition.filtering.simple.ValueConditionOperator;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class QueryExecutor {
    private static final String EVERLIVE_KEY = "vzK1tHn68Q4BHHux";
    private static EverliveApp app;

    private static QueryExecutor queryExecutor;
    private static SelfieUser currentUser;

    private static RequestResultCallbackAction onLoginCallback;

    private static RequestResultCallbackAction<User> userCallbackAction = new RequestResultCallbackAction<User>() {
        @Override
        public void invoke(RequestResult<User> requestResult) {
            final boolean hasErrors = !requestResult.getSuccess();
            if (!hasErrors) {
                User authentication = requestResult.getValue();
                app.workWith().data(SelfieUser.class)
                        .getById(authentication.getId())
                        .executeAsync(selfieUserCallbackAction);
            }
        }
    };

    @SuppressWarnings("unchecked")
    private static RequestResultCallbackAction<SelfieUser> selfieUserCallbackAction = new RequestResultCallbackAction<SelfieUser>() {
        @Override
        public void invoke(RequestResult<SelfieUser> requestResult) {
            final boolean hasErrors = !requestResult.getSuccess();
            if (!hasErrors) {
                currentUser = requestResult.getValue();

                // Set user position on login
                GeoPoint currentLocation = CurrentLocationListener.getInstance().getLocation();
                QueryExecutor queryExecutor = QueryExecutor.getInstance();
                queryExecutor.updateCurrentUserLocation(currentLocation);

                onLoginCallback.invoke(requestResult);
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

    public void setUser(RequestResultCallbackAction<User> callbackAction) {
        app.workWith().users().getMe().executeAsync(callbackAction);
    }

    public void updateCurrentUserLocation(final GeoPoint location) {
        if (currentUser != null) {
            currentUser.setLocation(location);
            this.updateUser(currentUser, currentUser.getId());
        }
    }

    public void updateUser(SelfieUser updatedUser, UUID userId) {
        app.workWith().users(SelfieUser.class).updateById(userId, updatedUser)
                .executeAsync(GlobalCallbacks.logResult);
    }

    @SuppressWarnings("rawtypes")
    public void registerUser(SelfieUser user, String password, RequestResultCallbackAction callback) {
        UserSecretInfo userSecretInfo = new UserSecretInfo();
        userSecretInfo.setPassword(password);

        app.workWith().users(SelfieUser.class)
                .create(user, userSecretInfo).executeAsync(callback);
    }

    @SuppressWarnings("rawtypes")
    public void loginUser(String username, String password, RequestResultCallbackAction callback) {
        onLoginCallback = callback;

        app.workWith().authentication().login(username, password)
                .executeAsync(new RequestResultCallbackAction<Object>() {
                    @Override
                    public void invoke(RequestResult<Object> requestResult) {
                        final boolean hasErrors = !requestResult.getSuccess();
                        if (!hasErrors) {
                            setUser(userCallbackAction);
                        }
                    }
                });
    }

    public void logoutUser() {
        app.workWith().authentication().logout().executeAsync(null);
    }

    public void getNearByUsers(final RequestResultCallbackAction<ArrayList<SelfieUser>> callback) {
        final FieldsDefinition includedFieldsDefinition = new FieldsDefinition();
        includedFieldsDefinition.addIncludedFields("Id", "Username", "Age", "Location", "Sex");

        app.workWith().users(SelfieUser.class).getAll()
                .where(new ValueCondition("Id", currentUser.getId(), ValueConditionOperator.NotEqualTo))
                .select(includedFieldsDefinition)
                .executeAsync(new RequestResultCallbackAction<ArrayList<SelfieUser>>() {
                    @Override
                    public void invoke(RequestResult<ArrayList<SelfieUser>> requestResult) {
                        final boolean hasErrors = !requestResult.getSuccess();

                        if (!hasErrors) {
                            ArrayList<SelfieUser> users = requestResult.getValue();
                            ArrayList<SelfieUser> nearByUsers = new ArrayList<SelfieUser>();
                            GeoPoint currentPos = currentUser.getLocation();

                            for (SelfieUser user : users) {
                                GeoPoint userPos = user.getLocation();
                                if (user.getId() != currentUser.getId() && userPos != null) {
                                    double distance = distance(currentPos.getLatitude(), currentPos.getLongitude(), userPos.getLatitude(), userPos.getLongitude(), 'K');
                                    if (distance < 10) {
                                        nearByUsers.add(user);
                                    }
                                }
                            }

                            callback.invoke(new RequestResult<ArrayList<SelfieUser>>(nearByUsers, new DynamicData()));
                        } else {
                            // TODO: Handle error
                        }
                    }
                });
    }

    /**
     * Passed to function:
     * lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)
     * lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)
     * unit = the unit you desire for results
     * where:
     * 'M' is statute miles
     * 'K' is kilometers (default)
     * 'N' is nautical miles
     */
    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /// This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /// This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad / Math.PI * 180.0);
    }

}

