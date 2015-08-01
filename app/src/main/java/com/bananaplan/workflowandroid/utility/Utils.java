package com.bananaplan.workflowandroid.utility;

import android.content.Context;
import android.content.res.Resources;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;

/**
 * Created by Ben on 2015/7/25.
 */
public class Utils {

    public static String getTaskItemStatusString(final Context context, final TaskItem.Status status) {
        String r = "";
        Resources resources = context.getResources();
        switch (status) {
            case IN_SCHEDULE:
                r = resources.getString(R.string.task_progress_in_schedule);
                break;
            case NOT_START:
                r = resources.getString(R.string.task_progress_not_start);
                break;
            case PAUSE:
                r = resources.getString(R.string.task_progress_pause);
                break;
            case WORKING:
                r = resources.getString(R.string.task_progress_working);
                break;
            case FINISH:
                r = resources.getString(R.string.task_progress_finish);
                break;
            default:
                break;
        }
        return r;
    }
}
