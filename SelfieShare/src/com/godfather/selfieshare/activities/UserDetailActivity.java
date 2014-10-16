package com.godfather.selfieshare.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.godfather.selfieshare.R;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.models.Selfie;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class UserDetailActivity extends BaseActivity<UserDetailActivity>
        implements OnClickListener {

    private String userId;
    private QueryExecutor queryExecutor;

    @Override
    public void onClick(View v) {
        this.appMain.cameraLauncher.execute("takePicture", new RequestResultCallbackAction<byte[]>() {
            @Override
            public void invoke(RequestResult<byte[]> requestResult) {
                Boolean hasErrors = !requestResult.getSuccess();
                if (!hasErrors) {
                    byte[] image = requestResult.getValue();
                    InputStream stream = new ByteArrayInputStream(image);

                    uploadImage(stream);
                }
            }
        });
    }

    @Override
    public void create() {
        queryExecutor = QueryExecutor.getInstance();

        this.findViewById(R.id.btnSendPicture).setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        userId = extras.getString("userId");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        this.appMain.cameraLauncher.onActivityResult(requestCode, resultCode, intent);
    }

    private void uploadImage(final InputStream stream) {
        this.queryExecutor.uploadImage(stream,
                new RequestResultCallbackAction<ArrayList<com.telerik.everlive.sdk.core.model.system.File>>() {
                    @Override
                    public void invoke(RequestResult<ArrayList<com.telerik.everlive.sdk.core.model.system.File>> requestResult) {
                        if (requestResult.getSuccess()) {
                            ArrayList<com.telerik.everlive.sdk.core.model.system.File> files = requestResult.getValue();
                            com.telerik.everlive.sdk.core.model.system.File file = files.get(0);

                            UUID pictureId = UUID.fromString(file.getId().toString());
                            UUID toId = UUID.fromString(userId);

                            Selfie selfie = new Selfie();

                            selfie.setPicture(pictureId);
                            selfie.setTo(toId);

                            queryExecutor.addSelfie(selfie, new RequestResultCallbackAction() {
                                        @Override
                                        public void invoke(
                                                RequestResult requestResult) {
                                            if (requestResult.getSuccess()) {
                                                UserDetailActivity.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                finish();
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
    protected int getActivityLayout() {
        return R.layout.activity_user_detail;
    }
}
