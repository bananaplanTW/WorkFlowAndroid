package com.bananaplan.workflowandroid.assigntask.tasks;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

import org.w3c.dom.Text;


/**
 * @author Danny Lin
 * @since 2015/7/16.
 */
public class TaskItemViewHolder extends RecyclerView.ViewHolder {

    public View view;
    public TextView title;
    public TextView workingTime;
    public TextView tool;
    public TextView worker;
    public TextView status;
    public TextView warning;

    public TaskItemViewHolder(View v) {
        super(v);
        view = v;
        title = (TextView) v.findViewById(R.id.task_title);
        warning = (TextView) v.findViewById(R.id.taskitem_listview_warning);
        workingTime = (TextView) v.findViewById(R.id.current_task_working_time);
        tool = (TextView) v.findViewById(R.id.task_tool);
        worker = (TextView) v.findViewById(R.id.task_worker);
        status = (TextView) v.findViewById(R.id.task_status);
    }
}
