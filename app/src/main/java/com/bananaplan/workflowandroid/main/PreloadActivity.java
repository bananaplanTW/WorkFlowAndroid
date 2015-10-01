package com.bananaplan.workflowandroid.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.loading.LoadingDataTask;


public class PreloadActivity extends AppCompatActivity implements LoadingDataTask.OnFinishLoadingDataListener {

    private static final String TAG = "PreloadActivity";
    private LoadingDataTask mLoadingDataTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoadingDataTask == null) {
            mLoadingDataTask = new LoadingDataTask(this, this);
        }
        mLoadingDataTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLoadingDataTask.cancel(true);
        mLoadingDataTask = null;
    }

    private void launchMainActivity() {
        startActivity(new Intent(PreloadActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onFinishLoadingData() {
        launchMainActivity();
    }
}
