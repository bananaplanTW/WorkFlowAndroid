package com.bananaplan.workflowandroid.assigntask.tasks;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

/**
 * @author Danny Lin
 * @since 2015/7/16.
 */
public class TaskItemViewHolder extends RecyclerView.ViewHolder {

    public View taskItem;
    public TextView taskTitle;
    public TextView taskSubtitle;
    public TextView taskStatus;
    public TextView taskTime;


    public TaskItemViewHolder(View v) {
        super(v);
        taskItem = v;
        taskTitle = (TextView) v.findViewById(R.id.task_title);
        taskSubtitle = (TextView) v.findViewById(R.id.task_subtitle);
        taskStatus = (TextView) v.findViewById(R.id.task_status);
        taskTime = (TextView) v.findViewById(R.id.task_time);
    }
}
