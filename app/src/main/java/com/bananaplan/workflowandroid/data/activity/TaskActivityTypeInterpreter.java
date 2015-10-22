package com.bananaplan.workflowandroid.data.activity;

import android.content.Context;

import com.bananaplan.workflowandroid.R;

/**
 * Created by daz on 10/11/15.
 */
public class TaskActivityTypeInterpreter {
    public static String getTranslation(Context context, String type) {
        switch (type) {
            case "start":
                return context.getString(R.string.task_start);
            case "suspend":
                return context.getString(R.string.task_suspend);
            case "complete":
                return context.getString(R.string.task_complete);
            case "pause":
                return context.getString(R.string.task_pause);
            case "resume":
                return context.getString(R.string.task_resume);
            case "pass":
                return context.getString(R.string.task_pass);
            case "fail":
                return context.getString(R.string.task_fail);
            case "create_exception":
                return context.getString(R.string.task_create_exception);
            case "complete_exception":
                return context.getString(R.string.task_complete_exception);
            case "dispatch":
                return context.getString(R.string.task_dispatch);

            default:
                return type;
        }

    }
}
