package com.bananaplan.workflowandroid.overview.workeroverview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.OverviewScrollView;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.data.worker.attendance.LeaveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Ben on 2015/8/14.
 */
public class AttendanceStatusFragment extends OvTabFragmentBase implements
        OvTabFragmentBase.OvCallBack, View.OnClickListener {
    private ListView mListView;
    private DateAdapter mAdapter;
    private static int sListViewHeaderHeight = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().findViewById(R.id.worker_ov_attendance_btn_add_leave).setOnClickListener(this);
        mListView = (ListView) getActivity().findViewById(R.id.worker_ov_attendance_listview);
        mListView.addHeaderView(getTaskItemListViewHeader(), null, false);
        onItemSelected(getSelectedWorker());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.ftragment_worker_ov_attendane_status, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.worker_ov_attendance_btn_add_leave:
                Toast.makeText(getActivity(), "Add leave date", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public Object getCallBack() {
        return this;
    }

    private class DateAdapter extends ArrayAdapter<LeaveData> {
        public DateAdapter(ArrayList<LeaveData> data) {
            super(getActivity(), 0, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.worker_attendance_listview_view_row, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
                final ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.height = (int) getResources().getDimension(R.dimen.ov_taskitem_listview_item_height);
                if (position % 2 == 0) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.gray4));
                } else {
                    convertView.setBackgroundColor(Color.WHITE);
                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LeaveData data = getItem(position);
            holder.date.setText(Utils.timestamp2Date(data.date, Utils.DATE_FORMAT_YMD));
            int typeResId = -1;
            switch (data.type) {
                case PRIVATE:
                    typeResId = R.string.worker_ov_tab_attendance_leave_reason_private;
                    break;
                case WORK:
                    typeResId = R.string.worker_ov_tab_attendance_leave_reason_work;
                    break;
                case SICK:
                    typeResId = R.string.worker_ov_tab_attendance_leave_reason_sick;
                    break;
            }
            holder.type.setText((typeResId != -1 ? getResources().getString(typeResId) : ""));
            holder.reason.setText(data.reason);
            return convertView;
        }
    }

    private View getTaskItemListViewHeader() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.worker_attendance_listview_view_row, null);
        ViewHolder holder = new ViewHolder(view);
        for (View divider : holder.dividerViews) {
            divider.setVisibility(View.INVISIBLE);
        }
        for (View divider : holder.horizontalDividerViews) {
            divider.setVisibility(View.VISIBLE);
        }
        ViewTreeObserver observer = view.getViewTreeObserver();
        if (sListViewHeaderHeight == 0 && observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    sListViewHeaderHeight = view.getHeight();
                    if (mAdapter != null && mAdapter.getCount() > 0) {
                        ViewGroup.LayoutParams params = mListView.getLayoutParams();
                        params.height = (int) (mAdapter.getCount() * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height)) + sListViewHeaderHeight;
                        mListView.requestLayout();
                        ((OverviewScrollView) getActivity().findViewById(R.id.scroll)).setScrollEnable(true);
                    }
                }
            });
        }
        sListViewHeaderHeight = view.getHeight();
        return view;
    }

    private static class ViewHolder {
        TextView date;
        TextView type;
        TextView reason;
        ArrayList<View> dividerViews = new ArrayList<>();
        ArrayList<View> horizontalDividerViews = new ArrayList<>();

        public ViewHolder(View view) {
            if (!(view instanceof LinearLayout)) return;
            LinearLayout root = (LinearLayout) view;
            date = (TextView) root.findViewById(R.id.worker_attendance_listview_date);
            type = (TextView) root.findViewById(R.id.worker_attendance_listview_type);
            reason = (TextView) root.findViewById(R.id.worker_attendance_listview_reason);
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                if (child.getId() == R.id.horozontal_divider) {
                    horizontalDividerViews.add(child);
                }
                if (!(child instanceof LinearLayout)) continue;
                LinearLayout secondRoot = (LinearLayout) child;
                for (int j = 0; j < secondRoot.getChildCount(); j++) {
                    if (secondRoot.getChildAt(j).getId() == R.id.listview_taskitem_divider) {
                        dividerViews.add(secondRoot.getChildAt(j));
                    }
                }
            }
        }
    }

    @Override
    public void onItemSelected(Object item) {
        Worker worker = (Worker) item;
        if (worker == null) return;
        ArrayList<LeaveData> leaveDatas = new ArrayList<>(worker.leaveDatas);
        Collections.sort(leaveDatas, new Comparator<LeaveData>() {
            @Override
            public int compare(LeaveData lhs, LeaveData rhs) {
                return rhs.date.compareTo(lhs.date);
            }
        });
        if (mAdapter == null) {
            mAdapter = new DateAdapter(leaveDatas);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(leaveDatas);
        }
        mAdapter.notifyDataSetChanged();
        if (mAdapter != null && mAdapter.getCount() > 0) {
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = (int) (mAdapter.getCount()
                    * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height))
                    + sListViewHeaderHeight;
            mListView.requestLayout();
        }
        ((OverviewScrollView) getActivity().findViewById(R.id.scroll)).setScrollEnable(true);
    }
}
