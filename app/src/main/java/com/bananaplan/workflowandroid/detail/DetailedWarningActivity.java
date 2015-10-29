package com.bananaplan.workflowandroid.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.TaskWarning;
import com.bananaplan.workflowandroid.data.WorkingData;


public class DetailedWarningActivity extends AppCompatActivity {

    private static final String TAG = "DetailedWarningActivity";

    public static final String EXTRA_WARNING_ID = "extra_warning_id";

    private static final String TAG_DETAILED_WARNING_STATUS_FRAGMENT = "tag_detailed_task_status_fragment";

    private ActionBar mActionBar;

    private TaskWarning mTaskWarning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_warning);
        initialize(getIntent());
    }

    private void initialize(Intent intent) {
        mTaskWarning = WorkingData.getInstance(this).getTaskWarningById(intent.getStringExtra(EXTRA_WARNING_ID));
        findViews();
        setupActionBar();
        setupViews();
    }

    private void findViews() {

    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {

    }

    private void setupTaskLog() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        Fragment fragment = fragmentManager.findFragmentByTag(TAG_DETAILED_WARNING_STATUS_FRAGMENT);
//        if (fragment == null) {
//            fragment = new DetailedTaskStatusFragment();
//
//            Bundle bundle = new Bundle();
//            bundle.putString(EXTRA_WARNING_ID, mTask.id);
//
//            fragment.setArguments(bundle);
//            fragmentTransaction.add(R.id.detailed_task_log_container, fragment, TAG_DETAILED_WARNING_STATUS_FRAGMENT);
//        }
//
//        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
