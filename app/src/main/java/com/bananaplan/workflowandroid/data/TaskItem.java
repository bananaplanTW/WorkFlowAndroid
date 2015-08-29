package com.bananaplan.workflowandroid.data;


import java.util.ArrayList;
import java.util.Date;

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

    public String title;

    public long id;
    public long taskCaseId;
    public long workerId;
    public long equipmentId;

    public Date startDate;
    public Date finishDate;
    public long expectedWorkingTime = -1L;

    public ArrayList<Warning> warningList = new ArrayList<>();
    public int errorCount;

    public Status status = Status.NOT_START;


    public TaskItem() {

    }

    public TaskItem(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public TaskItem(String title, long expectedWorkingTime, long equipmentId, long workerId) {
        this.title = title;
        this.expectedWorkingTime = expectedWorkingTime;
        this.equipmentId = equipmentId;
        this.workerId = workerId;
    }

    public int getUnSolvedWarningCount() {
        int count = 0;
        for (Warning warning : warningList) {
            if (warning.status == Warning.WarningStatus.UNSOLVED) {
                count++;
            }
        }
        return count;
    }

    // TODO: Get this information from server
    public String getWorkingTime() {
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
