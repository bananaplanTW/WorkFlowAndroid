package com.bananaplan.workflowandroid.workeroverview;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.main.WorkingData;
import com.bananaplan.workflowandroid.utility.BarChartData;
import com.bananaplan.workflowandroid.utility.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/1.
 */
public class WorkerOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener
        , View.OnClickListener, TabHost.OnTabChangeListener {

    private static class TAB_TAG {
        private static final String TASK_ITEMS     = "tab_tag_task_items";
        private static final String WORKER_STATUS = "tab_tag_worker_status";
    }

    private MainActivity mActivity;

    private Spinner mFactoriesSpinner;
    private EditText mWorkerSearchEditText;
    private ListView mWorkerListView;
    private ImageView mIvWorkerAvatar;
    private TextView mTvWorkerName;
    private TextView mTvWorkerTitle;
    private TextView mTvWorkerFactoryName;
    private TextView mTvWorkerAddress;
    private TextView mTvWorkerPhone;
    private TextView mTvEditWorker;
    private TabHost mTabHost;
    private TextView mDateChoosed;
    private LinearLayout mBarChartContainer;
    private TextView mTvWorkingHours;
    private TextView mTvOvertimeHours;
    private TextView mTvIdleHours;
    private ListView mTaskItemListView;

    private FactorySpinnerAdapter mFactorySpinnerAdapter;
    private WorkerLisViewAdapter mWorkerLisViewAdapter;
    private WorkerItem mSelectedWorker;
    private TaskItemListViewAdapter mTaskItemListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker_overview, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof MainActivity)) return;
        mActivity = (MainActivity) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // findview
        mFactoriesSpinner = (Spinner) mActivity.findViewById(R.id.ov_leftpane_spinner);
        mWorkerSearchEditText = (EditText) mActivity.findViewById(R.id.ov_leftpane_search_edittext);
        mWorkerListView = (ListView) mActivity.findViewById(R.id.ov_leftpane_listview);
        mIvWorkerAvatar = (ImageView) mActivity.findViewById(R.id.worker_ov_right_pane_worker_avatar);
        mTvWorkerName = (TextView) mActivity.findViewById(R.id.worker_ov_right_pane_worker_name);
        mTvWorkerTitle = (TextView) mActivity.findViewById(R.id.worker_ov_right_pane_worker_title);
        mTvWorkerFactoryName = (TextView) mActivity.findViewById(R.id.worker_ov_right_pane_worker_factory_name);
        mTvWorkerAddress = (TextView) mActivity.findViewById(R.id.worker_ov_right_pane_worker_address);
        mTvWorkerPhone = (TextView) mActivity.findViewById(R.id.worker_ov_right_pane_worker_phone);
        mTvEditWorker = (TextView) mActivity.findViewById(R.id.worker_ov_right_pane_edit_worker);
        mTvEditWorker.setOnClickListener(this);
        mTabHost = (TabHost) mActivity.findViewById(R.id.worker_ov_right_pane_tab_host);
        mTabHost.setup();
        mTabHost.setOnTabChangedListener(this);
        setupTabs();
        mDateChoosed = (TextView) mActivity.findViewById(R.id.ov_statistics_week_chooser_date);
        mBarChartContainer = (LinearLayout) mActivity.findViewById(R.id.ov_statistics_chart_container);
        mTvWorkingHours = (TextView) mActivity.findViewById(R.id.ov_statistics_working_hour_tv);
        mTvOvertimeHours = (TextView) mActivity.findViewById(R.id.ov_statistics_overtime_hour_tv);
        mTvIdleHours = (TextView) mActivity.findViewById(R.id.ov_statistics_idle_hour_tv);
        ((RelativeLayout) mActivity.findViewById(R.id.ov_statistics_overtime_hour_vg)).setVisibility(View.VISIBLE);
        ((RelativeLayout) mActivity.findViewById(R.id.ov_statistics_idle_hour_vg)).setVisibility(View.VISIBLE);
        ((TextView) mActivity.findViewById(R.id.worker_ov_edit_task_item)).setOnClickListener(this);
        ((LinearLayout) mActivity.findViewById(R.id.ov_statistics_week_chooser)).setOnClickListener(this);
        mTaskItemListView = (ListView) mActivity.findViewById(R.id.listview_task_item);
        mTaskItemListView.setOnItemClickListener(this);

        // factory spinner
        mFactorySpinnerAdapter = new FactorySpinnerAdapter(getFactoriesSpinnerData());
        mFactoriesSpinner.setAdapter(mFactorySpinnerAdapter);
        mFactoriesSpinner.setOnItemSelectedListener(this);

        // search worker edittext
        mWorkerSearchEditText.addTextChangedListener(this);

        // worker listview
        mWorkerLisViewAdapter = new WorkerLisViewAdapter(getWorkerLisviewAdapterData());
        mWorkerListView.setAdapter(mWorkerLisViewAdapter);
        mWorkerListView.setOnItemClickListener(this);

        if (mSelectedWorker == null && mWorkerLisViewAdapter.getCount() > 0) {
            mSelectedWorker = mWorkerLisViewAdapter.getItem(0);
        }
        if (mSelectedWorker != null) {
            onWorkerSelected(mSelectedWorker);
        }
        initTaskItemListViewHeader();
    }

    private void initTaskItemListViewHeader() {
        View view = mActivity.findViewById(R.id.worker_ov_task_item_listview_vg);
        if (view == null) return;
        TaskItemListViewAdapterViewHolder holder = new TaskItemListViewAdapterViewHolder(view);
        for (View divider : holder.dividerViews) {
            divider.setVisibility(View.INVISIBLE);
        }
    }

    private class TaskItemListViewAdapter extends ArrayAdapter<TaskItem> {
        public TaskItemListViewAdapter(ArrayList<TaskItem> items) {
            super(mActivity, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskItemListViewAdapterViewHolder holder;
            if (convertView == null) {
                convertView = mActivity.getLayoutInflater().inflate(R.layout.worker_taskitem_listview_view, parent, false);
                holder = new TaskItemListViewAdapterViewHolder(convertView);
                convertView.setTag(holder);
                final ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.height = (int) getResources().getDimension(R.dimen.ov_taskitem_listview_item_height);
                if (position % 2 == 0) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.listview_taskitem_row_odd));
                } else {
                    convertView.setBackgroundColor(getResources().getColor(R.color.listview_taskitem_row_even));
                }
            } else {
                holder = (TaskItemListViewAdapterViewHolder) convertView.getTag();
            }
            TaskItem taskItem = getItem(position);
            holder.tvStartDate.setText(taskItem.getStartedDate());
            holder.tvStatus.setText(Utils.getTaskItemStatusString(getActivity(), taskItem.status));
            holder.tvCaseName.setText(WorkingData.getInstance(mActivity).getTaskCaseById(taskItem.taskCaseId).name);
            holder.tvItemName.setText(taskItem.title);
            holder.tvExpectedTime.setText(taskItem.getExpectedFinishedTime());
            holder.tvWorkTime.setText(taskItem.getWorkingTime());
            holder.tvTool.setText(WorkingData.getInstance(mActivity).getToolById(taskItem.toolId).name);
            holder.tvErrorCount.setText("1");
            holder.tvWarning.setText("");
            return convertView;
        }
    }

    public class TaskItemListViewAdapterViewHolder {
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

        public TaskItemListViewAdapterViewHolder(View view) {
            tvStartDate = (TextView) view.findViewById(R.id.worker_taskitem_listview_start_date);
            tvStatus = (TextView) view.findViewById(R.id.worker_taskitem_listview_status);
            tvCaseName = (TextView) view.findViewById(R.id.worker_taskitem_listview_case_name);
            tvItemName = (TextView) view.findViewById(R.id.worker_taskitem_listview_task_item_name);
            tvExpectedTime = (TextView) view.findViewById(R.id.worker_taskitem_listview_expected_time);
            tvWorkTime = (TextView) view.findViewById(R.id.worker_taskitem_listview_work_time);
            tvTool = (TextView) view.findViewById(R.id.worker_taskitem_listview_tool_used);
            tvErrorCount = (TextView) view.findViewById(R.id.worker_taskitem_listview_error_count);
            tvWarning = (TextView) view.findViewById(R.id.worker_taskitem_listview_warning);
            if (view instanceof ViewGroup) {
                ViewGroup root = (ViewGroup) view;
                for (int i = 0; i < root.getChildCount(); i++) {
                    View child = root.getChildAt(i);
                    if (child.getId() == R.id.listview_taskitem_divider) {
                        dividerViews.add(child);
                    }
                }
            }
        }
    }

    private void setupTabs() {
        mActivity.getLayoutInflater().inflate(R.layout.fragment_worker_ov_tabs, mTabHost.getTabContentView(), true);
        TabHost.TabSpec taskItemsTabSpec = mTabHost.newTabSpec(TAB_TAG.TASK_ITEMS)
                .setIndicator(getTabTitleView(0))
                .setContent(R.id.worker_ov_tab_task_items);
        TabHost.TabSpec workerStatusTabSpec = mTabHost.newTabSpec(TAB_TAG.WORKER_STATUS)
                .setIndicator(getTabTitleView(1))
                .setContent(R.id.worker_ov_tab_worker_status);
        mTabHost.addTab(taskItemsTabSpec);
        mTabHost.addTab(workerStatusTabSpec);
    }

    private View getTabTitleView(final int pos) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.worker_ov_tab, null);
        String text = getResources().getString(pos == 0 ? R.string.worker_ov_worker_tab_title_task_items : R.string.worker_ov_worker_tab_title_worker_status);
        ((TextView) view.findViewById(R.id.worker_ov_tab_title)).setText(text);
        return view;
    }

    private ArrayList<Factory> getFactoriesSpinnerData() {
        ArrayList<Factory> tmp = new ArrayList<>();
        tmp.add(new Factory(-1, getResources().getString(R.string.worker_ov_all_factories))); // all factories
        tmp.addAll(WorkingData.getInstance(mActivity).getFactories());
        return tmp;
    }

    private class FactorySpinnerAdapter extends IconSpinnerAdapter<Factory> {

        public FactorySpinnerAdapter(ArrayList<Factory> objects) {
            super(mActivity, -1, objects);
        }

        @Override
        public Factory getItem(int position) {
            return (Factory) super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id;
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return getItem(position).name;
        }

        @Override
        public String getSpinnerViewDisplayString(int position) {
            return getItem(position).name;
        }

        @Override
        public boolean isDropdownSelectedIconVisible(int position) {
            return position == mFactoriesSpinner.getSelectedItemPosition();
        }

        @Override
        public int getSpinnerIconResourceId() {
            return R.drawable.ic_business_black;
        }
    }

    private ArrayList<WorkerItem> getWorkerLisviewAdapterData() {
        ArrayList<WorkerItem> tmp = new ArrayList<>();
        for (Factory factory : WorkingData.getInstance(mActivity).getFactories()) {
            tmp.addAll(factory.workerItems);
        }
        return tmp;
    }

    private class WorkerLisViewAdapter extends ArrayAdapter<WorkerItem> implements Filterable {
        private int mSelectedPosition = 0;
        private CustomFilter mFilter;
        private ArrayList<WorkerItem> mOrigData;
        private ArrayList<WorkerItem> mFilteredData;

        public WorkerLisViewAdapter(ArrayList<WorkerItem> workers) {
            super(mActivity, -1, workers);
            mOrigData = workers;
            mFilteredData = new ArrayList<>(mOrigData);
            mFilter = new CustomFilter();
        }

        @Override
        public int getCount() {
            return mFilteredData.size();
        }

        @Override
        public WorkerItem getItem(int position) {
            return mFilteredData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mFilteredData.get(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mActivity.getLayoutInflater().inflate(R.layout.worker_overview_worker_listview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.avatar.setImageDrawable(getItem(position).getAvator());
            holder.name.setText(getItem(position).name);
            holder.title.setText(getItem(position).title);

            // update background of selected item
            if (position == mSelectedPosition) {
                holder.root.setBackgroundColor(getResources().getColor(R.color.listview_selected_bg));
                holder.name.setTextColor(Color.WHITE);
                holder.title.setTextColor(Color.WHITE);
            } else {
                holder.root.setBackgroundColor(Color.TRANSPARENT);
                holder.name.setTextColor(getResources().getColor(R.color.overview_listview_first_item_textcolor));
                holder.title.setTextColor(getResources().getColor(R.color.overview_listview_second_item_textcolor));
            }

            return convertView;
        }

        private class ViewHolder {
            RelativeLayout root;
            ImageView avatar;
            TextView name;
            TextView title;

            public ViewHolder(View view) {
                root = (RelativeLayout) view.findViewById(R.id.worker_ov_worker_listview_root);
                avatar = (ImageView) view.findViewById(R.id.worker_ov_worker_listview_worker_avatar);
                name = (TextView) view.findViewById(R.id.worker_ov_worker_listview_worker_name);
                title = (TextView) view.findViewById(R.id.worker_ov_worker_listview_worker_title);
            }
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class CustomFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                ArrayList<WorkerItem> filterResult = new ArrayList<>();
                for (WorkerItem worker : mOrigData) {
                    if ((TextUtils.isEmpty(constraint) ? true : worker.name.toLowerCase().contains(constraint))
                            && (mFactoriesSpinner.getSelectedItemId() == -1
                            || worker.factoryId == mFactoriesSpinner.getSelectedItemId())) {
                        filterResult.add(worker);
                    }
                }
                result.values = filterResult;
                result.count = filterResult.size();
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredData.clear();
                mFilteredData.addAll((ArrayList<WorkerItem>) results.values);
                notifyDataSetChanged();
            }
        }

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.worker_ov_right_pane_edit_worker:
                editWorker();
                break;
            case R.id.worker_ov_edit_task_item:
                editTaskItem();
                break;
            default:
                break;
        }
    }

    private void editTaskItem() {
        // TODO
    }

    private void editWorker() {
        // TODO
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_listview:
                mWorkerLisViewAdapter.setSelectedPosition(position);
                onWorkerSelected(mWorkerLisViewAdapter.getItem(position));
                mWorkerLisViewAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_spinner:
                oFactorySelected((Factory) mFactoriesSpinner.getSelectedItem());
                break;
            default:
                break;
        }
    }

    private void oFactorySelected(Factory factory) {
        mWorkerLisViewAdapter.getFilter().filter(mWorkerSearchEditText.getText().toString());
    }

    private void onWorkerSelected(WorkerItem worker) {
        if (worker == null) return;
        // update worker's personal info.
        mIvWorkerAvatar.setImageDrawable(worker.getAvator());
        mTvWorkerName.setText(worker.name);
        mTvWorkerTitle.setText(worker.title);
        mTvWorkerFactoryName.setText(WorkingData.getInstance(mActivity).getFactoryById(worker.factoryId).name);
        mTvWorkerAddress.setText(getResources().getString(R.string.worker_ov_worker_address)
                + (TextUtils.isEmpty(worker.address) ? "" : worker.address));
        mTvWorkerPhone.setText(getResources().getString(R.string.worker_ov_worker_phone)
                + (TextUtils.isEmpty(worker.phone) ? "" : worker.phone));

        // update statistics
        updateStatisticsView();

        // update task item listview
        ArrayList<TaskItem> items = WorkingData.getInstance(mActivity).getTaskItemsByWorker(worker);
        if (mTaskItemListViewAdapter == null) {
            mTaskItemListViewAdapter = new TaskItemListViewAdapter(items);
            mTaskItemListView.setAdapter(mTaskItemListViewAdapter);
        } else {
            mTaskItemListViewAdapter.clear();
            mTaskItemListViewAdapter.addAll(items);
        }
        if (items.size() > 0) {
            ViewGroup.LayoutParams params = mTaskItemListView.getLayoutParams();
            params.height = (int) (items.size() * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height));
        }
        mTaskItemListViewAdapter.notifyDataSetChanged();
    }

    private void updateStatisticsView() {
        BarChartData data = new BarChartData();
        data.genRandomData(mActivity, 3);
        mBarChartContainer.removeAllViews();
        mBarChartContainer.addView(Utils.genBarChart(mActivity, data),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        mTvWorkingHours.setText(getResources().getString(R.string.overview_working_hours, data.getWorkingHours()));
        mTvOvertimeHours.setText(getResources().getString(R.string.overview_overtime_hours, data.getOvertimeHours()));
        mTvIdleHours.setText(getResources().getString(R.string.overview_idle_hours, data.getIdleHours()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        mWorkerLisViewAdapter.getFilter().filter(mWorkerSearchEditText.getText().toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void onTabChanged(String tabId) {
        // do nothing
    }
}
