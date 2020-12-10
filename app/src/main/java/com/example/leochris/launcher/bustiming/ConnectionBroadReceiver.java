package com.example.leochris.launcher.bustiming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.WebSettings;

/**
 * Created by leochris on 3/13/17.
 */

public class ConnectionBroadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (isNetworkAvailable(context)) {
            if(BusTiming.webview != null) {
                //fit to screen
                BusTiming.webview.getSettings().setJavaScriptEnabled(true);
                BusTiming.webview.getSettings().setDomStorageEnabled(true);
                BusTiming.webview.getSettings().setLoadWithOverviewMode(true);
                BusTiming.webview.getSettings().setUseWideViewPort(true);

                //disable scrolling
                BusTiming.webview.setVerticalScrollBarEnabled(false);
                BusTiming.webview.setHorizontalScrollBarEnabled(false);
                BusTiming.webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

                BusTiming.webview.reload();
            }
        }
    }

    public boolean isNetworkAvailable(Context context) {
        // Get Connectivity Manager class object from Systems Service
        ConnectivityManager cm = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get Network Info from connectivity Manager
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
