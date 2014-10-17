package com.godfather.selfieshare.data;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.godfather.selfieshare.controllers.CurrentLocationListener;
import com.godfather.selfieshare.controllers.SettingsDataSource;
import com.godfather.selfieshare.models.Selfie;
import com.godfather.selfieshare.models.SelfieUser;
import com.godfather.selfieshare.models.Settings;
import com.godfather.selfieshare.utils.GlobalCallbacks;
import com.telerik.everlive.sdk.core.EverliveApp;
import com.telerik.everlive.sdk.core.model.base.DynamicData;
import com.telerik.everlive.sdk.core.model.base.ItemBase;
import com.telerik.everlive.sdk.core.model.system.GeoPoint;
import com.telerik.everlive.sdk.core.model.system.User;
import com.telerik.everlive.sdk.core.query.definition.FieldsDefinition;
import com.telerik.everlive.sdk.core.query.definition.FileField;
import com.telerik.everlive.sdk.core.query.definition.UserSecretInfo;
import com.telerik.everlive.sdk.core.query.definition.expand.ExpandDefinition;
import com.telerik.everlive.sdk.core.query.definition.expand.ExpandDefinitionBase;
import com.telerik.everlive.sdk.core.query.definition.filtering.Condition;
import com.telerik.everlive.sdk.core.query.definition.filtering.compound.CompoundCondition;
import com.telerik.everlive.sdk.core.query.definition.filtering.compound.CompoundConditionOperator;
import com.telerik.everlive.sdk.core.query.definition.filtering.simple.ValueCondition;
import com.telerik.everlive.sdk.core.query.definition.filtering.simple.ValueConditionOperator;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class QueryExecutor {
	private static final String EVERLIVE_KEY = "vzK1tHn68Q4BHHux";
	private static EverliveApp app;

	private SecureRandom random = new SecureRandom();
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
				GeoPoint currentLocation = CurrentLocationListener
						.getInstance().getLocation();
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
	public void registerUser(SelfieUser user, String password,
			RequestResultCallbackAction callback) {
		UserSecretInfo userSecretInfo = new UserSecretInfo();
		userSecretInfo.setPassword(password);

		app.workWith().users(SelfieUser.class).create(user, userSecretInfo)
				.executeAsync(callback);
	}

	@SuppressWarnings("rawtypes")
	public void loginUser(String username, String password,
			RequestResultCallbackAction callback) {
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

	private List<Condition> getSettings(Context context) {
		SettingsDataSource datasource = new SettingsDataSource(context);
		datasource.open();
		Settings settings = datasource.getSettings();
		datasource.close();

		List<Condition> valueConditions = new ArrayList<Condition>(4);

		int sexOperatorValue = 0;

		if (settings.getMale() == 1 && settings.getFemale() == 1) {
			sexOperatorValue = 2;
		} else if (settings.getMale() == 1) {
			sexOperatorValue = 1;
		} else if (settings.getFemale() == 1) {
			sexOperatorValue = 0;
		} else {
			sexOperatorValue = 2;
		}

		ValueConditionOperator sexConditionOperator = null;

		switch (sexOperatorValue) {
		case 0:
			// Female
			sexConditionOperator = ValueConditionOperator.NotEqualTo;
			break;
		case 1:
			// Male
			sexConditionOperator = ValueConditionOperator.EqualTo;
			break;
		case 2:
			// Both
			sexConditionOperator = ValueConditionOperator.GreaterThanOrEqualTo;
			break;
		}

		valueConditions.add(new ValueCondition("Sex", 1, sexConditionOperator));
//		valueConditions.add(new ValueCondition("Age", settings.getMinAge(),
//				ValueConditionOperator.GreaterThanOrEqualTo));
//		valueConditions.add(new ValueCondition("Age", settings.getMaxAge(),
//				ValueConditionOperator.LessThanOrEqualTo));

		return valueConditions;
	}

	public void getNearByUsers(Context context,
			final RequestResultCallbackAction<ArrayList<SelfieUser>> callback) {
		final FieldsDefinition includedFieldsDefinition = new FieldsDefinition();
		includedFieldsDefinition.addIncludedFields("Id", "Username", "Age",
				"Location", "Sex");

		List<Condition> conditions = getSettings(context);

		Condition containsCond = new ValueCondition("Id", currentUser.getId(),
				ValueConditionOperator.NotEqualTo);

		conditions.add((ValueCondition) containsCond);

		Condition condition = new CompoundCondition(
				CompoundConditionOperator.And, conditions);

		app.workWith()
				.users(SelfieUser.class)
				.getAll()
				.where(condition)
				.select(includedFieldsDefinition)
				.executeAsync(
						new RequestResultCallbackAction<ArrayList<SelfieUser>>() {
							@Override
							public void invoke(
									RequestResult<ArrayList<SelfieUser>> requestResult) {
								final boolean hasErrors = !requestResult
										.getSuccess();

								if (!hasErrors) {
									ArrayList<SelfieUser> users = requestResult
											.getValue();
									ArrayList<SelfieUser> nearByUsers = new ArrayList<SelfieUser>();
									GeoPoint currentPos = currentUser
											.getLocation();

									for (SelfieUser user : users) {
										GeoPoint userPos = user.getLocation();
										if (user.getId() != currentUser.getId()
												&& userPos != null) {
											double distance = distance(
													currentPos.getLatitude(),
													currentPos.getLongitude(),
													userPos.getLatitude(),
													userPos.getLongitude(), 'K');
											if (distance < 10) {
												nearByUsers.add(user);
											}
										}
									}

									callback.invoke(new RequestResult<ArrayList<SelfieUser>>(
											nearByUsers, new DynamicData()));
								} else {
									// TODO: Handle error
								}
							}
						});
	}

	public void getRequestedSelfies(
			final RequestResultCallbackAction<ArrayList<Selfie>> callback) {
		final FieldsDefinition includedFieldsDefinition = new FieldsDefinition();
		includedFieldsDefinition.addIncludedFields("Id", "Picture", "To",
				"CreatedBy", "CreatedAt");

		ArrayList<ExpandDefinitionBase> expandDefinitions = new ArrayList<ExpandDefinitionBase>();
		expandDefinitions.add(new ExpandDefinition("Owner", "_Owner"));
		expandDefinitions.add(new ExpandDefinition("To", "_To"));
		expandDefinitions.add(new ExpandDefinition("Picture", "_Picture"));

		app.workWith()
				.data(Selfie.class)
				.getAll()
				.where(new ValueCondition("CreatedBy", currentUser.getId(),
						ValueConditionOperator.EqualTo))
				.expand(expandDefinitions)
				.select(includedFieldsDefinition)
				.executeAsync(
						new RequestResultCallbackAction<ArrayList<Selfie>>() {
							@Override
							public void invoke(
									RequestResult<ArrayList<Selfie>> requestResult) {
								final boolean hasErrors = !requestResult
										.getSuccess();

								if (!hasErrors) {
									ArrayList<Selfie> selfies = requestResult
											.getValue();

									callback.invoke(new RequestResult<ArrayList<Selfie>>(
											selfies, new DynamicData()));
								} else {
									// TODO: Handle error
								}
							}
						});
	}

	public void getReceivedSelfies(
			final RequestResultCallbackAction<ArrayList<Selfie>> callback) {
		final FieldsDefinition includedFieldsDefinition = new FieldsDefinition();
		includedFieldsDefinition.addIncludedFields("Id", "Picture", "To",
				"CreatedBy", "CreatedAt");

		ArrayList<ExpandDefinitionBase> expandDefinitions = new ArrayList<ExpandDefinitionBase>();
		expandDefinitions.add(new ExpandDefinition("Owner", "_Owner"));
		expandDefinitions.add(new ExpandDefinition("To", "_To"));
		expandDefinitions.add(new ExpandDefinition("Picture", "_Picture"));
		
		app.workWith()
				.data(Selfie.class)
				.getAll()
				.where(new ValueCondition("To", currentUser.getId(),
						ValueConditionOperator.EqualTo))
				.expand(expandDefinitions)
				.select(includedFieldsDefinition)
				.executeAsync(
						new RequestResultCallbackAction<ArrayList<Selfie>>() {
							@Override
							public void invoke(
									RequestResult<ArrayList<Selfie>> requestResult) {
								final boolean hasErrors = !requestResult
										.getSuccess();

								if (!hasErrors) {
									ArrayList<Selfie> selfies = requestResult
											.getValue();

									callback.invoke(new RequestResult<ArrayList<Selfie>>(
											selfies, new DynamicData()));
								} else {
									// TODO: Handle error
								}
							}
						});
	}

	public void uploadImage(InputStream inputStream,
			RequestResultCallbackAction callbackAction) {
		String fileName = new BigInteger(130, random).toString(32) + ".jpg";
		String contentType = "image/jpeg";

		FileField fileField = new FileField(fileName, contentType, inputStream);
		app.workWith().files().upload(fileField).executeAsync(callbackAction);
	}

	public void addSelfie(ItemBase selfie, RequestResultCallbackAction callback) {
		app.workWith().data(Selfie.class).create(selfie).executeAsync(callback);
	}

	/**
	 * Passed to function: lat1, lon1 = Latitude and Longitude of point 1 (in
	 * decimal degrees) lat2, lon2 = Latitude and Longitude of point 2 (in
	 * decimal degrees) unit = the unit you desire for results where: 'M' is
	 * statute miles 'K' is kilometers (default) 'N' is nautical miles
	 */
	private double distance(double lat1, double lon1, double lat2, double lon2,
			char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
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

	// / This function converts decimal degrees to radians
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	// / This function converts radians to decimal degrees
	private double rad2deg(double rad) {
		return (rad / Math.PI * 180.0);
	}

	public void countRequestedSelfies(final RequestResultCallbackAction callback) {
		app.workWith()
				.data(Selfie.class)
				.getCount()
				.where(new ValueCondition("CreatedBy", currentUser.getId(),
						ValueConditionOperator.EqualTo))
				.executeAsync(new RequestResultCallbackAction() {
					@Override
					public void invoke(RequestResult requestResult) {
						final boolean hasErrors = !requestResult.getSuccess();

						if (!hasErrors) {
							int countSelfies = (Integer) requestResult
									.getValue();

							callback.invoke(new RequestResult(countSelfies,
									new DynamicData()));
						} else {
							// TODO: Handle error
						}
					}
				});
	}

	public void countReceivedSelfies(final RequestResultCallbackAction callback) {
		app.workWith()
				.data(Selfie.class)
				.getCount()
				.where(new ValueCondition("To", currentUser.getId(),
						ValueConditionOperator.EqualTo))
				.executeAsync(new RequestResultCallbackAction() {
					@Override
					public void invoke(RequestResult requestResult) {
						final boolean hasErrors = !requestResult.getSuccess();

						if (!hasErrors) {
							int countSelfies = (Integer) requestResult
									.getValue();

							callback.invoke(new RequestResult(countSelfies,
									new DynamicData()));
						} else {
							// TODO: Handle error
						}
					}
				});
	}
}
