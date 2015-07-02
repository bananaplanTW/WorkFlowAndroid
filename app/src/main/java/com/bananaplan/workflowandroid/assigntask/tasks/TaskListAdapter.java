package com.bananaplan.workflowandroid.assigntask.tasks;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter to control the data in task list
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private static final String TAG = "TaskListAdapter";

    private Context mContext;

    private RecyclerView mRecyclerView;
    private List<TaskItem> mTaskDatas = new ArrayList<TaskItem>();

    private boolean mIsTouched = false;
    private boolean mIsDragged = false;
    private float mTouchDownX = 0F;
    private float mTouchDownY = 0F;

    private OnTouchListener mTaskItemOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mIsTouched = true;
                    mTouchDownX = event.getRawX();
                    mTouchDownY = event.getRawY();

                    break;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getRawX() - mTouchDownX;
                    float deltaY = event.getRawY() - mTouchDownY;

                    if (!mIsTouched || mIsDragged || Math.abs(deltaX) >= 10 || Math.abs(deltaY) < 5) {
                        break;
                    }

                    int itemPosition = mRecyclerView.getChildAdapterPosition(v);

                    // Pass task content(type: string)
                    String passData = mTaskDatas.get(itemPosition).title;
                    ClipData.Item item = new ClipData.Item(passData);
                    ClipData dragData = new ClipData(passData,
                                                     new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                                                     item);

                    // Instantiates the drag shadow image
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);

                    Log.d(TAG, "Pass data = " + passData);

                    // Starts the drag
                    v.startDrag(dragData,  // the data to be dragged
                                shadow,    // the drag shadow image
                                null,      // no need to use local data
                                0          // flags (not currently used, set to 0)
                    );

                    mIsDragged = true;

                    break;

                case MotionEvent.ACTION_UP:
                    resetTaskItemTouch();

                    break;

                case MotionEvent.ACTION_CANCEL:
                    resetTaskItemTouch();

                    break;
            }

            return true;
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {

        public View taskItem;
        public TextView taskTitle;
        public TextView taskSubtitle;
        public TextView taskStatus;
        public TextView taskTime;


        public ViewHolder(View v) {
            super(v);
            taskItem = v;
            taskTitle = (TextView) v.findViewById(R.id.task_title);
            taskSubtitle = (TextView) v.findViewById(R.id.task_subtitle);
            taskStatus = (TextView) v.findViewById(R.id.task_status);
            taskTime = (TextView) v.findViewById(R.id.task_time);
        }
    }

    public TaskListAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
    }

    public void setTaskDatas(List<TaskItem> taskDatas) {
        mTaskDatas = taskDatas;
    }

    public void resetTaskItemTouch() {
        mIsTouched = false;
        mIsDragged = false;
        mTouchDownX = 0F;
        mTouchDownY = 0F;
    }

    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.task_item, parent, false);

        // Set touch listener for triggering drag start
        v.setOnTouchListener(mTaskItemOnTouchListener);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Title
        holder.taskTitle.setText(mTaskDatas.get(position).title);

        // Subtitle
        holder.taskSubtitle.setText(mTaskDatas.get(position).subtitle);

        // Task time
        holder.taskTime.setText(mTaskDatas.get(position).time);

        // Status
        Resources res = mContext.getResources();
        int color = 0;
        switch (mTaskDatas.get(position).status) {
            case TaskItem.Status.UNDERGOING:
                holder.taskStatus.setText("進行中");
                color = res.getColor(R.color.task_item_status_undergoing_color);
                break;
            case TaskItem.Status.OVERTIME:
                holder.taskStatus.setText("超過預定");
                color = res.getColor(R.color.task_item_status_overtime_color);
                break;
            case TaskItem.Status.COMPLETED:
//                holder.taskItem.getBackground().setColorFilter(
//                        res.getColor(R.color.task_item_status_completed_filter_color), PorterDuff.Mode.MULTIPLY);
                holder.taskStatus.setText("已經完成");
                color = res.getColor(R.color.task_item_status_completed_color);
                break;
        }
        GradientDrawable taskStatusBackground = (GradientDrawable) holder.taskStatus.getBackground();
        taskStatusBackground.setColor(color);
    }

    @Override
    public int getItemCount() {
        return mTaskDatas.size();
    }
}
