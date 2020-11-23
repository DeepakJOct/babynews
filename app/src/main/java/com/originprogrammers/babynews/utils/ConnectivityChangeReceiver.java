package com.originprogrammers.babynews.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.originprogrammers.babynews.InternetActivity;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    public ConnectivityChangeReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
//                CommonUtils.showSnackBar(context, "YOU ARE BACK ONLINE");
                Log.d("NetworkChangeReceiver", "Online. Connected to internet.");
            } else {
                context.startActivity(new Intent(((AppCompatActivity) context).getApplicationContext(), InternetActivity.class));
//                CommonUtils.showSnackBar(context, "NO INTERNET CONNECTION");
                Log.d("NetworkChangeReceiver", "Offline. No internet.");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
