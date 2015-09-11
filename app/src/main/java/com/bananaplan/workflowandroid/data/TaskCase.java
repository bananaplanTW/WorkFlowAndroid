package com.bananaplan.workflowandroid.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Data in a task case
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskCase {
    public class Size {
        public int length;
        public int width;
        public int height;
    }

    public long id;
    public String name;
    public long vendorId;
    public long workerId;
    public Date deliveredDate;
    public Date materialPurchasedDate;
    public Date layoutDeliveredDate;
    public int sheetCount;
    public int modelCount;
    public String others;
    public Size size;
    public List<TaskItem> taskItems;


    public TaskCase() {
        taskItems = new ArrayList<TaskItem>();
    }

    public TaskCase(int id, String name) {
        this(id, name, new ArrayList<TaskItem>());
    }

    public TaskCase(int id, String name, List<TaskItem> taskItems) {
        this.id = id;
        this.name = name;
        this.vendorId = -1;
        this.taskItems = taskItems;
    }

    public int getFinishPercent() {
        if (taskItems.size() == 0) return 100;
        return getFinishItemsCount() * 100 / taskItems.size();
    }

    // TODO: Calaulate by taskitems
    public String getHoursPassedBy() {
        return "32 : 12"; // TODO
    }

    // TODO: Calaulate by taskitems
    public String getHoursUnFinished() {
        return "12 : 12"; // TODO
    }

    // TODO: Calaulate by taskitems
    public String getHoursForecast() {
        return "01 : 12"; // TODO
    }

    public int getFinishItemsCount() {
        int count = 0;
        for (TaskItem item : taskItems) {
            if (item.status == TaskItem.Status.FINISH) {
                count++;
            }
        }
        return count;
    }

    public String getSize() {
        if (this.size == null) return "0 x 0 x 0";
        return this.size.length + " x " + this.size.width + " x " + this.size.height;
    }
}
