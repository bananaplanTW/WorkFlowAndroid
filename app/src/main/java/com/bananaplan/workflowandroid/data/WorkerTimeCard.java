package com.bananaplan.workflowandroid.data;

/**
 * Created by Ben on 2015/10/9.
 */
public class WorkerTimeCard {

    public String id;
    public String employeeId;
    public long startDate;
    public long endDate;
    public CaseTimeCard.STATUS status;
    public long createdDate;
    public long updatedDate;

    public WorkerTimeCard(String id, String employeeId, long startDate,
                          long endDate, CaseTimeCard.STATUS status, long createdDate, long updatedDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
