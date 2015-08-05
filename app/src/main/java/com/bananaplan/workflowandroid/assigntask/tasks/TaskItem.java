package com.bananaplan.workflowandroid.assigntask.tasks;


import java.util.ArrayList;

/**
 * Data in a task item
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskItem {

    public enum Status {
        IN_SCHEDULE, NOT_START, WORKING, PAUSE, FINISH
    }

    public long id;
    public long taskCaseId;
    public long workerId;
    public long toolId;

    public String title;
    public ArrayList<Warning> warningList = new ArrayList<Warning>();

    public Status status = Status.NOT_START;


    public TaskItem(long id, String title) {
        this.id = id;
        this.title = title;
    }

    // TODO: Get this information from server
    public String getWorkingTime() {
        return "11 : 00";
    }

    public String getExpectedFinishTime() {
        return "11 : 00";
    }

    // TODO: Get this information from server
    public String getExpectedFinishedTime() {
        return "11:00";
    }

    // TODO: Map to tool id
    public String getToolName() { // use for test
        return "CNC";
    }

    // TODO: Map to worker id
    public String getWorkerName() { // use for test
        return "Danny Lin";
    }
}
