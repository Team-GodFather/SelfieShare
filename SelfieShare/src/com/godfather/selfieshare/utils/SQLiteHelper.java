package com.godfather.selfieshare.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_SETTING = "settings";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MALE = "male";
	public static final String COLUMN_FEMALE = "female";
	public static final String COLUMN_MIN_AGE = "min_age";
	public static final String COLUMN_MAX_AGE = "max_age";

	private static final String DATABASE_NAME = "settings.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SETTING + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_MALE
			+ " integer, " + COLUMN_FEMALE + " integer, " + COLUMN_MIN_AGE
			+ " integer, " + COLUMN_MAX_AGE + " integer);";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING);
		onCreate(db);
	}
}