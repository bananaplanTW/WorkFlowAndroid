package com.bananaplan.workflowandroid.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.overview.StatusFragment;


public class DetailedTaskActivity extends AppCompatActivity {

    private static final String TAG = "DetailedTaskActivity";

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private static final String TAG_DETAILED_TASK_STATUS_FRAGMENT = "tag_detailed_task_status_fragment";

    private ActionBar mActionBar;

    private TextView mDetailedTaskName;
    private TextView mDetailedCaseName;

    private Task mTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task);
        initialize(getIntent());
    }

    private void initialize(Intent intent) {
        mTask = WorkingData.getInstance(this).getTaskById(intent.getStringExtra(EXTRA_TASK_ID));
        findViews();
        setupActionBar();
        setupViews();
        setupTaskLog();
    }

    private void findViews() {
        mDetailedTaskName = (TextView) findViewById(R.id.detailed_task_name);
        mDetailedCaseName = (TextView) findViewById(R.id.detailed_case_name);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_detailed_worker_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        mDetailedTaskName.setText(mTask.name);
        mDetailedCaseName.setText(WorkingData.getInstance(this).getCaseById(mTask.caseId).name);
    }

    private void setupTaskLog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = fragmentManager.findFragmentByTag(TAG_DETAILED_TASK_STATUS_FRAGMENT);
        if (fragment == null) {
            fragment = new DetailedTaskStatusFragment();

            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_TASK_ID, mTask.id);

            fragment.setArguments(bundle);
            fragmentTransaction.add(R.id.detailed_task_log_container, fragment, TAG_DETAILED_TASK_STATUS_FRAGMENT);
        }

        fragmentTransaction.commit();
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
