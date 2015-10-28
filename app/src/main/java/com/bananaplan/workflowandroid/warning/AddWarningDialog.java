package com.bananaplan.workflowandroid.warning;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Manager;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.TaskWarning;
import com.bananaplan.workflowandroid.data.Warning;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.warning.actions.CreateTaskWarningCommand;
import com.bananaplan.workflowandroid.detail.TaskScheduleFragment;
import com.bananaplan.workflowandroid.utility.data.TextSpinnerAdapter;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by daz on 10/28/15.
 */
public class AddWarningDialog  extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        CreateTaskWarningCommand.OnFinishedCreatingTaskWarningListener{

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private String mTaskId;

    private Spinner mWarningListSpinner;
    private Spinner mManagerListSpinner;
    private EditText mCommentEditText;
    private TextView mCreateWarningButton;
    private ProgressDialog mProgressDialog;

    private ArrayList<Warning> mWarningListData;
    private TextSpinnerAdapter<Warning> mWarningListAdapter;

    private ArrayList<Manager> mManagerListData;
    private TextSpinnerAdapter<Manager> mManagerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_warning);

        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        initialize();
    }

    private void initialize () {
        findViews();
        setupViews();
        setupWarningList();
        setupMangerList();
    }
    private void findViews () {
        mWarningListSpinner = (Spinner) findViewById(R.id.warning_list);
        mManagerListSpinner = (Spinner) findViewById(R.id.manager_list);
        mCommentEditText = (EditText) findViewById(R.id.warning_text);
        mCreateWarningButton = (TextView) findViewById(R.id.create_warning);
    }
    private void setupViews () {
        mCreateWarningButton.setOnClickListener(this);
    }
    private void setupWarningList () {
        mWarningListData = WorkingData.getInstance(this).getWarnings();
        mWarningListAdapter = new TextSpinnerAdapter<>(this, R.layout.text_spinner_item, mWarningListData);
        mWarningListSpinner.setAdapter(mWarningListAdapter);

        mWarningListSpinner.setOnItemSelectedListener(this);
    }
    private void setupMangerList () {
        mManagerListData = WorkingData.getInstance(this).getManagers();

        mManagerListAdapter = new TextSpinnerAdapter<>(this, R.layout.text_spinner_item, mManagerListData);
        mManagerListSpinner.setAdapter(mManagerListAdapter);

        mManagerListSpinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_warning:
                Warning warning = (Warning) mWarningListSpinner.getSelectedItem();
                Manager manager = (Manager) mManagerListSpinner.getSelectedItem();
                String comment = mCommentEditText.getText().toString();
                CreateTaskWarningCommand createTaskWarningCommand = new CreateTaskWarningCommand(this, mTaskId, warning.id, manager.id, comment, this);
                createTaskWarningCommand.execute();

                mProgressDialog = ProgressDialog.show(this, "Adding warning", "please wait...", true);
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.warning_list:
                break;
            case R.id.manager_list:
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onCreatedTaskWarning(String taskWarningId) {
        mProgressDialog.dismiss();

        Task task = WorkingData.getInstance(this).getTaskById(mTaskId);
        Warning warning = (Warning) mWarningListSpinner.getSelectedItem();
        Manager manager = (Manager) mManagerListSpinner.getSelectedItem();
        // [TODO] should use factory
        TaskWarning taskWarning = new TaskWarning(
                taskWarningId, warning.name, task.caseId, mTaskId, task.workerId, manager.id, TaskWarning.Status.OPEN, 0, new Date().getTime());

        WorkingData.getInstance(this).addTaskWarning(taskWarning);
        task.taskWarnings.add(taskWarning);
        setResult(Activity.RESULT_OK);
        finish();
    }
    @Override
    public void onFailCreatingTaskWarning() {
        mProgressDialog.dismiss();
        finish();
    }
}
