package com.bananaplan.workflowandroid.data.worker.attendance;

import com.bananaplan.workflowandroid.data.IdData;
import com.bananaplan.workflowandroid.data.LeaveInMainInfo;


/**
 * Created by Ben on 2015/8/30.
 */
public class WorkerAttendance extends IdData {

    public String workerId;
    public String description;
    public String timeRange;

    public LeaveInMainInfo.Type type;

    public long from = 0L;
    public long to = 0L;


    public WorkerAttendance() {

    }

    public WorkerAttendance(String id, String workerId, String description, String timeRange,
                            LeaveInMainInfo.Type type, long from, long to) {
        this.id = id;
        this.workerId = workerId;
        this.description = description;
        this.timeRange = timeRange;
        this.type = type;
        this.from = from;
        this.to = to;
    }
}
