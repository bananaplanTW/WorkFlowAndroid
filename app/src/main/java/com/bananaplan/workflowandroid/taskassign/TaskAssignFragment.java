package com.bananaplan.workflowandroid.taskassign;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;


/**
 *
 *
 * @author Danny Lin
 * @since 2015.05.30
 */
public class TaskAssignFragment extends Fragment {

    private Activity mActivity;

    private Spinner mFactorySpinner;
    private Spinner mTaskSpinner;
    private ArrayAdapter mFactoryAdapter;
    private ArrayAdapter mTaskAdapter;

    private String[] mFactoryData = {"Factory 1", "Factory 2", "Factory 3"};
    private String[] mTaskData = {"Case 1", "Case 2", "Case 3"};


    public TaskAssignFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_assign, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        findViews();
        initFactorySpinner();
        initTaskSpinner();
    }

    private void findViews() {
        mFactorySpinner = (Spinner) mActivity.findViewById(R.id.factory_spinner);
        mTaskSpinner = (Spinner) mActivity.findViewById(R.id.task_spinner);
    }

    private void initFactorySpinner() {
        mFactoryAdapter = new ArrayAdapter(mActivity, R.layout.factory_spinner_item, mFactoryData);
        mFactoryAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mFactorySpinner.setAdapter(mFactoryAdapter);
    }

    private void initTaskSpinner() {
        mTaskAdapter = new ArrayAdapter(mActivity, R.layout.task_spinner_item, mTaskData);
        mTaskAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mTaskSpinner.setAdapter(mTaskAdapter);
    }
}
