package com.bananaplan.workflowandroid.assigntask.tasks;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Equipment;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.view.CustomProgressBar;
import com.bananaplan.workflowandroid.utility.view.SquareAvatar;


/**
 * Adapter to control and show a task case
 * Task list is composed of a header and a grid view
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class CaseAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "TaskCaseAdapter";

    private static class ItemViewType {
        public static final int HEADER = 0;
        public static final int ITEM = 1;
    }

    private Context mContext;

    private Case mSelectedCase = null;


    private class CaseHeaderViewHolder extends RecyclerView.ViewHolder {

        public View header;
        public ViewGroup participatedWorkerContainer;
        public CustomProgressBar progressBar;
        public TextView vendor;
        public TextView pic;
        public TextView unfinishedTime;
        public TextView spentTime;
        public TextView editCaseButton;


        public CaseHeaderViewHolder(View v) {
            super(v);
            header = v;
            participatedWorkerContainer = (ViewGroup) v.findViewById(R.id.participated_worker_container);
            progressBar = (CustomProgressBar) v.findViewById(R.id.case_information_progressbar);
            vendor = (TextView) v.findViewById(R.id.case_principal_vendor);
            pic = (TextView) v.findViewById(R.id.case_pic);
            spentTime = (TextView) v.findViewById(R.id.case_spent_time);
            unfinishedTime = (TextView) v.findViewById(R.id.case_unfinished_time);
            editCaseButton = (TextView) v.findViewById(R.id.case_edit_button);
        }
    }

    public class TaskCardViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView name;
        public TextView workingTime;
        public TextView equipment;
        public TextView worker;
        public TextView status;
        public TextView warning;

        public TaskCardViewHolder(View v) {
            super(v);
            view = v;
            name = (TextView) v.findViewById(R.id.task_card_title);
            warning = (TextView) v.findViewById(R.id.listview_task_warning);
            workingTime = (TextView) v.findViewById(R.id.task_card_current_task_working_time);
            equipment = (TextView) v.findViewById(R.id.task_card_equipment);
            worker = (TextView) v.findViewById(R.id.task_card_worker);
            status = (TextView) v.findViewById(R.id.task_card_status);
        }
    }

    /**
     * When initialize the adapter, we should pass all of task cases' titles and the current task case data
     * to be displayed.
     */
    public CaseAdapter(Context context, Case firstDisplayedCase) {
        mContext = context;
        mSelectedCase = firstDisplayedCase;
    }

    public void swapCase(Case aCase) {
        mSelectedCase = aCase;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ItemViewType.HEADER == viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.assign_task_case_header, parent, false);
            return new CaseHeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.task_card, parent, false);
            return new TaskCardViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        if (isHeaderPosition(position)) {
            onBindHeaderViewHolder(vh);
        } else {
            onBindItemViewHolder(vh, position);
        }
    }

    private void onBindHeaderViewHolder(ViewHolder vh) {
        CaseHeaderViewHolder holder = (CaseHeaderViewHolder) vh;
        int avatarCount = mContext.getResources().getInteger(R.integer.assign_task_case_information_avatar_count);
        int avatarWidthHeight = mContext.getResources().
                getDimensionPixelSize(R.dimen.task_case_information_participated_worker_avatar_width_height);
        int avatarPadding = mContext.getResources().
                getDimensionPixelSize(R.dimen.task_case_information_participated_worker_avatar_padding);

        holder.progressBar.setProgress(mSelectedCase.getFinishPercent());
        holder.vendor.setText(WorkingData.getInstance(mContext).getVendorById(mSelectedCase.vendorId).name);
        holder.pic.setText(WorkingData.getInstance(mContext).getManagerById(mSelectedCase.managerId).name);
        holder.spentTime.setText(Utils.millisecondsToTimeString(mSelectedCase.getSpentTime()));
        holder.unfinishedTime.setText(Utils.millisecondsToTimeString(mSelectedCase.getUnfinishedTime()));

        holder.participatedWorkerContainer.removeAllViews();
        int count = 0;
        for (String workerId : mSelectedCase.workerIds) {
            if (count == avatarCount) break;

            SquareAvatar avatar = null;
            if (count == avatarCount - 1) {
                avatar = new SquareAvatar(mContext, null, "+" + String.valueOf(mSelectedCase.workerIds.size() - count));
            } else {
                avatar = new SquareAvatar(mContext,
                                          WorkingData.getInstance(mContext).getWorkerById(workerId).getAvator(), null);
            }

            avatar.setLayoutParams(new RelativeLayout.LayoutParams(avatarWidthHeight, avatarWidthHeight));
            avatar.setPadding(avatarPadding, avatarPadding, avatarPadding, avatarPadding);

            holder.participatedWorkerContainer.addView(avatar);
            count++;
        }

        holder.editCaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Edit case", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onBindItemViewHolder(ViewHolder vh, int position) {
        TaskCardViewHolder holder = (TaskCardViewHolder) vh;
        Task task = getItem(position);

        // Name
        holder.name.setText(task.name);

        // Warning
        Utils.setTaskItemWarningTextView((Activity) mContext, task, holder.warning, false);

        // Task expected time
        holder.workingTime.setText(Utils.millisecondsToTimeString(task.expectedTime));

        // Equipment
        Equipment equipment = WorkingData.getInstance(mContext).getEquipmentById(task.equipmentId);
        holder.equipment.setText(equipment == null ?
                mContext.getString(R.string.task_card_no_equipment) : equipment.name);

        // Worker
        Worker worker = WorkingData.getInstance(mContext).getWorkerById(task.workerId);
        holder.worker.setText(worker == null ?
                mContext.getString(R.string.task_card_no_worker) : worker.name);

        // Status
        holder.status.setText(Utils.getTaskItemStatusString(mContext, task));
    }

    public Task getItem(int position) {
        return mSelectedCase.tasks.get(--position);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = ItemViewType.ITEM;
        if (isHeaderPosition(position)) {
            viewType = ItemViewType.HEADER;
        }

        return viewType;
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return mSelectedCase.tasks == null ? 0 : mSelectedCase.tasks.size() + 1;
    }
}
