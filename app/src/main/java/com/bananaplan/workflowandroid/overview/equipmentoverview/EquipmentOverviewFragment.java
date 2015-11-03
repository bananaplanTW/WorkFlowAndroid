package com.bananaplan.workflowandroid.overview.equipmentoverview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Equipment;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.utility.OverviewScrollView;
import com.bananaplan.workflowandroid.utility.TabManager;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.overview.FactorySpinnerAdapter;
import com.bananaplan.workflowandroid.overview.TaskItemFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class EquipmentOverviewFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, TextWatcher, AdapterView.OnItemClickListener,
        View.OnClickListener, DataObserver {

    public static class TAB_TAG {
        private static final String TASK_ITEMS = "tab_tag_task_items";
        private static final String FIX_RECORD = "tab_tag_fix_records";
    }

    private Spinner mFactorySpinner;
    private FactorySpinnerAdapter mFactorySpinnerAdapter;
    private List<Factory> mFactorySpinnerDataSet = new ArrayList<>();

    private ListView mEquipmentList;
    private EquipmentDataAdapter mEquipmentListAdapter;
    private List<Equipment> mEquipmentListDataSet = new ArrayList<>();
    private Equipment mSelectedEquipment;

    private EditText mSearchText;
    private TabHost mTabHost;
    private TabManager mTabMgr;


    private class EquipmentDataAdapter extends ArrayAdapter<Equipment> implements Filterable {

        private int mSelectedPosition;
        private CustomFilter mFilter;
        private List<Equipment> mOrigData;
        private List<Equipment> mFilteredData;


        private class ViewHolder {

            private ViewGroup root;
            private ImageView icon;
            private TextView name;

            public ViewHolder(View v) {
                root = (ViewGroup) v.findViewById(R.id.root);
                icon = (ImageView) v.findViewById(R.id.icon);
                name = (TextView) v.findViewById(R.id.name);
            }
        }

        public EquipmentDataAdapter(List<Equipment> objects) {
            super(getActivity(), 0, objects);
            mOrigData = objects;
            mFilteredData = new ArrayList<>(mOrigData);
            mFilter = new CustomFilter();
        }

        @Override
        public void addAll(Collection<? extends Equipment> collection) {
            super.addAll(collection);
            mFilteredData = new ArrayList<>(collection);
        }

        @Override
        public void clear() {
            super.clear();
            mFilteredData.clear();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.equipment_ov_listview_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.findViewById(R.id.header_divider).setVisibility(position == 0 ? View.VISIBLE : View.GONE);

            if (position == mSelectedPosition) {
                holder.root.setBackgroundColor(getResources().getColor(R.color.blue1));
                holder.name.setTextColor(Color.WHITE);
                holder.icon.setImageDrawable(
                        getResources().getDrawable(R.drawable.equipmentp_white, null));
            } else {
                holder.root.setBackgroundColor(Color.TRANSPARENT);
                holder.name.setTextColor(getResources().getColor(R.color.black1));
                holder.icon.setImageDrawable(
                        getResources().getDrawable(R.drawable.equipmentp_gary, null));
            }

            holder.name.setText(getItem(position).name);

            return convertView;
        }

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
        }

        public int getSelectedPosition() {
            return mSelectedPosition;
        }

        public void updateDataSet(List<Equipment> dataSet) {
            mOrigData = dataSet;
            mFilteredData = dataSet;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFilteredData.size();
        }

        @Override
        public Equipment getItem(int position) {
            return mFilteredData.get(position);
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
                ArrayList<Equipment> filterResult = new ArrayList<>();

                Factory selectedFactory = (Factory) mFactorySpinner.getSelectedItem();
                for (Equipment equipment : mOrigData) {
                    if ((TextUtils.isEmpty(constraint)
                            || equipment.name.toLowerCase().contains(constraint))
                            && TextUtils.isEmpty(selectedFactory.id)
                            || (Utils.isSameId(equipment.factoryId, selectedFactory.id))) {
                        filterResult.add(equipment);
                    }
                }

                result.values = filterResult;
                result.count = filterResult.size();

                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredData = (ArrayList<Equipment>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equipment_ov, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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
        setupEquipmentList();
    }

    private void findViews() {
        mFactorySpinner = (Spinner) getView().findViewById(R.id.ov_leftpane_spinner);
        mEquipmentList = (ListView) getView().findViewById(R.id.ov_leftpane_listview);
        mSearchText = (EditText) getView().findViewById(R.id.ov_leftpane_search_edittext);
        mTabHost = (TabHost) getView().findViewById(R.id.tab_host);
    }

    private void setupViews() {
        mSearchText.addTextChangedListener(this);
        mEquipmentList.setOnItemClickListener(this);
        getView().findViewById(R.id.equipment_edit).setOnClickListener(this);
    }

    private void setupTabs() {
        mTabHost.setup();
        mTabMgr = new TabManager((MainActivity) getActivity(), mTabHost, android.R.id.tabcontent);

        Bundle bundle = new Bundle();
        TabHost.TabSpec taskItemsTabSpec = mTabHost.newTabSpec(TAB_TAG.TASK_ITEMS)
                .setIndicator(getTabTitleView(TAB_TAG.TASK_ITEMS));
        bundle.putString(TaskItemFragment.FROM, getClass().getSimpleName());

        mTabMgr.addTab(taskItemsTabSpec, TaskItemFragment.class, bundle);
        TabHost.TabSpec fixRecordTabSpec = mTabHost.newTabSpec(TAB_TAG.FIX_RECORD)
                .setIndicator(getTabTitleView(TAB_TAG.FIX_RECORD));
        mTabMgr.addTab(fixRecordTabSpec, MaintenanceRecordsFragment.class, null);
    }

    private void setupFactorySpinner() {
        setFactorySpinnerData();
        mFactorySpinnerAdapter = new FactorySpinnerAdapter(getActivity(), mFactorySpinnerDataSet,
                new IconSpinnerAdapter.OnItemSelectedCallback() {
            @Override
            public int getSelectedPos() {
                if (mFactorySpinner != null) {
                    return mFactorySpinner.getSelectedItemPosition();
                }
                return 0;
            }
        });
        mFactorySpinner.setAdapter(mFactorySpinnerAdapter);
        mFactorySpinner.setOnItemSelectedListener(this);
    }

    private void setFactorySpinnerData() {
        mFactorySpinnerDataSet.clear();
        mFactorySpinnerDataSet.add(new Factory("", getResources().getString(R.string.worker_ov_all_factories))); // all factories
        mFactorySpinnerDataSet.addAll(WorkingData.getInstance(getActivity()).getFactories());
    }

    private void setupEquipmentList() {
        setEquipmentListData(null);
        mEquipmentListAdapter = new EquipmentDataAdapter(mEquipmentListDataSet);
        mEquipmentList.setAdapter(mEquipmentListAdapter);
    }

    private void setEquipmentListData(String factoryId) {
        mEquipmentListDataSet.clear();

        if (TextUtils.isEmpty(factoryId)) {
            mEquipmentListDataSet.addAll(WorkingData.getInstance(getActivity()).getEquipments());

        } else {
            for (Equipment equipment : WorkingData.getInstance(getActivity()).getEquipments()) {
                if (!equipment.factoryId.equals(factoryId)) continue;
                mEquipmentListDataSet.add(equipment);
            }
        }

        if (mSelectedEquipment == null) {
            mSelectedEquipment = mEquipmentListDataSet.get(0);
        }
    }

    private View getTabTitleView(final String tag) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab, null);

        int titleResId;
        switch (tag) {
            case TAB_TAG.TASK_ITEMS:
                titleResId = R.string.worker_ov_worker_tab_title_task_items;
                break;
            case TAB_TAG.FIX_RECORD:
                titleResId = R.string.worker_ov_worker_tab_title_fix_record;
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_spinner:
                setEquipmentListData(mFactorySpinnerDataSet.get(position).id);
                mEquipmentListAdapter.updateDataSet(mEquipmentListDataSet);

                mSearchText.setText("");
                mEquipmentList.setSelection(0);
                mEquipmentListAdapter.setSelectedPosition(0);
                mEquipmentListAdapter.notifyDataSetChanged();

                selectEquipment(mEquipmentListAdapter.getItem(0), true);

                break;
        }
    }

    private void selectEquipment(Equipment selectedEquipment, boolean isScrollToTop) {
        mSelectedEquipment = selectedEquipment;
        onEquipmentSelected(mSelectedEquipment, isScrollToTop);
    }

    private void onEquipmentSelected(Equipment equipment, boolean isScrollToTop) {
        if (equipment == null) return;

        if (isScrollToTop) {
            ((OverviewScrollView) getView().findViewById(R.id.scroll)).setScrollEnable(false);
        } else {
            ((OverviewScrollView) getView().findViewById(R.id.scroll)).setScrollEnable(true);
        }

        ((TextView) getView().findViewById(R.id.equipment_name)).setText(equipment.name);
        ((TextView) getView().findViewById(R.id.equipment_factory_name))
                .setText(WorkingData.getInstance(getActivity()).getFactoryById(equipment.factoryId).name);
        ((TextView) getView().findViewById(R.id.equipment_purchase_date))
                .setText(getResources().getString(R.string.equipment_purchase_date)
                        + Utils.timestamp2Date(equipment.purchasedDate, Utils.DATE_FORMAT_YMD));
        ((TextView) getView().findViewById(R.id.equipment_fix_date))
                .setText(getResources().getString(R.string.equipment_fix_date)
                        + Utils.timestamp2Date(equipment.getRecentlyMaintenanceDate(), Utils.DATE_FORMAT_YMD));

        if (mTabMgr != null) {
            mTabMgr.selectItem(equipment);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mEquipmentListAdapter.getFilter().filter(mSearchText.getText().toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_listview:
                if (mSelectedEquipment == mEquipmentListAdapter.getItem(position)) return;

                mEquipmentListAdapter.setSelectedPosition(position);
                mEquipmentListAdapter.notifyDataSetChanged();

                selectEquipment(mEquipmentListAdapter.getItem(position), true);

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.equipment_edit:
                Toast.makeText(getActivity(), "Edit equipment = " + getSelectedEquipment().name, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public Equipment getSelectedEquipment() {
        return mSelectedEquipment;
    }

    @Override
    public void updateData() {
        setFactorySpinnerData();
        mFactorySpinnerAdapter.notifyDataSetChanged();

        setEquipmentListData(mFactorySpinnerDataSet.get(mFactorySpinner.getSelectedItemPosition()).id);
        mEquipmentListAdapter.updateDataSet(mEquipmentListDataSet);

        selectEquipment(mEquipmentListDataSet.get(mEquipmentListAdapter.getSelectedPosition()), false);
    }
}
