package com.bananaplan.workflowandroid.data;

import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

    public long lastUpdatedTime = -1L;

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

    public List<String> workerIds = new ArrayList<>();
    public List<Tag> tags = new ArrayList<>();

    public List<Task> tasks = new ArrayList<>();
    public HashMap<String, CaseTimeCard> timeCards = new HashMap<>();


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
                List<Tag> tags,
                List<String> workerIds,
                long lastUpdatedTime) {
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
        this.tags = tags;
        this.workerIds = workerIds;
        this.lastUpdatedTime = lastUpdatedTime;

        this.tasks = new ArrayList<>();
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

    public void update(Case aCase) {
        this.name = aCase.name;
        this.description = aCase.description;
        this.vendorId = aCase.vendorId;
        this.managerId = aCase.managerId;
        this.deliveredDate = aCase.deliveredDate;
        this.materialPurchasedDate = aCase.materialPurchasedDate;
        this.layoutDeliveredDate = aCase.layoutDeliveredDate;
        this.movableMoldSize = aCase.movableMoldSize;
        this.fixedMoldSize = aCase.fixedMoldSize;
        this.supportBlockMoldSize = aCase.supportBlockMoldSize;
        this.plateCount = aCase.plateCount;
        this.supportBlockCount = aCase.supportBlockCount;
        this.tags = aCase.tags;
        this.workerIds = aCase.workerIds;
        this.lastUpdatedTime = aCase.lastUpdatedTime;
    }

    public long getSpentTime() {
        long time = 0L;
        for (Task task : tasks) {
            time += task.spentTime;
        }

        return time;
    }

    public long getUnfinishedTime() {
        long time = 0L;
        for (Task task : tasks) {
            time += task.expectedTime;
        }

        return time - getSpentTime() < 0L ? 0L : time - getSpentTime();
    }

    public int getFinishedPercent() {
        return tasks.size() <= 0 ? 100 : getFinishItemsCount() * 100 / tasks.size();
    }

    public String getHoursExpected() {
        long time = 0L;
        for (Task task : tasks) {
            time += task.expectedTime;
        }
        return Utils.millisecondsToTimeString(time);
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

    public long[][] getBarChartData(long start, long end) {
        long[][] data = new long[1][7];
        Arrays.fill(data[0], 0);
        for (CaseTimeCard timeCard : timeCards.values()) {
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

    public int getUnSolvedWarningCount() {
        int count = 0;
        for (Task task : tasks) {
            count += task.getUnSolvedWarningCount();
        }
        return count;
    }
}
