package com.bananaplan.workflowandroid.assigntask;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to show all workers' working status
 *
 * @author Danny Lin
 * @since 2015.05.31
 */
public class WorkerFragment extends Fragment {

    private Activity mActivity;
    private View mFragmentView;

    private GridView mWorkerGridView;
    private WorkerGridViewAdapter mWorkerGridViewAdapter;

    private ArrayList<String> mWorkerDatas = new ArrayList<String>();


    private class WorkerGridViewAdapter extends ArrayAdapter {

        private Context mContext;


        private class WorkerViewHolder {
            ImageView workerAvatar;
            TextView workerName;
            TextView workerTitle;
            TextView workingContent;
            View workingStatusLight;
            TextView workingStatus;
            TextView workingTime;
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
                view = mActivity.getLayoutInflater().inflate(R.layout.worker_grid_view_item, parent, false);
                workerViewHolder = createWorkerViewHolder(view);
                view.setTag(workerViewHolder);
            }

            setWorkerViewHolder(workerViewHolder, (String) getItem(position));

            return view;
        }

        private void setWorkerViewHolder(WorkerViewHolder viewHolder, String item) {
            viewHolder.workerName.setText(item);
        }

        private WorkerViewHolder createWorkerViewHolder(View view) {
            WorkerViewHolder workerViewHolder = new WorkerViewHolder();

            workerViewHolder.workerAvatar = (ImageView) view.findViewById(R.id.worker_avatar);
            workerViewHolder.workerName = (TextView) view.findViewById(R.id.worker_name);
            workerViewHolder.workerTitle = (TextView) view.findViewById(R.id.worker_title);
            workerViewHolder.workingContent = (TextView) view.findViewById(R.id.working_content);
            workerViewHolder.workingStatusLight = view.findViewById(R.id.working_status_light);
            workerViewHolder.workingStatus = (TextView) view.findViewById(R.id.working_status);
            workerViewHolder.workingTime = (TextView) view.findViewById(R.id.working_time);

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
        mWorkerGridViewAdapter = new WorkerGridViewAdapter(mActivity, R.layout.worker_grid_view_item, mWorkerDatas);
        mWorkerGridView.setAdapter(mWorkerGridViewAdapter);
    }

    public void addWorker(String worker) {
        mWorkerDatas.add(worker);
    }

    public void clearWorkers() {
        mWorkerDatas.clear();
    }

    public ArrayList<String> getWorkerDatas() {
        return mWorkerDatas;
    }
}
