package com.bananaplan.workflowandroid.assigntask.tasks;

import java.util.ArrayList;
import java.util.List;


/**
 * Data in a task case
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskCase {
    public long id;
    public String name;
    public long vendorId;
    public long workerId;
    public List<TaskItem> taskItems;
    public int finishPercent;

    public TaskCase(int id, String name) {
        this(id, name, new ArrayList<TaskItem>());
    }

    public TaskCase(int id, String name, List<TaskItem> taskItems) {
        this.id = id;
        this.name = name;
        this.vendorId = -1;
        this.taskItems = taskItems;
        this.finishPercent = (int) (Math.random() * 100 + 1);
    }

    public int getFinishPercent() {
        return  this.finishPercent; // TODO
    }

    public String getHoursPassedBy() {
        return "32 : 12 : 16"; // TODO
    }

    public String getHoursUnFinished() {
        return "12 : 12 : 46"; // TODO
    }

    public String getHoursForecast() {
        return "01 : 12 : 22"; // TODO
    }
}
