package com.bananaplan.workflowandroid.detail;

import android.content.Context;
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
import com.bananaplan.workflowandroid.utility.Utils;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.List;


/**
 * Task schedule of a worker
 *
 * @author Danny Lin
 * @since 2015/9/21.
 */
public class TaskScheduleFragment extends Fragment implements View.OnClickListener {

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
        mWorker = WorkingData.getInstance(getActivity()).getWorkerById(getArguments()
                .getString(DetailedWorkerActivity.EXTRA_WORKER_ID));
        findViews();
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
        ArrayList<Task> data = new ArrayList<>(mWorker.scheduledTasks);
        mAdapter = new TaskAdapter(data);
        mListView.setAdapter(mAdapter);
        mListView.setDropListener(mAdapter);
        mController = buildController(mListView);
        mListView.setFloatViewManager(mController);
        mListView.setOnTouchListener(mController);
        mListView.setDragEnabled(true);
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

        public TaskAdapter(ArrayList<Task> items) {
            super(getActivity(), 0, items);
            mData = items;
            calDivPos();
        }

        private void calDivPos() {
            mDivPos = mData.size() / 2; // TODO
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
            return false;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return mData.size() > 0 ? mData.size() + 1 : 0;
        }

        @Override
        public boolean isEnabled(int position) {
            return position != mDivPos;
        }

        public int getDivPosition() {
            return mDivPos;
        }

        @Override
        public Task getItem(int position) {
            if (position != mDivPos) {
                return mData.get(dataPosition(position));
            }
            return null;
        }

        private int dataPosition(int position) {
            return position > mDivPos ? position - 1 : position;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mDivPos) {
                return SECTION_DIV;
            } else {
                return SECTION_DATA;
            }
        }

        @Override
        public void drop(int from, int to) {
            if (from != to) {
                if (to != mDivPos) {
                    Task task = mData.remove(dataPosition(from));
                    mData.add(dataPosition(to), task);
                    mWorker.scheduledTasks.clear();
                    mWorker.scheduledTasks.addAll(mData);
                }
                if (from < mDivPos && to > mDivPos) {
                    mDivPos--;
                } else if (from > mDivPos && to < mDivPos) {
                    mDivPos++;
                } else if (mDivPos == to) {
                    mDivPos = from;
                }
                notifyDataSetChanged();
            }
        }
    }

    private void setListViewItemContent(ViewHolder holder, Task task) {
        holder.cas.setText(WorkingData.getInstance(getActivity()).getCaseById(task.caseId).name);
        holder.task.setText(task.name);
        holder.expectedTime.setText(Utils.millisecondsToTimeString(task.expectedTime));
        holder.workTime.setText(Utils.millisecondsToTimeString(task.getWorkingTime()));
        holder.equipment.setText(WorkingData.getInstance(getActivity()).hasEquipment(task.equipmentId) ?
                WorkingData.getInstance(getActivity()).getEquipmentById(task.equipmentId).name : "");
//        holder.expectedCompleteTime.setText(task.getExpectedFinishedTime());
        holder.errorCnt.setText(String.valueOf(task.errorCount));
        Utils.setTaskItemWarningTextView(getActivity(), task, holder.warnings, true);
    }

    private void setupCurrentTask() {
        new ViewHolder(getActivity().findViewById(R.id.header), true, true);

        ViewHolder holder = new ViewHolder(getActivity().findViewById(R.id.detailed_worker_current_task), false, true);
        if (!mWorker.hasWipTask()) return;
        setListViewItemContent(holder, mWorker.wipTask);
    }

    private class ViewHolder {
        View view;
        TextView id;
        TextView cas;
        TextView task;
        TextView expectedTime;
        TextView workTime;
        TextView equipment;
        TextView expectedCompleteTime;
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
            expectedCompleteTime = (TextView) root.findViewById(R.id.detailed_worker_task_schedule_expected_completed_time);
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
                expectedCompleteTime.setText(getString(R.string.detailed_worker_task_schedule_header_expected_completed_time));
                expectedCompleteTime.setTextAppearance(getActivity(), R.style.DetailedWorkerTaskSchedule_Header);
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
                break;
            case R.id.add_warning_button:
                break;
            case R.id.manage_warning_button:
                break;
            case R.id.manage_tasks_button:
                break;
        }
    }
}
