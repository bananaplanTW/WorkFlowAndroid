package com.bananaplan.workflowandroid.detail.worker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.worker.actions.CompleteTaskForWorkerCommand;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.warning.AddWarningDialog;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Task schedule of a worker
 *
 * @author Danny Lin
 * @since 2015/9/21.
 */
public class TaskScheduleFragment extends Fragment implements View.OnClickListener {

    private static final int CREATE_WARNING = 10001;

    private Context mContext;

    private Worker mWorker;
    private DragSortListView mListView;
    private TaskAdapter mAdapter;
    private DragSortController mController;
    private ViewHolder mListViewHeaderHolder;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    private void initialize() {
        mContext = getContext();
        mWorker = WorkingData.getInstance(getActivity()).getWorkerById(getArguments()
                .getString(DetailedWorkerActivity.EXTRA_WORKER_ID));
        findViews();
        setupCurrentTaskHeader();
        setupCurrentTask();
    }

    private void findViews() {
        getActivity().findViewById(R.id.complete_task_button).setOnClickListener(this);
        getActivity().findViewById(R.id.add_warning_button).setOnClickListener(this);
        getActivity().findViewById(R.id.manage_warning_button).setOnClickListener(this);
        getActivity().findViewById(R.id.manage_tasks_button).setOnClickListener(this);
        mListView = (DragSortListView) getActivity().findViewById(R.id.task_list);
        mListViewHeaderHolder = new ViewHolder(
                getActivity().getLayoutInflater().inflate(
                        R.layout.detailed_worker_task_schedule_item, null),
                true,
                false);
        mListView.addHeaderView(mListViewHeaderHolder.getView());
        ArrayList<Task> data = new ArrayList<>(mWorker.getScheduledTasks());
        mAdapter = new TaskAdapter(data);
        mListView.setAdapter(mAdapter);
        mListView.setDropListener(mAdapter);
        mController = buildController(mListView);
        mListView.setFloatViewManager(mController);
        mListView.setOnTouchListener(mController);
        mListView.setDragEnabled(!mAdapter.isDivEnable());
    }

