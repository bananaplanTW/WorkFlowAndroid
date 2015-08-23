package com.bananaplan.workflowandroid.workeroverview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.main.WorkingData;
import com.bananaplan.workflowandroid.utility.BarChartData;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/14.
 */
public class WorkerTaskItemFragment extends OvTabFragmentBase implements View.OnClickListener, AdapterView.OnItemClickListener, OvTabFragmentBase.WorkerOvCallBack {
    private TextView mDateChoosed;
    private LinearLayout mBarChartContainer;
    private TextView mTvWorkingHours;
    private TextView mTvOvertimeHours;
    private TextView mTvIdleHours;
    private ListView mTaskItemListView;

    private TaskItemListViewAdapter mTaskItemListViewAdapter;
    private int mTaskItemListViewHeaderHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_worker_ov_taskitem, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDateChoosed = (TextView) getActivity().findViewById(R.id.ov_statistics_week_chooser_date);
        mBarChartContainer = (LinearLayout) getActivity().findViewById(R.id.ov_statistics_chart_container);
        ((LinearLayout.LayoutParams)mBarChartContainer.getLayoutParams()).topMargin = getResources().getDimensionPixelOffset(R.dimen.worker_ov_statistics_margin_top);
        mTvWorkingHours = (TextView) getActivity().findViewById(R.id.ov_statistics_working_hour_tv);
        mTvOvertimeHours = (TextView) getActivity().findViewById(R.id.ov_statistics_overtime_hour_tv);
        mTvIdleHours = (TextView) getActivity().findViewById(R.id.ov_statistics_idle_hour_tv);
        getActivity().findViewById(R.id.ov_statistics_overtime_hour_vg).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.ov_statistics_idle_hour_vg).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.worker_ov_edit_task_item).setOnClickListener(this);
        getActivity().findViewById(R.id.ov_statistics_week_chooser).setOnClickListener(this);
        mTaskItemListView = (ListView) getActivity().findViewById(R.id.listview_task_item);
        mTaskItemListView.setOnItemClickListener(this);
        mTaskItemListView.addHeaderView(getTaskItemListViewHeader(), null, false);
        onWorkerSelected(getSelectedWorker());
    }

    private View getTaskItemListViewHeader() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.worker_taskitem_listview_view, null);
        TaskItemListViewAdapterViewHolder holder = new TaskItemListViewAdapterViewHolder(view);
        for (View divider : holder.dividerViews) {
            divider.setVisibility(View.INVISIBLE);
        }
        for (View divider : holder.horozontalDividerViews) {
            divider.setVisibility(View.VISIBLE);
        }
        ViewTreeObserver observer = view.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mTaskItemListViewHeaderHeight = view.getHeight();
                    if (mTaskItemListViewAdapter != null && mTaskItemListViewAdapter.getCount() > 0) {
                        ViewGroup.LayoutParams params = mTaskItemListView.getLayoutParams();
                        params.height = (int) (mTaskItemListViewAdapter.getCount() * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height)) + mTaskItemListViewHeaderHeight;
                        mTaskItemListViewAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        mTaskItemListViewHeaderHeight = view.getHeight();
        return view;
    }

    private class TaskItemListViewAdapter extends ArrayAdapter<TaskItem> {
        public TaskItemListViewAdapter(ArrayList<TaskItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskItemListViewAdapterViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.worker_taskitem_listview_view, parent, false);
                holder = new TaskItemListViewAdapterViewHolder(convertView);
                convertView.setTag(holder);
                final ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.height = (int) getResources().getDimension(R.dimen.ov_taskitem_listview_item_height);
                if (position % 2 == 0) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.gray4));
                } else {
                    convertView.setBackgroundColor(Color.WHITE);
                }
            } else {
                holder = (TaskItemListViewAdapterViewHolder) convertView.getTag();
            }
            TaskItem taskItem = getItem(position);
            holder.tvStartDate.setText(Utils.timestamp2Date(taskItem.startDate, false));
            holder.tvStatus.setText(Utils.getTaskItemStatusString(getActivity(), taskItem));
            holder.tvCaseName.setText(WorkingData.getInstance(getActivity()).getTaskCaseById(taskItem.taskCaseId).name);
            holder.tvItemName.setText(taskItem.title);
            holder.tvExpectedTime.setText(taskItem.getExpectedFinishedTime());
            holder.tvWorkTime.setText(taskItem.getWorkingTime());
            holder.tvTool.setText(WorkingData.getInstance(getActivity()).getToolById(taskItem.toolId).name);
            holder.tvErrorCount.setText(String.valueOf(taskItem.errorCount));
            Utils.setTaskItemWarningTextView(getActivity(), taskItem, holder.tvWarning, true);
            int txtColor;
            if (taskItem.status == TaskItem.Status.FINISH) {
                txtColor = getResources().getColor(R.color.gray1);
                holder.tvStatus.setTextColor(txtColor);
            } else {
                txtColor = getResources().getColor(R.color.black1);
                if (TaskItem.Status.WORKING == taskItem.status) {
                    holder.tvStatus.setBackground(getResources().getDrawable(R.drawable.border_textview_bg_green, null));
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.green));
                } else {
                    holder.tvStatus.setBackground(null);
                    holder.tvStatus.setTextColor(txtColor);
                }
            }
            holder.tvStartDate.setTextColor(txtColor);
            holder.tvCaseName.setTextColor(txtColor);
            holder.tvItemName.setTextColor(txtColor);
            holder.tvExpectedTime.setTextColor(txtColor);
            holder.tvWorkTime.setTextColor(txtColor);
            holder.tvTool.setTextColor(txtColor);
            holder.tvErrorCount.setTextColor(txtColor);
            return convertView;
        }
    }

    public static class TaskItemListViewAdapterViewHolder {
        TextView tvStartDate;
        TextView tvStatus;
        TextView tvCaseName;
        TextView tvItemName;
        TextView tvExpectedTime;
        TextView tvWorkTime;
        TextView tvTool;
        TextView tvErrorCount;
        TextView tvWarning;
        ArrayList<View> dividerViews = new ArrayList<>();
        ArrayList<View> horozontalDividerViews = new ArrayList<>();

        public TaskItemListViewAdapterViewHolder(View view) {
            if (!(view instanceof LinearLayout)) return;
            LinearLayout root = (LinearLayout) view;
            tvStartDate = (TextView) view.findViewById(R.id.worker_taskitem_listview_start_date);
            tvStatus = (TextView) view.findViewById(R.id.worker_taskitem_listview_status);
            tvCaseName = (TextView) view.findViewById(R.id.worker_taskitem_listview_case_name);
            tvItemName = (TextView) view.findViewById(R.id.worker_taskitem_listview_task_item_name);
            tvExpectedTime = (TextView) view.findViewById(R.id.worker_taskitem_listview_expected_time);
            tvWorkTime = (TextView) view.findViewById(R.id.worker_taskitem_listview_work_time);
            tvTool = (TextView) view.findViewById(R.id.worker_taskitem_listview_tool_used);
            tvErrorCount = (TextView) view.findViewById(R.id.worker_taskitem_listview_error_count);
            tvWarning = (TextView) view.findViewById(R.id.worker_taskitem_listview_warning);
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                if (child.getId() == R.id.listview_taskitem_horozontal_divider) {
                    horozontalDividerViews.add(child);
                }
                if (!(child instanceof LinearLayout)) continue;
                LinearLayout secondRoot = (LinearLayout) child;
                for (int j = 0; j < secondRoot.getChildCount(); j++) {
                    if (secondRoot.getChildAt(j).getId() == R.id.listview_taskitem_divider) {
                        dividerViews.add(secondRoot.getChildAt(j));
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onWorkerSelected(WorkerItem worker) {
        // update statistics
        updateStatisticsView();

        // update task item listview
        ArrayList<TaskItem> items = WorkingData.getInstance(getActivity()).getTaskItemsByWorker(worker);
        if (mTaskItemListViewAdapter == null) {
            mTaskItemListViewAdapter = new TaskItemListViewAdapter(items);
            mTaskItemListView.setAdapter(mTaskItemListViewAdapter);
        } else {
            mTaskItemListViewAdapter.clear();
            mTaskItemListViewAdapter.addAll(items);
        }
        if (items.size() > 0) {
            ViewGroup.LayoutParams params = mTaskItemListView.getLayoutParams();
            params.height = (int) (items.size() * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height)) + mTaskItemListViewHeaderHeight;
        }
        mTaskItemListViewAdapter.notifyDataSetChanged();
    }

    private void updateStatisticsView() {
        BarChartData data = new BarChartData(this.getClass().getName());
        data.genRandomData(getActivity(), 3);
        mBarChartContainer.removeAllViews();
        mBarChartContainer.addView(Utils.genBarChart(getActivity(), data),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        mTvWorkingHours.setText(getResources().getString(R.string.overview_working_hours, data.getWorkingHours()));
        mTvOvertimeHours.setText(getResources().getString(R.string.overview_overtime_hours, data.getOvertimeHours()));
        mTvIdleHours.setText(getResources().getString(R.string.overview_idle_hours, data.getIdleHours()));
    }

    @Override
    public Object getCallBack() {
        return this;
    }
}
