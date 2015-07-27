package com.bananaplan.workflowandroid.assigntask.workers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
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
    public TextView task;
    public TextView currentTaskWorkingTime;


    public WorkerViewHolder(View view) {
        super(view);
        avatar = (ImageView) view.findViewById(R.id.worker_avatar);
        name = (TextView) view.findViewById(R.id.worker_name);
        title = (TextView) view.findViewById(R.id.worker_title);
        task = (TextView) view.findViewById(R.id.current_task_title);
        currentTaskWorkingTime = (TextView) view.findViewById(R.id.current_task_working_time);
    }
}
