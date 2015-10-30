package com.bananaplan.workflowandroid.detail.task;

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
import com.bananaplan.workflowandroid.data.Equipment;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.Utils;


public class DetailedTaskActivity extends AppCompatActivity {

    private static final String TAG = "DetailedTaskActivity";

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private static final String TAG_DETAILED_TASK_STATUS_FRAGMENT = "tag_detailed_task_status_fragment";

    private ActionBar mActionBar;

    private TextView mDetailedTaskName;
    private TextView mDetailedCaseName;

    private TextView mDetailedInformationCaseName;
    private TextView mDetailedInformationTaskName;
    private TextView mDetailedInformaationExpectedTime;
    private TextView mDetailedInformationSpentTime;
    private TextView mDetailedInformationEquipment;
    private TextView mDetailedInformationWarning;

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
        mDetailedInformationCaseName = (TextView) findViewById(R.id.detailed_information_case_name);
        mDetailedInformationTaskName = (TextView) findViewById(R.id.detailed_information_task_name);
        mDetailedInformaationExpectedTime = (TextView) findViewById(R.id.detailed_information_expected_time);
        mDetailedInformationSpentTime = (TextView) findViewById(R.id.detailed_information_spent_time);
        mDetailedInformationEquipment = (TextView) findViewById(R.id.detailed_information_equipment);
        mDetailedInformationWarning = (TextView) findViewById(R.id.detailed_information_warning);
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
        String taskName = mTask.name;
        String caseName = WorkingData.getInstance(this).getCaseById(mTask.caseId).name;
        Equipment equipment = WorkingData.getInstance(this).getEquipmentById(mTask.equipmentId);

        mDetailedTaskName.setText(taskName);
        mDetailedCaseName.setText(caseName);
        mDetailedInformationCaseName.setText(caseName);
        mDetailedInformationTaskName.setText(taskName);
        mDetailedInformaationExpectedTime.setText(Utils.millisecondsToTimeString(mTask.expectedTime));
        mDetailedInformationSpentTime.setText(Utils.millisecondsToTimeString(mTask.spentTime));
        mDetailedInformationEquipment.setText(equipment == null ?
                getString(R.string.no_equipment) : equipment.name);
        Utils.setTaskItemWarningTextView(this, mTask, mDetailedInformationWarning, true);
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
