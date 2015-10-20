package com.bananaplan.workflowandroid.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.login.CheckLoggedInStatusCommand;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, CheckLoggedInStatusCommand.OnFinishCheckingLoggedInStatusListener {

    private EditText mAccountEditText;
    private EditText mPassowrdEditText;
    private Button mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }


    private void initialize() {
        findViews();
        setupViews();

        hideAllViews();
    }
    private void findViews () {
        mAccountEditText = (EditText) findViewById(R.id.login_account_edit_text);
        mPassowrdEditText = (EditText) findViewById(R.id.login_password_edit_text);
        mLoginButton = (Button) findViewById(R.id.login_button);
    }
    private void setupViews () {
        mLoginButton.setOnClickListener(this);
    }
    private void hideAllViews () {
        mAccountEditText.setVisibility(View.GONE);
        mPassowrdEditText.setVisibility(View.GONE);
        mLoginButton.setVisibility(View.GONE);
    }
    private void showAllViews () {
        mAccountEditText.setVisibility(View.VISIBLE);
        mPassowrdEditText.setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);

        WorkingData.setUserId(sharedPreferences.getString(WorkingData.USER_ID, ""));
        WorkingData.setUserToken(sharedPreferences.getString(WorkingData.USER_TOKEN, ""));

        CheckLoggedInStatusCommand checkLoggedInStatusCommand = new CheckLoggedInStatusCommand(this, this);
        checkLoggedInStatusCommand.execute();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                String account = mAccountEditText.getText().toString();
                String password = mPassowrdEditText.getText().toString();
                Log.d("DAZZZZ", "要登入摟 " + account + "/" + password);
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
}
