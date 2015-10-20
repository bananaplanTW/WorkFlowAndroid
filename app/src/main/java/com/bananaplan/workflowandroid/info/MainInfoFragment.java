package com.bananaplan.workflowandroid.info;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class MainInfoFragment extends Fragment implements View.OnClickListener {

    private TextView mWorkerOnCount;
    private TextView mWorkerOvertimeCount;
    private TextView mWarningCount;
    private TextView mCosts;

    private RecyclerView mDelayList;
    private RecyclerView mReviewList;
    private RecyclerView mLeaveList;

    private ListView mWarningTasks;
    private WarningListViewAdapter mWarningAdapter;


    private class WarningListViewAdapter extends ArrayAdapter<Warning> {

        public WarningListViewAdapter(ArrayList<Warning> warnings) {
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
        findViews();
        setupWarningList();
        setDatas();
    }

    private void findViews() {
        mWorkerOnCount = (TextView) getView().findViewById(R.id.main_information_worker_on_count);
        mWorkerOvertimeCount = (TextView) getView().findViewById(R.id.main_information_worker_overtime_count);
        mWarningCount = (TextView) getView().findViewById(R.id.main_information_warning_count);
        mCosts = (TextView) getView().findViewById(R.id.main_information_costs);
        mDelayList = (RecyclerView) getView().findViewById(R.id.main_information_list_delay);
        mReviewList = (RecyclerView) getView().findViewById(R.id.main_information_list_review);
        mLeaveList = (RecyclerView) getView().findViewById(R.id.main_information_list_leave);
        mWarningTasks = (ListView) getView().findViewById(R.id.main_information_list_warning);
    }

    private void setupWarningList() {
        mWarningTasks.setHeaderDividersEnabled(true);
        mWarningTasks.addHeaderView(LayoutInflater.from(getActivity())
                .inflate(R.layout.main_information_list_warning_title, null), null, false);
    }

    private void setDatas() {
        final WorkingData data = WorkingData.getInstance(getActivity());

        new AsyncTask<Void, Void, Void>() {
            int workerOnCount = 0;
            int workerOvertimeCount = 0;
            int warningCount = 0;

            @Override
            protected Void doInBackground(Void... params) {
                // TODO: count workers on
                for (Worker worker : data.getWorkers()) {
                    if (worker.isOvertime) {
                        workerOvertimeCount++;
                    }
                }
                for (Task task : data.getTasks()) {
                    for (Warning warning : task.warnings) {
                        if (warning.status == Warning.Status.OPEN) {
                            warningCount++;
                        }
                    }
                }
                ArrayList<Warning> warnings = new ArrayList<>();
                for (Vendor vendor : data.getVendors()) {
                    for (Case _case : vendor.getCases()) {
                        for (Task task : _case.tasks) {
                            for (Warning warning : task.warnings) {
                                if (warning.status == Warning.Status.OPEN) {
                                    warnings.add(warning);
                                }
                            }
                        }
                    }
                }
                mWarningAdapter = new WarningListViewAdapter(warnings);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mWorkerOnCount.setText(Integer.toString(workerOnCount));
                mWorkerOvertimeCount.setText(Integer.toString(workerOvertimeCount));
                mWarningCount.setText(Integer.toString(warningCount));
                mCosts.setText("$" + Long.toString(data.cost));

                mWarningTasks.setAdapter(mWarningAdapter);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {

    }
}
