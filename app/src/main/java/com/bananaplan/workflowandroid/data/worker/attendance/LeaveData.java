package com.bananaplan.workflowandroid.data.worker.attendance;

import java.util.Date;

/**
 * Created by Ben on 2015/8/30.
 */
public class LeaveData {
    public enum TYPE {
        PRIVATE, WORK, SICK
    }

    public Date date;
    public TYPE type;
    public String reason;
}
