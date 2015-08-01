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


    public TaskCase(int id, String name) {
        this(id, name, new ArrayList<TaskItem>());
    }

    public TaskCase(int id, String name, List<TaskItem> taskItems) {
        this.id = id;
        this.name = name;
        this.vendorId = -1;
        this.taskItems = taskItems;
    }

    // TODO: Calaulate by taskitems
    public int getFinishPercent() {
        return (int) (Math.random() * 100);
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
}
