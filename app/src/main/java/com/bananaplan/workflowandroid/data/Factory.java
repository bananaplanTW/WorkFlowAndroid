package com.bananaplan.workflowandroid.data;

import java.util.ArrayList;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Factory extends IdData {

    public ArrayList<Worker> workers;


    public Factory(String id, String name) {
        this(id, name, new ArrayList<Worker>());
    }

    public Factory(String id, String name, ArrayList<Worker> workers) {
        this.id = id;
        this.name = name;
        this.workers = workers;
    }
}
