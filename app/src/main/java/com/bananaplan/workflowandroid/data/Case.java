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

    public static class Size {
        public double length;
        public double width;
        public double height;
        public double weight;

        public Size(double[] size) {
            length = size[0];
            width = size[1];
            height = size[2];
            weight = size[3];
        }

        public String toString() {
            return "length = " + length + " " +
                   "width = " + width + " " +
                   "height = " + height + " " +
                   "weight = " + weight;
        }
    }

    public String description;

    public String managerId;
    public String workerId;
    public String vendorId;

    public Date deliveredDate;

    // TODO: Use Adapter pattern
    public Date materialPurchasedDate;
    public Date layoutDeliveredDate;
    public int plateCount;
    public int supportBlockCount;
    public Size movableMoldSize;
    public Size fixedMoldSize;
    public Size supportBlockMoldSize;
    ////////

    public List<String> tagIds;
    public List<String> workerIds;

    public List<Task> tasks;
    public List<Tag> tags;
    public List<Worker> workers;


    public Case() {

    }

    public Case(String id,
                String name,
                String description,
                String vendorId,
                String managerId,
                Date deliveredDate,
                Date materialPurchasedDate,
                Date layoutDeliveredDate,
                Size movableMoldSize,
                Size fixedMoldSize,
                Size supportBlockMoldSize,
                int plateCount,
                int supportBlockCount,
                List<String> tagIds,
                List<String> workerIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.vendorId = vendorId;
        this.managerId = managerId;
        this.deliveredDate = deliveredDate;
        this.materialPurchasedDate = materialPurchasedDate;
        this.layoutDeliveredDate = layoutDeliveredDate;
        this.movableMoldSize = movableMoldSize;
        this.fixedMoldSize = fixedMoldSize;
        this.supportBlockMoldSize = supportBlockMoldSize;
        this.plateCount = plateCount;
        this.supportBlockCount = supportBlockCount;
        this.tagIds = tagIds;
        this.workerIds = workerIds;

        if (this.tagIds == null) {
            this.tagIds = new ArrayList<>();
        }
        if (this.workerIds == null) {
            this.workerIds = new ArrayList<>();
        }
        this.tasks = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.workers = new ArrayList<>();
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
