package com.bananaplan.workflowandroid.assigntask.workers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerGridViewAdapter.OnRefreshCaseListener;
import com.bananaplan.workflowandroid.data.Worker;

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

    private Context mContext;
    private OnRefreshCaseListener mOnRefreshCaseListener;
    private View mFragmentView;

    private RecyclerView mWorkerGridView;
    private GridLayoutManager mGridLayoutManager;
    private WorkerGridViewAdapter mWorkerGridViewAdapter;

    private List<Worker> mWorkerDataSet = new ArrayList<Worker>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        mGridLayoutManager = new GridLayoutManager(mContext,
                mContext.getResources().getInteger(R.integer.worker_gridview_column_count));
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mWorkerGridViewAdapter = new WorkerGridViewAdapter(mContext, mOnRefreshCaseListener,
                                                           mWorkerGridView, mWorkerDataSet);

        mWorkerGridView.setLayoutManager(mGridLayoutManager);
        mWorkerGridView.addItemDecoration(new WorkerCardDecoration(mContext));
        mWorkerGridView.setAdapter(mWorkerGridViewAdapter);
    }

//    public void addWorker(WorkerItem workerItem) {
//        mWorkerDatas.add(workerItem);
//    }

//    public void clearWorkers() {
//        mWorkerDatas.clear();
//    }

    public void setOnRefreshCaseListener(OnRefreshCaseListener listener) {
        mOnRefreshCaseListener = listener;
    }

    // TODO: Might need to be modified. Use addWorker(), etc.
    public List<Worker> getWorkerDatas() {
        return mWorkerDataSet;
    }
}
