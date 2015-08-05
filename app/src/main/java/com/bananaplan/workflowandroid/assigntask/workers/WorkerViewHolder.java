package com.bananaplan.workflowandroid.assigntask.workers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

/**
 * @author Danny Lin
 * @since 2015/7/29.
 */
public class WorkerViewHolder extends RecyclerView.ViewHolder {

    public ImageView avatar;
    public TextView name;
    public TextView title;

    public ViewGroup currentWarnings;
    public TextView currentTaskTitle;
    public TextView currentTaskId;
    public TextView currentTaskWorkingTime;

    public TextView nextTaskTitle;


    public WorkerViewHolder(View view) {
        super(view);
        avatar = (ImageView) view.findViewById(R.id.worker_avatar);
        name = (TextView) view.findViewById(R.id.worker_name);
        title = (TextView) view.findViewById(R.id.worker_title);
        currentWarnings = (ViewGroup) view.findViewById(R.id.current_warning_container);
        currentTaskTitle = (TextView) view.findViewById(R.id.current_task_title);
        currentTaskId = (TextView) view.findViewById(R.id.current_task_id);
        currentTaskWorkingTime = (TextView) view.findViewById(R.id.current_task_working_time);
        nextTaskTitle = (TextView) view.findViewById(R.id.worker_item_next_task);
    }
}
