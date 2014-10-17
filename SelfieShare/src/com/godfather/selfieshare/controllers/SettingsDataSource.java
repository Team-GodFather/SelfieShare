package com.godfather.selfieshare.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.godfather.selfieshare.models.Settings;
import com.godfather.selfieshare.utils.SQLiteHelper;

public class SettingsDataSource {
	private final int DEFAULT_MALE = 0;
	private final int DEFAULT_FEMALE = 0;
	private final int DEFAULT_MIN_AGE = 1;
	private final int DEFAULT_MAX_AGE = 120;

	// Database fields
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_MALE, SQLiteHelper.COLUMN_FEMALE,
			SQLiteHelper.COLUMN_MIN_AGE, SQLiteHelper.COLUMN_MAX_AGE };

	public SettingsDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createSettings(int male, int female, int minAge, int maxAge) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_MALE, male);
		values.put(SQLiteHelper.COLUMN_FEMALE, female);
		values.put(SQLiteHelper.COLUMN_MIN_AGE, minAge);
		values.put(SQLiteHelper.COLUMN_MAX_AGE, maxAge);

		database.insert(SQLiteHelper.TABLE_SETTING, null, values);
	}

	public void deleteSettings() {
		database.delete(SQLiteHelper.TABLE_SETTING, null, null);
	}

	public Settings getSettings() {
		Settings settings = new Settings();

		Cursor cursor = database.query(SQLiteHelper.TABLE_SETTING, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();

		if (cursor.isAfterLast()) {
			createSettings(DEFAULT_MALE, DEFAULT_FEMALE, DEFAULT_MIN_AGE,
					DEFAULT_MAX_AGE);
			settings.setMale(DEFAULT_MALE);
			settings.setFemale(DEFAULT_FEMALE);
			settings.setMinAge(DEFAULT_MIN_AGE);
			settings.setMaxAge(DEFAULT_MAX_AGE);
		} else {
			settings.setId(cursor.getLong(0));
			settings.setMale(cursor.getInt(1));
			settings.setFemale(cursor.getInt(2));
			settings.setMinAge(cursor.getInt(3));
			settings.setMaxAge(cursor.getInt(4));
		}
		cursor.close();
		return settings;
	}
}