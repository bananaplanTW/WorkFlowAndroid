package com.bananaplan.workflowandroid.data;


import com.bananaplan.workflowandroid.data.worker.status.BaseData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public Date startDate;
    public Date endDate;
    public Date assignDate;

    public long currentStartTime = -1L;
    public long expectedTime = -1L;
    public long spentTime = -1L;

    public List<Warning> warnings;
    public List<Task> subTaskIds;

    public int errorCount;

    public Status status = Task.Status.UNCLAIMED;
    public ArrayList<BaseData> records = new ArrayList<>();


    public Task() {

    }

    public Task(String id,
                String name,
                String caseId,
                String workerId,
                String equipmentId,
                Status status,
                long expectedTime,
                long spentTime,
                Date assignDate,
                Date startDate,
                Date endDate,
                List<Warning> warnings) {
        this.id = id;
        this.name = name;
        this.caseId = caseId;
        this.workerId = workerId;
        this.equipmentId = equipmentId;
        this.status = status;
        this.expectedTime = expectedTime;
        this.spentTime = spentTime;
        this.assignDate = assignDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.warnings = warnings;

        if (this.warnings == null) {
            this.warnings = new ArrayList<>();
        }
    }

    public Task(String id, String name) {
        this.id = id;
        this.name = name;
        this.warnings = new ArrayList<>();
    }

    public Task(String name, long expectedTime, String equipmentId, String workerId) {
        this.name = name;
        this.expectedTime = expectedTime;
        this.equipmentId = equipmentId;
        this.workerId = workerId;
        this.warnings = new ArrayList<>();
    }

    public int getUnSolvedWarningCount() {
        int count = 0;
        for (Warning warning : warnings) {
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

    public static Status convertStringToStatus(String status) {
        Status result = Status.UNCLAIMED;

        if ("pending".equals(status)) {
            result = Status.PENDING;

        } else if ("unclaimed".equals(status)) {
            result = Status.UNCLAIMED;

        } else if ("wip".equals(status)) {
            result = Status.WIP;

        } else if ("pause".equals(status)) {
            result = Status.PAUSE;

        } else if ("done".equals(status)) {
            result = Status.DONE;

        } else if ("exception".equals(status)) {
            result = Status.EXCEPTION;

        } else if ("stop".equals(status)) {
            result = Status.STOP;

        } else if ("cancel".equals(status)) {
            result = Status.CANCEL;

        } else if ("inreview".equals(status)) {
            result = Status.INREVIEW;
        }

        return result;
    }
}
