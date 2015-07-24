package com.bananaplan.workflowandroid.assigntask.tasks;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.caseoverview.CaseCustomProgressBar;

/**
 * @author Danny Lin
 * @since 2015/7/16.
 */
public class TaskCaseHeaderViewHolder extends RecyclerView.ViewHolder {

    public View header;
    public Spinner taskCaseSpinner;
    public CaseCustomProgressBar progressBar;
    public TextView vendor;
    public TextView personInCharge;
    public TextView uncompletedTaskTime;
    public TextView undergoingTaskTime;
    public TextView editCaseButton;

    public boolean isTaskCaseSpinnerInitialized = false;

    public TaskCaseHeaderViewHolder(View v) {
        super(v);
        header = v;
        taskCaseSpinner = (Spinner) v.findViewById(R.id.task_case_spinner);
        progressBar = (CaseCustomProgressBar) v.findViewById(R.id.task_case_information_progressbar);
        vendor = (TextView) v.findViewById(R.id.task_case_principal_vendor);
        personInCharge = (TextView) v.findViewById(R.id.task_case_person_in_charge);
        uncompletedTaskTime = (TextView) v.findViewById(R.id.task_case_hours_unfinished);
        undergoingTaskTime = (TextView) v.findViewById(R.id.task_case_hours_pass_by);
        editCaseButton = (TextView) v.findViewById(R.id.task_case_edit_button);
    }
}
