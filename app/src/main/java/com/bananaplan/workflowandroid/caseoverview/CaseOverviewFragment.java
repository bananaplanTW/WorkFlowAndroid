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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.WorkingData;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.workers.Factory;
import com.bananaplan.workflowandroid.main.MainActivity;

import java.util.ArrayList;

/**
 * @author Ben Lai
 * @since 2015/7/16.
 */
public class CaseOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private MainActivity mActivity;
    private WorkingData mData;

    // views in left pane
    private Spinner mSpinner;
    private EditText mEtCaseSearch;
    private ListView mTaskCaseListView;


    private SpinnerAdapter mSpinnerAdapter;
    private ListViewAdapter mListviewAdapter;

    // views in right pane
    private TextView mTvCaseNameSelected;
    private TextView mTvCaseFactorySelected;
    private TextView mTvCasePersonInChargeSelected;
    private ProgressBar mPbCaseSelected;
    private TextView mTvCaseHoursPassedBy;
    private TextView mTvCaseHoursUnfinished;
    private TextView mTvCaseHoursForecast;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
        mData = mActivity.getWorkingData();
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
        mSpinnerAdapter = new SpinnerAdapter(getActivity(), R.layout.case_spinner_view, getFactoryNames());
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);

        // right pane
        mTvCaseNameSelected = (TextView) getActivity().findViewById(R.id.case_tv_case_name_selected);
        mTvCaseFactorySelected = (TextView) getActivity().findViewById(R.id.case_tv_factory_selected);
        mTvCasePersonInChargeSelected = (TextView) getActivity().findViewById(R.id.case_tv_person_in_charge_selected);
        mPbCaseSelected = (ProgressBar) getActivity().findViewById(R.id.case_progressBar);
        mTvCaseHoursPassedBy = (TextView) getActivity().findViewById(R.id.case_tv_hours_passed_by);
        mTvCaseHoursUnfinished = (TextView) getActivity().findViewById(R.id.case_tv_hours_unfinished);
        mTvCaseHoursForecast = (TextView) getActivity().findViewById(R.id.case_tv_hours_forecast);

        mListviewAdapter = new ListViewAdapter(getActivity(), getTaskCases());
        mTaskCaseListView.setAdapter(mListviewAdapter);
        mTaskCaseListView.setOnItemClickListener(this);
    }

    private ArrayList<Factory> getFactoryNames() {
        return mData.getFactories();
    }

    private ArrayList<TaskCase> getTaskCases() {
        ArrayList<TaskCase> cases = new ArrayList<>();
        TaskCase firstCase = null;
        for (Factory factory : mData.getFactories()) {
            if (factory.getId() == mSpinner.getSelectedItemId() || mSpinner.getSelectedItemId() == 0) {
                for (TaskCase taskCase : factory.getTaskCases()) {
                    if (firstCase == null) {
                        firstCase = taskCase;
                    }
                    cases.add(taskCase);
                }
            }
        }
        if (firstCase != null) {
            openCase(firstCase);
        }
        return cases;
    }

    private class SpinnerAdapter extends ArrayAdapter<Factory> {
        private LayoutInflater mInflator;

        public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<Factory> objects) {
            super(context, textViewResourceId, objects);
            mInflator = getActivity().getLayoutInflater();
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpinnerViewHolder holder;
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.case_spinner_view, null);
                holder = new SpinnerViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (SpinnerViewHolder) convertView.getTag();
            }
            holder.mTv.setText(getItem(position).getName());
            return convertView;
        }

        @Override
        public Factory getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            SpinnerDropdownViewHolder holder;
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.case_spinner_dropdown_view, null);
                holder = new SpinnerDropdownViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (SpinnerDropdownViewHolder) convertView.getTag();
            }
            holder.mTv.setText(getItem(position).getName());
            return convertView;
        }

        private class SpinnerDropdownViewHolder {
            private TextView mTv;

            public SpinnerDropdownViewHolder(View v) {
                this.mTv = (TextView) v.findViewById(R.id.case_spinner_dropdown_view_tv_factory_name);
            }
        }

        private class SpinnerViewHolder {
            private ImageView mIv1, mIv2;
            private TextView mTv;

            public SpinnerViewHolder(View v) {
                this.mIv1 = (ImageView) v.findViewById(R.id.case_spinner_view_iv1);
                this.mTv = (TextView) v.findViewById(R.id.case_spinner_view_tv_factory_name);
                this.mIv2 = (ImageView) v.findViewById(R.id.case_spinner_view_iv2);
            }
        }
    }

    private class ListViewAdapter extends ArrayAdapter<TaskCase> implements Filterable {
        private LayoutInflater mInflator;
        private ArrayList<TaskCase> mOrigCases;
        private ArrayList<TaskCase> mFilteredCases;
        private CustomFilter mFilter;
        private int mPositionSelected;

        public ListViewAdapter(Context context, ArrayList<TaskCase> cases) {
            super(context, 0, cases);
            mInflator = getActivity().getLayoutInflater();
            mOrigCases = cases;
            mFilteredCases = cases;
            mFilter = new CustomFilter();
        }

        @Override
        public TaskCase getItem(int position) {
            return mFilteredCases.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mFilteredCases.get(position).getId();
        }

        @Override
        public int getCount() {
            return mFilteredCases.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.case_listview_view, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTvCaseName.setText(getItem(position).getName());
            holder.mTvFactory.setText(getItem(position).getFactory().getName());
            holder.mTvStatus.setText(String.valueOf(getItem(position).getFinishPercent()));
            if (position == mPositionSelected) {
                holder.mRoot.setBackgroundColor(Color.BLUE);
            } else {
                holder.mRoot.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }

        private class ViewHolder {
            LinearLayout mRoot;
            TextView mTvStatus;
            TextView mTvFactory;
            TextView mTvCaseName;

            public ViewHolder(View view) {
                mRoot = (LinearLayout) view.findViewById(R.id.case_listview_root);
                mTvStatus = (TextView) view.findViewById(R.id.case_listview_view_tv_status);
                mTvFactory = (TextView) view.findViewById(R.id.case_listview_view_tv_factory_name);
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
                if (!TextUtils.isEmpty(constraint)) {
                    ArrayList<TaskCase> filterResult = new ArrayList<TaskCase>();
                    for (TaskCase taskCase: mOrigCases) {
                        if (taskCase.getName().toLowerCase().contains(constraint)) {
                            filterResult.add(taskCase);
                        }
                    }
                    result.values = filterResult;
                    result.count = filterResult.size();
                } else {
                    result.values = mOrigCases;
                    result.count = mOrigCases.size();
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredCases = (ArrayList<TaskCase>) results.values;
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
        // TODO
    }

    private void openCase(TaskCase taskCase) {
        mTvCaseNameSelected.setText(taskCase.getName());
        mTvCaseFactorySelected.setText(taskCase.getFactory().getName());
        mTvCasePersonInChargeSelected.setText(taskCase.getPersonInCharge());
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
