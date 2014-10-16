package com.godfather.selfieshare.utils;

import java.io.File;

import android.os.Environment;

public final class StorageUtils {

	// Standard storage location for digital camera files
	private static final String CAMERA_DIR = "/dcim/";

	public File getAlbumStorageDir() {
		return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR);
	}
}
