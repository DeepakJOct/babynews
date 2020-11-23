package com.originprogrammers.babynews;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import com.originprogrammers.babynews.utils.CommonUtils;
import com.originprogrammers.babynews.utils.ConnectivityChangeReceiver;
import com.originprogrammers.babynews.utils.Constants;
import com.originprogrammers.babynews.utils.Prefs;

public class InternetActivity extends AppCompatActivity {

    private ConnectivityChangeReceiver receiver = new ConnectivityChangeReceiver();
    boolean isConnected;
    Handler h;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.initPrefs(this);
        setContentView(R.layout.activity_internet);
//        getSupportActionBar().hide();
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                isConnected = CommonUtils.checkInternetConnection(InternetActivity.this);
                Log.d("InternetAct::", "isConneted->" + isConnected);

                if(isConnected) {
                    Prefs.putBoolean(Constants.IS_FROM_INTERNET_ACTIVITY, true);
                    finish();
                }
            }
        };
        timer = new Timer();
        timer.schedule(t, 0, 1500);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
