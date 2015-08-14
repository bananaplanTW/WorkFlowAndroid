package com.bananaplan.workflowandroid.workeroverview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;

/**
 * Created by Ben on 2015/8/14.
 */
public class WorkerAttendanceStatusFragment extends WorkerFragmentBase {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.ftragment_worker_ov_attendane_status, container, false);
    }

    @Override
    public void onWorkerSelected(WorkerItem worker) {

    }
}
