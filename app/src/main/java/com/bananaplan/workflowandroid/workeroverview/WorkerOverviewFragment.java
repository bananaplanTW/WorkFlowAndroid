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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.WorkingData;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.workers.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.utility.IconSpinnerAdapter;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/1.
 */
public class WorkerOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener
        , View.OnClickListener {

    private MainActivity mActivity;
    private WorkingData mWorkingData;

    private Spinner mFactoriesSpinner;
    private EditText mWorkerSearchEditText;
    private ListView mWorkerListView;

    private FactorySpinnerAdapter mFactorySpinnerAdapter;
    private WorkerLisviewAdapter mWorkerLisviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker_overview, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof MainActivity)) return;
        mActivity = (MainActivity) activity;
        mWorkingData = mActivity.getWorkingData();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // findview
        mFactoriesSpinner = (Spinner) mActivity.findViewById(R.id.worker_ov_factory_spinner);
        mWorkerSearchEditText = (EditText) mActivity.findViewById(R.id.worker_ov_worker_search_edittext);
        mWorkerListView = (ListView) mActivity.findViewById(R.id.worker_ov_workers_listview);

        // factory spinner
        mFactorySpinnerAdapter = new FactorySpinnerAdapter(getFactoriesSpinnerData());
        mFactoriesSpinner.setAdapter(mFactorySpinnerAdapter);
        mFactoriesSpinner.setOnItemSelectedListener(this);

        // search worker edittext
        mWorkerSearchEditText.addTextChangedListener(this);

        // worker listview
        mWorkerLisviewAdapter = new WorkerLisviewAdapter(getWorkerLisviewAdapterData());
        mWorkerListView.setAdapter(mWorkerLisviewAdapter);
        mWorkerListView.setOnItemClickListener(this);
    }

    private ArrayList<Factory> getFactoriesSpinnerData() {
        ArrayList<Factory> tmp = new ArrayList<>();
        tmp.add(new Factory(-1, getResources().getString(R.string.worker_ov_all_factories))); // all factories
        tmp.addAll(mWorkingData.getFactories());
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
        for (Factory factory : mWorkingData.getFactories()) {
            tmp.addAll(factory.workerItems);
        }
        return tmp;
    }

    private class WorkerLisviewAdapter extends ArrayAdapter<WorkerItem> implements Filterable {
        private int mSelectedPosition = 0;
        private CustomFilter mFilter;
        private ArrayList<WorkerItem> mOrigData;
        private ArrayList<WorkerItem> mFilteredData;

        public WorkerLisviewAdapter(ArrayList<WorkerItem> workers) {
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
                            && (mFactoriesSpinner.getSelectedItemId() == -1 || worker.factoryId == mFactoriesSpinner.getSelectedItemId())) {
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

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.worker_ov_workers_listview:
                mWorkerLisviewAdapter.setSelectedPosition(position);
                onWorkerSelected(mWorkerLisviewAdapter.getItem(position));
                mWorkerLisviewAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.worker_ov_factory_spinner:
                oFactorySelected((Factory) mFactoriesSpinner.getSelectedItem());
                break;
            default:
                break;
        }
    }

    private void oFactorySelected(Factory factory) {
        mWorkerLisviewAdapter.getFilter().filter(mWorkerSearchEditText.getText().toString());
    }

    private void onWorkerSelected(WorkerItem worker) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        mWorkerLisviewAdapter.getFilter().filter(mWorkerSearchEditText.getText().toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }
}
