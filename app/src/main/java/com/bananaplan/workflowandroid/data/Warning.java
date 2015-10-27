package com.bananaplan.workflowandroid.data;


/**
 * @author Danny Lin
 * @since 2015/7/30.
 */
public class Warning extends IdData {

    public enum Status {
        OPEN, CLOSE
    }

    public String taskId;
    public String caseId;
    public String workerId;
    public String managerId;
    public String description;

    public long spentTime;

    public Status status = Status.OPEN;


    public Warning(
            String id,
            String name,
            String caseId,
            String taskId,
            String workerId,
            Status status,
            long spentTime,
            long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.caseId = caseId;
        this.taskId = taskId;
        this.workerId = workerId;
        this.status = status;
        this.spentTime = spentTime;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Warning(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    public void update(Warning warning) {
        this.name = warning.name;
        this.caseId = warning.caseId;
        this.taskId = warning.taskId;
        this.workerId = warning.workerId;
        this.status = warning.status;
        this.spentTime = warning.spentTime;
        this.lastUpdatedTime = warning.lastUpdatedTime;
    }

    public static Status convertStringToStatus(String status) {
        Status result = Status.OPEN;

        if ("OPEN".equals(status)) {
            result = Status.OPEN;
        } else if ("CLOSE".equals(status)) {
            result = Status.CLOSE;
        }

        return result;
    }
}
