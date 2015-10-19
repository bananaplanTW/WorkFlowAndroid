package com.bananaplan.workflowandroid.info;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class InfoFragment extends Fragment implements View.OnClickListener {

    private TextView mCntWorkers;
    private TextView mCntOvertimeWorkers;
    private TextView mCntWarningTasks;
    private TextView mCosts;
    private TextView mNotiCnt;
    private ListView mDelayTasks;
    private ListView mWarningTasks;
    private ListView mCheckedTasks;
    private ListView mLeaveWorkers;
    private WarningListViewAdapter mWarningAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        final WorkingData data = WorkingData.getInstance(getActivity());
        mCntWorkers = (TextView) getActivity().findViewById(R.id.cnt_worker_on);
        mCntOvertimeWorkers = (TextView) getActivity().findViewById(R.id.cnt_worker_overtime);
        mCntWarningTasks = (TextView) getActivity().findViewById(R.id.cnt_warnings);
        mCosts = (TextView) getActivity().findViewById(R.id.cost);
        mNotiCnt = (TextView) getActivity().findViewById(R.id.noti_cnt);
        getActivity().findViewById(R.id.noti_cnt_vg).setOnClickListener(this);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mDelayTasks = (ListView) getActivity().findViewById(R.id.list_delay);
        mDelayTasks.setHeaderDividersEnabled(true);
        mDelayTasks.addHeaderView(inflater.inflate(R.layout.info_frag_list_delay_title, null), null, false);
        mWarningTasks = (ListView) getActivity().findViewById(R.id.list_warning);
        mWarningTasks.setHeaderDividersEnabled(true);
        mWarningTasks.addHeaderView(inflater.inflate(R.layout.info_frag_list_warning_title, null), null, false);
        mCheckedTasks = (ListView) getActivity().findViewById(R.id.list_checked);
        mCheckedTasks.setHeaderDividersEnabled(true);
        mCheckedTasks.addHeaderView(inflater.inflate(R.layout.info_frag_list_checked_title, null), null, false);
        mLeaveWorkers = (ListView) getActivity().findViewById(R.id.list_leave_workers);
        mLeaveWorkers.setHeaderDividersEnabled(true);
        mLeaveWorkers.addHeaderView(inflater.inflate(R.layout.info_frag_list_leave_workers_title, null), null, false);
        new AsyncTask<Void, Void, Void>() {
            int cntWorkers = 0;
            int cntOvertimeWorkers = 0;
            int cntWarningTasks = 0;

            @Override
            protected Void doInBackground(Void... params) {
                // TODO: count workers on
                for (Worker worker : data.getWorkers()) {
                    if (worker.isOvertime) {
                        cntOvertimeWorkers++;
                    }
                }
                for (Task task : data.getTasks()) {
                    for (Warning warning : task.warnings) {
                        if (warning.status == Warning.Status.OPEN) {
                            cntWarningTasks++;
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
                mCntWorkers.setText(Integer.toString(cntWorkers));
                mCntOvertimeWorkers.setText(Integer.toString(cntOvertimeWorkers));
                mCntWarningTasks.setText(Integer.toString(cntWarningTasks));
                mCosts.setText("$" + Long.toString(data.cost));
                mWarningTasks.setAdapter(mWarningAdapter);
            }
        }.execute();
    }

    private class WarningListViewAdapter extends ArrayAdapter<Warning> {
        public WarningListViewAdapter(ArrayList<Warning> warnings) {
            super(getActivity(), 0, warnings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.info_frag_list_warning_content, parent, false);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.noti_cnt_vg:
                break;
        }
    }
}
