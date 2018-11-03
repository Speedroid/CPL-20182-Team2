package cspmbilelab.com.newsnailmail.splash;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import cspmbilelab.com.newsnailmail.R;

public class NewSnailMailSplashActivity extends Activity {

    private ConnectivityManager cManager;
    private NetworkInfo mobile;
    private NetworkInfo wifi;

    private static String TAG = "** NewSnailMail SA**";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_snail_mail_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg)
            {
                finish();
            }
        } ;
        handler.sendEmptyMessageDelayed(0,3000);
    }






}
