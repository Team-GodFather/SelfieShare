package com.godfather.selfieshare;

import android.app.ProgressDialog;
import android.content.*;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.godfather.selfieshare.controllers.LoginValidator;
import com.godfather.selfieshare.controllers.Message;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.utils.MusicService;
import com.godfather.selfieshare.utils.MusicService.LocalBinder;
import com.telerik.everlive.sdk.core.result.RequestResult;
import com.telerik.everlive.sdk.core.result.RequestResultCallbackAction;

public class MainActivity extends BaseActivity implements Button.OnClickListener {
    private static final String LOGIN_SUCCESSFUL = "Successful login!";
    private static final String LOGIN_FAILED = "Incorrect username or password!";
    private static final String LOGIN_VALIDATION = "Please enter correct data in the fields!";

    private MusicService mService;
    private boolean mBound = false;
    private Message message;
    private QueryExecutor queryExecutor;
    private ProgressDialog connectionProgressDialog;

    private EditText loginUsername;
    private EditText loginPassword;

    @Override
    protected String getActivityTitle() {
        return this.getString(R.string.title_activity_main);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate() {
        this.message = new Message(this);
        this.queryExecutor = QueryExecutor.getInstance();
        this.connectionProgressDialog = new ProgressDialog(this);
        this.connectionProgressDialog.setMessage("Logging in ...");

        this.loginUsername = (EditText) this.findViewById(R.id.loginUsername);
        this.loginPassword = (EditText) this.findViewById(R.id.loginPassword);

        // Bind click listeners
        this.findViewById(R.id.loginBtn).setOnClickListener(this);
        this.findViewById(R.id.signUpBtn).setOnClickListener(this);

        Intent intent = new Intent(MainActivity.this, MusicService.class);
        this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unbindService(mConnection);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                this.login(view);
                break;
            case R.id.signUpBtn:
                this.register(view);
                break;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.loadSong(R.raw.selfie);
            mService.playSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void login(View view) {
        String username = loginUsername.getText().toString();
        String password = loginPassword.getText().toString();

        if (LoginValidator.validate(username, password)) {
            connectionProgressDialog.show();
            queryExecutor.loginUser(username, password, loginThread());
        } else {
            message.print(LOGIN_VALIDATION);
        }
    }

    public RequestResultCallbackAction<?> loginThread() {
        return new RequestResultCallbackAction<Object>() {
            @Override
            public void invoke(RequestResult<Object> requestResult) {
                final boolean hasErrors = !requestResult.getSuccess();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionProgressDialog.dismiss();

                        if (!hasErrors) {
                            message.print(LOGIN_SUCCESSFUL);

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            message.print(LOGIN_FAILED);
                        }
                    }
                });
            }
        };
    }

    public void register(View view) {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivityForResult(intent, 1);
        //startActivity(intent);
    }
}
