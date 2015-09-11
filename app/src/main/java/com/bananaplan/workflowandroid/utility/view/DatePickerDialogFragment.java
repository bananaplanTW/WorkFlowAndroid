package com.bananaplan.workflowandroid.utility.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;


/**
 * @author Danny Lin
 * @since 2015/9/11.
 */
public class DatePickerDialogFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private int mYear;
    private int mMonth;
    private int mDay;


    public static DatePickerDialogFragment newInstance(DatePickerDialog.OnDateSetListener callback,
                                                       int year, int month, int day) {
        DatePickerDialogFragment timePicker = new DatePickerDialogFragment();
        timePicker.initialize(callback, year, month, day);
        return timePicker;
    }

    private void initialize(DatePickerDialog.OnDateSetListener callback, int year, int month, int day) {
        mOnDateSetListener = callback;
        mYear = year;
        mMonth = month;
        mDay = day;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), mOnDateSetListener, mYear, mMonth, mDay);
    }
}
