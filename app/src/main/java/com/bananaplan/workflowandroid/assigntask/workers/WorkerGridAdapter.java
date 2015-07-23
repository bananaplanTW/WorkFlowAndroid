package com.bananaplan.workflowandroid.assigntask.workers;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnDragListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

import java.util.List;


/**
 * Adapter for the grid view to show workers' information
 *
 * @author Danny Lin
 * @since 2015/7/1.
 */
public class WorkerGridAdapter extends ArrayAdapter {

    private static final String TAG = "WorkerGridAdapter";

    private final Context mContext;

    private GridView mGridView;
    private List mWorkerDatas;

    // The color to indicate where the dragged object can be dropped
    private PorterDuffColorFilter mDroppedAreaFilter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.LIGHTEN);
    // The color to indicate that the dragged object has entered the dropped area
    private PorterDuffColorFilter mEnteredFilter = new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.LIGHTEN);

    private OnDragListener mOnDragListener = new OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            final int action = event.getAction();

            switch (action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.getBackground().setColorFilter(mDroppedAreaFilter);
                        v.invalidate();
                        return true;
                    }
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.getBackground().setColorFilter(mEnteredFilter);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.getBackground().setColorFilter(mDroppedAreaFilter);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Get the task data from the item
                    // Put the data into view
                    String dragTaskData = item.getText().toString();
                    if (GridView.INVALID_POSITION != mGridView.getPositionForView(v)) {
                        ((WorkerItem) mWorkerDatas.get(mGridView.getPositionForView(v))).task = dragTaskData;
                    }

                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.getBackground().clearColorFilter();
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


    private class WorkerViewHolder {
        ImageView avatar;
        TextView name;
        TextView title;
        TextView task;
        View statusLight;
        TextView status;
        TextView time;
    }

    public WorkerGridAdapter(final Context context, GridView gridView, int resource, List objects) {
        super(context, resource, objects);
        mContext = context;
        mGridView = gridView;
        mWorkerDatas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        WorkerViewHolder workerViewHolder;

        if (convertView != null) {
            view = convertView;
            workerViewHolder = (WorkerViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.worker_item, parent, false);

            // Set drag listener
            view.setOnDragListener(mOnDragListener);

            workerViewHolder = createWorkerViewHolder(view);
            view.setTag(workerViewHolder);
        }

        setWorkerViewHolder(workerViewHolder, (WorkerItem) mWorkerDatas.get(position));

        return view;
    }

    private void setWorkerViewHolder(WorkerViewHolder viewHolder, WorkerItem workerItem) {
        viewHolder.name.setText(workerItem.name);
        viewHolder.title.setText(workerItem.title);
        viewHolder.task.setText(workerItem.task);

        // TODO: Status light
        switch (workerItem.getStatus()) {
            case WorkerItem.WorkingStatus.NORMAL:
                viewHolder.status.setText("正常工作中");
                break;
            case WorkerItem.WorkingStatus.DELAY:
                break;
        }

        viewHolder.time.setText(workerItem.getTime());
    }

    private WorkerViewHolder createWorkerViewHolder(View view) {
        WorkerViewHolder workerViewHolder = new WorkerViewHolder();

        workerViewHolder.avatar = (ImageView) view.findViewById(R.id.worker_avatar);
        workerViewHolder.name = (TextView) view.findViewById(R.id.worker_name);
        workerViewHolder.title = (TextView) view.findViewById(R.id.worker_title);
        workerViewHolder.task = (TextView) view.findViewById(R.id.task);
        workerViewHolder.statusLight = view.findViewById(R.id.working_status_light);
        workerViewHolder.status = (TextView) view.findViewById(R.id.working_status);
        workerViewHolder.time = (TextView) view.findViewById(R.id.task_working_time);

        return workerViewHolder;
    }
}
