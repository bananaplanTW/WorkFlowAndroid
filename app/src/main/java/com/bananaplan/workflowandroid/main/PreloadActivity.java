package com.bananaplan.workflowandroid.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.loading.LoadingDataTask;
import com.bananaplan.workflowandroid.login.CheckLoggedInStatusCommand;


public class PreloadActivity extends AppCompatActivity implements LoadingDataTask.OnFinishLoadingDataListener {

    private static final String TAG = "PreloadActivity";

    private LoadingDataTask mLoadingDataTask;

    // NIC = NoInternetConnection
    private View mNICContainer;
    private Button mNICRetryButton;
    private ProgressBar mNICProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);
        initialize();
    }

    private void initialize() {
        findViews();
        setupViews();
    }

    private void findViews() {
        mNICContainer = findViewById(R.id.no_internet_connection_container);
        mNICRetryButton = (Button) findViewById(R.id.no_internet_connection_retry_button);
        mNICProgressBar = (ProgressBar) findViewById(R.id.no_internet_connection_progress_bar);
    }

    private void setupViews() {
        mNICRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNICContainer.setVisibility(View.GONE);
                mNICProgressBar.setVisibility(View.VISIBLE);
                startLoadingData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLoadingData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelLoadingData();
    }

    private void startLoadingData() {
        if (mLoadingDataTask == null) {
            mLoadingDataTask = new LoadingDataTask(this, this);
        }
        mLoadingDataTask.execute();
    }

    private void cancelLoadingData() {
        if (mLoadingDataTask == null) return;
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

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {
        if (isFailCausedByInternet) {
            mNICContainer.setVisibility(View.VISIBLE);
            mNICProgressBar.setVisibility(View.GONE);
            mLoadingDataTask = null;
        }
    }
}
