package com.bananaplan.workflowandroid.overview.caseoverview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.TaskCase;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.overview.TaskItemFragment;
import com.bananaplan.workflowandroid.utility.OverviewScrollView;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.utility.TabManager;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;

/**
 * @author Ben Lai
 * @since 2015/7/16.
 */
public class CaseOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    public static class TAB_TAG {
        private static final String TASK_ITEMS   = "tab_tag_task_items";
        private static final String WARNING      = "tab_tag_warning";
    }

    // views
    private Spinner mVendorsSpinner;
    private EditText mEtCaseSearch;
    private ListView mTaskCaseListView;
    private TextView mTvCaseNameSelected;
    private TextView mTvCaseVendorSelected;
    private TextView mTvCaseWorkerInChargeSelected;
    private ProgressBar mPbCaseSelected;
    private TextView mTvCaseHoursPassedBy;
    private TextView mTvCaseHoursUnfinished;
    private TextView mTvCaseHoursExpected;
    private TextView mTvTaskItemCount;
    private TextView mTvCaseDeliveryDate;
    private TextView mTvCaseFeedDate;
    private TextView mTvCaseFigureDate;
    private TextView mTvCaseSize;
    private TextView mTvCaseSheetCount;
    private TextView mTvCaseModelCount;
    private TextView mTvCaseOthers;
    private TextView mTvCaseProgress;
    private TabHost mTabHost;
    private TabManager mTabMgr;

    // data
    private VendorSpinnerAdapter mVendorSpinnerAdapter;
    private TaskCaseListViewAdapter mTaskCaseListViewAdapter;
    private TaskCase mSelectedTaskCase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case_ov, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVendorsSpinner = (Spinner) getActivity().findViewById(R.id.ov_leftpane_spinner);
        mEtCaseSearch = (EditText) getActivity().findViewById(R.id.ov_leftpane_search_edittext);
        mTaskCaseListView = (ListView) getActivity().findViewById(R.id.ov_leftpane_listview);

        mEtCaseSearch.addTextChangedListener(this);
        mVendorSpinnerAdapter = new VendorSpinnerAdapter(getActivity(), getSpinnerVendorData());
        mVendorsSpinner.setAdapter(mVendorSpinnerAdapter);
        mVendorsSpinner.setOnItemSelectedListener(this);

        // right pane
        mTvCaseNameSelected = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_case_name);
        mTvCaseVendorSelected = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_vendor_name);
        mTvCaseWorkerInChargeSelected = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_worker_name);
        mPbCaseSelected = (ProgressBar) getActivity().findViewById(R.id.case_ov_right_pane_case_progress_bar);
        mTvCaseHoursPassedBy = (TextView) getActivity().findViewById(R.id.case_tv_hours_passed_by);
        mTvCaseHoursUnfinished = (TextView) getActivity().findViewById(R.id.case_tv_hours_unfinished);
        mTvCaseHoursExpected = (TextView) getActivity().findViewById(R.id.case_tv_hours_forecast);
        mTvTaskItemCount = (TextView) getActivity().findViewById(R.id.case_tv_task_item_count);
        mTvCaseDeliveryDate = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_delivery_date);
        mTvCaseFeedDate = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_feed_date);
        mTvCaseFigureDate = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_figure_date);
        mTvCaseSize = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_size);
        mTvCaseSheetCount = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_sheet_count);
        mTvCaseModelCount = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_model_count);
        mTvCaseOthers = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_others);
        mTvCaseProgress = (TextView) getActivity().findViewById(R.id.case_ov_right_pane_case_progress);
        mTabHost = (TabHost) getActivity().findViewById(R.id.case_ov_right_pane_tab_host);
        mTabHost.setup();
        mTabMgr = new TabManager((MainActivity) getActivity(), this, mTabHost, android.R.id.tabcontent);
        setupTabs();

        mTaskCaseListViewAdapter = new TaskCaseListViewAdapter(getActivity(), getTaskCases());
        mTaskCaseListView.setAdapter(mTaskCaseListViewAdapter);
        mTaskCaseListView.setOnItemClickListener(this);

        onTaskCaseSelected(mSelectedTaskCase);
    }

    private void setupTabs() {
        Bundle bundle = new Bundle();
        bundle.putString(TaskItemFragment.FROM, getClass().getSimpleName());
        TabHost.TabSpec taskItemsTabSpec = mTabHost.newTabSpec(TAB_TAG.TASK_ITEMS)
                .setIndicator(getTabTitleView(TAB_TAG.TASK_ITEMS));
        mTabMgr.addTab(taskItemsTabSpec, TaskItemFragment.class, bundle);

        TabHost.TabSpec warningTabSpec = mTabHost.newTabSpec(TAB_TAG.WARNING)
                .setIndicator(getTabTitleView(TAB_TAG.WARNING));
        mTabMgr.addTab(warningTabSpec, CaseWarningFragment.class, null);
    }

    private View getTabTitleView(final String tag) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.ov_tab, null);
        int titleResId;
        switch (tag) {
            case TAB_TAG.TASK_ITEMS:
                titleResId = R.string.case_ov_tab_title_task_items;
                break;
            case TAB_TAG.WARNING:
                titleResId = R.string.case_ov_tab_title_warning;
                break;
            default:
                titleResId = -1;
                break;
        }
        String text = titleResId != -1 ? getResources().getString(titleResId) : "";
        ((TextView) view.findViewById(R.id.ov_tab_title)).setText(text);
        return view;
    }

    private ArrayList<Vendor> getSpinnerVendorData() {
        ArrayList<Vendor> tmp = new ArrayList<>();
        tmp.add(new Vendor(-1L, getResources().getString(R.string.case_spinner_all_vendors))); // all vendors
        tmp.addAll(WorkingData.getInstance(getActivity()).getVendors());
        return tmp;
    }

    private ArrayList<TaskCase> getTaskCases() {
        ArrayList<TaskCase> cases = new ArrayList<>();
        for (Vendor vendor : WorkingData.getInstance(getActivity()).getVendors()) {
            for (TaskCase taskCase : vendor.taskCases) {
                if (mSelectedTaskCase == null) {
                    mSelectedTaskCase = taskCase;
                }
                cases.add(taskCase);
            }
        }
        return cases;
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
        mTaskCaseListViewAdapter.getFilter().filter(s.toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mVendorsSpinner.getId()) {
            mTaskCaseListViewAdapter.getFilter().filter(mEtCaseSearch.getText().toString());
        }
    }

    private void onTaskCaseSelected(TaskCase taskCase) {
        if (taskCase == null) return;
        mTvCaseNameSelected.setText(taskCase.name);
        mTvCaseVendorSelected.setText(WorkingData.getInstance(getActivity()).getVendorById(taskCase.vendorId).name);
        if (mSelectedTaskCase.workerId > 0) {
            mTvCaseWorkerInChargeSelected.setText(WorkingData.getInstance(getActivity()).getWorkerItemById(taskCase.workerId).name);
        }
        mTvCaseHoursPassedBy.setText(taskCase.getHoursPassedBy());
        mTvCaseHoursUnfinished.setText(taskCase.getHoursUnFinished());
        mTvCaseHoursExpected.setText(taskCase.getHoursForecast());
        mPbCaseSelected.setProgress(taskCase.getFinishPercent());
        mTvTaskItemCount.setText(String.valueOf(taskCase.taskItems.size()));
        mTvCaseProgress.setText(taskCase.getFinishItemsCount() + "/" + taskCase.taskItems.size());
        mTvCaseFeedDate.setText(Utils.timestamp2Date(taskCase.materialPurchasedDate, Utils.DATE_FORMAT_YMD));
        mTvCaseFigureDate.setText(Utils.timestamp2Date(taskCase.layoutDeliveredDate, Utils.DATE_FORMAT_YMD));
        mTvCaseDeliveryDate.setText(Utils.timestamp2Date(taskCase.deliveredDate, Utils.DATE_FORMAT_YMD));
        mTvCaseSheetCount.setText(String.valueOf(taskCase.sheetCount));
        mTvCaseModelCount.setText(String.valueOf(taskCase.modelCount));
        mTvCaseOthers.setText(taskCase.others);
        mTvCaseSize.setText(taskCase.getSize());
        if (mTabMgr != null) {
            mTabMgr.selectItem(taskCase);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mTaskCaseListView.getId()) {
            ((OverviewScrollView) getActivity().findViewById(R.id.scroll)).setScrollEnable(false);
            if (mSelectedTaskCase == mTaskCaseListViewAdapter.getItem(position)) return;
            mSelectedTaskCase = mTaskCaseListViewAdapter.getItem(position);
            onTaskCaseSelected(mSelectedTaskCase);
            mTaskCaseListViewAdapter.setPositionSelected(position);
            mTaskCaseListViewAdapter.notifyDataSetChanged();
        }
    }

    private class VendorSpinnerAdapter extends IconSpinnerAdapter<Vendor> {

        public VendorSpinnerAdapter(Context context, ArrayList<Vendor> objects) {
            super(context, 0, objects);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id;
        }

        @Override
        public Vendor getItem(int position) {
            return (Vendor) super.getItem(position);
        }

        @Override
        public String getSpinnerViewDisplayString(int position) {
            return getItem(position).name;
        }

        @Override
        public int getSpinnerIconResourceId() {
            return R.drawable.ic_work_black;
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return getItem(position).name;
        }

        @Override
        public boolean isDropdownSelectedIconVisible(int position) {
            return mVendorsSpinner.getSelectedItemId() == getItem(position).id;
        }
    }

    private class TaskCaseListViewAdapter extends ArrayAdapter<TaskCase> implements Filterable {
        private ArrayList<TaskCase> mOrigCases;
        private ArrayList<TaskCase> mFilteredCases;
        private CustomFilter mFilter;
        private int mPositionSelected;

        public TaskCaseListViewAdapter(Context context, ArrayList<TaskCase> cases) {
            super(context, 0, cases);
            mOrigCases = cases;
            mFilteredCases = new ArrayList<>(cases);
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.case_ov_taskcase_listview_itemview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TaskCase taskCase = getItem(position);
            holder.mTvCaseName.setText(taskCase.name);
            holder.mTvVendor.setText(WorkingData.getInstance(getActivity()).getVendorById(taskCase.vendorId).name);
            if (taskCase.getFinishPercent() == 100) {
                holder.mTvStatus.setText(getResources().getString(R.string.case_finished));
                holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.bg_solid_textview_bg_gray, null));
                holder.mTvCaseName.setTextColor(getResources().getColor(R.color.gray1));
            } else {
                holder.mTvStatus.setText(taskCase.getFinishItemsCount() + "/" + taskCase.taskItems.size());
                holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.bg_solid_textview_bg_red, null));
                holder.mTvCaseName.setTextColor(getResources().getColor(R.color.black1));
            }
            if (position == mPositionSelected) {
                holder.mRoot.setBackgroundColor(getResources().getColor(R.color.blue1));
                holder.mTvCaseName.setTextColor(Color.WHITE);
                holder.mTvVendor.setTextColor(Color.WHITE);
            } else {
                holder.mRoot.setBackgroundColor(Color.TRANSPARENT);
                holder.mTvCaseName.setTextColor(getResources().getColor(R.color.black1));
                holder.mTvVendor.setTextColor(getResources().getColor(R.color.gray1));
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        public void setPositionSelected(int position) {
            mPositionSelected = position;
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

        private class CustomFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                ArrayList<TaskCase> filterResult = new ArrayList<>();
                for (TaskCase taskCase : mOrigCases) {
                    if ((TextUtils.isEmpty(constraint) || taskCase.name.toLowerCase().contains(constraint))
                            && (mVendorsSpinner.getSelectedItemId() == -1 || taskCase.vendorId == mVendorsSpinner.getSelectedItemId())) {
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
    }

    public TaskCase getSelectedTaskCase() {
        return mSelectedTaskCase;
    }
}
