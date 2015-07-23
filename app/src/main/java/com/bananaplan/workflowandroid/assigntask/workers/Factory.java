package com.bananaplan.workflowandroid.assigntask.workers;


import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Factory {
    public long id;
    public String name;
    public ArrayList<WorkerItem> workerItems;

    public Factory(long id, String name) {
        this(id, name, new ArrayList<WorkerItem>());
    }

    public Factory(long id, String name, ArrayList<WorkerItem> workerDatas) {
        this.id = id;
        this.name = name;
        this.workerItems = workerDatas;
    }
}
