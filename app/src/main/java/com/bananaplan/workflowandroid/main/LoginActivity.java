package com.bananaplan.workflowandroid.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.login.CheckLoggedInStatusCommand;
import com.bananaplan.workflowandroid.login.UserLoggingInCommand;
import com.parse.ParsePush;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        CheckLoggedInStatusCommand.OnFinishCheckingLoggedInStatusListener,
        UserLoggingInCommand.OnFinishLoggedInListener {

    private SharedPreferences mSharedPreferences;

    private LinearLayout mLoginContainerLinearLayout;

    private EditText mCompanyDomain;
    private EditText mAccountEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

    private ImageView mNiCloudImage;
    private View mNICContainer;
    private Button mNICRetryButton;

    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainApplication.sUseTestData) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return;
        }
        setContentView(R.layout.activity_login);
        initialize();
    }


    private void initialize() {
        mSharedPreferences = getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
        findViews();
        setupViews();
        hideAllViews();
    }
    private void findViews () {
        mNiCloudImage = (ImageView) findViewById(R.id.login_nicloud_image);
        mLoginContainerLinearLayout = (LinearLayout) findViewById(R.id.login_container);
        mCompanyDomain = (EditText) findViewById(R.id.login_company_domain);
        mAccountEditText = (EditText) findViewById(R.id.login_account_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mNICContainer = findViewById(R.id.no_internet_connection_container);
        mNICRetryButton = (Button) findViewById(R.id.no_internet_connection_retry_button);
    }

    private void setupViews () {
        mLoginButton.setOnClickListener(this);
        mNICRetryButton.setOnClickListener(this);
    }

    private void hideAllViews () {
        mLoginContainerLinearLayout.setVisibility(View.GONE);
    }

    private void showAllViews () {
        mLoginContainerLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        LoadingDataUtils.sBaseUrl = mSharedPreferences.getString(LoadingDataUtils.BASE_URL, "");
        WorkingData.setUserId(mSharedPreferences.getString(WorkingData.USER_ID, ""));
        WorkingData.setAuthToken(mSharedPreferences.getString(WorkingData.AUTH_TOKEN, ""));

        checkLoggedInStatus();
    }

    private void checkLoggedInStatus() {
        CheckLoggedInStatusCommand checkLoggedInStatusCommand = new CheckLoggedInStatusCommand(this, this);
        checkLoggedInStatusCommand.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                LoadingDataUtils.sBaseUrl = "http://" + mCompanyDomain.getText().toString();
                String username = mAccountEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                UserLoggingInCommand userLoggingInCommand = new UserLoggingInCommand(this, username, password, this);
                userLoggingInCommand.execute();

                break;

            case R.id.no_internet_connection_retry_button:
                mNICContainer.startAnimation(mFadeOutAnimation);
                mNiCloudImage.startAnimation(mFadeInAnimation);
                mNICContainer.setVisibility(View.GONE);
                mNiCloudImage.setVisibility(View.VISIBLE);
                checkLoggedInStatus();

                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Intent intent = new Intent(LoginActivity.this, PreloadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoggedOut(boolean isFailCausedByInternet) {
        if (isFailCausedByInternet) {
            mNICContainer.startAnimation(mFadeInAnimation);
            mNiCloudImage.startAnimation(mFadeOutAnimation);
            mNICContainer.setVisibility(View.VISIBLE);
            mNiCloudImage.setVisibility(View.GONE);
        } else {
            showAllViews();
        }
    }

    @Override
    public void onLoggedInSucceed(String userId, String authToken) {
        mSharedPreferences.edit()
                .putString(WorkingData.USER_ID, userId)
                .putString(WorkingData.AUTH_TOKEN, authToken)
                .putString(LoadingDataUtils.BASE_URL, LoadingDataUtils.sBaseUrl)
                .apply();

        WorkingData.setUserId(userId);
        WorkingData.setAuthToken(authToken);

        ParsePush.subscribeInBackground("user_" + userId);

        startActivity(new Intent(LoginActivity.this, PreloadActivity.class));
        finish();
    }

    @Override
    public void onLoggedInFailed(boolean isFailCausedByInternet) {
        if (isFailCausedByInternet) {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
        }
    }
}
