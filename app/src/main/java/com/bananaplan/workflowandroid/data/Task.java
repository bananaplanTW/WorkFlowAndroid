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
        PENDING, UNCLAIMED, WIP, PAUSE, DONE, EXCEPTION, STOP, CANCEL, IN_REVIEW
    }

    public String caseId;
    public String workerId;
    public String equipmentId;

    public Date startDate;  // The starting working date of this task
    public Date endDate;
    public Date assignDate;

    public long currentStartTime = 0L;
    public long expectedTime = 0L;
    public long startTime = 0L;  // The starting time of this working section
    public long spentTime = 0L;

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
                Date assignDate,
                Date startDate,
                Date endDate,
                List<Warning> warnings,
                long expectedTime,
                long startTime,
                long spentTime,
                long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.caseId = caseId;
        this.workerId = workerId;
        this.equipmentId = equipmentId;
        this.status = status;
        this.expectedTime = expectedTime;
        this.startTime = startTime;
        this.spentTime = spentTime;
        this.assignDate = assignDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.warnings = warnings;
        this.lastUpdatedTime = lastUpdatedTime;

        if (this.warnings == null) {
            this.warnings = new ArrayList<>();
        }
    }

    public Task(String id, String name) {
        this.id = id;
        this.name = name;
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

    public void update(Task task) {
        this.name = task.name;
        this.caseId = task.caseId;
        this.workerId = task.workerId;
        this.equipmentId = task.equipmentId;
        this.status = task.status;
        this.expectedTime = task.expectedTime;
        this.startTime = task.startTime;
        this.spentTime = task.spentTime;
        this.assignDate = task.assignDate;
        this.startDate = task.startDate;
        this.endDate = task.endDate;
        this.warnings = task.warnings;
        this.lastUpdatedTime = task.lastUpdatedTime;
    }

    public long getWorkingTime() {
        return System.currentTimeMillis() - startTime + spentTime;
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
            result = Status.IN_REVIEW;
        }

        return result;
    }
}
