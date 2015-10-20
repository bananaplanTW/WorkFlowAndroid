package com.bananaplan.workflowandroid.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.login.CheckLoggedInStatusCommand;
import com.bananaplan.workflowandroid.login.UserLoggingInCommand;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        CheckLoggedInStatusCommand.OnFinishCheckingLoggedInStatusListener,
        UserLoggingInCommand.OnFinishLoggedInListener {

    private SharedPreferences mSharedPreferences;
    private EditText mAccountEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }


    private void initialize() {
        mSharedPreferences = getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);

        findViews();
        setupViews();

        hideAllViews();
    }
    private void findViews () {
        mAccountEditText = (EditText) findViewById(R.id.login_account_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        mLoginButton = (Button) findViewById(R.id.login_button);
    }
    private void setupViews () {
        mLoginButton.setOnClickListener(this);
    }
    private void hideAllViews () {
        mAccountEditText.setVisibility(View.GONE);
        mPasswordEditText.setVisibility(View.GONE);
        mLoginButton.setVisibility(View.GONE);
    }
    private void showAllViews () {
        mAccountEditText.setVisibility(View.VISIBLE);
        mPasswordEditText.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();

        WorkingData.setUserId(mSharedPreferences.getString(WorkingData.USER_ID, ""));
        WorkingData.setAuthToken(mSharedPreferences.getString(WorkingData.AUTH_TOKEN, ""));

        CheckLoggedInStatusCommand checkLoggedInStatusCommand = new CheckLoggedInStatusCommand(this, this);
        checkLoggedInStatusCommand.execute();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                String username = mAccountEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                UserLoggingInCommand userLoggingInCommand = new UserLoggingInCommand(this, username, password, this);
                userLoggingInCommand.execute();
                break;
            default:
                // no ops
                break;
        }
    }


    @Override
    public void onLoggedIn() {
        startActivity(new Intent(LoginActivity.this, PreloadActivity.class));
        finish();
    }
    @Override
    public void onLoggedOut() {
        showAllViews();
    }


    @Override
    public void onLoggedInSucceed(String userId, String authToken) {
        mSharedPreferences.edit().putString(WorkingData.USER_ID, userId).putString(WorkingData.AUTH_TOKEN, authToken).commit();
        WorkingData.setUserId(userId);
        WorkingData.setAuthToken(authToken);

        startActivity(new Intent(LoginActivity.this, PreloadActivity.class));
        finish();
    }
    @Override
    public void onLoggedInFailed() {
        Toast.makeText(this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
    }
}
