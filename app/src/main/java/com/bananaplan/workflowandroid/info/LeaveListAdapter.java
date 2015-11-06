package com.bananaplan.workflowandroid.info;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.LeaveInMainInfo;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.detail.task.DetailedTaskActivity;
import com.bananaplan.workflowandroid.detail.worker.DetailedWorkerActivity;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.Date;
import java.util.List;

/**
 * @author Danny Lin
 * @since 2015/10/26.
 */
public class LeaveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<LeaveInMainInfo> mDataSet;


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView leaveType;
        public TextView leaveWorker;
        public TextView leaveDate;

        public ItemViewHolder(View view) {
            super(view);
            this.view = view;
            leaveType = (TextView) view.findViewById(R.id.main_information_list_leave_type);
            leaveWorker = (TextView) view.findViewById(R.id.main_information_list_leave_worker);
            leaveDate = (TextView) view.findViewById(R.id.main_information_list_leave_date);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailedWorkerActivity.class);
                    intent.putExtra(DetailedWorkerActivity.EXTRA_WORKER_ID, mDataSet.get(getAdapterPosition()).workerId);

                    mContext.startActivity(intent);
                }
            });
        }
    }

    public LeaveListAdapter(Context context, List<LeaveInMainInfo> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.main_information_list_leave_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemVH = (ItemViewHolder) holder;
        LeaveInMainInfo leaveInMainInfo = mDataSet.get(position);

        itemVH.leaveType.setText(LeaveInMainInfo.getLeaveString(mContext, leaveInMainInfo.type));
        itemVH.leaveWorker.setText(WorkingData.getInstance(mContext).getWorkerById(leaveInMainInfo.workerId).name);
        itemVH.leaveDate.setText(Utils.timestamp2Date(new Date(leaveInMainInfo.from), Utils.DATE_FORMAT_YMD));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
