package com.bananaplan.workflowandroid.data;

/**
 * Created by Ben on 2015/10/9.
 */
public class CaseTimeCard {
    public enum STATUS { OPEN, CLOSE }

    public String id;
    public String caseId;
    public String taskId;
    public String employeeId;
    public long startDate;
    public long endDate;
    public STATUS status;
    public long createdDate;
    public long updatedDate;

    public CaseTimeCard(String id, String caseId, String taskId, String employeeId, long startDate,
                        long endDate, STATUS status, long createdDate, long updatedDate) {
        this.id = id;
        this.caseId = caseId;
        this.taskId = taskId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
