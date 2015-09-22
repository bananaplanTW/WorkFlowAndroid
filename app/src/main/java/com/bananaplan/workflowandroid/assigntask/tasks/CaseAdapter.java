package com.bananaplan.workflowandroid.assigntask.tasks;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Equipment;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.view.CustomProgressBar;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter to control and show a task case
 * Task list is composed of a header and a grid view
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class CaseAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "TaskCaseAdapter";

    public interface OnSelectCaseListener {
        void onSelectCase(int position);
    }

    private static class ItemViewType {
        public static final int TYPE_HEADER = -2;
        public static final int TYPE_ITEM = -1;
    }

    private Context mContext;

    private List<String> mTaskCaseTitles = null;
    private Case mCase = null;

    private TaskCaseSpinnerAdapter mTaskCaseSpinnerAdapter;

    private OnSelectCaseListener mOnSelectCaseListener;

    private int mSelectedTaskCasePosition = 0;
    private boolean mIsTaskCaseSpinnerInitialized = false;


    private class TaskCaseSpinnerAdapter extends IconSpinnerAdapter<String> {
        public TaskCaseSpinnerAdapter(Context context, int resource, List<String> datas) {
            super(context, resource, datas);
        }

        @Override
        public String getSpinnerViewDisplayString(int position) {
            return (String) getItem(position);
        }

        @Override
        public int getSpinnerIconResourceId() {
            return R.drawable.case_spinner_icon;
        }

        @Override
        public boolean isDropdownSelectedIconVisible(int position) {
            return mSelectedTaskCasePosition == position;
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return (String) getItem(position);
        }
    }

    private class TaskCaseHeaderViewHolder extends RecyclerView.ViewHolder {

        public View header;
        public Spinner taskCaseSpinner;
        public CustomProgressBar progressBar;
        public TextView vendor;
        public TextView personInCharge;
        public TextView uncompletedTaskTime;
        public TextView undergoingTaskTime;
        public TextView editCaseButton;

        public boolean isTaskCaseSpinnerInitialized = false;

        public TaskCaseHeaderViewHolder(View v) {
            super(v);
            header = v;
            taskCaseSpinner = (Spinner) v.findViewById(R.id.task_case_spinner);
            progressBar = (CustomProgressBar) v.findViewById(R.id.task_case_information_progressbar);
            vendor = (TextView) v.findViewById(R.id.task_case_principal_vendor);
            personInCharge = (TextView) v.findViewById(R.id.task_case_person_in_charge);
            uncompletedTaskTime = (TextView) v.findViewById(R.id.task_case_hours_unfinished);
            undergoingTaskTime = (TextView) v.findViewById(R.id.task_case_hours_pass_by);
            editCaseButton = (TextView) v.findViewById(R.id.task_case_edit_button);
        }
    }

    public class TaskCardViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView name;
        public TextView workingTime;
        public TextView equipment;
        public TextView worker;
        public TextView status;
        public TextView warning;

        public TaskCardViewHolder(View v) {
            super(v);
            view = v;
            name = (TextView) v.findViewById(R.id.task_card_title);
            warning = (TextView) v.findViewById(R.id.taskitem_listview_warning);
            workingTime = (TextView) v.findViewById(R.id.task_card_current_task_working_time);
            equipment = (TextView) v.findViewById(R.id.task_card_equipment);
            worker = (TextView) v.findViewById(R.id.task_card_worker);
            status = (TextView) v.findViewById(R.id.task_card_status);
        }
    }

    public CaseAdapter(Context context) {
        mContext = context;
    }

    public void setOnSelectCaseListener(OnSelectCaseListener listener) {
        mOnSelectCaseListener = listener;
    }

    /**
     * When initialize the adapter, we should pass all of task cases' titles and the current task case data
     * to be displayed.
     *
     * @param taskCaseTitles
     * @param firstDisplayedCase
     */
    public void initCaseDatas(ArrayList<String> taskCaseTitles, Case firstDisplayedCase) {
        mTaskCaseTitles = taskCaseTitles;
        mCase = firstDisplayedCase;
    }

    public boolean isInitialized() {
        return mTaskCaseTitles != null && mCase != null;
    }

    public void swapCase(Case aCase) {
        mCase = aCase;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ItemViewType.TYPE_HEADER == viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.task_case_header, parent, false);
            return new TaskCaseHeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.task_card, parent, false);
            return new TaskCardViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        if (isHeaderPosition(position)) {
            onBindHeaderViewHolder(vh);
        } else {
            onBindItemViewHolder(vh, position);
        }
    }

    private void onBindHeaderViewHolder(ViewHolder vh) {
        TaskCaseHeaderViewHolder holder = (TaskCaseHeaderViewHolder) vh;
        mIsTaskCaseSpinnerInitialized = false;

        bindTaskCaseSpinner(holder);
        bindTaskCaseInformation(holder);
    }

    private void bindTaskCaseSpinner(TaskCaseHeaderViewHolder holder) {
        mTaskCaseSpinnerAdapter = new TaskCaseSpinnerAdapter(mContext, R.layout.icon_spinner_item, mTaskCaseTitles);
        mTaskCaseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        holder.taskCaseSpinner.setAdapter(mTaskCaseSpinnerAdapter);
        holder.taskCaseSpinner.setSelection(mSelectedTaskCasePosition);
        holder.taskCaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Avoid the first call of onItemSelected() when the spinner is initialized.
                if (!mIsTaskCaseSpinnerInitialized) {
                    mIsTaskCaseSpinnerInitialized = true;
                    return;
                }
                mSelectedTaskCasePosition = position;
                mOnSelectCaseListener.onSelectCase(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindTaskCaseInformation(TaskCaseHeaderViewHolder holder) {
        holder.progressBar.setProgress(mCase.getFinishPercent());
        holder.vendor.setText(WorkingData.getInstance(mContext).getVendorById(mCase.vendorId).name);
        holder.personInCharge.setText(WorkingData.getInstance(mContext).getWorkerById(mCase.workerId).name); // TODO: Manager
        holder.uncompletedTaskTime.setText(mCase.getHoursUnFinished());
        holder.undergoingTaskTime.setText(mCase.getHoursPassedBy());
        holder.editCaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Edit case", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onBindItemViewHolder(ViewHolder vh, int position) {
        TaskCardViewHolder holder = (TaskCardViewHolder) vh;
        Task task = getItem(position);

        // Name
        holder.name.setText(task.name);

        // Warning
        Utils.setTaskItemWarningTextView((Activity) mContext, task, holder.warning, false);

        // Task working time
        holder.workingTime.setText(task.getWorkingTime());

        // Equipment
        Equipment equipment = WorkingData.getInstance(mContext).getEquipmentById(task.equipmentId);
        holder.equipment.setText(equipment == null ? "無使用工具" : equipment.name);

        // Worker
        Worker worker = WorkingData.getInstance(mContext).getWorkerById(task.workerId);
        holder.worker.setText(worker == null ? "無員工" : worker.name);

        // Status
        holder.status.setText(Utils.getTaskItemStatusString(mContext, task));
    }

    public Task getItem(int position) {
        return mCase.tasks.get(--position);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = ItemViewType.TYPE_ITEM;
        if (isHeaderPosition(position)) {
            viewType = ItemViewType.TYPE_HEADER;
        }

        return viewType;
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return mCase.tasks == null ? 0 : mCase.tasks.size() + 1;
    }
}