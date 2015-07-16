package com.bananaplan.workflowandroid.assigntask.tasks;

import android.content.ClipData;
import android.content.ClipDescription;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * OnTouchListener for TaskList.
 * Control the drag and drop of each task item.
 *
 * @author Danny Lin
 * @since 2015/7/10.
 */
public class TaskCaseOnTouchListener implements View.OnTouchListener {

    private static final String TAG = "TaskListOnTouchListener";

    private static final int THRESHOLD_DRAG_THETA = 35;

    private RecyclerView mRecyclerView;
    private View mDownView = null;

    private boolean mIsTouched = false;
    private boolean mIsDragging = false;
    private float mTouchDownX = 0F;
    private float mTouchDownY = 0F;


    public TaskCaseOnTouchListener(RecyclerView recyclerView) {
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

                double deltaZ = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                int theta = Math.round((float) (Math.asin(deltaY / deltaZ) / Math.PI*180));

                if (!mIsTouched || mIsDragging || x < mTouchDownX || theta > THRESHOLD_DRAG_THETA) {
                    break;
                }

                startDragTaskItem();

                break;

            case MotionEvent.ACTION_UP:
                if (mDownView == null) break;
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

        // Pass task content(type: string)
        String passData = ((TaskCaseAdapter) mRecyclerView.getAdapter()).getItem(itemPosition).title;
        ClipData.Item item = new ClipData.Item(passData);
        ClipData dragData = new ClipData(passData,
                new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                item);

        // Instantiates the drag shadow image
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(mDownView);

        // Starts the drag
        mDownView.startDrag(dragData,  // the data to be dragged
                shadow,    // the drag shadow image
                null,      // no need to use local data
                0          // flags (not currently used, set to 0)
        );

        mIsDragging = true;
    }

    private void resetTaskItemTouch() {
        mDownView = null;
        mIsTouched = false;
        mIsDragging = false;
        mTouchDownX = 0F;
        mTouchDownY = 0F;
    }
}
