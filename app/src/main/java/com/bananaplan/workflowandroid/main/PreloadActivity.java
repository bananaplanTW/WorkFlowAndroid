package com.bananaplan.workflowandroid.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.utility.server.RestfulUtils;

import java.util.concurrent.ExecutionException;


// TODO: Load data needs to be put in a service
public class PreloadActivity extends AppCompatActivity {

    private static final String TAG = "PreloadActivity";

    private static final int PRELOAD_TIME_OUT = 500;

    private static final class WorkingDataUrl {
        public static final String WORKER = "http://10.1.1.41:3000/api/employees";
    }

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
        //loadWorkingData();
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

    private void loadWorkingData() {
        try {
            String weatherJSONString =
                    new RestfulUtils.GetRequest().execute(WorkingDataUrl.WORKER).get();
            Log.d(TAG, weatherJSONString);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void launchMainActivity(long timeOut) {
        timeOut = timeOut > PRELOAD_TIME_OUT ? 0 : PRELOAD_TIME_OUT - timeOut;
        mHandler.postDelayed(mLaunchMainActivity, timeOut);
    }
}
