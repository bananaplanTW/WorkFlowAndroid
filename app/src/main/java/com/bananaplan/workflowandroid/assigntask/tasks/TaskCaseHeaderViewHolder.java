package com.bananaplan.workflowandroid.assigntask.tasks;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

/**
 * @author Danny Lin
 * @since 2015/7/16.
 */
public class TaskCaseHeaderViewHolder extends RecyclerView.ViewHolder {

    public View header;
    public Spinner taskCaseSpinner;
    public TextView uncompletedTaskTime;
    public TextView undergoingTaskTime;
    public TextView undergoingWorkerCount;

    public boolean isTaskCaseSpinnerInitialized = false;

    public TaskCaseHeaderViewHolder(View v) {
        super(v);
        header = v;
        taskCaseSpinner = (Spinner) v.findViewById(R.id.task_case_spinner);
        uncompletedTaskTime = (TextView) v.findViewById(R.id.uncompleted_task_time);
        undergoingTaskTime = (TextView) v.findViewById(R.id.undergoing_task_time);
        undergoingWorkerCount = (TextView) v.findViewById(R.id.undergoing_worker_count);
    }
}
