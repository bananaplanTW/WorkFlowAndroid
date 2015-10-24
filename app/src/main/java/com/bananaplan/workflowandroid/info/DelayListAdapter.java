package com.bananaplan.workflowandroid.info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.task.actions.IncrementTaskAlertScheduleCommand;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/10/21.
 */
public class DelayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ADD_NOTIFY_TIME = 30;

    private Context mContext;
    private List<Task> mData;


    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView delayTime;
        public TextView caseName;
        public TextView taskName;
        public TextView pic;
        public TextView nextNotifyTime;
        public TextView addNotifyTimeButton;

        public ItemViewHolder(View view) {
            super(view);
            findViews(view);
            setupButtons();
        }

        private void findViews(View view) {
            delayTime = (TextView) view.findViewById(R.id.main_information_list_title_delay_time);
            caseName = (TextView) view.findViewById(R.id.main_information_list_title_case_name);
            taskName = (TextView) view.findViewById(R.id.main_information_list_title_task_name);
            pic = (TextView) view.findViewById(R.id.main_information_list_title_pic);
            nextNotifyTime = (TextView) view.findViewById(R.id.main_information_list_title_next_notify_time);
            addNotifyTimeButton = (TextView) view.findViewById(R.id.main_information_list_title_add_notify_time_button);
        }

        private void setupButtons() {
            addNotifyTimeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_information_list_title_add_notify_time_button:
                    // [TODO] should user current notification time, if not, use current time
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis() + 1800000);
                    String taskId = mData.get(getAdapterPosition()).id;
                    IncrementTaskAlertScheduleCommand incrementTaskAlertScheduleCommand = new IncrementTaskAlertScheduleCommand(mContext, taskId, c.getTimeInMillis(), "+30分鐘");
                    incrementTaskAlertScheduleCommand.execute();
                    break;
            }
        }
    }

    public DelayListAdapter(Context context, List<Task> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.main_information_list_delay_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemVH = (ItemViewHolder) holder;
        Task task = mData.get(position);

        itemVH.delayTime.setText(Utils.millisecondsToTimeString(task.getWorkingTime()-task.expectedTime));
        itemVH.caseName.setText(WorkingData.getInstance(mContext).getCaseById(task.caseId).name);
        itemVH.taskName.setText(task.name);
        itemVH.pic.setText(WorkingData.getInstance(mContext).getWorkerById(task.workerId).name);
        itemVH.addNotifyTimeButton.setText(
                String.format(mContext.getString(R.string.main_information_add_notify_time_button), ADD_NOTIFY_TIME));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
