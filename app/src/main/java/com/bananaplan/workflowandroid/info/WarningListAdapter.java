package com.bananaplan.workflowandroid.info;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.TaskWarning;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.List;

/**
 * Created by logicmelody on 2015/11/10.
 */
public class WarningListAdapter extends ArrayAdapter<TaskWarning> {

    private Context mContext;

    private class ViewHolder {

        TextView title;
        TextView _case;
        TextView task;
        TextView manager;

        public ViewHolder(View v) {
            title = (TextView) v.findViewById(R.id.title);
            _case = (TextView) v.findViewById(R.id.case_name);
            task = (TextView) v.findViewById(R.id.task);
            manager = (TextView) v.findViewById(R.id.manager);
        }
    }

    public WarningListAdapter(Context context, List<TaskWarning> taskWarnings) {
        super(context, 0, taskWarnings);
        mContext = context;
    }

    public void updateData(List<TaskWarning> data) {
        clear();
        addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_information_list_warning_content, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WorkingData data = WorkingData.getInstance(mContext);
        TaskWarning taskWarning = getItem(position);

        if (taskWarning != null) {
            Utils.setTaskItemWarningTextView((Activity) mContext, data.getTaskById(taskWarning.taskId), holder.title, false);
            holder._case.setText(data.getCaseById(data.getTaskById(taskWarning.taskId).caseId).name);
            holder.task.setText(data.getTaskById(taskWarning.taskId).name);
            holder.manager.setText(data.getManagerById(taskWarning.managerId).name);
        }

        return convertView;
    }
}