package com.bananaplan.workflowandroid.overview.workeroverview;

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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.overview.StatusFragment;
import com.bananaplan.workflowandroid.overview.TaskItemFragment;
import com.bananaplan.workflowandroid.utility.OverviewScrollView;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.TabManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 2015/8/1.
 */
public class WorkerOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener, View.OnClickListener, TabHost.OnTabChangeListener, DataObserver {

    public static class TAB_TAG {
        private static final String TASK_ITEMS = "tab_tag_task_items";
        private static final String WORKER_STATUS = "tab_tag_worker_status";
        private static final String WORKER_ATTENDANCE_STATUS = "tab_tag_worker_attendance_status";
    }

    private Spinner mFactorySpinner;
    private FactorySpinnerAdapter mFactorySpinnerAdapter;
    private List<Factory> mFactorySpinnerDataSet = new ArrayList<>();

    private ListView mWorkerListView;
    private WorkerLisViewAdapter mWorkerLisViewAdapter;
    private List<Worker> mWorkerListDataSet = new ArrayList<>();
    private Worker mSelectedWorker;

    private EditText mWorkerSearchEditText;
    private ImageView mIvWorkerAvatar;
    private TextView mTvWorkerName;
    private TextView mTvWorkerWipEquipment;
    private TextView mTvWorkerFactoryName;
    private TextView mTvWorkerAddress;
    private TextView mTvWorkerPhone;
    private TabHost mTabHost;

    private TabManager mTabMgr;


    private class FactorySpinnerAdapter extends IconSpinnerAdapter<Factory> {

        public FactorySpinnerAdapter(List<Factory> objects) {
            super(getActivity(), -1, objects, new OnItemSelectedCallback() {
                @Override
                public int getSelectedPos() {
                    if (mFactorySpinner != null) {
                        return mFactorySpinner.getSelectedItemPosition();
                    }
                    return 0;
                }
            });
        }

        @Override
        public Factory getItem(int position) {
            return (Factory) super.getItem(position);
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
        public int getSpinnerIconResourceId() {
            return R.drawable.ic_business_black;
        }
    }

    private class WorkerLisViewAdapter extends ArrayAdapter<Worker> implements Filterable {

        private int mSelectedPosition = 0;
        private CustomFilter mFilter;
        private List<Worker> mOrigData;
        private List<Worker> mFilteredData;


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

        public WorkerLisViewAdapter(List<Worker> workers) {
            super(getActivity(), -1, workers);
            mOrigData = workers;
            mFilteredData = new ArrayList<>(mOrigData);
            mFilter = new CustomFilter();
        }

        @Override
        public int getCount() {
            return mFilteredData.size();
        }

        @Override
        public Worker getItem(int position) {
            return mFilteredData.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.worker_ov_worker_listview_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            convertView.findViewById(R.id.header_divider).setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            holder.avatar.setImageDrawable(getItem(position).getAvator());
            holder.name.setText(getItem(position).name);
            holder.title.setText(getItem(position).jobTitle);

            // update background of selected item
            if (position == mSelectedPosition) {
                holder.root.setBackgroundColor(getResources().getColor(R.color.blue1));
                holder.name.setTextColor(Color.WHITE);
                holder.title.setTextColor(Color.WHITE);
            } else {
                holder.root.setBackgroundColor(Color.TRANSPARENT);
                holder.name.setTextColor(getResources().getColor(R.color.black1));
                holder.title.setTextColor(getResources().getColor(R.color.gray1));
            }

            return convertView;
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
                ArrayList<Worker> filterResult = new ArrayList<>();

                Factory selectedFactory = (Factory) mFactorySpinner.getSelectedItem();
                for (Worker worker : mOrigData) {
                    if ((TextUtils.isEmpty(constraint) || worker.name.toLowerCase().contains(constraint))
                            && TextUtils.isEmpty(selectedFactory.id)
                            || (Utils.isSameId(worker.factoryId, selectedFactory.id))) {
                        filterResult.add(worker);
                    }
                }

                result.values = filterResult;
                result.count = filterResult.size();

                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredData = (ArrayList<Worker>) results.values;
                notifyDataSetChanged();
            }
        }

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
        }

