package com.bananaplan.workflowandroid.data.activity;

import android.content.Context;

import com.bananaplan.workflowandroid.R;

/**
 * Created by logicmelody on 2015/10/30.
 */
public class TaskWarningActivityTypeInterpreter {
    public static String getTranslation(Context context, String type) {
        switch (type) {
            case "open":
                return context.getString(R.string.task_warning_open);
            case "close":
                return context.getString(R.string.task_warning_close);
            default:
                return type;
        }
    }
}
