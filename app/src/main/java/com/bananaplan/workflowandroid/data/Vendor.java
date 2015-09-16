package com.bananaplan.workflowandroid.data;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/7/22.
 */
public class Vendor extends IdData {

    public ArrayList<TaskCase> taskCases;


    public Vendor(long id, String name) {
        this(id, name, new ArrayList<TaskCase>());
    }

    public Vendor(long id, String name, ArrayList<TaskCase> taskCases) {
        this.id = id;
        this.name = name;
        this.taskCases = taskCases;
    }
}
