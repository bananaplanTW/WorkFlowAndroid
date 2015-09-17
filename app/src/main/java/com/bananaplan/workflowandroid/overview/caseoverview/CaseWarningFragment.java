package com.bananaplan.workflowandroid.overview.caseoverview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.TaskCase;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Warning;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.OverviewScrollView;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/23.
 */
public class CaseWarningFragment extends OvTabFragmentBase implements OvTabFragmentBase.OvCallBack {
    private ListView mWarningListView;
    private WarningListViewAdapter mWarningAdapter;
    private int mListViewHeaderHeight = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_case_ov_warning, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWarningListView = (ListView) getActivity().findViewById(R.id.case_listview_warning);
        mWarningListView.addHeaderView(getHeaderView(), null, false);
        onItemSelected(getSelectedTaskCase());
    }

    private ArrayList<Warning> getWarnings(TaskCase taskCase) {
        if (taskCase != null) {
            ArrayList<Warning> warnings = new ArrayList<>();
            for (Task item : taskCase.tasks) {
                for (Warning warning : item.warningList) {
                    warnings.add(warning);
                }
            }
            return warnings;
        }
        return new ArrayList<>();
    }

    private class WarningListViewAdapter extends ArrayAdapter<Warning> {
        public WarningListViewAdapter(ArrayList<Warning> warnings) {
            super(getActivity(), 0, warnings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.case_ov_warning_listview_itemview, parent, false);
                holder = new ViewHolder(convertView);
                final ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.height = (int) getResources().getDimension(R.dimen.ov_taskitem_listview_item_height);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position % 2 == 0) {
                convertView.setBackgroundColor(getResources().getColor(R.color.gray4));
            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }
            Warning warning = getItem(position);
            Task item = WorkingData.getInstance(getActivity()).getTaskItemById(warning.taskItemId);
            Worker worker = WorkingData.getInstance(getActivity()).getWorkerItemById(item.workerId);
            Utils.setTaskItemWarningTextView(getActivity(), warning, holder.warning);
            holder.title.setText(item.name);
            holder.responsibleWorkerName.setText(worker.name);
            holder.responsibleWorkerAvatar.setImageDrawable(worker.getAvator());
            if (warning.handle > 0) {
                holder.handleWorkerName.setText(WorkingData.getInstance(getActivity()).getWorkerItemById(warning.handle).name);
            } else {
                holder.handleWorkerName.setText("");
            }
            holder.time.setText("");
            holder.description.setText(TextUtils.isEmpty(warning.description) ? "" : warning.description);
            return convertView;
        }
    }

    private static class ViewHolder {
        private TextView warning;
        private TextView title;
        private ImageView responsibleWorkerAvatar;
        private TextView responsibleWorkerName;
        private TextView handleWorkerName;
        private TextView time;
        private TextView description;
        private LinearLayout responsibleWorkerInfo;
        private TextView responsibleWorkerString;
        ArrayList<View> dividerViews = new ArrayList<>();
        ArrayList<View> horizontalDividerViews = new ArrayList<>();

        public ViewHolder(View v) {
            warning = (TextView) v.findViewById(R.id.taskitem_listview_warning);
            title = (TextView) v.findViewById(R.id.title);
            responsibleWorkerAvatar = (ImageView) v.findViewById(R.id.resposible_worker_avator);
            responsibleWorkerName = (TextView) v.findViewById(R.id.resposible_worker_name);
            handleWorkerName = (TextView) v.findViewById(R.id.handle_worker);
            time = (TextView) v.findViewById(R.id.time);
            description = (TextView) v.findViewById(R.id.description);
            responsibleWorkerInfo = (LinearLayout) v.findViewById(R.id.resposible_worker_info);
            responsibleWorkerString = (TextView) v.findViewById(R.id.worker_name_string);
            if (!(v instanceof LinearLayout)) return;
            LinearLayout root = (LinearLayout) v;
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

    private View getHeaderView() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.case_ov_warning_listview_itemview, null);
        if (view == null) throw new NullPointerException();
        ViewHolder holder = new ViewHolder(view);
        holder.responsibleWorkerInfo.setVisibility(View.GONE);
        holder.responsibleWorkerString.setVisibility(View.VISIBLE);
        for (View divider : holder.dividerViews) {
            divider.setVisibility(View.INVISIBLE);
        }
        for (View divider : holder.horizontalDividerViews) {
            divider.setVisibility(View.VISIBLE);
        }
        ViewTreeObserver observer = view.getViewTreeObserver();
        if (mListViewHeaderHeight <= 0 && observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mListViewHeaderHeight = view.getHeight();
                    if (mWarningAdapter != null && mWarningAdapter.getCount() > 0) {
                        ViewGroup.LayoutParams params = mWarningListView.getLayoutParams();
                        params.height = (int) (mWarningAdapter.getCount() * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height))
                                + mListViewHeaderHeight
                                + mWarningListView.getDividerHeight() * (mWarningAdapter.getCount() - 1)
                                + mWarningListView.getPaddingTop();
                        mWarningListView.requestLayout();
                        ((OverviewScrollView) getActivity().findViewById(R.id.scroll)).setScrollEnable(true);
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onItemSelected(Object item) {
        if (item == null) return;
        TaskCase taskCase = (TaskCase) item;
        ArrayList<Warning> warnings = getWarnings(taskCase);
        if (mWarningAdapter == null) {
            mWarningAdapter = new WarningListViewAdapter(warnings);
            mWarningListView.setAdapter(mWarningAdapter);
        } else {
            mWarningAdapter.clear();
            mWarningAdapter.addAll(warnings);
        }
        mWarningAdapter.notifyDataSetChanged();
        if (mWarningAdapter != null && mWarningAdapter.getCount() > 0) {
            ViewGroup.LayoutParams params = mWarningListView.getLayoutParams();
            params.height = (int) (mWarningAdapter.getCount()
                    * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height))
                    + mListViewHeaderHeight + mWarningListView.getDividerHeight() * (mWarningAdapter.getCount() - 1);
            mWarningListView.requestLayout();
            ((OverviewScrollView) getActivity().findViewById(R.id.scroll)).setScrollEnable(true);
        }
    }

    @Override
    public Object getCallBack() {
        return this;
    }
}
