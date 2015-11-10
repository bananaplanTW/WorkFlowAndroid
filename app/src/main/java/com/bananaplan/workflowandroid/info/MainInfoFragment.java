package com.bananaplan.workflowandroid.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.bananaplan.workflowandroid.detail.warning.DetailedWarningActivity;
import com.bananaplan.workflowandroid.utility.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class MainInfoFragment extends Fragment implements DataObserver {

    public static final int REQUEST_DETAILED_WARNING = 5;

    private Context mContext;

    private TextView mWorkerOnCountText;
    private TextView mWorkerOvertimeCountText;
    private TextView mWarningCountText;
    private TextView mCostsText;

    private RecyclerView mDelayList;
    private DelayListAdapter mDelayListAdapter;
    private List<Task> mDelayTasks = new ArrayList<>();

    private RecyclerView mReviewList;
    private ReviewListAdapter mReviewListAdapter;
    private List<Task> mReviewTasks = new ArrayList<>();

    private RecyclerView mLeaveList;
    private LeaveListAdapter mLeaveListAdapter;

    private ListView mWarningList;
    private WarningListAdapter mWarningListAdapter;
    private List<TaskWarning> mTaskWarnings = new ArrayList<>();

    private int mWorkerOnCount = 0;
    private int mWorkerOvertimeCount = 0;
    private int mWarningCount = 0;


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
        mWarningList = (ListView) getView().findViewById(R.id.main_information_list_warning);
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
        mWarningListAdapter = new WarningListAdapter(getActivity(), mTaskWarnings);
        mWarningList.setHeaderDividersEnabled(true);
        mWarningList.setAdapter(mWarningListAdapter);
        mWarningList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailedWarningActivity.class);
                intent.putExtra(DetailedWarningActivity.EXTRA_WARNING_ID,
                        ((WarningListAdapter) mWarningList.getAdapter()).getItem(position).id);

                startActivityForResult(intent, REQUEST_DETAILED_WARNING);
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
        mWarningListAdapter.notifyDataSetChanged();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_DETAILED_WARNING:
                updateData();

                break;
        }
    }
}
