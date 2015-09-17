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
        IN_SCHEDULE, NOT_START, WORKING, PAUSE, FINISH
    }

    public long taskCaseId;
    public long workerId;
    public long equipmentId;

    public Date startDate;
    public Date finishDate;
    public long expectedWorkingTime = -1L;

    public ArrayList<Warning> warningList = new ArrayList<>();
    public int errorCount;

    public Status status = Status.NOT_START;
    public ArrayList<BaseData> records = new ArrayList<>();

    public Task() {

    }

    public Task(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Task(String name, long expectedWorkingTime, long equipmentId, long workerId) {
        this.name = name;
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
}
