package com.bananaplan.workflowandroid.addcase;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.TaskCase;
import com.bananaplan.workflowandroid.data.TaskItem;
import com.bananaplan.workflowandroid.management.ManagementDialog;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.view.DatePickerDialogFragment;
import com.bananaplan.workflowandroid.utility.view.TimePickerDialogFragment;
import com.bananaplan.workflowandroid.utility.view.TitleEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Adapter for add-tasks grid view in AddCaseFragment
 *
 * @author Danny Lin
 * @since 2015/9/1.
 */
public class AddCaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AddTaskAdapter";

    private static final class DialogTag {
        public static final String TIME_PICKER = "dialog_time_picker";
        public static final String DATE_PICKER = "dialog_date_picker";
    }

    private static final class ItemViewType {
        public static final int INFO_HEADER = 0;
        public static final int TASK = 1;
        public static final int ADD = 2;
    }

    private Context mContext;
    private TaskCase mTaskCase;
    private ArrayList<TaskItem> mTasksData = new ArrayList<TaskItem>();

    private int mSpanCount = 0;


    private final class InfoHeaderViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, TitleEditText.OnClickContentListener {

        public TextView saveTemplateButton;
        public TextView saveCaseButton;
        public TitleEditText caseName;
        public TitleEditText vendor;
        public TitleEditText managerPIC;
        public TitleEditText materialPurchasedDate;
        public TitleEditText layoutDeliveredDate;
        public TitleEditText deliveredDate;
        public TitleEditText plateCount;
        public TitleEditText supportBlockCount;
        public EditText length;
        public EditText width;
        public EditText height;


        public InfoHeaderViewHolder(View v) {
            super(v);
            findViews(v);
            setupViews(v);
        }

        private void findViews(View v) {
            saveTemplateButton = (TextView) v.findViewById(R.id.save_template_button);
            saveCaseButton = (TextView) v.findViewById(R.id.save_case_button);
            caseName = (TitleEditText) v.findViewById(R.id.case_name_edit_text);
            vendor = (TitleEditText) v.findViewById(R.id.vendor_edit_text);
            managerPIC = (TitleEditText) v.findViewById(R.id.manager_pic_edit_text);
            materialPurchasedDate = (TitleEditText) v.findViewById(R.id.material_purchased_date_edit_text);
            layoutDeliveredDate = (TitleEditText) v.findViewById(R.id.layout_delivered_date_edit_text);
            deliveredDate = (TitleEditText) v.findViewById(R.id.delivered_date_edit_text);
            plateCount = (TitleEditText) v.findViewById(R.id.plate_count_edit_text);
            supportBlockCount = (TitleEditText) v.findViewById(R.id.support_block_count_edit_text);
            length = (EditText) v.findViewById(R.id.length_edit_text);
            width = (EditText) v.findViewById(R.id.width_edit_text);
            height = (EditText) v.findViewById(R.id.height_edit_text);
        }

        private void setupViews(View v) {
            ((TextView) v.findViewById(R.id.measurement_text)).setPadding(length.getPaddingLeft(), 0, 0, 0);

            saveTemplateButton.setOnClickListener(this);
            saveCaseButton.setOnClickListener(this);
            vendor.setOnClickContentListener(this);
            managerPIC.setOnClickContentListener(this);
            materialPurchasedDate.setOnClickContentListener(this);
            layoutDeliveredDate.setOnClickContentListener(this);
            deliveredDate.setOnClickContentListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_case_button:
                    break;
            }
        }

        @Override
        public void onClickContent(TitleEditText tet) {
            switch (tet.getId()) {
                case R.id.vendor_edit_text:
                    mContext.startActivity(
                            ManagementDialog.showManagementDialog(mContext, ManagementDialog.ManagementType.VENDOR));
                    Utils.hideSoftKeyboard((Activity) mContext);
                    break;

                case R.id.manager_pic_edit_text:
                    mContext.startActivity(
                            ManagementDialog.showManagementDialog(mContext, ManagementDialog.ManagementType.MANAGER_PIC));
                    Utils.hideSoftKeyboard((Activity) mContext);
                    break;

                case R.id.material_purchased_date_edit_text:
                    setDate(tet, mTaskCase.materialPurchasedDate);
                    Utils.hideSoftKeyboard((Activity) mContext);
                    break;

                case R.id.layout_delivered_date_edit_text:
                    setDate(tet, mTaskCase.layoutDeliveredDate);
                    Utils.hideSoftKeyboard((Activity) mContext);
                    break;

                case R.id.delivered_date_edit_text:
                    setDate(tet, mTaskCase.deliveredDate);
                    Utils.hideSoftKeyboard((Activity) mContext);
                    break;
            }
        }

        private void setDate(TitleEditText tet, Date taskCaseDate) {
            if (taskCaseDate == null) {
                taskCaseDate = new Date();
            }
            showDatePicker(tet, taskCaseDate);
        }

        private void showDatePicker(final TitleEditText tet, final Date taskCaseDate) {
            FragmentManager fm = ((Activity) mContext).getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prevDatePickerDialog = fm.findFragmentByTag(DialogTag.DATE_PICKER);
            if (prevDatePickerDialog != null) {
                ft.remove(prevDatePickerDialog);
            }

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(taskCaseDate.getTime());
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialogFragment.newInstance(new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // The month value is 0-based.
                    tet.setContent(String.format(mContext.getString(R.string.date_format_string),
                                   Utils.pad(monthOfYear+1), Utils.pad(dayOfMonth), Utils.pad(year)));

                    Calendar cal = Calendar.getInstance();
                    cal.clear();
                    cal.set(year, monthOfYear, dayOfMonth);

                    taskCaseDate.setTime(cal.getTime().getTime());
                }

            }, year, month, day).show(ft, DialogTag.DATE_PICKER);
        }
    }

    private final class TaskViewHolder extends RecyclerView.ViewHolder implements
            View.OnFocusChangeListener, View.OnClickListener {

        public TextView index;
        public EditText title;
        public EditText expectedWorkingTime;
        public EditText equipment;
        public EditText workerPIC;
        public TextView detailButton;

        private TextWatcher mTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTasksData.get(TaskViewHolder.this.getAdapterPosition()).name = s.toString();
            }
        };

        public TaskViewHolder(View v) {
            super(v);
            findViews(v);
            setupListeners();
        }

        private void findViews(View v) {
            index = (TextView) v.findViewById(R.id.add_case_task_index);
            title = (EditText) v.findViewById(R.id.add_case_task_title_edit_text);
            expectedWorkingTime = (EditText) v.findViewById(R.id.add_case_task_expected_working_time_edit_text);
            equipment = (EditText) v.findViewById(R.id.add_case_task_equipment_edit_text);
            workerPIC = (EditText) v.findViewById(R.id.add_case_task_worker_pic_edit_text);
            detailButton = (TextView) v.findViewById(R.id.add_case_task_detail_button);
        }

        private void setupListeners() {
            title.addTextChangedListener(mTextWatcher);
            expectedWorkingTime.setOnFocusChangeListener(this);
            expectedWorkingTime.setOnClickListener(this);
            equipment.setOnFocusChangeListener(this);
            equipment.setOnClickListener(this);
            workerPIC.setOnFocusChangeListener(this);
            workerPIC.setOnClickListener(this);
            detailButton.setOnClickListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) return;
            setupOnClickEvents(v.getId());
        }

        @Override
        public void onClick(View v) {
            setupOnClickEvents(v.getId());
        }

        private void setupOnClickEvents(int id) {
            switch (id) {
                case R.id.add_case_task_expected_working_time_edit_text:
                    showTimePicker();
                    break;
                case R.id.add_case_task_equipment_edit_text:
                    mContext.startActivity(
                            ManagementDialog.showManagementDialog(mContext, ManagementDialog.ManagementType.EQUIPMENT));
                    break;
                case R.id.add_case_task_worker_pic_edit_text:
                    mContext.startActivity(
                            ManagementDialog.showManagementDialog(mContext, ManagementDialog.ManagementType.WORKER_PIC));
                    break;
                case R.id.add_case_task_detail_button:
                    Toast.makeText(mContext, "Detail " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        private void showTimePicker() {
            FragmentManager fm = ((Activity) mContext).getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prevTimePickerDialog = fm.findFragmentByTag(DialogTag.TIME_PICKER);
            if (prevTimePickerDialog != null) {
                ft.remove(prevTimePickerDialog);
            }

            TimePickerDialogFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mTasksData.get(TaskViewHolder.this.getAdapterPosition()).expectedWorkingTime =
                            Utils.timeToMilliseconds(hourOfDay, minute);
                    TaskViewHolder.this.expectedWorkingTime.setText(
                            getExpectedWorkingTime(mTasksData.get(TaskViewHolder.this.getAdapterPosition()).expectedWorkingTime));
                }

            }, 0, 0, true).show(ft, DialogTag.TIME_PICKER);
        }
    }

    private final class AddViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View view;

        public AddViewHolder(View v) {
            super(v);
            view = v;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            for (int i = 0 ; i < mSpanCount ; i++) {
                mTasksData.add(mTasksData.size()-1, new TaskItem());
            }
            notifyItemRangeInserted(mTasksData.size()-1-mSpanCount, mSpanCount);
            ((RecyclerView) v.getParent()).smoothScrollToPosition(mTasksData.size()-mSpanCount);
        }
    }

    public AddCaseAdapter(Context context, int spanCount) {
        mContext = context;
        mSpanCount = spanCount;
        mTaskCase = new TaskCase();
        initTasksData();
    }

    private void initTasksData() {
        // Header item
        mTasksData.add(new TaskItem());

        // Task item
        for (int i = 0 ; i < mSpanCount ; i++) {
            mTasksData.add(new TaskItem());
        }

        // Add item
        mTasksData.add(new TaskItem());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case ItemViewType.INFO_HEADER:
                v = LayoutInflater.from(mContext).inflate(R.layout.add_case_info_header, parent, false);
                viewHolder = new InfoHeaderViewHolder(v);
                break;
            case ItemViewType.ADD:
                v = LayoutInflater.from(mContext).inflate(R.layout.add_case_add_item, parent, false);
                viewHolder = new AddViewHolder(v);
                break;
            case ItemViewType.TASK:
                v = LayoutInflater.from(mContext).inflate(R.layout.add_case_task_item, parent, false);
                viewHolder = new TaskViewHolder(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isAddItem(position)) {
            return;
        } else if (isInfoHeader(position)) {
            onBindInfoHeaderViewHolder((InfoHeaderViewHolder) holder);
        } else {
            onBindTaskViewHolder((TaskViewHolder) holder, position);
        }
    }

    private void onBindInfoHeaderViewHolder(InfoHeaderViewHolder holder) {
        holder.materialPurchasedDate.setContent(getDate(mTaskCase.materialPurchasedDate));
        holder.layoutDeliveredDate.setContent(getDate(mTaskCase.layoutDeliveredDate));
        holder.deliveredDate.setContent(getDate(mTaskCase.deliveredDate));
    }

    private void onBindTaskViewHolder(TaskViewHolder holder, int position) {
        holder.index.setText(String.valueOf(position));
        holder.title.setText(mTasksData.get(position).name);
        holder.expectedWorkingTime.setText(getExpectedWorkingTime(mTasksData.get(position).expectedWorkingTime));
    }

    private String getDate(Date date) {
        return date == null ? "" : Utils.millisecondsToDate(mContext, date.getTime());
    }

    private String getExpectedWorkingTime(long milliseconds) {
        String s = "";
        if (milliseconds != -1L) {
            int[] times = Utils.millisecondsToTime(milliseconds);
            s = String.format(mContext.getString(R.string.add_case_task_expected_working_time),
                              Utils.pad(times[0]), Utils.pad(times[1]));
        }
        return s;
    }

    private boolean isInfoHeader(int position) {
        return position == 0;
    }

    private boolean isAddItem(int position) {
        return position == mTasksData.size()-1;
    }

    @Override
    public int getItemCount() {
        return mTasksData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isInfoHeader(position)) {
            return ItemViewType.INFO_HEADER;
        } else if (isAddItem(position)) {
            return ItemViewType.ADD;
        } else {
            return ItemViewType.TASK;
        }
    }
}
