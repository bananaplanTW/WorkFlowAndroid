package com.bananaplan.workflowandroid.data.equipment;

import java.util.Date;

/**
 * Created by Ben on 2015/9/5.
 */
public class MaintenanceRecord {
    public String reason;
    public Date date;

    public MaintenanceRecord(String reason, Date date) {
        this.reason = reason;
        this.date = date;
    }
}
