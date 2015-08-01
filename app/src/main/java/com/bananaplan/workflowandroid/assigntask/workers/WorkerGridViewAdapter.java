package com.bananaplan.workflowandroid.assigntask.workers;

import android.content.ClipData;
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
import android.widget.GridView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.main.WorkingData;

import java.util.List;


/**
 * Adapter for the grid view to show workers' information
 *
 * @author Danny Lin
 * @since 2015/7/1.
 */
public class WorkerGridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "WorkerGridAdapter";

    private final Context mContext;

    private RecyclerView mGridView;
    private List<WorkerItem> mWorkerDatas;

    private OnDragListener mOnDragListener = new OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            final int action = event.getAction();
            GradientDrawable workerItemBackground = (GradientDrawable) v.getBackground();

            int strokeWidth = mContext.getResources().getDimensionPixelSize(R.dimen.worker_item_stroke_width);
            int originalStrokeColor = mContext.getResources().getColor(R.color.worker_item_stroke_color);
            int enteredStrokeColor = mContext.getResources().getColor(R.color.worker_item_entered_stroke_color);

            switch (action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    // Determines if this View can accept the dragged data
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:
                    workerItemBackground.setStroke(strokeWidth * 2, enteredStrokeColor);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    workerItemBackground.setStroke(strokeWidth, originalStrokeColor);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Get the task data from the item
                    // Put the data into view
                    String taskId = item.getText().toString();
                    if (GridView.INVALID_POSITION != mGridView.getChildAdapterPosition(v)) {
                        mWorkerDatas.get(mGridView.getChildAdapterPosition(v)).
                                currentTaskItem = WorkingData.getInstance(mContext).getTaskItemById(Long.valueOf(taskId));
                    }

                    workerItemBackground.setStroke(strokeWidth, originalStrokeColor);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    workerItemBackground.setStroke(strokeWidth, originalStrokeColor);
                    v.invalidate();
                    if (event.getResult()) {
                        notifyDataSetChanged();
                    }

                    return true;

                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    };


    public WorkerGridViewAdapter(Context context, RecyclerView gridView, List<WorkerItem> workerDatas) {
        mContext = context;
        mGridView = gridView;
        mWorkerDatas = workerDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.worker_item, parent, false);

        // Set drag listener
        v.setOnDragListener(mOnDragListener);

        return new WorkerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setWorkerViewHolder((WorkerViewHolder) holder, mWorkerDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mWorkerDatas.size();
    }

    private void setWorkerViewHolder(WorkerViewHolder viewHolder, WorkerItem workerItem) {
        viewHolder.name.setText(workerItem.name);
        viewHolder.title.setText(workerItem.title);
        viewHolder.task.setText(workerItem.hasCurrentTaskItem() ? workerItem.currentTaskItem.title : "");
        viewHolder.currentTaskWorkingTime.setText(
                workerItem.hasCurrentTaskItem() ? workerItem.currentTaskItem.getWorkingTime() : "");
    }
}
