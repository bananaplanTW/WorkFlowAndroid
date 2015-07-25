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
        public static final int FINISH = 4;
    }

    public long id;
    public String title;
    public long taskCaseId;
    public long workerId;
    public long toolId;
    public int progress;
    public int status;

    public TaskItem(long id, String title) {
        this.id = id;
        this.title = title;
        this.progress = (int) (Math.random() * 4);
        this.status = (int) (Math.random() * 2);
    }

    public int getStatus() {
        return this.status;
    }

    public int getProgress() {
        return this.progress;
    }

    public String getWorkingTime() {
        return "11 : 00 : 00";
    }

    public String getExpectedFinishTime() {
        return "11 : 00";
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
