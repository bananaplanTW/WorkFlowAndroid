package com.bananaplan.workflowandroid.assigntask.workers;

import com.bananaplan.workflowandroid.data.WorkerItem;
import com.bananaplan.workflowandroid.data.equipment.MaintenanceRecord;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ben on 2015/7/23.
 */
public class Equipment {

    public long id;
    public String name;
    public WorkerItem worker;
    public long factoryId;
    public Date purchaseDate;
    public ArrayList<MaintenanceRecord> records = new ArrayList<>();

    public Equipment(long id, String name) {
        this(id, name, -1);
    }

    public Equipment(long id, String name, long factoryId) {
        this.id = id;
        this.name = name;
        this.factoryId = factoryId;
    }

    public Date getRecentlyMaintenanceDate() {
        Date date = null;
        for (MaintenanceRecord record : records) {
            if (date == null) {
                date = record.date;
            }
            if (date.compareTo(record.date) < 0) {
                date = record.date;
            }
        }
        return date;
    }
}
