package com.bananaplan.workflowandroid.info;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Leave;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.Date;
import java.util.List;

/**
 * @author Danny Lin
 * @since 2015/10/26.
 */
public class LeaveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<Leave> mDataSet;


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView leaveType;
        public TextView leaveWorker;
        public TextView leaveDate;

        public ItemViewHolder(View view) {
            super(view);
            leaveType = (TextView) view.findViewById(R.id.main_information_list_leave_type);
            leaveWorker = (TextView) view.findViewById(R.id.main_information_list_leave_worker);
            leaveDate = (TextView) view.findViewById(R.id.main_information_list_leave_date);
        }
    }

    public LeaveListAdapter(Context context, List<Leave> dataSet) {
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
        Leave leave = mDataSet.get(position);

        itemVH.leaveType.setText(Leave.getLeaveString(mContext, leave));
        itemVH.leaveWorker.setText(WorkingData.getInstance(mContext).getWorkerById(leave.workerId).name);
        itemVH.leaveDate.setText(Utils.timestamp2Date(new Date(leave.from), Utils.DATE_FORMAT_YMD));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
