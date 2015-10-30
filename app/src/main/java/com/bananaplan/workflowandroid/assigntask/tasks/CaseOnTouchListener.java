package com.bananaplan.workflowandroid.assigntask.tasks;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.detail.task.DetailedTaskActivity;


/**
 * OnTouchListener for TaskList.
 * Control the drag and drop of each task item.
 *
 * @author Danny Lin
 * @since 2015/7/10.
 */
public class CaseOnTouchListener implements View.OnTouchListener {

    private static final String TAG = "TaskListOnTouchListener";

    private static final int THRESHOLD_DRAG_THETA = 35;

    private Context mContext;

    private RecyclerView mRecyclerView;
    private View mDownView = null;

    private boolean mIsTouched = false;
    private boolean mIsFingerMoving = false;
    private boolean mIsItemDragging = false;
    private float mTouchDownX = 0F;
    private float mTouchDownY = 0F;


    public CaseOnTouchListener(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // Still need to pass motion event to list view to keep scroll behavior.
        v.onTouchEvent(event);

        float x = event.getRawX();
        float y = event.getRawY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownView = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
                Log.d(TAG, "Down position = " + mRecyclerView.getChildAdapterPosition(mDownView));

                if (mDownView == null || mRecyclerView.getChildAdapterPosition(mDownView) == 0) break;

                mIsTouched = true;
                mTouchDownX = x;
                mTouchDownY = y;

                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = Math.abs(x - mTouchDownX);
                float deltaY = Math.abs(y - mTouchDownY);

                if (mDownView == null || deltaX == 0F || deltaY == 0F) break;

                if (deltaX >= 4F || deltaY >= 4F) {
                    mIsFingerMoving = true;
                }

                double deltaZ = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                int theta = Math.round((float) (Math.asin(deltaY / deltaZ) / Math.PI*180));

                if (!mIsTouched || mIsItemDragging || x < mTouchDownX || theta > THRESHOLD_DRAG_THETA) {
                    break;
                }

                if (isAssignable()) {
                    startDragTaskItem();
                }

                break;

            case MotionEvent.ACTION_UP:
                if (mDownView == null) break;

                if (!mIsFingerMoving) {
                    int position = mRecyclerView.getChildAdapterPosition(mDownView);
                    if (position >= 0) {
                        Intent intent = new Intent(mContext, DetailedTaskActivity.class);
                        intent.putExtra(DetailedTaskActivity.EXTRA_TASK_ID,
                                ((CaseAdapter) mRecyclerView.getAdapter()).getItem(position).id);

                        mContext.startActivity(intent);
                    }
                }

                resetTaskItemTouch();

                break;

            case MotionEvent.ACTION_CANCEL:
                if (mDownView == null) break;
                resetTaskItemTouch();

                break;
        }

        return true;
    }

    private void startDragTaskItem() {
        int itemPosition = mRecyclerView.getChildAdapterPosition(mDownView);

        // We use myLocalState(3rd variable in startDrag()) to pass task id,
        // but we still need dragData passing to 1st variable in startDrag() to let drag-and-drop function work.
        String dragTaskId = String.valueOf(((CaseAdapter) mRecyclerView.getAdapter()).getItem(itemPosition).id);
        ClipData.Item item = new ClipData.Item(dragTaskId);
        ClipData dragData = new ClipData(dragTaskId,
                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                item);

        // Instantiates the drag shadow image
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(mDownView);

        // Starts the drag
        mDownView.startDrag(dragData,    // the data to be dragged
                            shadow,      // the drag shadow image
                            dragTaskId,  // no need to use local data
                            0            // flags (not currently used, set to 0)
        );

        mIsItemDragging = true;
    }

    private void resetTaskItemTouch() {
        mDownView = null;
        mIsTouched = false;
        mIsFingerMoving = false;
        mIsItemDragging = false;
        mTouchDownX = 0F;
        mTouchDownY = 0F;
    }

    /**
     * If the status of a task is "unclaimed" or "pending", the task can be assigned.
     *
     * @return
     */
    private boolean isAssignable() {
        Task downTask = ((CaseAdapter) mRecyclerView.getAdapter()).getItem(mRecyclerView.getChildAdapterPosition(mDownView));

        return Task.Status.UNCLAIMED.equals(downTask.status) || Task.Status.PENDING.equals(downTask.status);
    }
}
