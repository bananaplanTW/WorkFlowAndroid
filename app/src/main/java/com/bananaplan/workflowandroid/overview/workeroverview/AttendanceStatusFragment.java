package com.bananaplan.workflowandroid.overview.workeroverview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.data.worker.attendance.LeaveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Ben on 2015/8/14.
 */
public class AttendanceStatusFragment extends OvTabFragmentBase implements OvTabFragmentBase.OvCallBack {

    private RecyclerView mAttendanceList;
    private LinearLayoutManager mAttendanceListManager;

    private List<LeaveData> mLeaveDataSet = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.ftragment_worker_ov_attendance_status, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        findViews();
        onItemSelected(getSelectedWorker());
        setupAttendanceList();
    }

    private void findViews() {
        mAttendanceList = (RecyclerView) getView().findViewById(R.id.worker_ov_attendance_list);
    }

    @Override
    public void onItemSelected(Object item) {
        Worker worker = (Worker) item;
        if (worker == null) return;

        mLeaveDataSet.addAll(worker.leaveDatas);
        Collections.sort(mLeaveDataSet, new Comparator<LeaveData>() {
            @Override
            public int compare(LeaveData lhs, LeaveData rhs) {
                return rhs.date.compareTo(lhs.date);
            }
        });
    }

    private void setupAttendanceList() {
        mAttendanceListManager = new LinearLayoutManager(getActivity());
        mAttendanceList.setLayoutManager(mAttendanceListManager);
    }

    @Override
    public Object getCallBack() {
        return null;
    }
}
