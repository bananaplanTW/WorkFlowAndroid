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
    private WorkerGridAdapter mWorkerGridAdapter;

    private ArrayList<WorkerItem> mWorkerDatas = new ArrayList<WorkerItem>();


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
        mWorkerGridAdapter = new WorkerGridAdapter(mActivity, mWorkerGridView, R.layout.worker_item, mWorkerDatas);
        mWorkerGridView.setAdapter(mWorkerGridAdapter);
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
