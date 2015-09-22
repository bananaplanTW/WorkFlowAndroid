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
public class Case extends IdData {

    public class Size {
        public float length;
        public float width;
        public float height;
        public float weight;
    }

    public String description;

    public String managerId;
    public String workerId;
    public String vendorId;

    public Date deliveredDate;

    ////////  TODO: Use Adapter pattern
    public Date materialPurchasedDate;
    public Date layoutDeliveredDate;
    public int plateCount;
    public int supportBlockCount;
    public Size movableMoldSize;
    public Size fixedMoldSize;
    public Size supportBlockMoldSize;
    ////////

    public List<Task> tasks;
    public List<Tag> tags;


    public Case() {
        tasks = new ArrayList<Task>();
    }

    public Case(String id, String name) {
        this(id, name, new ArrayList<Task>());
    }

    public Case(String id, String name, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.vendorId = null;
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
            if (item.status == Task.Status.DONE) {
                count++;
            }
        }
        return count;
    }

    public String getSize() {
        if (this.movableMoldSize == null) return "0 x 0 x 0";
        return this.movableMoldSize.length + " x " + this.movableMoldSize.width + " x " + this.movableMoldSize.height;
    }
}
