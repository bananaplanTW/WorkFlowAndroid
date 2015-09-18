package com.bananaplan.workflowandroid.assigntask.workers;

import android.content.ClipDescription;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnDragListener;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;

import java.util.List;


/**
 * Adapter for the grid view to show workers' information
 *
 * @author Danny Lin
 * @since 2015/7/1.
 */
public class WorkerGridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "WorkerGridViewAdapter";

    public interface OnRefreshTaskCaseListener {
        void onRefreshTaskCase();
    }

    private final Context mContext;
    private OnRefreshTaskCaseListener mOnRefreshTaskCaseListener;

    private RecyclerView mGridView;
    private List<Worker> mWorkerDataSet;

    private OnDragListener mOnDragListener = new OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            final int action = event.getAction();
            GradientDrawable workerItemBackground = (GradientDrawable) v.getBackground();

            int strokeWidth = mContext.getResources().getDimensionPixelSize(R.dimen.worker_card_stroke_width);
            int highlightStrokeWidth = strokeWidth *
                    mContext.getResources().getInteger(R.integer.assign_task_drag_and_drop_highlight_stroke_width);
            int originalStrokeColor = mContext.getResources().getColor(R.color.worker_card_stroke_color);
            int dragAvailableStrokeColor = mContext.getResources().getColor(R.color.worker_card_drag_available_color);
            int enteredStrokeColor = mContext.getResources().getColor(R.color.worker_card_entered_stroke_color);

            String taskId = (String) event.getLocalState();
            Task dropTask = WorkingData.getInstance(mContext).getTaskItemById(Long.valueOf(taskId));

            switch (action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    if (!isWorkerHasTargetTask(mWorkerDataSet.get(mGridView.getChildAdapterPosition(v)), dropTask)) {
                        workerItemBackground.setStroke(highlightStrokeWidth, dragAvailableStrokeColor);
                        v.invalidate();
                    }
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:
                    if (!isWorkerHasTargetTask(mWorkerDataSet.get(mGridView.getChildAdapterPosition(v)), dropTask)) {
                        workerItemBackground.setStroke(highlightStrokeWidth, enteredStrokeColor);
                        v.invalidate();
                    }
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    if (!isWorkerHasTargetTask(mWorkerDataSet.get(mGridView.getChildAdapterPosition(v)), dropTask)) {
                        workerItemBackground.setStroke(highlightStrokeWidth, dragAvailableStrokeColor);
                        v.invalidate();
                    }
                    return true;

                case DragEvent.ACTION_DROP:
                    if (GridView.INVALID_POSITION != mGridView.getChildAdapterPosition(v)) {
                        assignTaskToWorker(dropTask, mWorkerDataSet.get(mGridView.getChildAdapterPosition(v)));
                    }

                    workerItemBackground.setStroke(strokeWidth, originalStrokeColor);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    workerItemBackground.setStroke(strokeWidth, originalStrokeColor);
                    v.invalidate();
                    if (event.getResult()) {
                        assignTaskFinished();
                    }

                    return true;

                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    };

    private class WorkerCardViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatar;
        public TextView name;
        public TextView jobTitle;
        public Switch overtime;

        public TextView currentTaskName;
        public TextView currentTaskCaseName;
        public TextView currentTaskWorkingTime;

        public TextView nextTaskName;


        public WorkerCardViewHolder(View view) {
            super(view);
            findViews(view);
            setupListeners();
        }

        private void findViews(View view) {
            avatar = (ImageView) view.findViewById(R.id.worker_card_avatar);
            name = (TextView) view.findViewById(R.id.worker_card_name);
            jobTitle = (TextView) view.findViewById(R.id.worker_card_job_title);
            overtime = (Switch) view.findViewById(R.id.worker_card_overtime_switch);
            currentTaskName = (TextView) view.findViewById(R.id.worker_card_current_task_name);
            currentTaskCaseName = (TextView) view.findViewById(R.id.worker_card_current_task_case_name);
            currentTaskWorkingTime = (TextView) view.findViewById(R.id.worker_card_current_task_working_time);
            nextTaskName = (TextView) view.findViewById(R.id.worker_card_next_task);
        }

        private void setupListeners() {
            overtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mWorkerDataSet.get(getAdapterPosition()).isOverTime = isChecked;
                }
            });
        }
    }

    public WorkerGridViewAdapter(Context context, OnRefreshTaskCaseListener listener,
                                 RecyclerView gridView, List<Worker> workerDataSet) {
        mContext = context;
        mOnRefreshTaskCaseListener = listener;
        mGridView = gridView;
        mWorkerDataSet = workerDataSet;
    }

    private void assignTaskToWorker(Task task, Worker worker) {
        if (isWorkerHasTargetTask(worker, task)) {
            return;
        }
        removeTaskFromCurrentWorker(task);

        task.workerId = worker.id;

        if (worker.hasCurrentTask()) {
            worker.nextTasks.add(task);
            task.status = Task.Status.IN_SCHEDULE;
        } else {
            worker.currentTask = task;
            task.status = Task.Status.WORKING;
        }
    }

    private void removeTaskFromCurrentWorker(Task dropTask) {
        Worker currentWorker = WorkingData.getInstance(mContext).getWorkerItemById(dropTask.workerId);
        if (currentWorker == null) {
            return;
        }

        // Current task
        if (currentWorker.hasCurrentTask() && currentWorker.currentTask.id == dropTask.id) {
            currentWorker.currentTask = null;
        }

        // Next tasks
        currentWorker.nextTasks.remove(dropTask);
    }

    private boolean isWorkerHasTargetTask(Worker worker, Task task) {
        if (worker.currentTask == null || task == null || worker.nextTasks.size() == 0) {
            return false;
        }

        boolean isWorkerHasTask = false;

        // Current task
        if (worker.currentTask.id == task.id) {
            isWorkerHasTask = true;
        }

        // Next tasks
        for (Task nextTask : worker.nextTasks) {
            if (nextTask.id == task.id) {
                isWorkerHasTask = true;
            }
        }

        return isWorkerHasTask;
    }

    private void assignTaskFinished() {
        if (mOnRefreshTaskCaseListener != null) {
            mOnRefreshTaskCaseListener.onRefreshTaskCase();
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.worker_card, parent, false);

        // Set drag listener
        v.setOnDragListener(mOnDragListener);

        return new WorkerCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WorkerCardViewHolder workerCardViewHolder = (WorkerCardViewHolder) holder;
        Worker worker =  mWorkerDataSet.get(position);

        workerCardViewHolder.avatar.setImageDrawable(worker.getAvator());
        workerCardViewHolder.name.setText(worker.name);
        workerCardViewHolder.jobTitle.setText(worker.jobTitle);
        workerCardViewHolder.overtime.setChecked(worker.isOverTime);

        // Current task name and current task case name
        if (worker.hasCurrentTask()) {
            workerCardViewHolder.currentTaskName.setText(worker.currentTask.name);
            workerCardViewHolder.currentTaskCaseName.setText(
                    WorkingData.getInstance(mContext).getTaskCaseById(worker.currentTask.taskCaseId).name);
            workerCardViewHolder.currentTaskWorkingTime.setText(worker.currentTask.getWorkingTime());
        } else {
            workerCardViewHolder.currentTaskName.setText("");
            workerCardViewHolder.currentTaskCaseName.setText("");
            workerCardViewHolder.currentTaskWorkingTime.setText("");
        }

        // Next tasks
        if (worker.hasNextTasks()) {
            Log.d(TAG, worker.name + " has " + worker.nextTasks.size() + " next tasks");
            workerCardViewHolder.nextTaskName.setText(worker.nextTasks.get(0).name);
        } else {
            workerCardViewHolder.nextTaskName.setText("無排程");
        }
    }

    @Override
    public int getItemCount() {
        return mWorkerDataSet.size();
    }
}
