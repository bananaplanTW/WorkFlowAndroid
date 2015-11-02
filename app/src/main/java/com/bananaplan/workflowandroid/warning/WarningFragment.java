package com.bananaplan.workflowandroid.warning;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.TaskWarning;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.overview.CaseAdapter;
import com.bananaplan.workflowandroid.overview.VendorSpinnerAdapter;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class WarningFragment extends Fragment implements TextWatcher,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener, DataObserver {

    private Spinner mVendorSpinner;
    private VendorSpinnerAdapter mVendorSpinnerAdapter;
    private List<Vendor> mVendorSpinnerDataSet = new ArrayList<>();

    private ListView mCaseListView;
    private CaseListViewAdapter mCaseListViewAdapter;
    private List<Case> mCaseListDataSet = new ArrayList<>();

    private RecyclerView mWarningCards;
    private GridLayoutManager mWarningCardsLayoutManager;
    private WarningCardAdapter mWarningCardAdapter;
    private List<TaskWarning> mWarningCardsDataSet = new ArrayList<>();

    private Case mSelectedCase;
    private EditText mEtCaseSearch;


    private class CaseListViewAdapter extends CaseAdapter {

        private class ViewHolder {

            RelativeLayout root;
            TextView warningCount;
            TextView _case;
            TextView vendor;

            public ViewHolder(View view) {
                root = (RelativeLayout) view.findViewById(R.id.root);
                warningCount = (TextView) view.findViewById(R.id.warning_count);
                _case = (TextView) view.findViewById(R.id.case_name);
                vendor = (TextView) view.findViewById(R.id.vendor_name);
            }
        }


        public CaseListViewAdapter(Context context, List<Case> cases) {
            super(context, cases);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.warning_frag_case_listview_itemview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            WorkingData data = WorkingData.getInstance(getActivity());
            Case aCase = getItem(position);

            convertView.findViewById(R.id.header_divider)
                    .setVisibility(position == 0 ? View.VISIBLE : View.GONE);

            int highlightBgColor;
            int caseNameColor;
            int vendorColor;

            // highlight the chosen item
            if (position == mPositionSelected) {
                highlightBgColor = getResources().getColor(R.color.blue1);
                vendorColor = Color.WHITE;
                caseNameColor = Color.WHITE;
            } else {
                highlightBgColor = Color.TRANSPARENT;
                vendorColor = getResources().getColor(R.color.gray1);
                caseNameColor = getResources().getColor(R.color.black1);
            }

            holder.root.setBackgroundColor(highlightBgColor);
            holder.warningCount.setText(Integer.toString(aCase.getUnSolvedWarningCount()));
            holder._case.setText(aCase.name);
            holder._case.setTextColor(caseNameColor);
            holder.vendor.setText(data.getVendorById(aCase.vendorId).name);
            holder.vendor.setTextColor(vendorColor);

            return convertView;
        }

        @Override
        public Vendor getSelectedVendor() {
            return (Vendor) mVendorSpinner.getSelectedItem();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_warning, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        setupVendorSpinner();
        setupCaseList();
        setupWarningCards();
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

    private void setupViews() {
        mVendorSpinner = (Spinner) getView().findViewById(R.id.ov_leftpane_spinner);
        mEtCaseSearch = (EditText) getView().findViewById(R.id.ov_leftpane_search_edittext);
        mCaseListView = (ListView) getView().findViewById(R.id.ov_leftpane_listview);
        mWarningCards = (RecyclerView) getView().findViewById(R.id.warning_cards);

        mEtCaseSearch.addTextChangedListener(this);
        mVendorSpinner.setOnItemSelectedListener(this);
        mCaseListView.setOnItemClickListener(this);
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
    }

    private void setSpinnerVendorData() {
        mVendorSpinnerDataSet.clear();
        mVendorSpinnerDataSet.add(new Vendor("", getResources().getString(R.string.case_spinner_all_vendors))); // all vendors
        mVendorSpinnerDataSet.addAll(WorkingData.getInstance(getActivity()).getVendors());
    }

    private void setupCaseList() {
        setCaseListData(null);
        mCaseListViewAdapter = new CaseListViewAdapter(getActivity(), mCaseListDataSet);
        mCaseListView.setAdapter(mCaseListViewAdapter);
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

    private void setupWarningCards() {
        setWarningCardsData();

        int spanCount = getActivity().getResources().getInteger(R.integer.warning_frag_gridview_column_count);
        mWarningCardAdapter = new WarningCardAdapter(getActivity(), mWarningCardsDataSet);
        mWarningCardsLayoutManager =
                new GridLayoutManager(getActivity(), spanCount);

        mWarningCards.setLayoutManager(mWarningCardsLayoutManager);
        mWarningCards.addItemDecoration(new WarningCardDecoration(getActivity(), spanCount));
        mWarningCards.setAdapter(mWarningCardAdapter);
    }

    private void setWarningCardsData() {
        if (mSelectedCase == null) return;

        mWarningCardsDataSet.clear();
        for (Task task : mSelectedCase.tasks) {
            for (TaskWarning taskWarning : task.taskWarnings) {
                mWarningCardsDataSet.add(taskWarning);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterCaseList(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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

                selectCase(mCaseListViewAdapter.getItem(0));

                break;
        }
    }

    private void filterCaseList(String query) {
        if (mCaseListViewAdapter == null || mCaseListViewAdapter.getFilter() == null) return;
        mCaseListViewAdapter.getFilter().filter(query);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // to nothing
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.ov_leftpane_listview:
                if (mSelectedCase == mCaseListViewAdapter.getItem(position)) return;

                mCaseListViewAdapter.setPositionSelected(position);
                mCaseListViewAdapter.notifyDataSetChanged();

                selectCase(mCaseListViewAdapter.getItem(position));

                break;
        }
    }

    private void selectCase(Case selectedCase) {
        mSelectedCase = selectedCase;
        setWarningCardsData();
        mWarningCardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manage_warning:
                manageWarnings();
                break;
        }
    }

    private void manageWarnings() {
        Toast.makeText(getActivity(), "manage warnings", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateData() {
        setSpinnerVendorData();
        mVendorSpinnerAdapter.notifyDataSetChanged();

        setCaseListData(mVendorSpinnerDataSet.get(mVendorSpinner.getSelectedItemPosition()).id);
        mCaseListViewAdapter.updateDataSet(mCaseListDataSet);

        selectCase(mCaseListDataSet.get(mCaseListViewAdapter.getPositionSelected()));
    }
}
