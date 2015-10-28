package com.bananaplan.workflowandroid.warning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Manager;
import com.bananaplan.workflowandroid.data.Warning;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.warning.actions.CreateTaskWarningCommand;
import com.bananaplan.workflowandroid.utility.data.TextSpinnerAdapter;

import java.util.ArrayList;

/**
 * Created by daz on 10/28/15.
 */
public class AddWarningDialog  extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private String mTaskId;

    private Spinner mWarningListSpinner;
    private Spinner mManagerListSpinner;
    private EditText mCommentEditText;
    private TextView mCreateWarningButton;

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
        mWarningListData = WorkingData.getInstance(getApplicationContext()).getWarnings();
        mWarningListAdapter = new TextSpinnerAdapter<>(getApplicationContext(), R.layout.text_spinner_item, mWarningListData);
        mWarningListSpinner.setAdapter(mWarningListAdapter);

        mWarningListSpinner.setOnItemSelectedListener(this);
    }
    private void setupMangerList () {
        mManagerListData = WorkingData.getInstance(getApplicationContext()).getManagers();

        mManagerListAdapter = new TextSpinnerAdapter<>(getApplicationContext(), R.layout.text_spinner_item, mManagerListData);
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
                CreateTaskWarningCommand createTaskWarningCommand = new CreateTaskWarningCommand(getApplicationContext(), mTaskId, warning.id, manager.id, comment);
                createTaskWarningCommand.execute();
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
}
