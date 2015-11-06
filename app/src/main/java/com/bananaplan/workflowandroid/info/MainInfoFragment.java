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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.TaskWarning;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class MainInfoFragment extends Fragment implements DataObserver {

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
    private LeaveListAdapter mLeaveListAdapter;

    private ListView mWarningTasks;
    private WarningListViewAdapter mWarningAdapter;

    private int mWorkerOnCount = 0;
    private int mWorkerOvertimeCount = 0;
    private int mWarningCount = 0;

    private List<Task> mDelayTasks = new ArrayList<>();
    private List<TaskWarning> mTaskWarnings = new ArrayList<>();
    private List<Task> mReviewTasks = new ArrayList<>();


    private class WarningListViewAdapter extends ArrayAdapter<TaskWarning> {

        private class ViewHolder {

            TextView title;
            TextView _case;
            TextView task;
            TextView manager;

            public ViewHolder(View v) {
                title = (TextView) v.findViewById(R.id.title);
                _case = (TextView) v.findViewById(R.id.case_name);
                task = (TextView) v.findViewById(R.id.task);
                manager = (TextView) v.findViewById(R.id.manager);
            }
        }

        public WarningListViewAdapter(List<TaskWarning> taskWarnings) {
            super(getActivity(), 0, taskWarnings);
        }

        public void updateData(List<TaskWarning> data) {
            clear();
            addAll(data);
            notifyDataSetChanged();
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
            TaskWarning taskWarning = getItem(position);
            if (taskWarning != null) {
                Utils.setTaskItemWarningTextView(getActivity(), data.getTaskById(taskWarning.taskId), holder.title, false);
                holder._case.setText(data.getCaseById(data.getTaskById(taskWarning.taskId).caseId).name);
                holder.task.setText(data.getTaskById(taskWarning.taskId).name);
                holder.manager.setText(data.getManagerById(taskWarning.managerId).name);
            }
            return convertView;
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

    @Override
    public void onStart() {
        super.onStart();
        WorkingData.getInstance(mContext).registerDataObserver(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        WorkingData.getInstance(mContext).removeDataObserver(this);
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

            for (TaskWarning taskWarning : task.taskWarnings) {
                if (taskWarning.status == TaskWarning.Status.OPENED) {
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
                    for (TaskWarning taskWarning : task.taskWarnings) {
                        if (taskWarning.status == TaskWarning.Status.OPENED) {
                            mTaskWarnings.add(taskWarning);
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
        mWarningAdapter = new WarningListViewAdapter(mTaskWarnings);
        mWarningTasks.setHeaderDividersEnabled(true);
        //mWarningTasks.addHeaderView(LayoutInflater.from(getActivity())
        //        .inflate(R.layout.main_information_list_warning_title, null), null, false);
        mWarningTasks.setAdapter(mWarningAdapter);
        mWarningTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.showDetailedWarningActivity(mContext,
                        ((WarningListViewAdapter) mWarningTasks.getAdapter()).getItem(position).id);
            }
        });
    }

    private void setupReviewList() {
        mReviewListAdapter = new ReviewListAdapter(getActivity(), mReviewTasks);
        mReviewList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReviewList.addItemDecoration(new DividerItemDecoration(mContext.getResources().
                getDrawable(R.drawable.drawer_divider), false, true));
        mReviewList.setAdapter(mReviewListAdapter);
    }

    private void setupLeaveList() {
        mLeaveListAdapter = new LeaveListAdapter(getActivity(), WorkingData.getInstance(getActivity()).getLeaves());
        mLeaveList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLeaveList.addItemDecoration(new DividerItemDecoration(mContext.getResources().
                getDrawable(R.drawable.drawer_divider), false, true));
        mLeaveList.setAdapter(mLeaveListAdapter);
    }

    @Override
    public void updateData() {
        resetDatas();
        retrieveDatas();

        setupBoards();
        mDelayListAdapter.notifyDataSetChanged();
        mWarningAdapter.notifyDataSetChanged();
        mReviewListAdapter.notifyDataSetChanged();
        mLeaveListAdapter.notifyDataSetChanged();
    }

    private void resetDatas() {
        mWorkerOnCount = 0;
        mWorkerOvertimeCount = 0;
        mWarningCount = 0;
        mDelayTasks.clear();
        mTaskWarnings.clear();
        mReviewTasks.clear();
    }
}
