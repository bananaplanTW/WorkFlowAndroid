package com.bananaplan.workflowandroid.utility.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;


/**
 * @author Danny Lin
 * @since 2015/9/11.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
    private int mHourOfDay;
    private int mMinute;
    private boolean mIs24HourMode;


    public static TimePickerDialogFragment newInstance(TimePickerDialog.OnTimeSetListener callback,
                                                       int hourOfDay, int minute, boolean is24HourMode) {
        TimePickerDialogFragment timePicker = new TimePickerDialogFragment();
        timePicker.initialize(callback, hourOfDay, minute, is24HourMode);
        return timePicker;
    }

    private void initialize(TimePickerDialog.OnTimeSetListener callback, int hourOfDay, int minute, boolean is24HourMode) {
        mOnTimeSetListener = callback;
        mHourOfDay = hourOfDay;
        mMinute = minute;
        mIs24HourMode = is24HourMode;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), mOnTimeSetListener, mHourOfDay, mMinute, mIs24HourMode);
    }
}
