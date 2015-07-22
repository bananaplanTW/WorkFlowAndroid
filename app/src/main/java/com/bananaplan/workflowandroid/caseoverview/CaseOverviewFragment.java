package com.bananaplan.workflowandroid.caseoverview;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.WorkingData;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.workers.Vendor;
import com.bananaplan.workflowandroid.main.MainActivity;

import java.util.ArrayList;

/**
 * @author Ben Lai
 * @since 2015/7/16.
 */
public class CaseOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private MainActivity mActivity;
    private WorkingData mWorkingData;

    // views in left pane
    private Spinner mSpinner;
    private EditText mEtCaseSearch;
    private ListView mTaskCaseListView;

    private SpinnerAdapter mSpinnerAdapter;
    private ListViewAdapter mListviewAdapter;

    // views in right pane
    private TextView mTvCaseNameSelected;
    private TextView mTvCaseVendorSelected;
    private TextView mTvCasePersonInChargeSelected;
    private ProgressBar mPbCaseSelected;
    private TextView mTvCaseHoursPassedBy;
    private TextView mTvCaseHoursUnfinished;
    private TextView mTvCaseHoursForecast;

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
        mEtCaseSearch = (EditText) getActivity().findViewById(R.id.et_search_case);
        mTaskCaseListView = (ListView) getActivity().findViewById(R.id.case_listview);

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

        mListviewAdapter = new ListViewAdapter(getActivity(), getTaskCases());
        mTaskCaseListView.setAdapter(mListviewAdapter);
        mTaskCaseListView.setOnItemClickListener(this);
    }

    private ArrayList<Vendor> getSpinnerVendorData() {
        ArrayList<Vendor> tmp = new ArrayList<Vendor>();
        tmp.add(new Vendor(-1L, getResources().getString(R.string.case_spinner_all_vendors))); // all vendors
        tmp.addAll(mWorkingData.getVendors());
        return tmp;
    }

    private ArrayList<TaskCase> getTaskCases() {
        ArrayList<TaskCase> cases = new ArrayList<>();
        TaskCase firstCase = null;
        for (Vendor vendor : mWorkingData.getVendors()) {
            for (TaskCase taskCase : vendor.taskCases) {
                if (firstCase == null) {
                    firstCase = taskCase;
                }
                cases.add(taskCase);
            }
        }
        if (firstCase != null) {
            openCase(firstCase);
        }
        return cases;
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

    private class ListViewAdapter extends ArrayAdapter<TaskCase> implements Filterable {
        private LayoutInflater mInflater;
        private ArrayList<TaskCase> mOrigCases;
        private ArrayList<TaskCase> mFilteredCases;
        private CustomFilter mFilter;
        private int mPositionSelected;

        public ListViewAdapter(Context context, ArrayList<TaskCase> cases) {
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
                convertView = mInflater.inflate(R.layout.case_listview_view, parent, false);
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
            } else {
                holder.mTvStatus.setText(String.valueOf(finishPercentage) + "%");
                if (finishPercentage <= 33) {
                    holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.case_border_textview_bg_green));
                } else if (finishPercentage <= 66) {
                    holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.case_border_textview_bg_orange));
                } else {
                    holder.mTvStatus.setBackground(getResources().getDrawable(R.drawable.case_border_textview_bg_red));
                }
            }
            if (position == mPositionSelected) {
                holder.mRoot.setBackgroundColor(Color.rgb(66, 139, 202));
            } else {
                holder.mRoot.setBackgroundColor(Color.TRANSPARENT);
            }
            final ViewGroup.LayoutParams params = convertView.getLayoutParams();
            params.height = (int) getResources().getDimension(R.dimen.case_overview_listview_item_height);
            return convertView;
        }

        private class ViewHolder {
            LinearLayout mRoot;
            TextView mTvStatus;
            TextView mTvVendor;
            TextView mTvCaseName;

            public ViewHolder(View view) {
                mRoot = (LinearLayout) view.findViewById(R.id.case_listview_root);
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
        mListviewAdapter.getFilter().filter(s.toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mSpinner.getId()) {
            mListviewAdapter.getFilter().filter(mEtCaseSearch.getText().toString());
        }
    }

    private void openCase(TaskCase taskCase) {
        mTvCaseNameSelected.setText(taskCase.name);
        mTvCaseVendorSelected.setText(mWorkingData.getVendorById(taskCase.vendorId).name);
        mTvCasePersonInChargeSelected.setText(taskCase.personInCharge);
        mTvCaseHoursPassedBy.setText(taskCase.getHoursPassedBy());
        mTvCaseHoursUnfinished.setText(taskCase.getHoursUnFinished());
        mTvCaseHoursForecast.setText(taskCase.getHoursForecast());
        mPbCaseSelected.setProgress(taskCase.getFinishPercent());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mTaskCaseListView.getId()) {
            TaskCase taskCase = mListviewAdapter.getItem(position);
            openCase(taskCase);
            mListviewAdapter.setPositionSelected(position);
            mListviewAdapter.notifyDataSetChanged();
        }
    }
}
