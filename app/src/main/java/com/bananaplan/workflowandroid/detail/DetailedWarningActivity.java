package com.bananaplan.workflowandroid.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Manager;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.TaskWarning;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.Utils;


public class DetailedWarningActivity extends AppCompatActivity {

    private static final String TAG = "DetailedWarningActivity";

    public static final String EXTRA_WARNING_ID = "extra_warning_id";

    private static final String TAG_DETAILED_WARNING_STATUS_FRAGMENT = "tag_detailed_task_status_fragment";

    private ActionBar mActionBar;

    private TextView mActionBarWarningName;
    private TextView mActionBarTaskName;

    private TextView mInformationWarningName;
    private TextView mInformationTaskName;
    private TextView mInformationManagerName;
    private TextView mInformationWarningSpentTime;

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
        mActionBarWarningName = (TextView) findViewById(R.id.detailed_actionbar_warning_name);
        mActionBarTaskName = (TextView) findViewById(R.id.detailed_actionbar_warning_task_name);
        mInformationWarningName = (TextView) findViewById(R.id.detailed_information_warning_name);
        mInformationTaskName = (TextView) findViewById(R.id.detailed_information_task_name);
        mInformationManagerName = (TextView) findViewById(R.id.detailed_information_manager_name);
        mInformationWarningSpentTime = (TextView) findViewById(R.id.detailed_information_warning_spent_time);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        switch (mTaskWarning.status) {
            case OPENED:
                toolbar.setBackgroundColor(getResources().getColor(R.color.warning_opened_background_color));
                break;

            case CLOSED:
                toolbar.setBackgroundColor(getResources().getColor(R.color.warning_closed_background_color));
                break;
        }
    }

    private void setupViews() {
        Task warningTask = WorkingData.getInstance(this).getTaskById(mTaskWarning.taskId);
        Manager picManager = WorkingData.getInstance(this).getManagerById(mTaskWarning.managerId);

        mActionBarWarningName.setText(mTaskWarning.name);
        mActionBarTaskName.setText(warningTask.name);
        mInformationWarningName.setText(mTaskWarning.name);
        mInformationTaskName.setText(warningTask.name);
        mInformationManagerName.setText(picManager.name);
        mInformationWarningSpentTime.setText(Utils.millisecondsToTimeString(mTaskWarning.spentTime));
    }

    private void setupWarningLog() {
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
