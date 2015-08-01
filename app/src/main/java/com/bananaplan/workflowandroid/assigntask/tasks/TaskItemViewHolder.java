package com.bananaplan.workflowandroid.assigntask.tasks;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;


/**
 * @author Danny Lin
 * @since 2015/7/16.
 */
public class TaskItemViewHolder extends RecyclerView.ViewHolder {

    public View view;
    public TextView title;
    public ViewGroup warningContainer;
    public TextView workingTime;
    public TextView tool;
    public TextView worker;
    public TextView status;


    public TaskItemViewHolder(View v) {
        super(v);
        view = v;
        title = (TextView) v.findViewById(R.id.task_title);
        warningContainer = (ViewGroup) v.findViewById(R.id.task_status_container);
        workingTime = (TextView) v.findViewById(R.id.current_task_working_time);
        tool = (TextView) v.findViewById(R.id.task_tool);
        worker = (TextView) v.findViewById(R.id.task_worker);
        status = (TextView) v.findViewById(R.id.task_status);
    }
}
