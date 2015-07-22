package com.bananaplan.workflowandroid.assigntask.tasks;


/**
 * Data in a task item
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskItem {

    public static final class Status {
        public static final int NORMAL = 0;
        public static final int WARNING = 1;
    }

    public static final class Progress {
        public static final int IN_SCHEDULE = 0;
        public static final int NOT_START = 1;
        public static final int WORKING = 2;
        public static final int PAUSE = 3;
    }

    public long id;
    public String title;
    public int status;
    public String statusText;
    public String workingTime;
    public String tool;
    public String worker;
    public int progress;
    public long taskCaseId;
    public long workerId;

    public TaskItem(String title, int status, String statusText,
                    String workingTime, String tool, String worker, int progress) {
        this(-1, title, status, statusText, workingTime, tool, worker, progress);
    }

    public TaskItem(long id, String title, int status, String statusText,
                    String workingTime, String tool, String worker, int progress) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.statusText = statusText;
        this.workingTime = workingTime;
        this.tool = tool;
        this.worker = worker;
        this.progress = progress;
    }
}
