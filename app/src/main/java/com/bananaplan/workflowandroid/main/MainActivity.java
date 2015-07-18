package com.bananaplan.workflowandroid.main;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.WorkingData;


public class MainActivity extends ActionBarActivity {

    private UIController mUIController;
    private WorkingData mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUIController = new UIController(this);
        mUIController.onCreate(savedInstanceState);
        // +++ ben
        mData = new WorkingData(this);
        mData.generateFakeData();
        // --- ben
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // +++ ben
        if (mUIController != null) {
            mUIController.onCreateOptionsMenu(menu);
        }
        // --- ben
        return true;
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

    // +++ ben
    public WorkingData getWorkingData() {
        return mData;
    }
    // --- ben
}
