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

    private static class ItemViewType {
        public static final int HEADER = 0;
        public static final int ITEM = 1;
    }

    private Context mContext;

    private List<String> mCaseTitles = null;
    private Case mSelectedCase = null;

    private CaseSpinnerAdapter mCaseSpinnerAdapter;

    private int mSelectedCasePosition = 0;
    private boolean mIsCaseSpinnerInitialized = false;


    private class CaseSpinnerAdapter extends IconSpinnerAdapter<String> {
        public CaseSpinnerAdapter(Context context, int resource, List<String> datas) {
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
            return mSelectedCasePosition == position;
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return (String) getItem(position);
        }
    }

    private class CaseHeaderViewHolder extends RecyclerView.ViewHolder {

        public View header;
        public Spinner caseSpinner;
        public CustomProgressBar progressBar;
        public TextView vendor;
        public TextView pic;
        public TextView uncompletedTaskTime;
        public TextView undergoingTaskTime;
        public TextView editCaseButton;


        public CaseHeaderViewHolder(View v) {
            super(v);
            header = v;
            caseSpinner = (Spinner) v.findViewById(R.id.case_spinner);
            progressBar = (CustomProgressBar) v.findViewById(R.id.case_information_progressbar);
            vendor = (TextView) v.findViewById(R.id.case_principal_vendor);
            pic = (TextView) v.findViewById(R.id.case_pic);
            uncompletedTaskTime = (TextView) v.findViewById(R.id.case_hours_unfinished);
            undergoingTaskTime = (TextView) v.findViewById(R.id.case_hours_pass_by);
            editCaseButton = (TextView) v.findViewById(R.id.case_edit_button);
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
            warning = (TextView) v.findViewById(R.id.listview_task_warning);
            workingTime = (TextView) v.findViewById(R.id.task_card_current_task_working_time);
            equipment = (TextView) v.findViewById(R.id.task_card_equipment);
            worker = (TextView) v.findViewById(R.id.task_card_worker);
            status = (TextView) v.findViewById(R.id.task_card_status);
        }
    }

    /**
     * When initialize the adapter, we should pass all of task cases' titles and the current task case data
     * to be displayed.
     */
    public CaseAdapter(Context context, ArrayList<String> caseTitles, Case firstDisplayedCase) {
        mContext = context;
        mCaseTitles = caseTitles;
        mSelectedCase = firstDisplayedCase;
    }

    public void swapCase(Case aCase) {
        mSelectedCase = aCase;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ItemViewType.HEADER == viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.task_case_header, parent, false);
            return new CaseHeaderViewHolder(v);
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
        CaseHeaderViewHolder holder = (CaseHeaderViewHolder) vh;
        mIsCaseSpinnerInitialized = false;

        bindTaskCaseSpinner(holder);
        bindTaskCaseInformation(holder);
    }

    private void bindTaskCaseSpinner(CaseHeaderViewHolder holder) {
        mCaseSpinnerAdapter = new CaseSpinnerAdapter(mContext, R.layout.icon_spinner_item, mCaseTitles);
        mCaseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        holder.caseSpinner.setAdapter(mCaseSpinnerAdapter);
        holder.caseSpinner.setSelection(mSelectedCasePosition);
        holder.caseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Avoid the first call of onItemSelected() when the spinner is initialized.
                if (!mIsCaseSpinnerInitialized) {
                    mIsCaseSpinnerInitialized = true;
                    return;
                }
                mSelectedCasePosition = position;

                swapCase(WorkingData.getInstance(mContext).getCases().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindTaskCaseInformation(CaseHeaderViewHolder holder) {
        holder.progressBar.setProgress(mSelectedCase.getFinishPercent());
        holder.vendor.setText(WorkingData.getInstance(mContext).getVendorById(mSelectedCase.vendorId).name);
        holder.pic.setText(WorkingData.getInstance(mContext).getManagerById(mSelectedCase.managerId).name);
        holder.uncompletedTaskTime.setText(mSelectedCase.getHoursUnFinished());
        holder.undergoingTaskTime.setText(mSelectedCase.getHoursPassedBy());
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

        // Task expected time
        holder.workingTime.setText(Utils.millisecondsToTimeString(task.expectedTime));

        // Equipment
        Equipment equipment = WorkingData.getInstance(mContext).getEquipmentById(task.equipmentId);
        holder.equipment.setText(equipment == null ?
                mContext.getString(R.string.task_card_no_equipment) : equipment.name);

        // Worker
        Worker worker = WorkingData.getInstance(mContext).getWorkerById(task.workerId);
        holder.worker.setText(worker == null ?
                mContext.getString(R.string.task_card_no_worker) : worker.name);

        // Status
        holder.status.setText(Utils.getTaskItemStatusString(mContext, task));
    }

    public Task getItem(int position) {
        return mSelectedCase.tasks.get(--position);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = ItemViewType.ITEM;
        if (isHeaderPosition(position)) {
            viewType = ItemViewType.HEADER;
        }

        return viewType;
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return mSelectedCase.tasks == null ? 0 : mSelectedCase.tasks.size() + 1;
    }
}
