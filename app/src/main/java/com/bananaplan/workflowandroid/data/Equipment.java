package com.bananaplan.workflowandroid.data;

import com.bananaplan.workflowandroid.data.equipment.MaintenanceRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Ben on 2015/7/23.
 */
public class Equipment extends IdData {

    public enum Status {
        WIP, STOP, MAINTENANCE
    }

    public String description;
    public String factoryId;

    public Status status;

    public Date purchasedDate;
    public Date lastMaintenanceDate;

    public HashMap<String, EquipmentTimeCard> timeCards = new HashMap<>();
    public ArrayList<MaintenanceRecord> records = new ArrayList<>();


    public Equipment(
            String id,
            String name,
            String description,
            String factoryId,
            Status status,
            Date purchasedDate,
            long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.factoryId = factoryId;
        this.status = status;
        this.purchasedDate = purchasedDate;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Equipment(String id, String name) {
        this(id, name, null);
    }

    public Equipment(String id, String name, String factoryId) {
        this.id = id;
        this.name = name;
        this.factoryId = factoryId;
    }

    public void update(Equipment equipment) {
        this.name = equipment.name;
        this.description = equipment.description;
        this.factoryId = equipment.factoryId;
        this.status = equipment.status;
        this.purchasedDate = equipment.purchasedDate;
        this.lastUpdatedTime = equipment.lastUpdatedTime;
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

    public static Status convertStringToStatus(String status) {
        Status result = Status.STOP;

        if ("wip".equals(status)) {
            result = Status.WIP;

        } else if ("stop".equals(status)) {
            result = Status.STOP;

        } else if ("maintenance".equals(status)) {
            result = Status.MAINTENANCE;

        }

        return result;
    }

    public long[][] getBarChartData(long start, long end) {
        long[][] data = new long[1][7];
        Arrays.fill(data[0], 0);
        for (EquipmentTimeCard timeCard : timeCards.values()) {
            if (timeCard.startDate >= start && timeCard.endDate < end) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(timeCard.startDate);
                int idx = (cal.get(Calendar.DAY_OF_WEEK) - 1 + 6) % 7;
                long value;
                if (timeCard.status == CaseTimeCard.STATUS.CLOSE) {
                    value = timeCard.endDate - timeCard.startDate;
                } else {
                    value = System.currentTimeMillis() - timeCard.startDate;
                }
                data[0][idx] += value;
            }
        }
        return data;
    }
}
