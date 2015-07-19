package com.bananaplan.workflowandroid.assigntask.tasks;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;


/**
 * Adapter to control and show a task case
 * Task list is composed of a header and a grid view
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskCaseAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "TaskListAdapter";

    public interface OnSelectTaskCaseListener {
        public void onSelectTaskCase(int position);  //TODO: Should pass task case id
    }

    private static class ItemViewType {
        public static final int TYPE_HEADER = -2;
        public static final int TYPE_ITEM = -1;
    }

    private Context mContext;

    private String[] mTaskCaseTitles = null;
    private TaskCase mTaskCase = null;

    private ArrayAdapter mTaskCaseSpinnerAdapter;

    private OnSelectTaskCaseListener mOnSelectTaskCaseListener;

    private int mSelectedTaskCasePosition = 0;
    private boolean mIsTaskCaseSpinnerInitialized = false;


    public void setOnSelectTaskCaseListener(OnSelectTaskCaseListener listener) {
        mOnSelectTaskCaseListener = listener;
    }

    public TaskCaseAdapter(Context context) {
        mContext = context;
    }

    /**
     * When initialize the adapter, we should pass all of task cases' titles and the current task case data
     * to be displayed.
     *
     * @param taskCaseTitles
     * @param firstDisplayedTaskCase
     */
    public void initTaskCaseDatas(String[] taskCaseTitles, TaskCase firstDisplayedTaskCase) {
        mTaskCaseTitles = taskCaseTitles;
        mTaskCase = firstDisplayedTaskCase;
    }

    public boolean isInitialized() {
        return mTaskCaseTitles != null && mTaskCase != null;
    }

    public void swapTaskCase(TaskCase taskCase) {
        mTaskCase = taskCase;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ItemViewType.TYPE_HEADER == viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.task_case_header, parent, false);
            return new TaskCaseHeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.task_item, parent, false);
            return new TaskItemViewHolder(v);
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
        TaskCaseHeaderViewHolder holder = (TaskCaseHeaderViewHolder) vh;
        mIsTaskCaseSpinnerInitialized = false;

        bindTaskCaseSpinner(holder);
        bindTaskCaseInformation(holder);
    }

    private void bindTaskCaseSpinner(TaskCaseHeaderViewHolder holder) {
        mTaskCaseSpinnerAdapter = new ArrayAdapter(mContext, R.layout.task_spinner_item, mTaskCaseTitles);
        mTaskCaseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        holder.taskCaseSpinner.setAdapter(mTaskCaseSpinnerAdapter);
        holder.taskCaseSpinner.setSelection(mSelectedTaskCasePosition);
        holder.taskCaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Avoid the first call of onItemSelected() when the spinner is initialized.
                if (!mIsTaskCaseSpinnerInitialized) {
                    mIsTaskCaseSpinnerInitialized = true;
                    return;
                }
                mSelectedTaskCasePosition = position;
                mOnSelectTaskCaseListener.onSelectTaskCase(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindTaskCaseInformation(TaskCaseHeaderViewHolder holder) {
        holder.uncompletedTaskTime.setText(mTaskCase.uncompletedTaskTime);
        holder.undergoingTaskTime.setText(mTaskCase.undergoingTaskTime);
        //holder.undergoingWorkerCount.setText(mTaskCase.undergoingWorkerCount);
    }

    private void onBindItemViewHolder(ViewHolder vh, int position) {
        TaskItemViewHolder holder = (TaskItemViewHolder) vh;
        TaskItem taskItem = getItem(position);

        // Title
        holder.title.setText(taskItem.title);

        // Status
        int colorId = 0;
        switch (taskItem.status) {
            case TaskItem.Status.WARNING:
                colorId = R.color.task_item_status_warning_color;
                break;
        }
        TextView taskStatus = (TextView) LayoutInflater.from(mContext).inflate(
                R.layout.task_item_status, holder.statusContainer, false);
        taskStatus.setText(taskItem.statusText);
        GradientDrawable taskStatusBackground = (GradientDrawable) taskStatus.getBackground();
        taskStatusBackground.setColor(mContext.getResources().getColor(colorId));
        holder.statusContainer.removeAllViews();
        holder.statusContainer.addView(taskStatus);

        // Task time
        holder.workingTime.setText(taskItem.workingTime);

        // Tool
        holder.tool.setText(taskItem.tool);

        // Worker
        holder.worker.setText(taskItem.worker);

        // Progress
        int progressStringId = 0;
        switch (taskItem.progress) {
            case TaskItem.Progress.IN_SCHEDULE:
                progressStringId = R.string.task_progress_in_schedule;
                break;
            case TaskItem.Progress.NOT_START:
                progressStringId = R.string.task_progress_not_start;
                break;
            case TaskItem.Progress.PAUSE:
                progressStringId = R.string.task_progress_pause;
                break;
            case TaskItem.Progress.WORKING:
                progressStringId = R.string.task_progress_working;
                break;
        }
        holder.progress.setText(mContext.getString(progressStringId));
    }

    public TaskItem getItem(int position) {
        return mTaskCase.mTaskItems.get(--position);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = ItemViewType.TYPE_ITEM;
        if (isHeaderPosition(position)) {
            viewType = ItemViewType.TYPE_HEADER;
        }

        return viewType;
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return mTaskCase.mTaskItems == null ? 0 : mTaskCase.mTaskItems.size() + 1;
    }
}
