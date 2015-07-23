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
    public long taskCaseId;
    public long workerId;
    public long toolId;

    public TaskItem(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getStatus() {
        return Status.WARNING;
    }

    public int getProgress() {
        return Progress.IN_SCHEDULE;
    }

    public String getWorkingTime() {
        return "11:00:00";
    }

    public String getWorningText() {
        return "Sand holes";
    }

    public String getToolName() { // use for test
        return "CNC";
    }

    public String getWorkerItemName() { // use for test
        return "Danny Lin";
    }
}
