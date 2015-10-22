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
        public Button addNotifyTimeButton;

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
            addNotifyTimeButton = (Button) view.findViewById(R.id.main_information_list_title_add_notify_time_button);
        }

        private void setupButtons() {
            addNotifyTimeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_information_list_title_add_notify_time_button:
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

        itemVH.delayTime.setText("");
        itemVH.caseName.setText(WorkingData.getInstance(mContext).getCaseById(mData.get(position).caseId).name);
        itemVH.taskName.setText(mData.get(position).name);
        itemVH.pic.setText(WorkingData.getInstance(mContext).getWorkerById(mData.get(position).workerId).name);
        itemVH.addNotifyTimeButton.setText(
                String.format(mContext.getString(R.string.main_information_add_notify_time_button), ADD_NOTIFY_TIME));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
