package com.originprogrammers.babynews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.originprogrammers.babynews.utils.ConnectivityChangeReceiver;
import com.originprogrammers.babynews.utils.Constants;
import com.originprogrammers.babynews.utils.Prefs;

public class LoadingActivity extends AppCompatActivity {

    public static LoadingActivity loadingActivity;
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private boolean isFromMainActivity = false;

    ConnectivityChangeReceiver connectivityChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.initPrefs(this);
        setContentView(R.layout.activity_loading);
        loadingActivity = this;
        connectivityChangeReceiver = new ConnectivityChangeReceiver();
        RelativeLayout rlLoading = findViewById(R.id.rl_loading);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            boolean isLoading = bundle.getBoolean("isLoading");
            isFromMainActivity = bundle.getBoolean("isFromMainActivity");
            if(isLoading) {
                rlLoading.setVisibility(View.VISIBLE);
            }
        }
        registerNetworkBroadcastForNoughat();

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        if(!isFromMainActivity) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /* Create an Intent that will start the MainActivity. */
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    LoadingActivity.this.startActivity(intent);
                    LoadingActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFromMainActivity && Prefs.getBoolean(Constants.IS_FROM_INTERNET_ACTIVITY, false)) {
            Prefs.putBoolean(Constants.IS_FROM_INTERNET_ACTIVITY, false);
            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
            LoadingActivity.this.startActivity(intent);
            LoadingActivity.this.finish();
        }
    }

    private void registerNetworkBroadcastForNoughat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(connectivityChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        super.onDestroy();
    }

    public static void stopLoadingAndFinish() {
        loadingActivity.finish();
    }
}
