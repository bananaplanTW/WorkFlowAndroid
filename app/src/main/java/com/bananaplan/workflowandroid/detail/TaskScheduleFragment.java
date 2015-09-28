package com.bananaplan.workflowandroid.detail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.Utils;


/**
 * Task schedule of a worker
 *
 * @author Danny Lin
 * @since 2015/9/21.
 */
public class TaskScheduleFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private View mMainView;

    private TextView mCompleteTaskButton;
    private TextView mAddWarningButton;
    private TextView mManageWarningButton;

    // Information of the current task
    private TextView mCurrentCase;
    private TextView mCurrentTask;
    private TextView mCurrentExpectedTime;
    private TextView mCurrentSpentTime;
    private TextView mCurrentEquipment;
    private TextView mCurrentExpectedCompletedTime;
    private TextView mCurrentError;
    private TextView mCurrentWarnings;

    private Worker mWorker;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_task_schedule, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        mMainView = getView();
        mWorker = WorkingData.getInstance(mContext).getWorkerById(getArguments().getString(DetailedWorkerActivity.EXTRA_WORKER_ID));
        findViews();
        setupCurrentTask();
        setupButtons();
    }

    private void findViews() {
        mCompleteTaskButton = (TextView) mMainView.findViewById(R.id.complete_task_button);
        mAddWarningButton = (TextView) mMainView.findViewById(R.id.add_warning_button);
        mManageWarningButton = (TextView) mMainView.findViewById(R.id.manage_warning_button);

        mCurrentCase = (TextView) mMainView.findViewById(R.id.detailed_worker_task_schedule_case);
        mCurrentTask = (TextView) mMainView.findViewById(R.id.detailed_worker_task_schedule_task);
        mCurrentExpectedTime = (TextView) mMainView.findViewById(R.id.detailed_worker_task_schedule_expected_time);
        mCurrentSpentTime = (TextView) mMainView.findViewById(R.id.detailed_worker_task_schedule_spent_time);
        mCurrentEquipment = (TextView) mMainView.findViewById(R.id.detailed_worker_task_schedule_equipment);
        mCurrentExpectedCompletedTime = (TextView) mMainView.findViewById(R.id.detailed_worker_task_schedule_expected_completed_time);
        mCurrentError = (TextView) mMainView.findViewById(R.id.detailed_worker_task_schedule_error);
        mCurrentWarnings = (TextView) mMainView.findViewById(R.id.taskitem_listview_warning);
    }

    private void setupCurrentTask() {
        if (!mWorker.hasCurrentTask()) return;

        mCurrentCase.setText(WorkingData.getInstance(mContext).getCaseById(mWorker.currentTask.caseId).name);
        mCurrentTask.setText(mWorker.currentTask.name);
        // TODO: Expected time
        // TODO: Spent time
        mCurrentEquipment.setText(WorkingData.getInstance(mContext).getEquipmentById(mWorker.currentTask.equipmentId).name);
        // TODO: Expected completed time
        // TODO: Error
        Utils.setTaskItemWarningTextView((Activity) mContext, mWorker.currentTask, mCurrentWarnings, true);
    }

    private void setupButtons() {
        mCompleteTaskButton.setOnClickListener(this);
        mAddWarningButton.setOnClickListener(this);
        mManageWarningButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complete_task_button:
                break;
            case R.id.add_warning_button:
                break;
            case R.id.manage_warning_button:
                break;
        }
    }
}
