package com.bananaplan.workflowandroid.assigntask.workers;

import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;

import java.util.ArrayList;

/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class WorkerItem {

    public static final class WorkingStatus {
        public static final int NORMAL = 0;
        public static final int DELAY = 1;
    }

    //public String avatar;
    public String name;
    public String title;
    public String task;
    public int status;
    public String time;
    // +++ ben
    private ArrayList<TaskItem> mTaskItems;
    // --- ben


    public WorkerItem(String name, String title, String task, int status, String time) {
        this.name = name;
        this.title = title;
        this.task = task;
        this.status = status;
        this.time = time;
        // +++ ben
        mTaskItems = new ArrayList<TaskItem>();
        // --- ben
    }

    // +++ ben
    public ArrayList<TaskItem> getTaskItems() {
        return mTaskItems;
    }
    // --- ben
}
