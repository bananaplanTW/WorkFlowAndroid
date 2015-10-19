package com.bananaplan.workflowandroid.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bananaplan.workflowandroid.R;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private UIController mUIController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUIController = new UIController(this);
        mUIController.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUIController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUIController.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mUIController.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mUIController.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mUIController.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mUIController.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUIController.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mUIController.isDrawerOpen()) {
            mUIController.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void openNavigationDrawer() {
        if (mUIController != null) {
            mUIController.openDrawer();
        }
    }
}
