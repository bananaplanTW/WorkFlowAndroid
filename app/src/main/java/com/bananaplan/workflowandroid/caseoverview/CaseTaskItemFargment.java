package com.bananaplan.workflowandroid.caseoverview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.main.WorkingData;
import com.bananaplan.workflowandroid.utility.BarChartData;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;

import it.sephiroth.android.library.widget.HListView;

/**
 * Created by Ben on 2015/8/23.
 */
public class CaseTaskItemFargment extends OvTabFragmentBase implements OvTabFragmentBase.CaseOvCallBack, View.OnClickListener, AdapterView.OnItemClickListener,
        it.sephiroth.android.library.widget.AdapterView.OnItemClickListener {
    private HListView mWorkerListView;
    private LinearLayout mStatisticsViewGroup;
    private LinearLayout mWeekPickerViewGroup;
    private ListView mTaskItemListView;
    private TaskItemListViewAdapter mTaskItemListViewAdapter;
    private WorkerItemListViewAdapter mWorkerItemListViewAdapter;
    private int mTaskItemListViewHeaderHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_case_ov_taskitem, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStatisticsViewGroup = (LinearLayout) getActivity().findViewById(R.id.ov_statistics_chart_container);
        ((LinearLayout.LayoutParams) mStatisticsViewGroup.getLayoutParams()).topMargin = getResources().getDimensionPixelOffset(R.dimen.case_ov_statistics_margin_top);
        mWeekPickerViewGroup = (LinearLayout) getActivity().findViewById(R.id.ov_statistics_week_chooser);
        mWeekPickerViewGroup.setOnClickListener(this);
        mWorkerListView = (HListView) getActivity().findViewById(R.id.case_workers_listview);
        mWorkerListView.setOnItemClickListener(this);
        mTaskItemListView = (ListView) getActivity().findViewById(R.id.case_listview_task_item);
        mTaskItemListView.setOnItemClickListener(this);
        mTaskItemListView.addHeaderView(getTaskItemHeaderView(), null, false);
        (getActivity().findViewById(R.id.case_ov_right_pane_edit_case)).setOnClickListener(this);
        onCaseSelected(getSelectedTaskCase());
    }

    @Override
    public void onCaseSelected(TaskCase taskCase) {
        if (taskCase == null) return;
        updateTaskItemListView();
        updateWorkerItemListView();
        updateStatisticsView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.case_ov_right_pane_edit_case:
                editTaskCase();
                break;
            case R.id.ov_statistics_week_chooser:
                // TODO
                break;
            default:
                break;
        }
    }

    private void editTaskCase() {
        // TODO
    }

    @Override
    public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> adapterView, View view, int i, long l) {
        launchWorkerPage(mWorkerItemListViewAdapter.getItem(i));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mTaskItemListView.getId()) {
            // TODO
        }
    }

    private void updateTaskItemListView() {
        ArrayList<TaskItem> items = getTaskItems();
        if (mTaskItemListViewAdapter == null) {
            mTaskItemListViewAdapter = new TaskItemListViewAdapter(getActivity(), items);
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

    private void updateWorkerItemListView() {
        ArrayList<WorkerItem> workers = getWorkerItems();
        if (mWorkerItemListViewAdapter == null) {
            mWorkerItemListViewAdapter = new WorkerItemListViewAdapter(getActivity(), workers);
            mWorkerListView.setAdapter(mWorkerItemListViewAdapter);
        } else {
            mWorkerItemListViewAdapter.clear();
            mWorkerItemListViewAdapter.addAll(workers);
        }
        mWorkerItemListViewAdapter.notifyDataSetChanged();
    }

    private View getTaskItemHeaderView() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.case_ov_taskitem_listview_itemview, null);
        if (view == null) throw new NullPointerException();
        TaskItemListViewAdapterViewHolder holder = new TaskItemListViewAdapterViewHolder(view);
        float textSize = getResources().getDimension(R.dimen.case_overview_taskitem_listview_header_text_size);
        holder.tvStatus.setTextSize(textSize);
        holder.tvTool.setTextSize(textSize);
        holder.tvWorkTime.setTextSize(textSize);
        holder.tvExpectedTime.setTextSize(textSize);
        holder.tvId.setTextSize(textSize);
        holder.tvWarning.setTextSize(textSize);
        holder.tvName.setTextSize(textSize);
        holder.llWorkerInfo.setVisibility(View.GONE);
        holder.tvWorkerNameString.setVisibility(View.VISIBLE);
        for (View divider : holder.dividerViews) {
            divider.setVisibility(View.INVISIBLE);
        }
        for (View divider : holder.horizontalDividerViews) {
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
        return view;
    }

    private void updateStatisticsView() {
        BarChartData data = new BarChartData(this.getClass().getName());
        data.genRandomData(getActivity(), 1);
        mStatisticsViewGroup.removeAllViews();
        mStatisticsViewGroup.addView(Utils.genBarChart(getActivity(), data),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        ((TextView) getActivity().findViewById(R.id.ov_statistics_working_hour_tv)).setText(getResources().getString(R.string.overview_finish_hours, data.getWorkingHours()));
    }

    private class TaskItemListViewAdapter extends ArrayAdapter<TaskItem> {

        public TaskItemListViewAdapter(Context context, ArrayList<TaskItem> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskItemListViewAdapterViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.case_ov_taskitem_listview_itemview, parent, false);
                holder = new TaskItemListViewAdapterViewHolder(convertView);
                holder.tvWarning.setText("");
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
            final TaskItem taskItem = getItem(position);
            holder.tvId.setText(String.valueOf(position + 1));
            holder.tvStatus.setText(Utils.getTaskItemStatusString(getActivity(), taskItem));
            if (TaskItem.Status.FINISH == taskItem.status) {
                holder.tvStatus.setBackground(null);
                holder.tvStatus.setTextColor(getResources().getColor(R.color.gray1));
                holder.tvName.setTextColor(getResources().getColor(R.color.gray1));
                holder.tvExpectedTime.setTextColor(getResources().getColor(R.color.gray1));
                holder.tvWorkTime.setTextColor(getResources().getColor(R.color.gray1));
                holder.tvTool.setTextColor(getResources().getColor(R.color.gray1));
            } else {
                if (TaskItem.Status.WORKING == taskItem.status) {
                    holder.tvStatus.setBackground(getResources().getDrawable(R.drawable.border_textview_bg_green, null));
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.green));
                } else {
                    holder.tvStatus.setBackground(null);
                    holder.tvStatus.setTextColor(getResources().getColor(R.color.black2));
                }
                holder.tvName.setTextColor(getResources().getColor(R.color.black2));
                holder.tvExpectedTime.setTextColor(getResources().getColor(R.color.black2));
                holder.tvWorkTime.setTextColor(getResources().getColor(R.color.black2));
                holder.tvTool.setTextColor(getResources().getColor(R.color.black2));
            }
            Utils.setTaskItemWarningTextView(getActivity(), taskItem, holder.tvWarning, true);
            holder.tvName.setText(taskItem.title);
            holder.tvExpectedTime.setText(taskItem.getExpectedFinishedTime());
            holder.tvWorkTime.setText(taskItem.getWorkingTime());
            if (taskItem.toolId > 0) {
                holder.tvTool.setText(WorkingData.getInstance(getActivity()).getToolById(taskItem.toolId).name);
            } else {
                holder.tvTool.setText("");
            }
            if (taskItem.workerId > 0) {
                final WorkerItem worker = WorkingData.getInstance(getActivity()).getWorkerItemById(taskItem.workerId);
                holder.tvWorkerName.setText(worker.name);
                holder.ivWorkerAvator.setVisibility(View.VISIBLE);
                holder.ivWorkerAvator.setImageDrawable(worker.getAvator());
                holder.llWorkerInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: view worker's info
                        Toast.makeText(getActivity(), "View worker's info", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                holder.tvWorkerName.setText("");
                holder.ivWorkerAvator.setImageDrawable(null);
            }
            return convertView;
        }
    }

    public static class TaskItemListViewAdapterViewHolder {
        TextView tvId;
        TextView tvStatus;
        TextView tvName;
        TextView tvExpectedTime;
        TextView tvWorkTime;
        TextView tvTool;
        TextView tvWarning;
        TextView tvWorkerName;
        TextView tvWorkerNameString;
        ImageView ivWorkerAvator;
        LinearLayout llWorkerInfo;
        ArrayList<View> dividerViews = new ArrayList<>();
        ArrayList<View> horizontalDividerViews = new ArrayList<>();

        public TaskItemListViewAdapterViewHolder(View view) {
            if (!(view instanceof LinearLayout)) return;
            LinearLayout root = (LinearLayout) view;
            tvId = (TextView) view.findViewById(R.id.taskitem_listview_id);
            tvStatus = (TextView) view.findViewById(R.id.taskitem_listview_status);
            tvName = (TextView) view.findViewById(R.id.taskitem_listview_name);
            tvExpectedTime = (TextView) view.findViewById(R.id.taskitem_listview_expected_time);
            tvWorkTime = (TextView) view.findViewById(R.id.taskitem_listview_work_time);
            tvTool = (TextView) view.findViewById(R.id.taskitem_listview_work_tool);
            tvWarning = (TextView) view.findViewById(R.id.taskitem_listview_warning);
            tvWorkerName = (TextView) view.findViewById(R.id.taskitem_listview_worker_name);
            tvWorkerNameString = (TextView) view.findViewById(R.id.taskitem_listview_worker_name_string);
            ivWorkerAvator = (ImageView) view.findViewById(R.id.taskitem_listview_worker_avator);
            llWorkerInfo = (LinearLayout) view.findViewById(R.id.taskitem_listview_worker_info);
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                if (child.getId() == R.id.horozontal_divider) {
                    horizontalDividerViews.add(child);
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

    private class WorkerItemListViewAdapter extends ArrayAdapter<WorkerItem> {
        private LayoutInflater mInflater;

        public WorkerItemListViewAdapter(Context context, ArrayList<WorkerItem> items) {
            super(context, 0, items);
            mInflater = getActivity().getLayoutInflater();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.case_ov_workers_listview_itemview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            WorkerItem worker = getItem(position);
            holder.avator.setImageDrawable(worker.getAvator());
            return convertView;
        }

        private class ViewHolder {
            ImageView avator;

            public ViewHolder(View view) {
                avator = (ImageView) view.findViewById(R.id.case_workers_listview_item_iv);
            }
        }
    }

    private void launchWorkerPage(WorkerItem worker) {

    }

    private ArrayList<TaskItem> getTaskItems() {
        if (getSelectedTaskCase() != null) {
            return new ArrayList<>(getSelectedTaskCase().taskItems);
        }
        return new ArrayList<>();
    }

    private ArrayList<WorkerItem> getWorkerItems() {
        ArrayList<WorkerItem> workers = new ArrayList<>();
        if (getSelectedTaskCase() != null) {
            for (TaskItem item : getSelectedTaskCase().taskItems) {
                workers.add(WorkingData.getInstance(getActivity()).getWorkerItemById(item.workerId));
            }
        }
        return workers;
    }

    @Override
    public Object getCallBack() {
        return this;
    }
}
