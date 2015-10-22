package com.bananaplan.workflowandroid.overview;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Equipment;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataTask;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.detail.DetailedWorkerActivity;
import com.bananaplan.workflowandroid.main.MainApplication;
import com.bananaplan.workflowandroid.overview.caseoverview.CaseOverviewFragment;
import com.bananaplan.workflowandroid.overview.equipmentoverview.EquipmentOverviewFragment;
import com.bananaplan.workflowandroid.overview.workeroverview.WorkerOverviewFragment;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.OverviewScrollView;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.data.BarChartData;
import com.bananaplan.workflowandroid.utility.view.AsyncDialog;
import com.bananaplan.workflowandroid.utility.view.DatePickerDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;

import it.sephiroth.android.library.widget.HListView;

/**
 * Created by Ben on 2015/8/14.
 */
public class TaskItemFragment extends OvTabFragmentBase implements View.OnClickListener,
        AdapterView.OnItemClickListener, OvTabFragmentBase.OvCallBack,
        it.sephiroth.android.library.widget.AdapterView.OnItemClickListener{

    public static final String FROM = "from";

    private String mFrom;
    private LinearLayout mBarChartContainer;
    private TextView mTvWorkingHours;
    private TextView mTvOvertimeHours;
    private ListView mTaskItemListView;
    private HListView mWorkerListView;
    private AsyncDialog mAsyncDialog;

    private TaskItemListViewAdapter mTaskItemListViewAdapter;
    private WorkerItemListViewAdapter mWorkerItemListViewAdapter;
    private int mTaskItemListViewHeaderHeight = 0;
    private long mChartStartDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_ov_taskitem, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mFrom = getArguments().getString(FROM, "");
        }
        if (TextUtils.isEmpty(mFrom)) throw new IllegalArgumentException("FROM parameter is null");
        mBarChartContainer = (LinearLayout) getActivity().findViewById(R.id.ov_statistics_chart_container);
        mTvWorkingHours = (TextView) getActivity().findViewById(R.id.ov_statistics_working_hour_tv);
        mTvOvertimeHours = (TextView) getActivity().findViewById(R.id.ov_statistics_overtime_hour_tv);
        getActivity().findViewById(R.id.edit_task).setOnClickListener(this);
        getActivity().findViewById(R.id.ov_statistics_week_chooser).setOnClickListener(this);
        updateChartStartDate(Calendar.getInstance());
        updateWeekPickerTextView();
        mTaskItemListView = (ListView) getActivity().findViewById(R.id.tasks);
        mTaskItemListView.setOnItemClickListener(this);
        mTaskItemListView.addHeaderView(getTaskItemListViewHeader(), null, false);
        if (mFrom.equals(EquipmentOverviewFragment.class.getSimpleName())) {
            getActivity().findViewById(R.id.ov_statistics_overtime_hour_vg).setVisibility(View.GONE);
            getActivity().findViewById(R.id.edit_task).setVisibility(View.GONE);
            getActivity().findViewById(R.id.workers_list_vg).setVisibility(View.GONE);
            ((LinearLayout.LayoutParams) mBarChartContainer.getLayoutParams()).topMargin =
                    getResources().getDimensionPixelOffset(R.dimen.case_ov_statistics_margin_top);
            onItemSelected(getSelectedEquipment());
        } else if (mFrom.equals(WorkerOverviewFragment.class.getSimpleName())) {
            getActivity().findViewById(R.id.ov_statistics_overtime_hour_vg).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.workers_list_vg).setVisibility(View.GONE);
            ((LinearLayout.LayoutParams) mBarChartContainer.getLayoutParams()).topMargin =
                    getResources().getDimensionPixelOffset(R.dimen.worker_ov_statistics_margin_top);
            onItemSelected(getSelectedWorker());
        } else if (mFrom.equals(CaseOverviewFragment.class.getSimpleName())) {
            getActivity().findViewById(R.id.ov_statistics_overtime_hour_vg).setVisibility(View.GONE);
            getActivity().findViewById(R.id.edit_task).setVisibility(View.GONE);
            getActivity().findViewById(R.id.workers_list_vg).setVisibility(View.VISIBLE);
            mWorkerListView = (HListView) getActivity().findViewById(R.id.workers);
            mWorkerListView.setOnItemClickListener(this);
            ((LinearLayout.LayoutParams) mBarChartContainer.getLayoutParams()).topMargin =
                    getResources().getDimensionPixelOffset(R.dimen.case_ov_statistics_margin_top);
            onItemSelected(getSelectedTaskCase());
        } else if (mFrom.equals(DetailedWorkerActivity.class.getSimpleName())) {
            getActivity().findViewById(R.id.workers_list_vg).setVisibility(View.GONE);
            getActivity().findViewById(R.id.edit_task).setVisibility(View.GONE);
            getActivity().findViewById(R.id.upper).setVisibility(View.GONE);
            getActivity().findViewById(R.id.current_task_items).setVisibility(View.VISIBLE);
            onItemSelected(getSelectedWorker());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAsyncDialog != null) {
            mAsyncDialog.clearPendingProgressDialog();
        }
    }

    AsyncDialog getAsyncDialog() {
        if (mAsyncDialog == null) {
            mAsyncDialog = new AsyncDialog(getActivity());
        }
        return mAsyncDialog;
    }

    private void updateChartStartDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.add(Calendar.DAY_OF_WEEK, (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 * (-1));
        mChartStartDate = cal.getTimeInMillis();
    }

    private void updateWeekPickerTextView() {
        TextView tv = (TextView) getActivity().findViewById(R.id.ov_statistics_week_chooser_date);
        if (tv == null) return;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mChartStartDate);
        String date = cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + " - ";
        cal.setTimeInMillis(cal.getTimeInMillis() + 86400000 * 7 - 1);
        date += cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH);
        tv.setText(date);
    }

    private int getItemViewLayoutId() {
        if (TextUtils.isEmpty(mFrom)) return -1;
        if (mFrom.equals(EquipmentOverviewFragment.class.getSimpleName())) {
            return R.layout.ov_taskitem_list_view_itemview_equipment;
        } else if (mFrom.equals(WorkerOverviewFragment.class.getSimpleName())
                || mFrom.equals(DetailedWorkerActivity.class.getSimpleName())) {
            return R.layout.ov_taskitem_list_view_itemview_worker;
        } else if (mFrom.equals(CaseOverviewFragment.class.getSimpleName())) {
            return R.layout.ov_taskitem_list_view_itemview_case;
        }
        return -1;
    }

    private View getTaskItemListViewHeader() {
        final View view = getActivity().getLayoutInflater().inflate(getItemViewLayoutId(), null);
        TaskItemListViewAdapterViewHolder holder = new TaskItemListViewAdapterViewHolder(view, true);
        if (holder.workerNameString != null) {
            holder.workerNameString.setVisibility(View.VISIBLE);
        }
        if (holder.workerInfo != null) {
            holder.workerInfo.setVisibility(View.GONE);
        }
        ViewTreeObserver observer = view.getViewTreeObserver();
        if (mTaskItemListViewHeaderHeight == 0 && observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mTaskItemListViewHeaderHeight = view.getHeight();
                    if (mTaskItemListViewAdapter != null) {
                        ViewGroup.LayoutParams params = mTaskItemListView.getLayoutParams();
                        params.height = (int) (mTaskItemListViewAdapter.getCount()
                                * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height))
                                + mTaskItemListViewHeaderHeight;
                        mTaskItemListView.requestLayout();
                        if ((getActivity().findViewById(R.id.scroll)) != null) {
                            ((OverviewScrollView) getActivity().findViewById(R.id.scroll)).setScrollEnable(true);
                        }
                    }
                }
            });
        }
        return view;
    }

    private class TaskItemListViewAdapter extends ArrayAdapter<Task> {
        public TaskItemListViewAdapter(ArrayList<Task> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskItemListViewAdapterViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(getItemViewLayoutId(), parent, false);
                holder = new TaskItemListViewAdapterViewHolder(convertView, false);
                convertView.setTag(holder);
                final ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.height = (int) getResources().getDimension(R.dimen.ov_taskitem_listview_item_height);
                if (position % 2 == 0) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.gray4));
                } else {
                    convertView.setBackgroundColor(Color.WHITE);
                }
                if (holder.workerNameString != null) {
                    holder.workerNameString.setVisibility(View.GONE);
                }
                if (holder.workerInfo != null) {
                    holder.workerInfo.setVisibility(View.VISIBLE);
                }
            } else {
                holder = (TaskItemListViewAdapterViewHolder) convertView.getTag();
            }
            final Task task = getItem(position);
            final Worker worker = WorkingData.getInstance(getActivity()).getWorkerById(task.workerId);

            int txtColor;
            if (task.status == Task.Status.DONE) {
                txtColor = getResources().getColor(R.color.gray1);
            } else {
                txtColor = getResources().getColor(R.color.black1);
            }

            if (holder.startDate != null) {
                holder.startDate.setText(Utils.timestamp2Date(task.startDate, Utils.DATE_FORMAT_MD));
                holder.startDate.setTextColor(txtColor);
            }
            if (holder.status != null) {
                holder.status.setText(Task.getTaskStatusString(getActivity(), task));
                holder.status.setTextColor(txtColor);
                if (Task.Status.WIP == task.status) {
                    holder.status.setBackground(getResources().getDrawable(R.drawable.border_textview_bg_green, null));
                    holder.status.setTextColor(getResources().getColor(R.color.green));
                } else {
                    holder.status.setBackground(null);
                }
            }
            if (holder.id != null) {
                holder.id.setText(String.valueOf(position + 1));
                holder.id.setTextColor(txtColor);
            }
            if (holder.caseName != null) {
                holder.caseName.setText(WorkingData.getInstance(getActivity()).hasCase(task.caseId) ?
                        WorkingData.getInstance(getActivity()).getCaseById(task.caseId).name : "");
                holder.caseName.setTextColor(txtColor);
            }
            if (holder.itemName != null) {
                holder.itemName.setText(task.name);
                holder.itemName.setTextColor(txtColor);
            }
            if (holder.expectedTime != null) {
                holder.expectedTime.setText(Utils.millisecondsToTimeString(task.expectedTime));
                holder.expectedTime.setTextColor(txtColor);
            }
            if (holder.workTime != null) {
                holder.workTime.setText(Utils.millisecondsToTimeString(task.getWorkingTime()));
                holder.workTime.setTextColor(txtColor);
            }
            if (holder.equipment != null) {
                holder.equipment.setText(WorkingData.getInstance(getActivity()).hasEquipment(task.equipmentId) ?
                        WorkingData.getInstance(getActivity()).getEquipmentById(task.equipmentId).name : "");
                holder.equipment.setTextColor(txtColor);
            }
            if (holder.errorCount != null) {
                holder.errorCount.setText(String.valueOf(task.errorCount));
                holder.errorCount.setTextColor(txtColor);
            }
            if (worker != null) {
                if (holder.workerName != null) {
                    holder.workerName.setText(worker.name);
                }
                if (holder.workerAvatar != null) {
                    holder.workerAvatar.setImageDrawable(worker.getAvator());
                }
                if (holder.workerInfo != null) {
                    holder.workerInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "View worker's info = " + worker.name, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            if (holder.warning != null) {
                Utils.setTaskItemWarningTextView(getActivity(), task, holder.warning, true);
            }
            return convertView;
        }
    }

    public class TaskItemListViewAdapterViewHolder {
        TextView id;
        TextView startDate;
        TextView status;
        TextView caseName;
        TextView itemName;
        TextView expectedTime;
        TextView workTime;
        TextView equipment;
        TextView errorCount;
        TextView warning;
        TextView workerNameString;
        TextView workerName;
        ImageView workerAvatar;
        LinearLayout workerInfo;

        public TaskItemListViewAdapterViewHolder(View view, boolean header) {
            if (!(view instanceof LinearLayout)) return;
            LinearLayout root = (LinearLayout) view;
            id = (TextView) view.findViewById(R.id.id);
            startDate = (TextView) view.findViewById(R.id.start_date);
            status = (TextView) view.findViewById(R.id.status);
            caseName = (TextView) view.findViewById(R.id.case_name);
            itemName = (TextView) view.findViewById(R.id.item_name);
            expectedTime = (TextView) view.findViewById(R.id.expected_time);
            workTime = (TextView) view.findViewById(R.id.work_time);
            equipment = (TextView) view.findViewById(R.id.equipment_used);
            errorCount = (TextView) view.findViewById(R.id.error_count);
            warning = (TextView) view.findViewById(R.id.warning);
            workerNameString = (TextView) view.findViewById(R.id.worker_name_string);
            workerName = (TextView) view.findViewById(R.id.worker_card_name);
            workerAvatar = (ImageView) view.findViewById(R.id.worker_card_avatar);
            workerInfo = (LinearLayout) view.findViewById(R.id.worker_info);
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                if (child.getId() == R.id.horozontal_divider) {
                    if (header) {
                        child.setVisibility(View.VISIBLE);
                    } else {
                        child.setVisibility(View.GONE);
                    }
                }
                if (!(child instanceof LinearLayout)) continue;
                LinearLayout secondRoot = (LinearLayout) child;
                for (int j = 0; j < secondRoot.getChildCount(); j++) {
                    if (secondRoot.getChildAt(j).getId() == R.id.listview_taskitem_divider) {
                        if (header) {
                            secondRoot.getChildAt(j).setVisibility(View.INVISIBLE);
                        } else {
                            secondRoot.getChildAt(j).setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ov_statistics_week_chooser:
                showDatePicker();
                break;
            case R.id.edit_task:
                Toast.makeText(getActivity(), "Edit item", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showDatePicker() {
        FragmentManager fm = getActivity().getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prevDatePickerDialog = fm.findFragmentByTag("date_picker");
        if (prevDatePickerDialog != null) {
            ft.remove(prevDatePickerDialog);
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mChartStartDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialogFragment.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar chosenCal = Calendar.getInstance();
                chosenCal.set(Calendar.YEAR, year);
                chosenCal.set(Calendar.MONTH, monthOfYear);
                chosenCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateChartStartDate(chosenCal);
                updateWeekPickerTextView();
                updateStatisticsView();
            }

        }, year, month, day).show(ft, "date_picker");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onItemSelected(Object item) {
        if (item == null) return;
        if (mFrom.equals(EquipmentOverviewFragment.class.getSimpleName())) {
            onEquipmentSelected((Equipment) item);
        } else if (mFrom.equals(WorkerOverviewFragment.class.getSimpleName())
                || mFrom.equals(DetailedWorkerActivity.class.getSimpleName())) {
            onWorkerSelected((Worker) item);
        } else if (mFrom.equals(CaseOverviewFragment.class.getSimpleName())) {
            onCaseSelected((Case) item);
        }
        if (mTaskItemListViewAdapter != null) {
            ViewGroup.LayoutParams params = mTaskItemListView.getLayoutParams();
            params.height = (int) (mTaskItemListViewAdapter.getCount()
                    * getResources().getDimension(R.dimen.ov_taskitem_listview_item_height))
                    + mTaskItemListViewHeaderHeight;
            mTaskItemListView.requestLayout();
        }
    }

    private void onCaseSelected(Case aCase) {
        // update statistics
        updateStatisticsView(aCase, R.string.overview_finish_hours);

        // update task item listView
        ArrayList<Task> items = new ArrayList<>(aCase.tasks);
        if (mTaskItemListViewAdapter == null) {
            mTaskItemListViewAdapter = new TaskItemListViewAdapter(items);
            mTaskItemListView.setAdapter(mTaskItemListViewAdapter);
        } else {
            mTaskItemListViewAdapter.clear();
            mTaskItemListViewAdapter.addAll(items);
        }

        ArrayList<Worker> workers = getWorkerItems();
        if (mWorkerItemListViewAdapter == null) {
            mWorkerItemListViewAdapter = new WorkerItemListViewAdapter(getActivity(), workers);
            mWorkerListView.setAdapter(mWorkerItemListViewAdapter);
        } else {
            mWorkerItemListViewAdapter.clear();
            mWorkerItemListViewAdapter.addAll(workers);
        }
        mWorkerItemListViewAdapter.notifyDataSetChanged();
    }

    private void onWorkerSelected(Worker worker) {
        // update statistics
        if (!mFrom.equals(DetailedWorkerActivity.class.getSimpleName())) {
            updateStatisticsView(worker, R.string.overview_working_hours);
        }

        // update task item listView
        ArrayList<Task> items = new ArrayList<>(worker.getScheduledTasks());
        if (worker.getWipTask() != null) {
            items.add(0, worker.getWipTask());
        }
        if (mTaskItemListViewAdapter == null) {
            mTaskItemListViewAdapter = new TaskItemListViewAdapter(items);
            mTaskItemListView.setAdapter(mTaskItemListViewAdapter);
        } else {
            mTaskItemListViewAdapter.clear();
            mTaskItemListViewAdapter.addAll(items);
        }
    }

    private void onEquipmentSelected(Equipment equipment) {
        // update statistics
        updateStatisticsView(equipment, R.string.overview_used_hours);

        // update task item listView
        ArrayList<Task> items = WorkingData.getInstance(getActivity()).getTasksByEquipment(equipment);
        if (mTaskItemListViewAdapter == null) {
            mTaskItemListViewAdapter = new TaskItemListViewAdapter(items);
            mTaskItemListView.setAdapter(mTaskItemListViewAdapter);
        } else {
            mTaskItemListViewAdapter.clear();
            mTaskItemListViewAdapter.addAll(items);
        }
    }

    private ArrayList<Worker> getWorkerItems() {
        ArrayList<Worker> workers = new ArrayList<>();
        if (getSelectedTaskCase() != null) {
            for (Task item : getSelectedTaskCase().tasks) {
                if (TextUtils.isEmpty(item.workerId)) continue;
                Worker worker = WorkingData.getInstance(getActivity()).getWorkerById(item.workerId);
                if (workers.contains(worker)) continue;
                workers.add(worker);
            }
        }
        return workers;
    }

    private void updateStatisticsView() {
        Object obj = null;
        int resId = -1;
        if (mFrom.equals(EquipmentOverviewFragment.class.getSimpleName())) {
            obj = getSelectedEquipment();
            resId = R.string.overview_used_hours;
        } else if (mFrom.equals(WorkerOverviewFragment.class.getSimpleName())
                || mFrom.equals(DetailedWorkerActivity.class.getSimpleName())) {
            obj = getSelectedWorker();
            resId = R.string.overview_working_hours;
        } else if (mFrom.equals(CaseOverviewFragment.class.getSimpleName())) {
            obj = getSelectedTaskCase();
            resId = R.string.overview_finish_hours;
        }
        updateStatisticsView(obj, resId);
    }

    private void updateStatisticsView(final Object obj, final int resId) {
        getAsyncDialog().runAsync(new Runnable() {
            @Override
            public void run() {
                if (MainApplication.sUseTestData) return;
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.clear(Calendar.MINUTE);
                cal.clear(Calendar.SECOND);
                cal.clear(Calendar.MILLISECOND);
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                long start = cal.getTimeInMillis();
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                long end = cal.getTimeInMillis();
                if (obj instanceof Case) {
                    LoadingDataUtils.loadTimeCardsByCase(getActivity(), ((Case) obj).id, start, end);
                } else if (obj instanceof Worker) {
                    LoadingDataUtils.loadTimeCardsByWorker(getActivity(), ((Worker) obj).id, start, end);
                } else if (obj instanceof Equipment) {
                    LoadingDataUtils.loadTimeCardsByEquipment(getActivity(), ((Equipment) obj).id, start, end);
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                BarChartData data = new BarChartData(mFrom);
                long start = mChartStartDate;
                long end = mChartStartDate + 86400000 * 7;
                if (obj instanceof Case) {
                    data.setData(((Case) obj).getBarChartData(start, end));
                } else if (obj instanceof Worker) {
                    data.setData(((Worker) obj).getBarChartData(getActivity(), start, end));
                } else if (obj instanceof Equipment) {
                    data.setData(((Equipment) obj).getBarChartData(start, end));
                }
                data.setStartDate(getActivity(), start);
                mBarChartContainer.removeAllViews();
                View barChartView = Utils.genBarChart(getActivity(), data);
                if (barChartView != null) {
                    mBarChartContainer.addView(barChartView,
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                }
                mTvWorkingHours.setText(getResources().getString(resId, data.getWorkingHours()));
                mTvOvertimeHours.setText(getResources().getString(R.string.overview_overtime_hours, data.getOvertimeHours()));
            }
        }, R.string.processing);
    }

    private class WorkerItemListViewAdapter extends ArrayAdapter<Worker> {
        private LayoutInflater mInflater;

        public WorkerItemListViewAdapter(Context context, ArrayList<Worker> items) {
            super(context, 0, items);
            mInflater = getActivity().getLayoutInflater();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ov_workers_listview_itemview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Worker worker = getItem(position);
            holder.avator.setImageDrawable(worker.getAvator());
            return convertView;
        }

        private class ViewHolder {
            ImageView avator;

            public ViewHolder(View view) {
                avator = (ImageView) view.findViewById(R.id.case_workers_listview_item_iv);
            }
        }
    }

    @Override
    public Object getCallBack() {
        return this;
    }

    @Override
    public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> adapterView,
                            View view, int i, long l) {
        Toast.makeText(getActivity(), "View worker's info = "
                + mWorkerItemListViewAdapter.getItem(i).name, Toast.LENGTH_SHORT).show();
    }
}
