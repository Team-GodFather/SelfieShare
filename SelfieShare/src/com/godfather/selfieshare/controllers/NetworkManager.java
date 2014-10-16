package com.godfather.selfieshare.controllers;

import android.app.ProgressDialog;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.KeyEvent;
import com.godfather.selfieshare.AppMain;

public class NetworkManager {
    public static final String WIFI = "wifi";
    // mobile
    public static final String MOBILE = "mobile";

    // Android L calls this Cellular, because I have no idea!
    public static final String CELLULAR = "cellular";
    // 2G network types
    public static final String GSM = "gsm";
    public static final String GPRS = "gprs";
    public static final String EDGE = "edge";
    // 3G network types
    public static final String CDMA = "cdma";
    public static final String UMTS = "umts";
    public static final String HSPA = "hspa";
    public static final String HSUPA = "hsupa";
    public static final String HSDPA = "hsdpa";
    public static final String ONEXRTT = "1xrtt";
    public static final String EHRPD = "ehrpd";
    // 4G network types
    public static final String LTE = "lte";
    public static final String UMB = "umb";
    public static final String HSPA_PLUS = "hspa+";
    // return type
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String TYPE_WIFI = "wifi";
    public static final String TYPE_2G = "2g";
    public static final String TYPE_3G = "3g";
    public static final String TYPE_4G = "4g";
    public static final String TYPE_NONE = "none";

    private static final String LOG_TAG = "NetworkManager";

    private boolean registered = false;

    AppMain appContext;
    ConnectivityManager sockMan;
    BroadcastReceiver receiver;
    ProgressDialog connectionProgressDialog;

    /**
     * Constructor.
     */
    public NetworkManager() {
        this.receiver = null;
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     */
    public void initialize(AppMain appContext) {
        this.appContext = appContext;
        this.sockMan = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // We need to listen to connectivity events to update navigator.connection
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (this.receiver == null) {
            this.receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateConnectionInfo(sockMan.getActiveNetworkInfo());
                }
            };
            appContext.registerReceiver(this.receiver, intentFilter);
            this.registered = true;
        }
    }

    /**
     * Stop network receiver.
     */
    public void destroy() {
        if (this.receiver != null && this.registered) {
            try {
                this.appContext.unregisterReceiver(this.receiver);
                this.registered = false;
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error unregistering network receiver: " + e.getMessage(), e);
            }
        }

        if (this.connectionProgressDialog != null && this.connectionProgressDialog.isShowing()) {
            this.connectionProgressDialog.dismiss();
        }
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------
    private void updateConnectionInfo(NetworkInfo networkInfo) {
        String networkType = this.getType(networkInfo);
        Boolean hasNetwork = !networkType.equals(TYPE_NONE) && !networkType.equals(TYPE_UNKNOWN);

        if (!hasNetwork && this.connectionProgressDialog == null) {
            this.connectionProgressDialog = new ProgressDialog(this.appContext.getCurrentActivity());
            this.connectionProgressDialog.setMessage("This application needs internet.");
            this.connectionProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return true; // Pretend we processed it
                }
            });

            this.connectionProgressDialog.setCancelable(false);
            this.connectionProgressDialog.show();
        } else if (hasNetwork && this.connectionProgressDialog != null && this.connectionProgressDialog.isShowing()) {
            this.connectionProgressDialog.dismiss();
            this.connectionProgressDialog = null;
        }
    }

    /**
     * Determine the type of connection
     *
     * @param info the network info so we can determine connection type.
     * @return the type of mobile network we are on
     */
    private String getType(NetworkInfo info) {
        if (info != null) {
            String type = info.getTypeName();

            if (type.toLowerCase().equals(WIFI)) {
                return TYPE_WIFI;
            } else if (type.toLowerCase().equals(MOBILE) || type.toLowerCase().equals(CELLULAR)) {
                type = info.getSubtypeName();
                if (type.toLowerCase().equals(GSM) ||
                        type.toLowerCase().equals(GPRS) ||
                        type.toLowerCase().equals(EDGE)) {
                    return TYPE_2G;
                } else if (type.toLowerCase().startsWith(CDMA) ||
                        type.toLowerCase().equals(UMTS) ||
                        type.toLowerCase().equals(ONEXRTT) ||
                        type.toLowerCase().equals(EHRPD) ||
                        type.toLowerCase().equals(HSUPA) ||
                        type.toLowerCase().equals(HSDPA) ||
                        type.toLowerCase().equals(HSPA)) {
                    return TYPE_3G;
                } else if (type.toLowerCase().equals(LTE) ||
                        type.toLowerCase().equals(UMB) ||
                        type.toLowerCase().equals(HSPA_PLUS)) {
                    return TYPE_4G;
                }
            }
        } else {
            return TYPE_NONE;
        }
        return TYPE_UNKNOWN;
    }
}

