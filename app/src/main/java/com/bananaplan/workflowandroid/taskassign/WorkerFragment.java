package com.bananaplan.workflowandroid.taskassign;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

import java.util.ArrayList;

/**
 * Fragment to show workers' working status
 *
 * @author Danny Lin
 * @since 2015.05.31
 */
public class WorkerFragment extends Fragment {

    private Activity mActivity;
    private View mFragmentView;

    private TextView mWorkerCountText;

    private ArrayList<String> mWorkerDatas = new ArrayList<String>();


    public WorkerFragment() {

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
        mWorkerCountText.setText(getTag() + " Worker count = " + mWorkerDatas.size());
    }

    private void initialize() {
        findViews();
    }

    private void findViews() {
        mFragmentView = getView();
        mWorkerCountText = (TextView) mFragmentView.findViewById(R.id.worker_count_text);
    }

    public void addWorker(String worker) {
        mWorkerDatas.add(worker);
    }

    public ArrayList<String> getWorkerDatas() {
        return mWorkerDatas;
    }
}
