package com.bananaplan.workflowandroid.assigntask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

import java.util.List;


/**
 * Adapter to control the data in task list
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private Context mContext;
    private List<TaskItem> mTaskDatas;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public View taskItem;
        public TextView taskTitle;
        public TextView taskSubtitle;
        public TextView taskStatus;
        public TextView taskTime;


        public ViewHolder(View v) {
            super(v);
            taskItem = v;
            taskTitle = (TextView) v.findViewById(R.id.task_title);
            taskSubtitle = (TextView) v.findViewById(R.id.task_subtitle);
            taskStatus = (TextView) v.findViewById(R.id.task_status);
            taskTime = (TextView) v.findViewById(R.id.task_time);
        }
    }

    public TaskListAdapter(Context context, List<TaskItem> taskDatas) {
        mContext = context;
        mTaskDatas = taskDatas;
    }

    public void setTaskDatas(List<TaskItem> taskDatas) {
        mTaskDatas = taskDatas;
    }

    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.task_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.taskTitle.setText(mTaskDatas.get(position).title);
        holder.taskSubtitle.setText(mTaskDatas.get(position).subtitle);
        holder.taskTime.setText(mTaskDatas.get(position).time);

        Resources res = mContext.getResources();
        int color = 0;
        switch (mTaskDatas.get(position).status) {
            case TaskItem.Status.UNDERGOING:
                holder.taskStatus.setText("進行中");
                color = res.getColor(R.color.task_item_status_undergoing_color);
                break;
            case TaskItem.Status.OVERTIME:
                holder.taskStatus.setText("超過預定");
                color = res.getColor(R.color.task_item_status_overtime_color);
                break;
            case TaskItem.Status.COMPLETED:
//                holder.taskItem.getBackground().setColorFilter(
//                        res.getColor(R.color.task_item_status_completed_filter_color), PorterDuff.Mode.MULTIPLY);
                holder.taskStatus.setText("已經完成");
                color = res.getColor(R.color.task_item_status_completed_color);
                break;
        }
        GradientDrawable taskStatusBackground = (GradientDrawable) holder.taskStatus.getBackground();
        taskStatusBackground.setColor(color);
    }

    @Override
    public int getItemCount() {
        return mTaskDatas.size();
    }
}
