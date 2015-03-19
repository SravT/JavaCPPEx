package edu.virginia.cs.cs4720.umbreon;

import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

/** Splash screen */
public class SplashActivity extends Activity {


    private static final int WAIT_TIME = 3000;
    private final static String TAG = "SplashActivity";
    private Timer wait;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView splash = new ImageView(this);
        splash.setImageResource(R.drawable.splash_umbrella);
        setContentView(splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, WAIT_TIME);
        Log.i(TAG,"Entered the onCreate() method");
    }
}