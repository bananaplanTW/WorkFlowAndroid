package com.bananaplan.workflowandroid.assigntask.workers;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem.WorkingStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to show all workers' working status
 *
 * @author Danny Lin
 * @since 2015.05.31
 */
public class WorkerFragment extends Fragment {

    public static final int MAX_WORKER_COUNT_IN_PAGE = 9;

    private Activity mActivity;
    private View mFragmentView;

    private GridView mWorkerGridView;
    private WorkerGridViewAdapter mWorkerGridViewAdapter;

    private ArrayList<WorkerItem> mWorkerDatas = new ArrayList<WorkerItem>();


    private class WorkerGridViewAdapter extends ArrayAdapter {

        private Context mContext;


        private class WorkerViewHolder {
            ImageView avatar;
            TextView name;
            TextView title;
            TextView task;
            View statusLight;
            TextView status;
            TextView time;
        }

        public WorkerGridViewAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            WorkerViewHolder workerViewHolder;

            if (convertView != null) {
                view = convertView;
                workerViewHolder = (WorkerViewHolder) view.getTag();
            } else {
                view = mActivity.getLayoutInflater().inflate(R.layout.worker_item, parent, false);
                workerViewHolder = createWorkerViewHolder(view);
                view.setTag(workerViewHolder);
            }

            setWorkerViewHolder(workerViewHolder, (WorkerItem) getItem(position));

            return view;
        }

        private void setWorkerViewHolder(WorkerViewHolder viewHolder, WorkerItem workerItem) {
            viewHolder.name.setText(workerItem.name);
            viewHolder.title.setText(workerItem.title);
            viewHolder.task.setText(workerItem.task);

            // TODO: Status light
            switch (workerItem.status) {
                case WorkingStatus.NORMAL:
                    viewHolder.status.setText("正常工作中");
                    break;
                case WorkingStatus.DELAY:
                    break;
            }

            viewHolder.time.setText(workerItem.time);
        }

        private WorkerViewHolder createWorkerViewHolder(View view) {
            WorkerViewHolder workerViewHolder = new WorkerViewHolder();

            workerViewHolder.avatar = (ImageView) view.findViewById(R.id.worker_avatar);
            workerViewHolder.name = (TextView) view.findViewById(R.id.worker_name);
            workerViewHolder.title = (TextView) view.findViewById(R.id.worker_title);
            workerViewHolder.task = (TextView) view.findViewById(R.id.task);
            workerViewHolder.statusLight = view.findViewById(R.id.working_status_light);
            workerViewHolder.status = (TextView) view.findViewById(R.id.working_status);
            workerViewHolder.time = (TextView) view.findViewById(R.id.working_time);

            return workerViewHolder;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        findViews();
        initWorkerGridView();
    }

    private void findViews() {
        mFragmentView = getView();
        mWorkerGridView = (GridView) mFragmentView.findViewById(R.id.worker_gridview);
    }

    private void initWorkerGridView() {
        mWorkerGridViewAdapter = new WorkerGridViewAdapter(mActivity, R.layout.worker_item, mWorkerDatas);
        mWorkerGridView.setAdapter(mWorkerGridViewAdapter);
    }

//    public void addWorker(WorkerItem workerItem) {
//        mWorkerDatas.add(workerItem);
//    }

//    public void clearWorkers() {
//        mWorkerDatas.clear();
//    }

    // TODO: Might need to be modified. Use addWorker(), etc.
    public ArrayList<WorkerItem> getWorkerDatas() {
        return mWorkerDatas;
    }
}
