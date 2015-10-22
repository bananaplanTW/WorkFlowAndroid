package com.bananaplan.workflowandroid.data.activity;

import android.content.Context;

import com.bananaplan.workflowandroid.R;

/**
 * Created by daz on 10/10/15.
 */
public class EmployeeActivityTypeInterpreter {
    public static String getTranslation(Context context, String type) {
        switch (type) {
            case "checkIn":
                return context.getString(R.string.worker_check_in);
            case "checkOut":
                return context.getString(R.string.worker_check_in);
            case "becomeWIP":
                return context.getString(R.string.worker_become_wip);
            case "becomePause":
                return context.getString(R.string.worker_become_pause);
            case "becomeResume":
                return context.getString(R.string.worker_become_resume);
            case "becomeOverwork":
                return context.getString(R.string.worker_become_overwork);
            case "becomeStop":
                return context.getString(R.string.worker_become_stop);
            case "becomePending":
                return context.getString(R.string.worker_become_pending);
            case "becomeOff":
                return context.getString(R.string.worker_become_off);


            case "dispatchTask":
                return context.getString(R.string.worker_dispatch_task);
            case "startTask":
                return context.getString(R.string.worker_start_task);
            case "suspendTask":
                return context.getString(R.string.worker_suspend_task);
            case "completeTask":
                return context.getString(R.string.worker_complete_task);
            case "unloadTask":
                return context.getString(R.string.worker_unload_task);
            case "passReviewTask":
                return context.getString(R.string.worker_pass_review_task);
            case "failReviewTask":
                return context.getString(R.string.worker_fail_review_task);
            case "createTaskException":
                return context.getString(R.string.worker_create_task_exception);
            case "completeTaskException":
                return context.getString(R.string.worker_complete_task_exception);


            default:
                return type;
        }

    }
}
