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
    public String personInCharge;
    public String uncompletedTaskTime;
    public String undergoingTaskTime;
    public int undergoingWorkerCount;
    public List<TaskItem> taskItems;

    public TaskCase(int id, String name, String personInCharge) {
        this(id, name, personInCharge, "", "", 0, new ArrayList<TaskItem>());
    }

    public TaskCase(int id, String name, String personInCharge, String uncompletedTaskTime, String undergoingTaskTime,
                    int undergoingWorkerCount, List<TaskItem> taskItems) {
        this.id = id;
        this.name = name;
        this.personInCharge = personInCharge;
        this.uncompletedTaskTime = uncompletedTaskTime;
        this.undergoingTaskTime = undergoingTaskTime;
        this.undergoingWorkerCount = undergoingWorkerCount;
        this.taskItems = taskItems;
        this.vendorId = -1;
    }

    public int getFinishPercent() {
        return 50; // TODO
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
