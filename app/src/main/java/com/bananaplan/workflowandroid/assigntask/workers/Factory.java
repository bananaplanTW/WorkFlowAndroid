package com.bananaplan.workflowandroid.assigntask.workers;


import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Factory {

    public List<WorkerItem> workerDatas;


    public Factory(List<WorkerItem> workerDatas) {
        this.workerDatas = workerDatas;
    }
}
