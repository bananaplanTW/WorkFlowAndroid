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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.overview.TaskItemFragment;
import com.bananaplan.workflowandroid.overview.VendorSpinnerAdapter;
import com.bananaplan.workflowandroid.utility.OverviewScrollView;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.utility.TabManager;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ben Lai
 * @since 2015/7/16.
 */
public class CaseOverviewFragment extends Fragment implements TextWatcher,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener,
        View.OnClickListener, DataObserver {

    public static class TAB_TAG {
        private static final String TASK_ITEMS = "tab_tag_task_items";
        private static final String WARNING = "tab_tag_warning";
    }

    private Spinner mVendorSpinner;
    private VendorSpinnerAdapter mVendorSpinnerAdapter;
    private List<Vendor> mVendorSpinnerDataSet = new ArrayList<>();

    private ListView mCaseListView;
    private CaseListViewOverviewListAdapterBase mCaseListViewAdapter;
    private Case mSelectedCase;
    private List<Case> mCaseListDataSet = new ArrayList<>();

    private EditText mEtCaseSearch;
    private TextView mTvCaseNameSelected;
    private TextView mTvCaseVendorSelected;
    private TextView mTvCaseManager;
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


    private class CaseListViewOverviewListAdapterBase extends CaseOverviewListAdapterBase {

        private class ViewHolder {

            RelativeLayout mRoot;
            TextView mTvStatus;
            TextView mTvVendor;
            TextView mTvCaseName;

            public ViewHolder(View view) {
                mRoot = (RelativeLayout) view.findViewById(R.id.case_listview_itemview_root);
                mTvStatus = (TextView) view.findViewById(R.id.case_listview_itemview_tv_progress);
                mTvVendor = (TextView) view.findViewById(R.id.case_listview_itemview_tv_vendor_name);
                mTvCaseName = (TextView) view.findViewById(R.id.case_listview_itemview_tv_case_name);
            }
        }

        public CaseListViewOverviewListAdapterBase(Context context, List<Case> cases) {
            super(context, cases);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.case_ov_taskcase_listview_itemview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Case aCase = getItem(position);
            convertView.findViewById(R.id.header_divider)
                    .setVisibility(position == 0 ? View.VISIBLE : View.GONE);

            int highlightBgColor;
            int caseNameColor;
            int vendorColor;
            String statusText;
            int statusBgId;

            if (aCase.getFinishedPercent() == 100) {
                statusText = getResources().getString(R.string.case_finished);
                statusBgId = R.drawable.bg_solid_textview_bg_gray;
                caseNameColor = getResources().getColor(R.color.gray1);
            } else {
                statusText = aCase.getFinishItemsCount() + "/" + aCase.tasks.size();
                statusBgId = R.drawable.case_overview_case_list_progress_background;
                caseNameColor = getResources().getColor(R.color.black1);
            }

            // highlight the chosen item
            if (position == mPositionSelected) {
                highlightBgColor = getResources().getColor(R.color.blue1);
                vendorColor = Color.WHITE;
                caseNameColor = Color.WHITE;
            } else {
                highlightBgColor = Color.TRANSPARENT;
                vendorColor = getResources().getColor(R.color.gray1);
            }

            holder.mRoot.setBackgroundColor(highlightBgColor);
            holder.mTvVendor.setTextColor(vendorColor);
            holder.mTvCaseName.setText(aCase.name);
            holder.mTvVendor.setText(WorkingData.getInstance(getActivity()).getVendorById(aCase.vendorId).name);
            holder.mTvStatus.setText(statusText);
            holder.mTvStatus.setBackground(getResources().getDrawable(statusBgId, null));
            holder.mTvCaseName.setTextColor(caseNameColor);

            return convertView;
        }

        @Override
        public Vendor getSelectedVendor() {
            return (Vendor) mVendorSpinner.getSelectedItem();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case_ov, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        onCaseSelected(mSelectedCase, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        WorkingData.getInstance(getActivity()).registerDataObserver(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        WorkingData.getInstance(getActivity()).removeDataObserver(this);
    }

    private void initialize() {
        findViews();
        setupViews();
        setupTabs();
        setupVendorSpinner();
        setupCaseList();
    }

    private void findViews() {
        mVendorSpinner = (Spinner) getView().findViewById(R.id.ov_leftpane_spinner);
        mEtCaseSearch = (EditText) getView().findViewById(R.id.ov_leftpane_search_edittext);
        mCaseListView = (ListView) getView().findViewById(R.id.ov_leftpane_listview);

        // right pane
        mTvCaseNameSelected = (TextView) getView().findViewById(R.id.case_ov_right_pane_case_name);
        mTvCaseVendorSelected = (TextView) getView().findViewById(R.id.case_ov_right_pane_vendor_name);
        mTvCaseManager = (TextView) getView().findViewById(R.id.case_ov_right_pane_manager_name);
        mPbCaseSelected = (ProgressBar) getView().findViewById(R.id.case_ov_right_pane_case_progress_bar);
        mTvCaseHoursPassedBy = (TextView) getView().findViewById(R.id.case_tv_hours_passed_by);
        mTvCaseHoursUnfinished = (TextView) getView().findViewById(R.id.case_tv_hours_unfinished);
        mTvCaseHoursExpected = (TextView) getView().findViewById(R.id.case_tv_hours_expected);
        mTvTaskItemCount = (TextView) getView().findViewById(R.id.case_tv_task_item_count);
        mTvCaseDeliveryDate = (TextView) getView().findViewById(R.id.case_ov_right_pane_delivery_date);
        mTvCaseFeedDate = (TextView) getView().findViewById(R.id.case_ov_right_pane_feed_date);
        mTvCaseFigureDate = (TextView) getView().findViewById(R.id.case_ov_right_pane_figure_date);
        mTvCaseSize = (TextView) getView().findViewById(R.id.case_ov_right_pane_size);
        mTvCaseSheetCount = (TextView) getView().findViewById(R.id.case_ov_right_pane_sheet_count);
        mTvCaseModelCount = (TextView) getView().findViewById(R.id.case_ov_right_pane_model_count);
        mTvCaseOthers = (TextView) getView().findViewById(R.id.case_ov_right_pane_others);
        mTvCaseProgress = (TextView) getView().findViewById(R.id.case_ov_right_pane_case_progress);
        mTabHost = (TabHost) getView().findViewById(R.id.case_ov_right_pane_tab_host);
    }

    private void setupViews() {
        mEtCaseSearch.addTextChangedListener(this);
        getView().findViewById(R.id.case_ov_right_pane_edit_case).setOnClickListener(this);
    }

    private void setupTabs() {
        mTabHost.setup();
        mTabMgr = new TabManager((MainActivity) getActivity(), mTabHost, android.R.id.tabcontent);

        Bundle bundle = new Bundle();
        bundle.putString(TaskItemFragment.FROM, getClass().getSimpleName());
        TabHost.TabSpec taskItemsTabSpec = mTabHost.newTabSpec(TAB_TAG.TASK_ITEMS)
                .setIndicator(getTabTitleView(TAB_TAG.TASK_ITEMS));
        mTabMgr.addTab(taskItemsTabSpec, TaskItemFragment.class, bundle);

        TabHost.TabSpec warningTabSpec = mTabHost.newTabSpec(TAB_TAG.WARNING)
                .setIndicator(getTabTitleView(TAB_TAG.WARNING));
        mTabMgr.addTab(warningTabSpec, CaseOverviewWarningFragment.class, null);
    }

    private void setupVendorSpinner() {
        setSpinnerVendorData();
        mVendorSpinnerAdapter = new VendorSpinnerAdapter(getActivity(), mVendorSpinnerDataSet,
                new IconSpinnerAdapter.OnItemSelectedCallback() {
                    @Override
                    public int getSelectedPos() {
                        if (mVendorSpinner != null) {
                            return mVendorSpinner.getSelectedItemPosition();
                        }
                        return 0;
                    }
                });
        mVendorSpinner.setAdapter(mVendorSpinnerAdapter);
        mVendorSpinner.setOnItemSelectedListener(this);
    }

    private void setSpinnerVendorData() {
        mVendorSpinnerDataSet.clear();
        mVendorSpinnerDataSet.add(new Vendor("", getResources().getString(R.string.case_spinner_all_vendors))); // all vendors
        mVendorSpinnerDataSet.addAll(WorkingData.getInstance(getActivity()).getVendors());
    }

    private void setupCaseList() {
        setCaseListData(null);
        mCaseListViewAdapter = new CaseListViewOverviewListAdapterBase(getActivity(), mCaseListDataSet);
        mCaseListView.setAdapter(mCaseListViewAdapter);
        mCaseListView.setOnItemClickListener(this);
    }

    private void setCaseListData(String vendorId) {
        mCaseListDataSet.clear();
        if (TextUtils.isEmpty(vendorId)) {
            for (Vendor vendor : WorkingData.getInstance(getActivity()).getVendors()) {
                for (Case aCase : vendor.getCases()) {
                    if (mSelectedCase == null) {
                        mSelectedCase = aCase;
                    }
                    mCaseListDataSet.add(aCase);
                }
            }

        } else {
            for (Case aCase : WorkingData.getInstance(getActivity()).getVendorById(vendorId).getCases()) {
                if (mSelectedCase == null) {
                    mSelectedCase = aCase;
                }
                mCaseListDataSet.add(aCase);
            }
        }
    }

    private View getTabTitleView(final String tag) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab, null);
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
        ((TextView) view.findViewById(R.id.tab_title)).setText(text);
        return view;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterCaseList(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.ov_leftpane_spinner:
                setCaseListData(mVendorSpinnerDataSet.get(position).id);
                mCaseListViewAdapter.updateDataSet(mCaseListDataSet);

                mEtCaseSearch.setText("");
                mCaseListView.setSelection(0);
                mCaseListViewAdapter.setPositionSelected(0);
                mCaseListViewAdapter.notifyDataSetChanged();

                selectCase(mCaseListViewAdapter.getItem(0), true);

                break;
        }
    }

    private void selectCase(Case selectedCase, boolean isScrollToTop) {
        mSelectedCase = selectedCase;
        onCaseSelected(selectedCase, isScrollToTop);
    }

    private void filterCaseList(String query) {
        if (mCaseListViewAdapter == null || mCaseListViewAdapter.getFilter() == null) return;
        mCaseListViewAdapter.getFilter().filter(query);
    }

    private void onCaseSelected(Case aCase, boolean isScrollToTop) {
        if (aCase == null) return;

        if (isScrollToTop) {
            ((OverviewScrollView) getView().findViewById(R.id.scroll)).setScrollEnable(false);
        } else {
            ((OverviewScrollView) getView().findViewById(R.id.scroll)).setScrollEnable(true);
        }

        mTvCaseNameSelected.setText(aCase.name);
        mTvCaseVendorSelected.setText(WorkingData.getInstance(getActivity()).getVendorById(aCase.vendorId).name);
        if (!TextUtils.isEmpty(mSelectedCase.managerId)) {
            mTvCaseManager.setText(WorkingData.getInstance(getActivity()).getManagerById(aCase.managerId).name);
        }
        mTvCaseHoursPassedBy.setText(Utils.millisecondsToTimeString(aCase.getSpentTime()));
        mTvCaseHoursUnfinished.setText(Utils.millisecondsToTimeString(aCase.getUnfinishedTime()));
        mTvCaseHoursExpected.setText(aCase.getHoursExpected());
        mPbCaseSelected.setProgress(aCase.getFinishedPercent());
        mTvTaskItemCount.setText(String.valueOf(aCase.tasks.size()));
        mTvCaseProgress.setText(aCase.getFinishItemsCount() + "/" + aCase.tasks.size());
        mTvCaseFeedDate.setText(Utils.timestamp2Date(aCase.materialPurchasedDate, Utils.DATE_FORMAT_YMD));
        mTvCaseFigureDate.setText(Utils.timestamp2Date(aCase.layoutDeliveredDate, Utils.DATE_FORMAT_YMD));
        mTvCaseDeliveryDate.setText(Utils.timestamp2Date(aCase.deliveredDate, Utils.DATE_FORMAT_YMD));
        mTvCaseSheetCount.setText(String.valueOf(aCase.plateCount));
        mTvCaseModelCount.setText(String.valueOf(aCase.supportBlockCount));
        mTvCaseOthers.setText(aCase.description);
        mTvCaseSize.setText(aCase.getSize());

        if (mTabMgr != null) {
            mTabMgr.selectItem(aCase);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.ov_leftpane_listview:
                if (mSelectedCase == mCaseListViewAdapter.getItem(position)) return;

                selectCase(mCaseListViewAdapter.getItem(position), true);

                mCaseListViewAdapter.setPositionSelected(position);
                mCaseListViewAdapter.notifyDataSetChanged();

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.case_ov_right_pane_edit_case:
                Toast.makeText(getActivity(), "Edit case = " + getSelectedCase().name, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public Case getSelectedCase() {
        return mSelectedCase;
    }

    @Override
    public void updateData() {
        setSpinnerVendorData();
        mVendorSpinnerAdapter.notifyDataSetChanged();

        setCaseListData(mVendorSpinnerDataSet.get(mVendorSpinner.getSelectedItemPosition()).id);
        mCaseListViewAdapter.updateDataSet(mCaseListDataSet);

        selectCase(mCaseListDataSet.get(mCaseListViewAdapter.getPositionSelected()), false);
    }
}
