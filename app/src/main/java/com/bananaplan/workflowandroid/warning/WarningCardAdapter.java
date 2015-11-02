package com.bananaplan.workflowandroid.warning;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Manager;
import com.bananaplan.workflowandroid.data.TaskWarning;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.detail.warning.DetailedWarningActivity;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/11/3.
 */
public class WarningCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<TaskWarning> mDataSet;


    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View view;
        public ViewGroup taskPartContainer;
        public TextView warningName;
        public TextView taskName;
        public TextView managerName;
        public TextView time;

        public ItemViewHolder(View view) {
            super(view);
            this.view = view;
            taskPartContainer = (ViewGroup) view.findViewById(R.id.warning_card_task_part_container);
            warningName = (TextView) view.findViewById(R.id.warning_card_name);
            taskName = (TextView) view.findViewById(R.id.warning_card_task_name);
            managerName = (TextView) view.findViewById(R.id.warning_card_manager);
            time = (TextView) view.findViewById(R.id.warning_card_time);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            showDetailedWarning(getAdapterPosition());
        }

        private void showDetailedWarning(int adapterPosition) {
            TaskWarning selectedTaskWarning = mDataSet.get(adapterPosition);
            Intent intent = new Intent(mContext, DetailedWarningActivity.class);
            intent.putExtra(DetailedWarningActivity.EXTRA_WARNING_ID, selectedTaskWarning.id);

            mContext.startActivity(intent);
        }
    }

    public WarningCardAdapter(Context mContext, List<TaskWarning> mDataSet) {
        this.mContext = mContext;
        this.mDataSet = mDataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.warning_card, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemVH = (ItemViewHolder) holder;
        TaskWarning taskWarning = mDataSet.get(position);
        Manager manager = WorkingData.getInstance(mContext).getManagerById(taskWarning.managerId);

        setWarningCardColor(itemVH, taskWarning.status);
        itemVH.warningName.setText(taskWarning.name);
        itemVH.taskName.setText(WorkingData.getInstance(mContext).getTaskById(taskWarning.taskId).name);
        itemVH.managerName.setText(manager != null ? manager.name : "");
        itemVH.time.setText(Utils.millisecondsToTimeString(taskWarning.spentTime));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private void setWarningCardColor(ItemViewHolder holder, TaskWarning.Status status) {
        Resources resources = mContext.getResources();
        GradientDrawable warningCardTaskPartBackground = (GradientDrawable) holder.taskPartContainer.getBackground();

        switch (status) {
            case OPENED:
                warningCardTaskPartBackground.
                        setColor(resources.getColor(R.color.warning_opened_background_color));

                break;

            case CLOSED:
                warningCardTaskPartBackground.
                        setColor(resources.getColor(R.color.warning_closed_background_color));

                break;
        }
    }
}
