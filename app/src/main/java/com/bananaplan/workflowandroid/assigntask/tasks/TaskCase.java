package com.bananaplan.workflowandroid.assigntask.tasks;

import java.util.List;


/**
 * Data in a task case
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class TaskCase {

    public String personInCharge;
    public String uncompletedTaskTime;
    public String undergoingTaskTime;
    public int undergoingWorkerCount;
    public List<TaskItem> taskDatas;


    public TaskCase(String personInCharge, String uncompletedTaskTime, String undergoingTaskTime,
                    int undergoingWorkerCount, List<TaskItem> taskDatas) {
        this.personInCharge = personInCharge;
        this.uncompletedTaskTime = uncompletedTaskTime;
        this.undergoingTaskTime = undergoingTaskTime;
        this.undergoingWorkerCount = undergoingWorkerCount;
        this.taskDatas = taskDatas;
    }
}
