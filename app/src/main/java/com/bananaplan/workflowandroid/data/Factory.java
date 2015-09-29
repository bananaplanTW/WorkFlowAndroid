package com.bananaplan.workflowandroid.data;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Factory extends IdData {

    public List<Manager> managers;
    public List<Worker> workers;


    public Factory(String id, String name, List<Manager> managers, long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.managers = managers;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Factory(String id, String name) {
        this(id, name, new ArrayList<Worker>());
    }

    public Factory(String id, String name, ArrayList<Worker> workers) {
        this.id = id;
        this.name = name;
        this.workers = workers;
    }

    public void update(Factory factory) {
        this.name = factory.name;
        this.managers = factory.managers;
        this.lastUpdatedTime = factory.lastUpdatedTime;
    }
}
