package com.bananaplan.workflowandroid.caseoverview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.WorkingData;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.Vendor;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.main.Utils;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

import it.sephiroth.android.library.widget.*;

/**
 * @author Ben Lai
 * @since 2015/7/16.
 */
public class CaseOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener
        , View.OnClickListener, View.OnTouchListener {
    private MainActivity mActivity;
    private WorkingData mWorkingData;

    // views in left pane
    private Spinner mSpinner;
    private EditText mEtCaseSearch;
    private ListView mTaskCaseListView;
    private ListView mTaskItemListView;

    private SpinnerAdapter mSpinnerAdapter;
    private TaskCaseListViewAdapter mTaskCaseListviewAdapter;
    private TaskItemListViewAdapter mTaskItemListViewAdapter;
    private WorkerItemListViewAdapter mWorkerItemListViewAdapter;

    // views in right pane
    private TextView mTvCaseNameSelected;
    private TextView mTvCaseVendorSelected;
    private TextView mTvCasePersonInChargeSelected;
    private ProgressBar mPbCaseSelected;
    private TextView mTvCaseHoursPassedBy;
    private TextView mTvCaseHoursUnfinished;
    private TextView mTvCaseHoursForecast;
    private TextView mTvEditCase;
    private HListView mWorkerListView;
    private LinearLayout mStatisticsViewGroup;

    private TaskCase mSelectedTaskCase;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mWorkingData = mActivity.getWorkingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case_overview, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSpinner = (Spinner) getActivity().findViewById(R.id.case_spinner);
        mEtCaseSearch = (EditText) getActivity().findViewById(R.id.case_et_search);
        mTaskCaseListView = (ListView) getActivity().findViewById(R.id.case_listview);
        mTaskItemListView = (ListView) getActivity().findViewById(R.id.case_listview_task_item);
        mWorkerListView = (HListView) getActivity().findViewById(R.id.case_workers_listview);

        mEtCaseSearch.addTextChangedListener(this);
        mSpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.case_spinner_view, getSpinnerVendorData());
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);

        // right pane
        mTvCaseNameSelected = (TextView) getActivity().findViewById(R.id.case_tv_case_name_selected);
        mTvCaseVendorSelected = (TextView) getActivity().findViewById(R.id.case_tv_vendor_selected);
        mTvCasePersonInChargeSelected = (TextView) getActivity().findViewById(R.id.case_tv_person_in_charge_selected);
        mPbCaseSelected = (ProgressBar) getActivity().findViewById(R.id.case_progressBar);
        mTvCaseHoursPassedBy = (TextView) getActivity().findViewById(R.id.case_tv_hours_passed_by);
        mTvCaseHoursUnfinished = (TextView) getActivity().findViewById(R.id.case_tv_hours_unfinished);
        mTvCaseHoursForecast = (TextView) getActivity().findViewById(R.id.case_tv_hours_forecast);
        mTvEditCase = (TextView) getActivity().findViewById(R.id.case_btn_edit_case);
        mStatisticsViewGroup = (LinearLayout) getActivity().findViewById(R.id.case_statistics_vg);

        mTaskCaseListviewAdapter = new TaskCaseListViewAdapter(getActivity(), getTaskCases());
        mTaskCaseListView.setAdapter(mTaskCaseListviewAdapter);
        mTaskCaseListView.setOnItemClickListener(this);

        mTvEditCase.setOnClickListener(this);

        updateTaskItemHeaderView();
        updateTaskItemListView();
        updateWorkerItemListView();
    }

    private ArrayList<Vendor> getSpinnerVendorData() {
        ArrayList<Vendor> tmp = new ArrayList<Vendor>();
        tmp.add(new Vendor(-1L, getResources().getString(R.string.case_spinner_all_vendors))); // all vendors
        tmp.addAll(mWorkingData.getVendors());
        return tmp;
    }

    private ArrayList<TaskCase> getTaskCases() {
        ArrayList<TaskCase> cases = new ArrayList<>();
        for (Vendor vendor : mWorkingData.getVendors()) {
            for (TaskCase taskCase : vendor.taskCases) {
                if (mSelectedTaskCase == null) {
                    mSelectedTaskCase = taskCase;
                }
                cases.add(taskCase);
            }
        }
        if (mSelectedTaskCase != null) {
            openCase();
        }
        return cases;
    }

    private ArrayList<WorkerItem> getWorkerItems() {
        ArrayList<WorkerItem> workers = new ArrayList<WorkerItem>();
        if (mSelectedTaskCase != null) {
            for (TaskItem item : mSelectedTaskCase.taskItems) {
                workers.add(mWorkingData.getWorkerItemById(item.workerId));
            }
        }
        return workers;
    }

    private ArrayList<TaskItem> getTaskItems() {
        if (mSelectedTaskCase != null) {
            return (ArrayList<TaskItem>) mSelectedTaskCase.taskItems;
        }
        return new ArrayList<TaskItem>();
    }

    private class SpinnerAdapter extends ArrayAdapter<Vendor> {
        private LayoutInflater mInflater;

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<Vendor> objects) {
            super(context, textViewResourceId, objects);
            mInflater = getActivity().getLayoutInflater();
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpinnerViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.case_spinner_view, null);
                holder = new SpinnerViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (SpinnerViewHolder) convertView.getTag();
            }
            holder.tvSpinnerVendor.setText(getItem(position).name);
            return convertView;
        }

        @Override
        public Vendor getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            SpinnerDropdownViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.case_spinner_dropdown_view, null);
                holder = new SpinnerDropdownViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (SpinnerDropdownViewHolder) convertView.getTag();
            }
            holder.tvDropdownSpinnerVendorName.setText(getItem(position).name);
            return convertView;
        }

        private class SpinnerDropdownViewHolder {
            private TextView tvDropdownSpinnerVendorName;

            public SpinnerDropdownViewHolder(View v) {
                this.tvDropdownSpinnerVendorName = (TextView) v.findViewById(R.id.case_spinner_dropdown_view_tv_vendor_name);
            }
        }

        private class SpinnerViewHolder {
            private TextView tvSpinnerVendor;

            public SpinnerViewHolder(View v) {
                this.tvSpinnerVendor = (TextView) v.findViewById(R.id.case_spinner_view_tv_vendor_name);
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
                convertView = mInflater.inflate(R.layout.case_workers_listview_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            WorkerItem worker = getItem(position);
            holder.avator.setImageDrawable(worker.avatar);
            return convertView;
        }

        private class ViewHolder {
            ImageView avator;

            public ViewHolder(View view) {
                avator = (ImageView) view.findViewById(R.id.case_workers_listview_item_iv);
            }
        }
    }

    private class TaskItemListViewAdapter extends ArrayAdapter<TaskItem> {
        private LayoutInflater mInflater;

        public TaskItemListViewAdapter(Context context, ArrayList<TaskItem> items) {
            super(context, 0, items);
            mInflater = getActivity().getLayoutInflater();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskItemListViewAdapterViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.case_taskitem_listview_view, parent, false);
                holder = new TaskItemListViewAdapterViewHolder(convertView);
                convertView.setTag(holder);
                for (View view : holder.dividerViews) {
                    view.setVisibility(View.VISIBLE);
                }
                final ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.height = (int) getResources().getDimension(R.dimen.case_overview_taskitem_listview_item_height);
            } else {
                holder = (TaskItemListViewAdapterViewHolder) convertView.getTag();
            }
            TaskItem taskItem = getItem(position);
            holder.tvId.setText(String.valueOf(position + 1));

            int progress = taskItem.getProgress();
            holder.tvStatus.setText(Utils.getTaskItemProgressString(getActivity(), progress));
            if (progress == TaskItem.Progress.FINISH) {
                holder.tvStatus.setBackground(getResources().getDrawable(R.drawable.border_textview_bg_gray));
                holder.tvStatus.setTextColor(getResources().getColor(R.color.taskitem_status_finish));
                holder.tvName.setTextColor(getResources().getColor(R.color.taskitem_status_finish));
                holder.tvExpectedTime.setTextColor(getResources().getColor(R.color.taskitem_status_finish));
                holder.tvWorkTime.setTextColor(getResources().getColor(R.color.taskitem_status_finish));
                holder.tvTool.setTextColor(getResources().getColor(R.color.taskitem_status_finish));
            } else if (progress == TaskItem.Progress.WORKING) {
                holder.tvStatus.setBackground(getResources().getDrawable(R.drawable.border_textview_bg_green));
                holder.tvStatus.setTextColor(getResources().getColor(R.color.taskitem_status_working));
            } else {
                holder.tvStatus.setBackground(null);
                holder.tvStatus.setTextColor(getResources().getColor(R.color.taskitem_status_others));
            }

            holder.tvName.setText(taskItem.title);
            holder.tvExpectedTime.setText(taskItem.getExpectedFinishTime());
            holder.tvWorkTime.setText(taskItem.getWorkingTime());
            if (taskItem.toolId > 0) {
                holder.tvTool.setText(mWorkingData.getToolById(taskItem.toolId).name);
            } else {
                holder.tvTool.setText("");
            }
            holder.tvWarning.setText(taskItem.getWorningText());
            if (taskItem.workerId > 0) {
                WorkerItem worker = mWorkingData.getWorkerItemById(taskItem.workerId);
                holder.tvWorkerName.setText(worker.name);
                if (worker.avatar != null) {
                    holder.ivWorkerAvator.setVisibility(View.VISIBLE);
                } else {
                    holder.ivWorkerAvator.setVisibility(View.GONE);
                }
                holder.ivWorkerAvator.setImageDrawable(worker.avatar);
            } else {
                holder.tvWorkerName.setText("");
                holder.ivWorkerAvator.setImageDrawable(null);
            }
            if (position % 2 == 0) {
                convertView.setBackgroundColor(getResources().getColor(R.color.listview_taskitem_row_odd));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.listview_taskitem_row_even));
            }
            return convertView;
        }
    }

    public class TaskItemListViewAdapterViewHolder {
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
        ArrayList<View> dividerViews = new ArrayList<View>();

        public TaskItemListViewAdapterViewHolder(View view) {
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

    private class TaskCaseListViewAdapter extends ArrayAdapter<TaskCase> implements Filterable {
        private LayoutInflater mInflater;
        private ArrayList<TaskCase> mOrigCases;
        private ArrayList<TaskCase> mFilteredCases;
        private CustomFilter mFilter;
        private int mPositionSelected;

        public TaskCaseListViewAdapter(Context context, ArrayList<TaskCase> cases) {
            super(context, 0, cases);
            mInflater = getActivity().getLayoutInflater();
            mOrigCases = cases;
            mFilteredCases = new ArrayList<TaskCase>();
            mFilteredCases.addAll(cases);
            mFilter = new CustomFilter();
        }

        @Override
        public TaskCase getItem(int position) {
            return mFilteredCases.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mFilteredCases.get(position).id;
        }

        @Override
        public int getCount() {
            return mFilteredCases.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.case_taskcase_listview_view, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTvCaseName.setText(getItem(position).name);
            holder.mTvVendor.setText(mWorkingData.getVendorById(getItem(position).vendorId).name);
            int finishPercentage = getItem(position).getFinishPercent();
            if (finishPercentage == 100) {
                holder.mTvStatus.setText(getResources().getString(R.string.case_finished));
                holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.case_border_textview_bg_gray));
                holder.mTvCaseName.setTextColor(getResources().getColor(R.color.listview_taskcase_taskname_textcolor_finished));
            } else {
                holder.mTvStatus.setText(String.valueOf(finishPercentage) + "%");
                holder.mTvCaseName.setTextColor(getResources().getColor(R.color.listview_taskcase_taskname_textcolor_unfinished));
                if (finishPercentage <= 33) {
                    holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.case_border_textview_bg_green));
                } else if (finishPercentage <= 66) {
                    holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.case_border_textview_bg_orange));
                } else {
                    holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.case_border_textview_bg_red));
                }
            }
            if (position == mPositionSelected) {
                holder.mRoot.setBackgroundColor(getResources().getColor(R.color.listview_taskcase_selected_bg));
                holder.mTvCaseName.setTextColor(Color.WHITE);
                holder.mTvVendor.setTextColor(Color.WHITE);
            } else {
                holder.mRoot.setBackgroundColor(Color.TRANSPARENT);
                holder.mTvVendor.setTextColor(getResources().getColor(R.color.case_overview_taskcase_listview_vendor_textcolor));
            }
            final ViewGroup.LayoutParams params = convertView.getLayoutParams();
            params.height = (int) getResources().getDimension(R.dimen.case_overview_taskcase_listview_item_height);
            return convertView;
        }

        private class ViewHolder {
            RelativeLayout mRoot;
            TextView mTvStatus;
            TextView mTvVendor;
            TextView mTvCaseName;

            public ViewHolder(View view) {
                mRoot = (RelativeLayout) view.findViewById(R.id.case_listview_root);
                mTvStatus = (TextView) view.findViewById(R.id.case_listview_view_tv_status);
                mTvVendor = (TextView) view.findViewById(R.id.case_listview_view_tv_vendor_name);
                mTvCaseName = (TextView) view.findViewById(R.id.case_listview_view_tv_case_name);
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
                ArrayList<TaskCase> filterResult = new ArrayList<TaskCase>();
                for (TaskCase taskCase: mOrigCases) {
                    if ((TextUtils.isEmpty(constraint) || taskCase.name.toLowerCase().contains(constraint))
                            && (mSpinner.getSelectedItemId() == -1 || taskCase.vendorId == mSpinner.getSelectedItemId())) {
                        filterResult.add(taskCase);
                    }
                }
                result.values = filterResult;
                result.count = filterResult.size();
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredCases.clear();
                mFilteredCases.addAll((ArrayList<TaskCase>) results.values);
                notifyDataSetChanged();
            }
        }

        public void setPositionSelected(int position) {
            mPositionSelected = position;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        mTaskCaseListviewAdapter.getFilter().filter(s.toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mSpinner.getId()) {
            mTaskCaseListviewAdapter.getFilter().filter(mEtCaseSearch.getText().toString());
        }
    }

    private void openCase() {
        mTvCaseNameSelected.setText(mSelectedTaskCase.name);
        mTvCaseVendorSelected.setText(mWorkingData.getVendorById(mSelectedTaskCase.vendorId).name);
        if (mSelectedTaskCase.workerId > 0) {
            mTvCasePersonInChargeSelected.setText(mWorkingData.getWorkerItemById(mSelectedTaskCase.workerId).name);
        }
        mTvCaseHoursPassedBy.setText(mSelectedTaskCase.getHoursPassedBy());
        mTvCaseHoursUnfinished.setText(mSelectedTaskCase.getHoursUnFinished());
        mTvCaseHoursForecast.setText(mSelectedTaskCase.getHoursForecast());
        mPbCaseSelected.setProgress(mSelectedTaskCase.getFinishPercent());
        updateTaskItemListView();
        updateWorkerItemListView();
        updateStatisticsView();
    }

    private void updateStatisticsView() {
        mStatisticsViewGroup.removeAllViews();
        mStatisticsViewGroup.addView(getBarChart(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private View getBarChart() {
        final String[] axis_x_string = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        final String[][] xy = new String[7][2];
        for (int i = 0; i < axis_x_string.length; i++) {
            xy[i][0] = axis_x_string[i];
            xy[i][1] = String.valueOf(((int) (Math.random() * 24 + 1)));
        }
        View view = null;
        final XYSeries series = new XYSeries("");
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);
        final XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer yRenderer = new XYSeriesRenderer();
        renderer.addSeriesRenderer(yRenderer);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setTextTypeface(null, Typeface.NORMAL);

        renderer.setShowGrid(true);
        renderer.setGridColor(getResources().getColor(R.color.case_overview_statistics_grid_color));

        renderer.setLabelsColor(Color.BLACK);
        renderer.setAxesColor(getResources().getColor(R.color.case_overview_statistics_axis_color));
        renderer.setBarSpacing(0.5);

        renderer.setXTitle("");
        renderer.setYTitle("");
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setXLabelsPadding(getResources().getDimension(R.dimen.case_overview_statistics_x_axis_padding));
        renderer.setYLabelsPadding(getResources().getDimension(R.dimen.case_overview_statistics_x_axis_padding));
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.CENTER);
        renderer.setXLabelsAngle(0);

        renderer.setXLabels(0);
        renderer.setYAxisMin(0);
        yRenderer.setColor(getResources().getColor(R.color.case_overview_statistics_bar_color));

        series.add(0, 0);
        renderer.addXTextLabel(0, "");
        for (int r = 0; r < xy.length; r++) {
            renderer.addXTextLabel(r + 1, xy[r][0]);
            series.add(r + 1, Integer.parseInt(xy[r][1]));
        }
        series.add(xy.length + 1, 0);
        renderer.addXTextLabel(xy.length + 1, "");
        renderer.setZoomEnabled(false);
        renderer.setZoomEnabled(false, false);
        renderer.setClickEnabled(true);
        renderer.setPanEnabled(false);
        renderer.setShowLegend(false);
        view = ChartFactory.getBarChartView(getActivity(), dataset, renderer, BarChart.Type.DEFAULT);
        view.setOnClickListener(this);
        view.setOnTouchListener(this);
        return view;
    }

    private void updateTaskItemListView() {
        mTaskItemListViewAdapter = new TaskItemListViewAdapter(getActivity(), getTaskItems());
        mTaskItemListView.setAdapter(mTaskItemListViewAdapter);
        if (getTaskItems().size() > 0) {
            ViewGroup.LayoutParams params = mTaskItemListView.getLayoutParams();
            params.height = (int) (getTaskItems().size() * getResources().getDimension(R.dimen.case_overview_taskitem_listview_item_height));
        }
    }

    private void updateWorkerItemListView() {
        mWorkerItemListViewAdapter = new WorkerItemListViewAdapter(getActivity(), getWorkerItems());
        mWorkerListView.setAdapter(mWorkerItemListViewAdapter);
    }

    private void updateTaskItemHeaderView() {
        View view = getActivity().findViewById(R.id.case_listview_task_item_layout);
        if (view != null) {
            TaskItemListViewAdapterViewHolder holder = new TaskItemListViewAdapterViewHolder(view);
            float textSize = getResources().getDimension(R.dimen.case_overview_taskitem_listview_header_text_size);
            holder.tvStatus.setTextSize(textSize);
            holder.tvTool.setTextSize(textSize);
            holder.tvWorkTime.setTextSize(textSize);
            holder.tvExpectedTime.setTextSize(textSize);
            holder.tvId.setTextSize(textSize);
            holder.tvWarning.setTextSize(textSize);
            holder.tvName.setTextSize(textSize);
            holder.tvWorkerName.setVisibility(View.GONE);
            holder.ivWorkerAvator.setVisibility(View.GONE);
            holder.tvWorkerNameString.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mTaskCaseListView.getId()) {
            mSelectedTaskCase = mTaskCaseListviewAdapter.getItem(position);
            openCase();
            mTaskCaseListviewAdapter.setPositionSelected(position);
            mTaskCaseListviewAdapter.notifyDataSetChanged();
        }
    }

    private void editTaskCase() {
        // TODO
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.case_btn_edit_case:
                editTaskCase();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
