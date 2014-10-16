package com.godfather.selfieshare.utils;

import android.util.Log;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class GlobalCallbacks {
    public static final String TAG = "SelfieShareCallback";

    public static final RequestResultCallbackAction<Object> logResult = new RequestResultCallbackAction<Object>() {
        @Override
        public void invoke(RequestResult <Object> requestResult) {
            final boolean hasErrors = !requestResult.getSuccess();

            if(hasErrors) {
                Log.e(TAG, requestResult.getError().getMessage());
            }
        }
    };
}