    private DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv) {
            @Override
            public int startDragPosition(MotionEvent ev) {
                if (super.dragHandleHitPosition(ev) == mAdapter.getDivPosition() + 1) {
                    return DragSortController.MISS;
                } else {
                    return super.startDragPosition(ev);
                }
            }
        };
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
        controller.setBackgroundColor(Color.TRANSPARENT);
        return controller;
    }

    private class TaskAdapter extends ArrayAdapter<Task> implements DragSortListView.DropListener {

        private final static int SECTION_DIV = 0;
        private final static int SECTION_DATA = 1;

        private List<Task> mData;
        private int mDivPos;
        private boolean mDivEnable = true;

        public TaskAdapter(ArrayList<Task> items) {
            super(getActivity(), 0, items);
            setupDataSet(items);
        }

        public void updateData (List<Task> data) {
            setupDataSet(data);
            notifyDataSetChanged();
        }

        private void setupDataSet(List<Task> data) {
            mData = data;
            calDivPos();
        }

        private void calDivPos() {
            if (mWorker.hasWipTask()) {
                mDivPos = 0;
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, mWorker.isOvertime ? WorkingData.getInstance(getActivity()).hourOvertime : WorkingData.getInstance(getActivity()).hourWorkingOff);
                calendar.set(Calendar.MINUTE, mWorker.isOvertime ? WorkingData.getInstance(getActivity()).minOvertime : WorkingData.getInstance(getActivity()).minWorkingOff);
                long availableTime = calendar.getTimeInMillis() - System.currentTimeMillis();
                if (mWorker.getWipTask().expectedTime - mWorker.getWipTask().getWorkingTime() > availableTime) {
                    return;
                } else {
                    for (int i = 0; i < mData.size(); i++) {
                        Task task = mData.get(i);
                        availableTime -= (task.expectedTime - task.getWorkingTime());
                        if (availableTime >= 0) {
                            mDivPos = (i + 1);
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int type = getItemViewType(position);
            if (type == SECTION_DIV) {
                return getActivity().getLayoutInflater().inflate(
                        R.layout.detailed_worker_task_schedule_item_horizontal_divider, parent, false);
            } else {
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder(
                            getActivity().getLayoutInflater().inflate(
                                    R.layout.detailed_worker_task_schedule_item, parent, false),
                            false,
                            false);
                    convertView = holder.getView();
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                Task task = getItem(position);
                holder.id.setText(String.valueOf(dataPosition(position) + 1));
                setListViewItemContent(holder, task);
                return convertView;
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            if (!mDivEnable) return true;
             else return false;
        }

        @Override
        public int getViewTypeCount() {
            if (!mDivEnable) return 1;
            else return 2;
        }

        @Override
        public int getCount() {
            return mData.size() > 0 ? (mDivEnable ? mData.size() + 1 : mData.size()) : 0;
        }

        @Override
        public boolean isEnabled(int position) {
            if (!mDivEnable) return true;
            else return position != mDivPos;
        }

        public int getDivPosition() {
            if (!mDivEnable) return -1;
            return mDivPos;
        }

        @Override
        public Task getItem(int position) {
            if (!mDivEnable) return mData.get(dataPosition(position));
            if (position != mDivPos) {
                return mData.get(dataPosition(position));
            }
            return null;
        }

        private int dataPosition(int position) {
            if (mDivEnable) {
                return position > mDivPos ? position - 1 : position;
            } else {
                return position;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (!mDivEnable) return SECTION_DATA;
            if (position == mDivPos) {
                return SECTION_DIV;
            } else {
                return SECTION_DATA;
            }
        }

        @Override
        public void drop(int from, int to) {
            if (from != to) {
                Task task = mData.remove(dataPosition(from));
                mData.add(dataPosition(to), task);
                mWorker.clearScheduleTasks();
                mWorker.addAllScheduleTasks(mData);
                calDivPos();
                notifyDataSetChanged();
            }
        }

        public void setDivEnable(boolean enable) {
            if (mDivEnable == enable) return;
            mDivEnable = enable;
            notifyDataSetChanged();
        }

        public boolean isDivEnable() {
            return mDivEnable;
        }
    }

    private void setListViewItemContent(ViewHolder holder, Task task) {
        if (task == null) return;
        holder.cas.setText(WorkingData.getInstance(getActivity()).getCaseById(task.caseId).name);
        holder.task.setText(task.name);
        holder.expectedTime.setText(Utils.millisecondsToTimeString(task.expectedTime));
        holder.workTime.setText(Utils.millisecondsToTimeString(task.getWorkingTime()));
        holder.equipment.setText(WorkingData.getInstance(getActivity()).hasEquipment(task.equipmentId) ?
                WorkingData.getInstance(getActivity()).getEquipmentById(task.equipmentId).name : "");
        holder.errorCnt.setText(String.valueOf(task.errorCount));
        Utils.setTaskItemWarningTextView(getActivity(), task, holder.warnings, true);
    }
    private void resetListViewItemContent (ViewHolder holder) {
        holder.cas.setText("");
        holder.task.setText("");
        holder.expectedTime.setText("");
        holder.workTime.setText("");
        holder.equipment.setText("");
        holder.errorCnt.setText("");
        holder.warnings.setText("");
    }

    private void setupCurrentTaskHeader() {
        new ViewHolder(getActivity().findViewById(R.id.header), true, true);
    }
    private void setupCurrentTask() {
        ViewHolder holder = new ViewHolder(getActivity().findViewById(R.id.detailed_worker_current_task), false, true);
        if (mWorker.hasWipTask()) {
            setListViewItemContent(holder, mWorker.getWipTask());
        } else {
            resetListViewItemContent(holder);
            holder.view.setBackgroundColor(Color.GRAY);
        }
    }

    private class ViewHolder {
        View view;
        TextView id;
        TextView cas;
        TextView task;
        TextView expectedTime;
        TextView workTime;
        TextView equipment;
        TextView errorCnt;
        TextView warnings;
        LinearLayout idRoot;
        LinearLayout taskRoot;

        public ViewHolder(View root, boolean header, boolean current) {
            view = root;
            id = (TextView) root.findViewById(R.id.id);
            idRoot = (LinearLayout) root.findViewById(R.id.id_root);
            cas = (TextView) root.findViewById(R.id.detailed_worker_task_schedule_case);
            task = (TextView) root.findViewById(R.id.detailed_worker_task_schedule_task);
            expectedTime = (TextView) root.findViewById(R.id.detailed_worker_task_schedule_expected_time);
            workTime = (TextView) root.findViewById(R.id.detailed_worker_task_schedule_spent_time);
            equipment = (TextView) root.findViewById(R.id.detailed_worker_task_schedule_equipment);
            errorCnt = (TextView) root.findViewById(R.id.detailed_worker_task_schedule_error);
            warnings = (TextView) root.findViewById(R.id.listview_task_warning);
            taskRoot = (LinearLayout) root.findViewById(R.id.task_item_root);
            if (current) {
                idRoot.setVisibility(View.GONE);
            }
            if (header) {
                if (root.getLayoutParams() != null) {
                    root.getLayoutParams().height = (int) getResources().getDimension(R.dimen.detailed_worker_task_schedule_header_height);
                }
                taskRoot.setBackground(null);
                for (int i = 0; i < taskRoot.getChildCount(); i++) {
                    if (taskRoot.getChildAt(i).getId() == R.id.divider) {
                        taskRoot.getChildAt(i).setVisibility(View.INVISIBLE);
                    }
                }
                cas.setText(getString(R.string.detailed_worker_task_schedule_header_case));
                cas.setTextAppearance(getActivity(), R.style.DetailedWorkerTaskSchedule_Header);
                task.setText(getString(R.string.detailed_worker_task_schedule_header_task));
                task.setTextAppearance(getActivity(), R.style.DetailedWorkerTaskSchedule_Header);
                expectedTime.setText(getString(R.string.detailed_worker_task_schedule_header_expected_time));
                expectedTime.setTextAppearance(getActivity(), R.style.DetailedWorkerTaskSchedule_Header);
                workTime.setText(getString(R.string.detailed_worker_task_schedule_header_work_time));
                workTime.setTextAppearance(getActivity(), R.style.DetailedWorkerTaskSchedule_Header);
                equipment.setText(getString(R.string.detailed_worker_task_schedule_header_equipment));
                equipment.setTextAppearance(getActivity(), R.style.DetailedWorkerTaskSchedule_Header);
                errorCnt.setText(getString(R.string.detailed_worker_task_schedule_header_error));
                errorCnt.setTextAppearance(getActivity(), R.style.DetailedWorkerTaskSchedule_Header);
                warnings.setText(getString(R.string.detailed_worker_task_schedule_header_warnings));
                warnings.setTextAppearance(getActivity(), R.style.DetailedWorkerTaskSchedule_Header);
            } else {
                if (root.getLayoutParams() != null) {
                    root.getLayoutParams().height = (int) getResources().getDimension(R.dimen.detailed_worker_task_schedule_item_height);
                }
                warnings.setText("");
            }
        }

        public View getView() {
            return view;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complete_task_button:
                if (mWorker.hasWipTask()) {
                    String taskId = mWorker.getWipTask().id;
                    CompleteTaskForWorkerCommand completeTaskForWorkerCommand = new CompleteTaskForWorkerCommand(mContext, mWorker.id, taskId);
                    completeTaskForWorkerCommand.execute();
                    WorkingData.getInstance(mContext).getTaskById(taskId).status = Task.Status.IN_REVIEW;

                    if (mWorker.hasScheduledTasks()) {
                        Task task = mWorker.getScheduledTasks().get(0);
                        mWorker.setWipTask(task);
                        mWorker.removeScheduleTask(task);
                    } else {
                        mWorker.status = Worker.Status.PENDING;
                        mWorker.setWipTask(null);
                    }
                    setupCurrentTask();
                    mAdapter.updateData((mWorker.getScheduledTasks()));
                }
                break;
            case R.id.add_warning_button:
                if (mWorker.hasWipTask()) {
                    Intent intent = new Intent(mContext, AddWarningDialog.class);
                    intent.putExtra(AddWarningDialog.EXTRA_TASK_ID, mWorker.getWipTask().id);
                    startActivityForResult(intent, CREATE_WARNING);
                }
                break;
            case R.id.manage_warning_button:
                break;
            case R.id.manage_tasks_button:
                if (mAdapter == null) return;
                mAdapter.setDivEnable(!mAdapter.isDivEnable());
                mListView.setDragEnabled(!mAdapter.isDivEnable());
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREATE_WARNING:
                if (resultCode == Activity.RESULT_OK) {
                    if (mWorker.hasScheduledTasks()) {
                        Task task = mWorker.getScheduledTasks().get(0);
                        mWorker.setWipTask(task);
                        mWorker.removeScheduleTask(task);
                    } else {
                        mWorker.status = Worker.Status.PENDING;
                        mWorker.setWipTask(null);
                    }
                    setupCurrentTask();
                    mAdapter.updateData((mWorker.getScheduledTasks()));
                }
                break;
            default:
                break;
        }
    }
}
