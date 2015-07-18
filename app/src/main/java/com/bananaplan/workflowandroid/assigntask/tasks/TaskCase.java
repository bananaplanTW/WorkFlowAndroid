package com.bananaplan.workflowandroid.assigntask.tasks;

import com.bananaplan.workflowandroid.assigntask.workers.Factory;

import java.util.List;


/**
 * Data in a task case
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskCase {

    // +++ ben
    public long id;
    public String name;
    private Factory factory;
    // --- ben
    public String personInCharge; // +++ ben: is personInCharge instance of WorkerItem
    public String uncompletedTaskTime;
    public String undergoingTaskTime;
    public int undergoingWorkerCount;
    public List<TaskItem> mTaskItems;


    public TaskCase(int id, String name, String personInCharge, String uncompletedTaskTime, String undergoingTaskTime,
                    int undergoingWorkerCount, List<TaskItem> taskItems) {
        // +++ ben
        this.id = id;
        this.name = name;
        // --- ben
        this.personInCharge = personInCharge;
        this.uncompletedTaskTime = uncompletedTaskTime;
        this.undergoingTaskTime = undergoingTaskTime;
        this.undergoingWorkerCount = undergoingWorkerCount;
        this.mTaskItems = taskItems;
    }

    // +++ ben
    public String getName() {
        return this.name;
    }

    public long getId() {
        return id;
    }

    public Factory getFactory() {
        return factory;
    }

    public int getFinishPercent() {
        return 50; // TODO
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public String getPersonInCharge() {
        return this.personInCharge;
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
    // --- ben
}
