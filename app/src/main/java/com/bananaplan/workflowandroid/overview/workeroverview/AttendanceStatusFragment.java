package com.bananaplan.workflowandroid.overview.workeroverview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.LeaveInMainInfo;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.loadingworkerattendance.LoadingWorkerAttendanceAsyncTask;
import com.bananaplan.workflowandroid.data.loading.loadingworkerattendance.LoadingWorkerAttendanceStrategy;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.data.worker.attendance.WorkerAttendance;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Created by Ben on 2015/8/14.
 */
public class AttendanceStatusFragment extends OvTabFragmentBase implements
        OvTabFragmentBase.OvCallBack, LoadingWorkerAttendanceAsyncTask.OnFinishLoadingDataListener {

    private ViewGroup mAttendanceStatusFragmentView;
    private TextView mNoAttendanceText;

    private RecyclerView mAttendanceList;
    private AttendanceAdapter mAttendanceAdapter;
    private LinearLayoutManager mAttendanceListManager;

    private ProgressDialog mProgressDialog;

    private List<WorkerAttendance> mWorkerAttendanceSet = new ArrayList<>();

    private Worker mWorker;


    private class AttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private List<WorkerAttendance> mDataSet;


        private class ItemViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public TextView attendanceDate;
            public TextView attendanceTimeRange;
            public TextView attendanceType;
            public TextView attendanceDescription;

            public ItemViewHolder(View view) {
                super(view);
                this.view = view;
                attendanceDate = (TextView) view.findViewById(R.id.worker_overview_attendance_list_date);
                attendanceTimeRange = (TextView) view.findViewById(R.id.worker_overview_attendance_list_time_range);
                attendanceType = (TextView) view.findViewById(R.id.worker_overview_attendance_list_type);
                attendanceDescription = (TextView) view.findViewById(R.id.worker_overview_attendance_list_description);
            }
        }

        public AttendanceAdapter(Context context, List<WorkerAttendance> data) {
            mContext = context;
            mDataSet = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.worker_ov_attendance_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder itemVH = (ItemViewHolder) holder;
            WorkerAttendance attendance = mDataSet.get(position);

            if (position % 2 == 0) {
                itemVH.view.setBackgroundResource(R.color.worker_overview_attendance_list_odd_background_color);
            } else {
                itemVH.view.setBackgroundResource(R.color.worker_overview_attendance_list_even_background_color);
            }

            itemVH.attendanceDate.setText(Utils.timestamp2Date(new Date(attendance.from), Utils.DATE_FORMAT_YMD));

            setAttendanceTimeRange(itemVH.attendanceTimeRange, attendance);

            itemVH.attendanceType.setText(LeaveInMainInfo.getLeaveString(mContext, attendance.type));

            itemVH.attendanceDescription.setText(attendance.description);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        private void setAttendanceTimeRange(TextView timeRange, WorkerAttendance attendance) {
            GradientDrawable attendanceTimeRangeBackground = (GradientDrawable) timeRange.getBackground();
            Resources resources = mContext.getResources();
            String forenoon = resources.getString(R.string.worker_overview_attendance_time_range_forenoon);
            String afternoon = resources.getString(R.string.worker_overview_attendance_time_range_afternoon);
            String wholeDay = resources.getString(R.string.worker_overview_attendance_time_range_whole_day);

            timeRange.setText(attendance.timeRange);

            int color = 0;
            if (forenoon.equals(attendance.timeRange)) {
                color = resources.getColor(R.color.worker_overview_attendance_list_time_range_forenoon_background_color);

            } else if (afternoon.equals(attendance.timeRange)) {
                color = resources.getColor(R.color.worker_overview_attendance_list_time_range_afternoon_background_color);

            } else if (wholeDay.equals(attendance.timeRange)) {
                color = resources.getColor(R.color.worker_overview_attendance_list_time_range_whole_day_background_color);

            }

            attendanceTimeRangeBackground.setColor(color);
        }
    }

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
        mAttendanceStatusFragmentView = (ViewGroup) getView().findViewById(R.id.worker_overview_attendance_view);
        mNoAttendanceText = (TextView) getView().findViewById(R.id.worker_overview_no_attendance_text);
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
                new LoadingWorkerAttendanceStrategy(getActivity(), mWorker.id, 0, System.currentTimeMillis());
        LoadingWorkerAttendanceAsyncTask loadingWorkerAttendanceAsyncTask =
                new LoadingWorkerAttendanceAsyncTask(getActivity(), loadingWorkerAttendanceStrategy, this);
        loadingWorkerAttendanceAsyncTask.execute();
    }

    private void setupAttendanceList() {
        mAttendanceAdapter = new AttendanceAdapter(getActivity(), mWorkerAttendanceSet);
        mAttendanceListManager = new LinearLayoutManager(getActivity());
        mAttendanceList.setLayoutManager(mAttendanceListManager);
        mAttendanceList.setAdapter(mAttendanceAdapter);
    }

    @Override
    public Object getCallBack() {
        return this;
    }

    @Override
    public void onFinishLoadingData() {
        //mProgressDialog.dismiss();

        mWorkerAttendanceSet.clear();
        mWorkerAttendanceSet.addAll(WorkingData.getInstance(getActivity()).getWorkerById(mWorker.id).getAttendanceList());
        Collections.sort(mWorkerAttendanceSet, new Comparator<WorkerAttendance>() {
            @Override
            public int compare(WorkerAttendance lhs, WorkerAttendance rhs) {
                return Long.compare(lhs.from, rhs.from);
            }
        });

        if (mWorkerAttendanceSet.size() == 0) {
            mAttendanceStatusFragmentView.setVisibility(View.GONE);
            mNoAttendanceText.setVisibility(View.VISIBLE);
        } else {
            mAttendanceStatusFragmentView.setVisibility(View.VISIBLE);
            mNoAttendanceText.setVisibility(View.GONE);
            mAttendanceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }
}
