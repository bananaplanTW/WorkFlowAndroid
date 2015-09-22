package com.bananaplan.workflowandroid.data;

import com.bananaplan.workflowandroid.data.equipment.MaintenanceRecord;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ben on 2015/7/23.
 */
public class Equipment extends IdData {

    // 運轉中, 停止運轉, 維修中
    public enum Status {
        WIP, STOP, MAINTENANCE
    }

    public String factoryId;

    public Date purchaseDate;
    public Date lastMaintenanceDate;
    public Status status;

    public ArrayList<MaintenanceRecord> records = new ArrayList<>();

    public Equipment(String id, String name) {
        this(id, name, null);
    }

    public Equipment(String id, String name, String factoryId) {
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
