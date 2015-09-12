package com.bananaplan.workflowandroid.overview.equipmentoverview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.Equipment;
import com.bananaplan.workflowandroid.data.equipment.MaintenanceRecord;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.OverviewScrollView;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/9/5.
 */
public class MaintenanceRecordsFragment extends OvTabFragmentBase implements OvTabFragmentBase.OvCallBack,
        View.OnClickListener {
    private ListView mListView;
    private RecordAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_equipment_ov_maintenance, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().findViewById(R.id.add_maintenance_record).setOnClickListener(this);
        mListView = (ListView) getActivity().findViewById(R.id.listview_maintenance_records);
        onItemSelected(getSelectedEquipment());
    }

    @Override
    public Object getCallBack() {
        return this;
    }

    private class RecordAdapter extends ArrayAdapter<MaintenanceRecord> {
        public RecordAdapter(ArrayList<MaintenanceRecord> records) {
            super(getActivity(), 0, records);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.equipment_ov_maintenance_listview_itemview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.findViewById(R.id.top_divider).setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            MaintenanceRecord record = getItem(position);
            holder.reason.setText(record.reason);
            holder.date.setText(Utils.timestamp2Date(record.date, Utils.DATE_FORMAT_YMD_HM_AMPM));
            return convertView;
        }

        private class ViewHolder {
            private TextView reason;
            private TextView date;
            private ImageView icon;

            public ViewHolder(View view) {
                reason = (TextView) view.findViewById(R.id.reason);
                date = (TextView) view.findViewById(R.id.date);
                icon = (ImageView) view.findViewById(R.id.icon);
            }
        }
    }

    @Override
    public void onItemSelected(Object item) {
        if (item == null) return;
        Equipment equipment = (Equipment) item;
        ArrayList<MaintenanceRecord> records = new ArrayList<>(equipment.records);
        if (mAdapter == null) {
            mAdapter = new RecordAdapter(records);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(records);
        }
        mAdapter.notifyDataSetChanged();
        if (mAdapter != null && mAdapter.getCount() > 0) {
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = (int) (mAdapter.getCount() * getResources().getDimension(R.dimen.equipment_ov_listview_item_min_height)
                    + mListView.getDividerHeight() * (mAdapter.getCount()));
            mListView.requestLayout();
            ((OverviewScrollView) getActivity().findViewById(R.id.scroll)).setScrollEnable(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_maintenance_record:
                Toast.makeText(getActivity(), "Add maintenance record", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
