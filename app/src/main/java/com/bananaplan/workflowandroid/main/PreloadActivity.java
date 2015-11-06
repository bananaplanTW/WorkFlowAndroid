package com.bananaplan.workflowandroid.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.loading.LoadingDataTask;


public class PreloadActivity extends AppCompatActivity implements LoadingDataTask.OnFinishLoadingDataListener {

    private static final String TAG = "PreloadActivity";

    private LoadingDataTask mLoadingDataTask;

    private ImageView mNiCloud;

    // NIC = NoInternetConnection
    private View mNICContainer;
    private Button mNICRetryButton;

    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);
        initialize();
    }

    private void initialize() {
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
        findViews();
        setupViews();
    }

    private void findViews() {
        mNiCloud = (ImageView) findViewById(R.id.ni_cloud);
        mNICContainer = findViewById(R.id.no_internet_connection_container);
        mNICRetryButton = (Button) findViewById(R.id.no_internet_connection_retry_button);
    }

    private void setupViews() {
        mNICRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNICContainer.startAnimation(mFadeOutAnimation);
                mNiCloud.startAnimation(mFadeInAnimation);
                mNICContainer.setVisibility(View.GONE);
                mNiCloud.setVisibility(View.VISIBLE);
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
        mNICContainer.startAnimation(mFadeInAnimation);
        mNiCloud.startAnimation(mFadeOutAnimation);
        mNICContainer.setVisibility(View.VISIBLE);
        mNiCloud.setVisibility(View.GONE);
        mLoadingDataTask = null;
    }
}
