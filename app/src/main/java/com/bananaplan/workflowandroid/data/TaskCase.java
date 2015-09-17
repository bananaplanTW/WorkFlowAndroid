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
public class TaskCase extends IdData {

    public class Size {
        public int length;
        public int width;
        public int height;
    }

    public long vendorId;
    public long workerId;
    public Date deliveredDate;
    public Date materialPurchasedDate;
    public Date layoutDeliveredDate;
    public int sheetCount;
    public int modelCount;
    public String others;
    public Size size;
    public List<Task> tasks;


    public TaskCase() {
        tasks = new ArrayList<Task>();
    }

    public TaskCase(int id, String name) {
        this(id, name, new ArrayList<Task>());
    }

    public TaskCase(int id, String name, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.vendorId = -1;
        this.tasks = tasks;
    }

    public int getFinishPercent() {
        if (tasks.size() == 0) return 100;
        return getFinishItemsCount() * 100 / tasks.size();
    }

    // TODO: Calculate by taskitems
    public String getHoursPassedBy() {
        return "32 : 12"; // TODO
    }

    // TODO: Calculate by taskitems
    public String getHoursUnFinished() {
        return "12 : 12"; // TODO
    }

    // TODO: Calculate by taskitems
    public String getHoursExpected() {
        return "01 : 12"; // TODO
    }

    public int getFinishItemsCount() {
        int count = 0;
        for (Task item : tasks) {
            if (item.status == Task.Status.FINISH) {
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
