package com.bananaplan.workflowandroid.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;


// TODO: Load data needs to be put in a service
public class PreloadActivity extends AppCompatActivity {

    private static final String TAG = "PreloadActivity";

    private static final int PRELOAD_TIME_OUT = 500;


    private Handler mHandler;
    private Runnable mLaunchMainActivity = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(PreloadActivity.this, MainActivity.class));
            Log.d(TAG, "Start MainActivity");
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);
        initialize();
    }

    private void initialize() {
        mHandler = new Handler();
        long startLoading = 0L;
        long finishLoading = 0L;

        setupActionBar();

        startLoading = System.currentTimeMillis();
        //WorkingData.getInstance(this);
        finishLoading = System.currentTimeMillis();

        launchMainActivity(finishLoading - startLoading);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() == null) return;

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "removeCallbacks");
        mHandler.removeCallbacks(mLaunchMainActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        launchMainActivity(0);
    }

    private void launchMainActivity(long timeOut) {
        timeOut = timeOut > PRELOAD_TIME_OUT ? 0 : PRELOAD_TIME_OUT - timeOut;
        mHandler.postDelayed(mLaunchMainActivity, timeOut);
    }
}
