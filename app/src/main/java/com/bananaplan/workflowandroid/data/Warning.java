package com.bananaplan.workflowandroid.data;

import java.util.Date;

/**
 * @author Danny Lin
 * @since 2015/7/30.
 */
public class Warning extends IdData {

    public enum Status {
        OPEN, CLOSE
    }

    public Status status = Status.OPEN;

    public Date endDate;

    public long spentTime;
    public long currentStartTime;

    public String taskId;
    public String caseId;
    public String workerId;
    public String description;


    public Warning(String name, Status status) {
        this.name = name;
        this.status = status;
    }
}
