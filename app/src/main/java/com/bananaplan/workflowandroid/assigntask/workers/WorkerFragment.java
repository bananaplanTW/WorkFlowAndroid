package com.bananaplan.workflowandroid.assigntask.workers;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkerItem;

import java.util.ArrayList;


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

    private RecyclerView mWorkerGridView;
    private GridLayoutManager mGridLayoutManager;
    private WorkerGridViewAdapter mWorkerGridViewAdapter;

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
        mWorkerGridView = (RecyclerView) mFragmentView.findViewById(R.id.worker_gridview);
    }

    private void initWorkerGridView() {
        mGridLayoutManager = new GridLayoutManager(
                mActivity, mActivity.getResources().getInteger(R.integer.worker_gridview_column_count));
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mWorkerGridViewAdapter = new WorkerGridViewAdapter(mActivity, mWorkerGridView, mWorkerDatas);

        mWorkerGridView.setLayoutManager(mGridLayoutManager);
        mWorkerGridView.addItemDecoration(new WorkerItemDecoration(mActivity));
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
