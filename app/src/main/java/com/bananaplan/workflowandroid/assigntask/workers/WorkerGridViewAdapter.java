package com.bananaplan.workflowandroid.assigntask.workers;

import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
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
import com.bananaplan.workflowandroid.detail.DetailedWorkerActivity;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.List;


/**
 * Adapter for the grid view to show workers' information
 *
 * @author Danny Lin
 * @since 2015/7/1.
 */
public class WorkerGridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "WorkerGridViewAdapter";

    public interface OnRefreshCaseListener {
        void onRefreshCase();
    }

    private final Context mContext;
    private OnRefreshCaseListener mOnRefreshCaseListener;

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
            Task dropTask = WorkingData.getInstance(mContext).getTaskById(taskId);

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
                        Worker dropWorker = mWorkerDataSet.get(mGridView.getChildAdapterPosition(v));
                        if (isWorkerHasTargetTask(dropWorker, dropTask)) break;

                        removeTaskFromCurrentWorker(dropTask);
                        assignTaskToWorker(dropTask, dropWorker);
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

        public View mainView;
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
            mainView = view;
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
            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailedWorkerActivity.class);
                    intent.putExtra(DetailedWorkerActivity.EXTRA_WORKER_ID, mWorkerDataSet.get(getAdapterPosition()).id);
                    mContext.startActivity(intent);
                }
            });

            overtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mWorkerDataSet.get(getAdapterPosition()).isOverTime = isChecked;
                }
            });
        }
    }

    public WorkerGridViewAdapter(Context context, OnRefreshCaseListener listener,
                                 RecyclerView gridView, List<Worker> workerDataSet) {
        mContext = context;
        mOnRefreshCaseListener = listener;
        mGridView = gridView;
        mWorkerDataSet = workerDataSet;
    }

    private void assignTaskToWorker(Task task, Worker worker) {
        task.workerId = worker.id;

        if (worker.hasCurrentTask()) {
            worker.nextTasks.add(task);
            task.status = Task.Status.PENDING;
        } else {
            worker.currentTask = task;
            task.status = Task.Status.WIP;
        }
    }

    private void removeTaskFromCurrentWorker(Task dropTask) {
        Worker currentWorker = WorkingData.getInstance(mContext).getWorkerById(dropTask.workerId);
        if (currentWorker == null || !isWorkerHasTargetTask(currentWorker, dropTask)) {
            return;
        }

        // Current task
        if (currentWorker.hasCurrentTask() && Utils.isSameId(currentWorker.currentTask.id, dropTask.id)) {
            currentWorker.currentTask = null;

        } else {
            // Next tasks
            currentWorker.nextTasks.remove(dropTask);
        }
    }

    private boolean isWorkerHasTargetTask(Worker worker, Task task) {
        if ((worker.currentTask == null && worker.nextTasks.size() == 0) || task == null) {
            return false;
        }

        boolean isWorkerHasTask = false;

        // Current task
        if (Utils.isSameId(worker.currentTask.id, task.id)) {
            isWorkerHasTask = true;
        }

        // Next tasks
        for (Task nextTask : worker.nextTasks) {
            if (Utils.isSameId(nextTask.id, task.id)) {
                isWorkerHasTask = true;
            }
        }

        return isWorkerHasTask;
    }

    private void assignTaskFinished() {
        if (mOnRefreshCaseListener != null) {
            mOnRefreshCaseListener.onRefreshCase();
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
                    WorkingData.getInstance(mContext).getCaseById(worker.currentTask.caseId).name);
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