        public int getSelectedPosition() {
            return mSelectedPosition;
        }

        public void updateDataSet(List<Worker> dataSet) {
            mOrigData = dataSet;
            mFilteredData = dataSet;
            notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker_ov, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
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
        setupFactorySpinner();
        setupWorkerList();
    }

    private void findViews() {
        mFactorySpinner = (Spinner) getActivity().findViewById(R.id.ov_leftpane_spinner);
        mWorkerSearchEditText = (EditText) getActivity().findViewById(R.id.ov_leftpane_search_edittext);
        mWorkerListView = (ListView) getActivity().findViewById(R.id.ov_leftpane_listview);
        mIvWorkerAvatar = (ImageView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_avatar);
        mTvWorkerName = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_name);
        mTvWorkerWipEquipment = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_wip_equipment);
        mTvWorkerFactoryName = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_factory_name);
        mTvWorkerAddress = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_address);
        mTvWorkerPhone = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_phone);
        mTabHost = (TabHost) getActivity().findViewById(R.id.worker_ov_right_pane_tab_host);
    }

    private void setupViews() {
        getActivity().findViewById(R.id.worker_ov_right_pane_edit_worker).setOnClickListener(this);
        mWorkerSearchEditText.addTextChangedListener(this);
    }

    private void setupTabs() {
        mTabHost.setup();
        mTabMgr = new TabManager((MainActivity) getActivity(), mTabHost, android.R.id.tabcontent);

        Bundle bundle1 = new Bundle();
        TabHost.TabSpec taskItemsTabSpec = mTabHost.newTabSpec(TAB_TAG.TASK_ITEMS)
                .setIndicator(getTabTitleView(TAB_TAG.TASK_ITEMS));
        bundle1.putString(TaskItemFragment.FROM, getClass().getSimpleName());
        mTabMgr.addTab(taskItemsTabSpec, TaskItemFragment.class, bundle1);

        TabHost.TabSpec workerStatusTabSpec = mTabHost.newTabSpec(TAB_TAG.WORKER_STATUS)
                .setIndicator(getTabTitleView(TAB_TAG.WORKER_STATUS));
        Bundle bundle2 = new Bundle();
        bundle2.putString(StatusFragment.FROM, getClass().getSimpleName());
        mTabMgr.addTab(workerStatusTabSpec, StatusFragment.class, bundle2);

        TabHost.TabSpec workerAttendanceStatusTabSpec = mTabHost.newTabSpec(TAB_TAG.WORKER_ATTENDANCE_STATUS)
                .setIndicator(getTabTitleView(TAB_TAG.WORKER_ATTENDANCE_STATUS));
        mTabMgr.addTab(workerAttendanceStatusTabSpec, AttendanceStatusFragment.class, null);
    }

    private void setupFactorySpinner() {
        setFactorySpinnerData();
        mFactorySpinnerAdapter = new FactorySpinnerAdapter(mFactorySpinnerDataSet);
        mFactorySpinner.setAdapter(mFactorySpinnerAdapter);
        mFactorySpinner.setOnItemSelectedListener(this);
    }

    private void setFactorySpinnerData() {
        mFactorySpinnerDataSet.clear();
        mFactorySpinnerDataSet.add(new Factory("", getResources().getString(R.string.worker_ov_all_factories))); // all factories
        mFactorySpinnerDataSet.addAll(WorkingData.getInstance(getActivity()).getFactories());
    }

    private void setupWorkerList() {
        setWorkerListData(null);
        mWorkerLisViewAdapter = new WorkerLisViewAdapter(mWorkerListDataSet);
        mWorkerListView.setAdapter(mWorkerLisViewAdapter);
        mWorkerListView.setOnItemClickListener(this);
    }

    private void setWorkerListData(String factoryId) {
        mWorkerListDataSet.clear();

        if (TextUtils.isEmpty(factoryId)) {
            mWorkerListDataSet.addAll(WorkingData.getInstance(getActivity()).getWorkers());
        } else {
            mWorkerListDataSet.addAll(WorkingData.getInstance(getActivity()).getFactoryById(factoryId).workers);
        }

        if (mSelectedWorker == null) {
            mSelectedWorker = mWorkerListDataSet.get(0);
        }
    }

    private View getTabTitleView(final String tag) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab, null);
        int titleResId;
        switch (tag) {
            case TAB_TAG.TASK_ITEMS:
                titleResId = R.string.worker_ov_worker_tab_title_task_items;
                break;
            case TAB_TAG.WORKER_STATUS:
                titleResId = R.string.worker_ov_worker_tab_title_worker_status;
                break;
            case TAB_TAG.WORKER_ATTENDANCE_STATUS:
                titleResId = R.string.worker_ov_worker_tab_title_worker_attendance_status;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.worker_ov_right_pane_edit_worker:
                editWorker();

                break;
        }
    }

    private void editWorker() {
        Toast.makeText(getActivity(), "Edit worker", Toast.LENGTH_SHORT).show();
        // TODO
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_listview:
                if (mSelectedWorker == mWorkerLisViewAdapter.getItem(position)) return;

                mWorkerLisViewAdapter.setSelectedPosition(position);
                mWorkerLisViewAdapter.notifyDataSetChanged();

                selectWorker(mWorkerLisViewAdapter.getItem(position), true);

                break;
        }
    }

    private void selectWorker(Worker worker, boolean isScrollToTop) {
        mSelectedWorker = worker;
        onWorkerSelected(mSelectedWorker, isScrollToTop);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_spinner:
                setWorkerListData(mFactorySpinnerDataSet.get(position).id);
                mWorkerLisViewAdapter.updateDataSet(mWorkerListDataSet);

                mWorkerSearchEditText.setText("");
                mWorkerListView.setSelection(0);
                mWorkerLisViewAdapter.setSelectedPosition(0);
                mWorkerLisViewAdapter.notifyDataSetChanged();

                selectWorker(mWorkerLisViewAdapter.getItem(0), true);

                break;
        }
    }

    /*
     * parameter calledFromActivityCreated: since WorkerFragmentBase is created later,
     * update worker's content only when WorkerFragmentBase fragment is ready
     */
    private void onWorkerSelected(Worker worker, boolean isScrollToTop) {
        if (worker == null) return;

        if (isScrollToTop) {
            ((OverviewScrollView) getView().findViewById(R.id.scroll)).setScrollEnable(false);
        } else {
            ((OverviewScrollView) getView().findViewById(R.id.scroll)).setScrollEnable(true);
        }

        mIvWorkerAvatar.setImageDrawable(worker.getAvator());
        mTvWorkerName.setText(worker.name);
        mTvWorkerWipEquipment.setText(Utils.getWorkerWipEquipmentName(getActivity(), worker));
        mTvWorkerFactoryName.setText(WorkingData.getInstance(getActivity()).getFactoryById(worker.factoryId).name);
        mTvWorkerAddress.setText(getResources().getString(R.string.worker_ov_worker_address)
                + (TextUtils.isEmpty(worker.address) ? "" : worker.address));
        mTvWorkerPhone.setText(getResources().getString(R.string.worker_ov_worker_phone)
                + (TextUtils.isEmpty(worker.phone) ? "" : worker.phone));
        if (mTabMgr != null) {
            mTabMgr.selectItem(worker);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void filterWorkerList(String query) {
        if (mWorkerLisViewAdapter == null || mWorkerLisViewAdapter.getFilter() == null) return;
        mWorkerLisViewAdapter.getFilter().filter(query);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterWorkerList(mWorkerSearchEditText.getText().toString());
    }

    @Override
    public void onTabChanged(String tabId) {
        // do nothing
    }

    public Worker getSelectedWorker() {
        return mSelectedWorker;
    }

    @Override
    public void updateData() {
        setFactorySpinnerData();
        mFactorySpinnerAdapter.notifyDataSetChanged();

        setWorkerListData(mFactorySpinnerDataSet.get(mFactorySpinner.getSelectedItemPosition()).id);
        mWorkerLisViewAdapter.updateDataSet(mWorkerListDataSet);

        selectWorker(mWorkerListDataSet.get(mWorkerLisViewAdapter.getSelectedPosition()), false);
    }
}
