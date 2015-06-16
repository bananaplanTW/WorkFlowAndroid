package com.bananaplan.workflowandroid.assigntask.tasks;


/**
 * Data in a task list item
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskItem {

    public static final class Status {
        public static final int UNDERGOING = 0;
        public static final int OVERTIME = 1;
        public static final int COMPLETED = 2;
    }

    public String title;
    public String subtitle;
    public int status;
    public String time;


    public TaskItem(String title, String subtitle, int status, String time) {
        this.title = title;
        this.subtitle = subtitle;
        this.status = status;
        this.time = time;
    }
}
