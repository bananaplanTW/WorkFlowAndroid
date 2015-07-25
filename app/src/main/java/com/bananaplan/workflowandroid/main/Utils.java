package com.bananaplan.workflowandroid.main;

import android.content.Context;
import android.content.res.Resources;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;

/**
 * Created by Ben on 2015/7/25.
 */
public class Utils {

    public static String getTaskItemProgressString(final Context context, final int progress) {
        String r = "";
        Resources resources = context.getResources();
        switch (progress) {
            case TaskItem.Progress.IN_SCHEDULE:
                r = resources.getString(R.string.task_progress_in_schedule);
                break;
            case TaskItem.Progress.NOT_START:
                r = resources.getString(R.string.task_progress_not_start);
                break;
            case TaskItem.Progress.PAUSE:
                r = resources.getString(R.string.task_progress_pause);
                break;
            case TaskItem.Progress.WORKING:
                r = resources.getString(R.string.task_progress_working);
                break;
            case TaskItem.Progress.FINISH:
                r = resources.getString(R.string.task_progress_finish);
                break;
            default:
                break;
        }
        return r;
    }
}
