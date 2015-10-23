package com.bananaplan.workflowandroid.info;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.Warning;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class MainInfoFragment extends Fragment {

    private Context mContext;

    private TextView mWorkerOnCountText;
    private TextView mWorkerOvertimeCountText;
    private TextView mWarningCountText;
    private TextView mCostsText;

    private RecyclerView mDelayList;
    private DelayListAdapter mDelayListAdapter;

    private RecyclerView mReviewList;
    private ReviewListAdapter mReviewListAdapter;

    private RecyclerView mLeaveList;

    private ListView mWarningTasks;
    private WarningListViewAdapter mWarningAdapter;

    private int mWorkerOnCount = 0;
    private int mWorkerOvertimeCount = 0;
    private int mWarningCount = 0;

    private List<Task> mDelayTasks = new ArrayList<>();
    private List<Warning> mWarnings = new ArrayList<>();
    private List<Task> mReviewTasks = new ArrayList<>();
    private List<Worker> mLeaveWorkers = new ArrayList<>();


    private class WarningListViewAdapter extends ArrayAdapter<Warning> {

        public WarningListViewAdapter(List<Warning> warnings) {
            super(getActivity(), 0, warnings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.main_information_list_warning_content, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            WorkingData data = WorkingData.getInstance(getActivity());
            Warning warning = getItem(position);
            if (warning != null) {
                Utils.setTaskItemWarningTextView(getActivity(), data.getTaskById(warning.taskId), holder.title, false);
                holder._case.setText(data.getCaseById(data.getTaskById(warning.taskId).caseId).name);
                holder.task.setText(data.getTaskById(warning.taskId).name);
                holder.worker.setText(data.getWorkerById(data.getTaskById(warning.taskId).workerId).name);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView title;
            TextView _case;
            TextView task;
            TextView worker;

            public ViewHolder(View v) {
                title = (TextView) v.findViewById(R.id.title);
                _case = (TextView) v.findViewById(R.id.case_name);
                task = (TextView) v.findViewById(R.id.task);
                worker = (TextView) v.findViewById(R.id.worker);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        findViews();
        retrieveDatas();
        setupBoards();
        setupDelayList();
        setupWarningList();
        setupReviewList();
        setupLeaveList();
    }

    private void findViews() {
        mWorkerOnCountText = (TextView) getView().findViewById(R.id.main_information_worker_on_count);
        mWorkerOvertimeCountText = (TextView) getView().findViewById(R.id.main_information_worker_overtime_count);
        mWarningCountText = (TextView) getView().findViewById(R.id.main_information_warning_count);
        mCostsText = (TextView) getView().findViewById(R.id.main_information_costs);
        mDelayList = (RecyclerView) getView().findViewById(R.id.main_information_list_delay);
        mReviewList = (RecyclerView) getView().findViewById(R.id.main_information_list_review);
        mLeaveList = (RecyclerView) getView().findViewById(R.id.main_information_list_leave);
        mWarningTasks = (ListView) getView().findViewById(R.id.main_information_list_warning);
    }

    private void retrieveDatas() {
        for (Task task : WorkingData.getInstance(mContext).getTasks()) {
            if (task.isDelayed && !Task.Status.IN_REVIEW.equals(task.status) && !Task.Status.DONE.equals(task.status)) {
                mDelayTasks.add(task);
            }
            if (Task.Status.IN_REVIEW.equals(task.status)) {
                mReviewTasks.add(task);
            }

            for (Warning warning : task.warnings) {
                if (warning.status == Warning.Status.OPEN) {
                    mWarningCount++;
                }
            }
        }

        for (Worker worker : WorkingData.getInstance(mContext).getWorkers()) {
            if (!Worker.Status.OFF.equals(worker.status) && !Worker.Status.STOP.equals(worker.status)) {
                mWorkerOnCount++;
            }

            if (worker.isOvertime) {
                mWorkerOvertimeCount++;
            }
        }

        for (Vendor vendor : WorkingData.getInstance(mContext).getVendors()) {
            for (Case _case : vendor.getCases()) {
                for (Task task : _case.tasks) {
                    for (Warning warning : task.warnings) {
                        if (warning.status == Warning.Status.OPEN) {
                            mWarnings.add(warning);
                        }
                    }
                }
            }
        }
    }

    private void setupBoards() {
        mWorkerOnCountText.setText(String.valueOf(mWorkerOnCount));
        mWorkerOvertimeCountText.setText(String.valueOf(mWorkerOvertimeCount));
        mWarningCountText.setText(String.valueOf(mWarningCount));
        mCostsText.setText(String.format(getString(R.string.main_information_$), WorkingData.getInstance(mContext).getCosts()));
    }

    private void setupDelayList() {
        mDelayListAdapter = new DelayListAdapter(getActivity(), mDelayTasks);
        mDelayList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDelayList.addItemDecoration(new DividerItemDecoration(mContext.getResources().
                getDrawable(R.drawable.drawer_divider), false, true));
        mDelayList.setAdapter(mDelayListAdapter);
    }

    private void setupWarningList() {
        mWarningAdapter = new WarningListViewAdapter(mWarnings);
        mWarningTasks.setHeaderDividersEnabled(true);
        mWarningTasks.addHeaderView(LayoutInflater.from(getActivity())
                .inflate(R.layout.main_information_list_warning_title, null), null, false);
        mWarningTasks.setAdapter(mWarningAdapter);
    }

    private void setupReviewList() {
        mReviewListAdapter = new ReviewListAdapter(getActivity(), mReviewTasks);
        mReviewList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReviewList.addItemDecoration(new DividerItemDecoration(mContext.getResources().
                getDrawable(R.drawable.drawer_divider), false, true));
        mReviewList.setAdapter(mReviewListAdapter);
    }

    private void setupLeaveList() {
        mLeaveList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
