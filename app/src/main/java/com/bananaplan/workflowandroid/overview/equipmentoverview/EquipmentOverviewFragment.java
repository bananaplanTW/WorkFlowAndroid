package com.bananaplan.workflowandroid.overview.equipmentoverview;

import android.graphics.Color;
import android.os.AsyncTask;
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

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.Equipment;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.utility.TabManager;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.view.FactorySpinnerAdapter;
import com.bananaplan.workflowandroid.overview.TaskItemFragment;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class EquipmentOverviewFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, TextWatcher, AdapterView.OnItemClickListener {

    public static class TAB_TAG {
        private static final String TASK_ITEMS              = "tab_tag_task_items";
        private static final String FIX_RECORD              = "tab_tag_fix_records";
    }

    private Spinner mSpinner;
    private ListView mListView;
    private EditText mEditText;
    private TabHost mTabHost;
    private TabManager mTabMgr;

    private FactorySpinnerAdapter mSpinnerAdapter;
    private EquipmentDataAdapter mEquipmentAdapter;

    private Equipment mSelectedEquipment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equipment_ov, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSpinner = (Spinner) getActivity().findViewById(R.id.ov_leftpane_spinner);
        mSpinner.setOnItemSelectedListener(this);
        mSpinnerAdapter = new FactorySpinnerAdapter(getActivity(),
                new ArrayList<Factory>(), mSpinner);
        mSpinner.setAdapter(mSpinnerAdapter);
        mListView = (ListView) getActivity().findViewById(R.id.ov_leftpane_listview);
        mEditText = (EditText) getActivity().findViewById(R.id.ov_leftpane_search_edittext);
        mEditText.addTextChangedListener(this);
        mListView.setOnItemClickListener(this);
        mEquipmentAdapter = new EquipmentDataAdapter(new ArrayList<Equipment>());
        mListView.setAdapter(mEquipmentAdapter);
        mTabHost = (TabHost) getActivity().findViewById(R.id.tab_host);
        mTabHost.setup();
        mTabMgr = new TabManager((MainActivity) getActivity(), this, mTabHost, android.R.id.tabcontent);
        setupTabs();
        new InitTask().execute();
    }

    private void setupTabs() {
        Bundle bundle = new Bundle();
        TabHost.TabSpec taskItemsTabSpec = mTabHost.newTabSpec(TAB_TAG.TASK_ITEMS)
                .setIndicator(getTabTitleView(TAB_TAG.TASK_ITEMS));
        bundle.putString(TaskItemFragment.FROM, getClass().getSimpleName());
        mTabMgr.addTab(taskItemsTabSpec, TaskItemFragment.class, bundle);
        TabHost.TabSpec fixRecordTabSpec = mTabHost.newTabSpec(TAB_TAG.FIX_RECORD)
                .setIndicator(getTabTitleView(TAB_TAG.FIX_RECORD));
        mTabMgr.addTab(fixRecordTabSpec, MaintenanceRecordsFragment.class, null);
    }

    private View getTabTitleView(final String tag) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.ov_tab, null);
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
        ((TextView) view.findViewById(R.id.ov_tab_title)).setText(text);
        return view;
    }

    private class EquipmentDataAdapter extends ArrayAdapter<Equipment> implements Filterable {
        private int mSelectedPosition;
        private CustomFilter mFilter;
        private ArrayList<Equipment> mOrigData;
        private ArrayList<Equipment> mFilteredData;

        public EquipmentDataAdapter(ArrayList<Equipment> objects) {
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

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
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
        public long getItemId(int position) {
            return mFilteredData.get(position).id;
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
                for (Equipment equipment : mOrigData) {
                    if ((TextUtils.isEmpty(constraint)
                            || equipment.name.toLowerCase().contains(constraint))
                            && ((mSpinner.getSelectedItemId() == -1)
                            || (equipment.factoryId == mSpinner.getSelectedItemId()))) {
                        filterResult.add(equipment);
                    }
                }
                result.values = filterResult;
                result.count = filterResult.size();
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredData.clear();
                mFilteredData.addAll((ArrayList<Equipment>) results.values);
                notifyDataSetChanged();
            }
        }
    }

    private class InitTask extends AsyncTask<Void, Void, Void> {
        ArrayList<Factory> factories;
        ArrayList<Equipment> equipments;

        @Override
        protected Void doInBackground(Void... params) {
            loadData();
            return null;
        }

        private void loadData() {
            factories = new ArrayList<>();
            factories.add(new Factory(-1, getResources()
                    .getString(R.string.worker_ov_all_factories))); // all factories
            factories.addAll(WorkingData.getInstance(getActivity()).getFactories());
            equipments = new ArrayList<>();
            equipments.addAll(WorkingData.getInstance(getActivity()).getTools());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSpinnerAdapter.clear();
            mSpinnerAdapter.addAll(factories);
            mSpinnerAdapter.notifyDataSetChanged();
            mEquipmentAdapter.clear();
            mEquipmentAdapter.addAll(equipments);
            mEquipmentAdapter.notifyDataSetChanged();
            if (mEquipmentAdapter.getCount() > 0) {
                mSelectedEquipment = mEquipmentAdapter.getItem(0);
                onToolSelected(mSelectedEquipment);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_spinner:
                oFactorySelected((Factory) mSpinner.getSelectedItem());
                break;
            default:
                break;
        }
    }

    private void onToolSelected(Equipment equipment) {
        if (equipment == null) return;
        ((TextView) getActivity().findViewById(R.id.equipment_name)).setText(equipment.name);
        ((TextView) getActivity().findViewById(R.id.equipment_factory_name))
                .setText(WorkingData.getInstance(getActivity()).getFactoryById(equipment.factoryId).name);
        ((TextView) getActivity().findViewById(R.id.equipment_purchase_date))
                .setText(getResources().getString(R.string.equipment_purchase_date)
                        + Utils.timestamp2Date(equipment.purchaseDate, Utils.DATE_FORMAT_YMD));
        ((TextView) getActivity().findViewById(R.id.equipment_fix_date))
                .setText(getResources().getString(R.string.equipment_fix_date)
                        + Utils.timestamp2Date(equipment.getRecentlyMaintenanceDate(), Utils.DATE_FORMAT_YMD));
        if (mTabMgr != null) {
            mTabMgr.selectItem(equipment);
        }
    }

    private void oFactorySelected(Factory factory) {
        mEquipmentAdapter.getFilter().filter(mEditText.getText().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
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
        mEquipmentAdapter.getFilter().filter(mEditText.getText().toString());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_listview:
                mEquipmentAdapter.setSelectedPosition(position);
                mSelectedEquipment = mEquipmentAdapter.getItem(position);
                onToolSelected(mSelectedEquipment);
                mEquipmentAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    public Equipment getSelectedTool() {
        return mSelectedEquipment;
    }
}
