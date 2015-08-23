package com.bananaplan.workflowandroid.workeroverview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;

/**
 * Created by Ben on 2015/8/14.
 */
public class WorkerAttendanceStatusFragment extends OvTabFragmentBase implements OvTabFragmentBase.WorkerOvCallBack {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.ftragment_worker_ov_attendane_status, container, false);
    }

    @Override
    public void onWorkerSelected(WorkerItem worker) {

    }

    @Override
    public Object getCallBack() {
        return this;
    }
}
