package com.bananaplan.workflowandroid.warning;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Manager;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.Warning;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.overview.CaseAdapter;
import com.bananaplan.workflowandroid.overview.VendorSpinnerAdapter;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class WarningFragment extends Fragment implements TextWatcher,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private Spinner mVendorSpinner;
    private EditText mEtCaseSearch;
    private ListView mCaseListView;
    private HListView mWarningGroupList;
    private GridView mWarningCards;

    private Case mSelectedCase;
    private HashMap<String, Integer> mWarningGroups;
    private WarningCardsAdapter mWarningCardsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_warning, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        initData();
        onCaseSelected();
    }

    private void setupViews() {
        mVendorSpinner = (Spinner) getActivity().findViewById(R.id.ov_leftpane_spinner);
        mEtCaseSearch = (EditText) getActivity().findViewById(R.id.ov_leftpane_search_edittext);
        mCaseListView = (ListView) getActivity().findViewById(R.id.ov_leftpane_listview);
        mWarningGroupList = (HListView) getActivity().findViewById(R.id.warning_list);
        mWarningCards = (GridView) getActivity().findViewById(R.id.warning_cards);

        mEtCaseSearch.addTextChangedListener(this);
        mVendorSpinner.setOnItemSelectedListener(this);
        mCaseListView.setOnItemClickListener(this);
        getActivity().findViewById(R.id.manage_warning).setOnClickListener(this);
        mWarningGroupList.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> parent, View view, int position, long id) {
                WarningGroupAdapter adapter = ((WarningGroupAdapter) mWarningGroupList.getAdapter());
                adapter.selectedPos = position;
                adapter.notifyDataSetChanged();
                filterWarningCards(adapter.getItem(position).name);
            }
        });
        mWarningCards.setOnItemClickListener(this);
    }

    private void filterWarningCards(String constraint) {
        if (mWarningCardsAdapter == null) return;
        mWarningCardsAdapter.getFilter().filter(constraint);
    }

    private void initData() {
        mVendorSpinner.setAdapter(new VendorSpinnerAdapter(getActivity(), getSpinnerVendorData(),
                new IconSpinnerAdapter.OnItemSelectedCallback() {
                    @Override
                    public int getSelectedPos() {
                        if (mVendorSpinner != null) {
                            return mVendorSpinner.getSelectedItemPosition();
                        }
                        return 0;
                    }
                }));
        mCaseListView.setAdapter(new CaseListViewAdapter(getActivity(), getTaskCases()));
        initWarningGroupsMap();
    }

    private void initWarningGroupsMap() {
        mWarningGroups = new HashMap<>();
        for (Task task : WorkingData.getInstance(getActivity()).getTasks()) {
            for (Warning warning : task.warnings) {
                if (warning.status == Warning.Status.OPEN) {
                    if (!mWarningGroups.containsKey(warning.name)) {
                        mWarningGroups.put(warning.name, 0);
                    }
                }
            }
        }
    }

    private ArrayList<Case> getTaskCases() {
        ArrayList<Case> cases = new ArrayList<>();
        for (Vendor vendor : WorkingData.getInstance(getActivity()).getVendors()) {
            for (Case aCase : vendor.getCases()) {
                if (mSelectedCase == null) {
                    mSelectedCase = aCase;
                }
                cases.add(aCase);
            }
        }
        return cases;
    }

    private class CaseListViewAdapter extends CaseAdapter {
        public CaseListViewAdapter(Context context, ArrayList<Case> cases) {
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

        @Override
        public Vendor getSelectedVendor() {
            return (Vendor) mVendorSpinner.getSelectedItem();
        }
    }

    private class WarningCardsAdapter extends ArrayAdapter<Warning> implements Filterable {
        private ArrayList<Warning> mOrigWarnings;
        private ArrayList<Warning> mFilteredWarnings;
        private CustomFilter mFilter;

        public WarningCardsAdapter(List<Warning> data) {
            super(getActivity(), 0, data);
            mOrigWarnings = (ArrayList<Warning>) data;
            mFilteredWarnings = new ArrayList<>(data);
            mFilter = new CustomFilter();
        }

        @Override
        public Warning getItem(int position) {
            return mFilteredWarnings.get(position);
        }

        @Override
        public int getCount() {
            return mFilteredWarnings.size();
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class CustomFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                ArrayList<Warning> filterResult = new ArrayList<>();
                for (Warning warning : mOrigWarnings) {
                    if (constraint.equals(getResources().getString(R.string.all_warnings)) ||
                            warning.name.equals(constraint)) {
                        filterResult.add(warning);
                    }
                }

                result.values = filterResult;
                result.count = filterResult.size();
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredWarnings.clear();
                mFilteredWarnings.addAll((ArrayList<Warning>) results.values);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.warning_frag_warning_card_itemview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            WorkingData data = WorkingData.getInstance(getActivity());
            Warning warning = getItem(position);
            Manager manager = data.getManagerById(warning.managerId);
            holder.name.setText(warning.name);
            holder.task.setText(data.getTaskById(warning.taskId).name);
            holder.manager.setText(manager != null ? manager.name : "");
            holder.time.setText(Utils.milliSeconds2MinsSecs(warning.spentTime));
            return convertView;
        }

        private class ViewHolder {
            TextView name;
            TextView task;
            TextView manager;
            TextView time;

            public ViewHolder(View v) {
                name = (TextView) v.findViewById(R.id.name);
                task = (TextView) v.findViewById(R.id.task);
                manager = (TextView) v.findViewById(R.id.manager);
                time = (TextView) v.findViewById(R.id.time);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        filterCaseList(s.toString());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // to nothing
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // to nothing
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.ov_leftpane_spinner:
                filterCaseList(mEtCaseSearch.getText().toString());
                break;
        }
    }

    private void filterCaseList(String query) {
        if (mCaseListView.getAdapter() == null ||
                ((CaseListViewAdapter) mCaseListView.getAdapter()).getFilter() == null) return;
        ((CaseListViewAdapter) mCaseListView.getAdapter()).getFilter().filter(query);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // to nothing
    }

    private ArrayList<Vendor> getSpinnerVendorData() {
        ArrayList<Vendor> tmp = new ArrayList<>();
        tmp.add(new Vendor("", getResources().getString(R.string.case_spinner_all_vendors))); // all vendors
        tmp.addAll(WorkingData.getInstance(getActivity()).getVendors());
        return tmp;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.ov_leftpane_listview:
                CaseListViewAdapter adapter = (CaseListViewAdapter) mCaseListView.getAdapter();
                if (mSelectedCase == adapter.getItem(position)) return;
                mSelectedCase = adapter.getItem(position);
                onCaseSelected();
                adapter.setPositionSelected(position);
                adapter.notifyDataSetChanged();
                break;
            case R.id.warning_cards:
                showWarningDetailFragment();
                break;
        }
    }

    private void showWarningDetailFragment() {
        Toast.makeText(getActivity(), "showWarningDetailFragment", Toast.LENGTH_SHORT).show();
    }

    private class WarningGroupAdapter extends ArrayAdapter<WarningGroup> {
        int selectedPos = 0;

        public WarningGroupAdapter(List<WarningGroup> data) {
            super(getActivity(), 0, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.warning_frag_warning_group_itemview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            WarningGroup group = getItem(position);
            holder.name.setText(group.name);
            holder.count.setText(Integer.toString(group.count));
            if (selectedPos == position) {
                convertView.setBackgroundResource(R.drawable.bg_warning_group_highlighted);
                holder.name.setTextColor(Color.WHITE);
            } else {
                convertView.setBackgroundResource(R.drawable.bg_warning_group);
                holder.name.setTextColor(getResources().getColor(R.color.black1));
            }
            return convertView;
        }

        private class ViewHolder {
            TextView name;
            TextView count;

            public ViewHolder(View v) {
                name = (TextView) v.findViewById(R.id.name);
                count = (TextView) v.findViewById(R.id.count);
            }
        }
    }

    private void onCaseSelected() {
        int total = 0;
        List<Warning> warnings = new ArrayList<>();
        // clear map value
        for (String key : mWarningGroups.keySet()) {
            mWarningGroups.put(key, 0);
        }
        // update map value
        if (mSelectedCase != null) {
            for (Task task : mSelectedCase.tasks) {
                for (Warning warning : task.warnings) {
                    if (warning.status != null && warning.status == Warning.Status.OPEN) {
                        warnings.add(warning);
                        int count = mWarningGroups.get(warning.name) + 1;
                        mWarningGroups.put(warning.name, count);
                    }
                }
            }
        }
        List<WarningGroup> groups = new ArrayList<>();
        for (String key : mWarningGroups.keySet()) {
            groups.add(new WarningGroup(key, mWarningGroups.get(key)));
            total += mWarningGroups.get(key);
        }
        groups.add(0, new WarningGroup(getResources().getString(R.string.all_warnings), total));
        mWarningGroupList.setAdapter(new WarningGroupAdapter(groups));
        if (mWarningCardsAdapter == null) {
            mWarningCardsAdapter = new WarningCardsAdapter(warnings);
            mWarningCards.setAdapter(mWarningCardsAdapter);
        } else {
            mWarningCardsAdapter.clear();
            mWarningCardsAdapter.addAll(warnings);
        }
        filterWarningCards(getResources().getString(R.string.all_warnings));
        mWarningCardsAdapter.notifyDataSetChanged();
        WarningGroupAdapter adapter = ((WarningGroupAdapter) mWarningGroupList.getAdapter());
        adapter.selectedPos = 0;
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
}
