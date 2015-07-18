package com.bananaplan.workflowandroid.assigntask.workers;


import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Factory {
    // +++ ben
    private long mId;
    private String mName;
    private ArrayList<TaskCase> mTaskCases;

    public Factory() {
        this(-1L, "", new ArrayList<TaskCase>());
    }

    public Factory(long id, String name, ArrayList<TaskCase> taskCases) {
        mId = id;
        mName = name;
        mTaskCases = taskCases;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return this.mName;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public ArrayList<TaskCase> getTaskCases() {
        return mTaskCases;
    }
    // --- ben

    public List<WorkerItem> workerDatas;


    public Factory(List<WorkerItem> workerDatas) {
        this.workerDatas = workerDatas;
    }
}
