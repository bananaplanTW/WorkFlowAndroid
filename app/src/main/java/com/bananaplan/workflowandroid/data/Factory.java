package com.bananaplan.workflowandroid.data;

import java.util.ArrayList;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Factory extends IdData {

    public ArrayList<WorkerItem> workerItems;


    public Factory(long id, String name) {
        this(id, name, new ArrayList<WorkerItem>());
    }

    public Factory(long id, String name, ArrayList<WorkerItem> workerItems) {
        this.id = id;
        this.name = name;
        this.workerItems = workerItems;
    }
}
