package com.bananaplan.workflowandroid.overview.workeroverview;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.loading.LoadingWorkerAttendanceAsyncTask;
import com.bananaplan.workflowandroid.data.loading.loadingworkerattendance.LoadingWorkerAttendanceStrategy;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.data.worker.attendance.LeaveData;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ben on 2015/8/14.
 */
public class AttendanceStatusFragment extends OvTabFragmentBase implements
        OvTabFragmentBase.OvCallBack, LoadingWorkerAttendanceAsyncTask.OnFinishLoadingDataListener {

    private RecyclerView mAttendanceList;
    private LinearLayoutManager mAttendanceListManager;

    private ProgressDialog mProgressDialog;

    private List<LeaveData> mLeaveDataSet = new ArrayList<>();

    private Worker mWorker;


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
        mProgressDialog = new ProgressDialog(getActivity());
        findViews();
        onItemSelected(getSelectedWorker());
        setupAttendanceList();
    }

    private void findViews() {
        mAttendanceList = (RecyclerView) getView().findViewById(R.id.worker_ov_attendance_list);
    }

    @Override
    public void onItemSelected(Object item) {
        mWorker = (Worker) item;
        if (mWorker == null) return;

        //mProgressDialog.show();
        loadWorkerAttendance();
    }

    private void loadWorkerAttendance() {
        LoadingWorkerAttendanceStrategy loadingWorkerAttendanceStrategy =
                new LoadingWorkerAttendanceStrategy(mWorker.id, 0, System.currentTimeMillis());
        LoadingWorkerAttendanceAsyncTask loadingWorkerAttendanceAsyncTask =
                new LoadingWorkerAttendanceAsyncTask(getActivity(), loadingWorkerAttendanceStrategy, this);
        loadingWorkerAttendanceAsyncTask.execute();
    }

    private void setupAttendanceList() {
        mAttendanceListManager = new LinearLayoutManager(getActivity());
        mAttendanceList.setLayoutManager(mAttendanceListManager);
    }

    @Override
    public Object getCallBack() {
        return this;
    }

    @Override
    public void onFinishLoadingData(JSONArray workerAttendance) {
        //mProgressDialog.dismiss();

    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }
}
