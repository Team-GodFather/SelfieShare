package com.godfather.selfieshare.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.Selfie;
import com.godfather.selfieshare.utils.StorageUtils;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class UserDetailActivity extends BaseActivity<UserDetailActivity>
		implements OnClickListener {

	private static final int ACTION_TAKE_PHOTO_B = 1;

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";

	private String userId;
	private String mCurrentPhotoPath;
	private Bitmap mImageBitmap;
	private QueryExecutor queryExecutor;

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private StorageUtils mAlbumStorageDirFactory = null;

	private java.io.File getAlbumDir() {
		java.io.File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir();

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private java.io.File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		java.io.File albumF = getAlbumDir();
		java.io.File imageF = java.io.File.createTempFile(imageFileName,
				JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private java.io.File setUpPhotoFile() throws IOException {

		java.io.File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch (actionCode) {
		case ACTION_TAKE_PHOTO_B:
			java.io.File f = null;

			try {
				f = setUpPhotoFile();
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;
		}

		startActivityForResult(takePictureIntent, actionCode);
	}

	@Override
	public void onClick(View v) {
		dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
	}

	@Override
	public void create() {
		queryExecutor = QueryExecutor.getInstance();

		this.findViewById(R.id.btnSendPicture).setOnClickListener(this);
		mAlbumStorageDirFactory = new StorageUtils();

		Bundle extras = getIntent().getExtras();
		userId = extras.getString("id");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50,
						byteArrayOutputStream);
				byte[] image = byteArrayOutputStream.toByteArray();
				InputStream stream = new ByteArrayInputStream(image);

				uploadImage(stream);
			}
			break;
		}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
	}

	private void uploadImage(final InputStream stream) {
		this.queryExecutor.uploadImage(stream,
				new RequestResultCallbackAction() {
					@Override
					public void invoke(RequestResult requestResult) {

						if (requestResult.getSuccess()) {
							ArrayList<com.telerik.everlive.sdk.core.model.system.File> files = (ArrayList<com.telerik.everlive.sdk.core.model.system.File>) requestResult
									.getValue();

							com.telerik.everlive.sdk.core.model.system.File file = files
									.get(0);

							UUID pictureId = UUID.fromString(file.getId()
									.toString());

							UUID toId = UUID.fromString(userId);

							Selfie selfie = new Selfie();

							selfie.setPicture(pictureId);
							selfie.setTo(toId);

							queryExecutor.addSelfie(selfie,
									new RequestResultCallbackAction() {
										@Override
										public void invoke(
												RequestResult requestResult) {
											if (requestResult.getSuccess()) {

												UserDetailActivity.this
														.runOnUiThread(new Runnable() {
															@Override
															public void run() {
																// finish();
																// BufferedInputStream
																// bis = new
																// BufferedInputStream(
																// stream,
																// 8192);
																// Bitmap
																// newGoalCover
																// =
																// BitmapFactory
																// .decodeStream(bis);

															}
														});
											}
										}
									});
						}
					}
				});
	}

	@Override
	protected String getActivityTitle() {
		return this.getString(R.string.title_activity_user_detail);
	}

	@Override
	protected int getActivityLayout() {
		return R.layout.activity_user_detail;
	}
}
