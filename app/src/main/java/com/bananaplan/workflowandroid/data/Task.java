package com.bananaplan.workflowandroid.data;


import com.bananaplan.workflowandroid.data.worker.status.BaseData;

import java.util.ArrayList;
import java.util.Date;

/**
 * Data in a task item
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class Task extends IdData {

    public enum Status {
        PENDING, UNCLAIMED, WIP, PAUSE, DONE, EXCEPTION, STOP, CANCEL, INREVIEW
    }

    public String caseId;
    public String workerId;
    public String equipmentId;

    public Date startDate;   // Milliseconds to Date
    public Date finishDate;  // Milliseconds to Date

    public long currentStartTime = -1L;
    public long expectedTime = -1L;
    public long spentTime = -1L;

    public ArrayList<Warning> warningList = new ArrayList<>();
    public int errorCount;

    public Status status = Task.Status.UNCLAIMED;
    public ArrayList<BaseData> records = new ArrayList<>();

    public Task() {

    }

    public Task(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Task(String name, long expectedTime, String equipmentId, String workerId) {
        this.name = name;
        this.expectedTime = expectedTime;
        this.equipmentId = equipmentId;
        this.workerId = workerId;
    }

    public int getUnSolvedWarningCount() {
        int count = 0;
        for (Warning warning : warningList) {
            if (warning.status == Warning.Status.OPEN) {
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
}
