package com.bananaplan.workflowandroid.data;


/**
 * Created by Ben on 2015/7/23.
 */
public class Equipment {

    public long id;
    public String name;
    public WorkerItem worker;
    public long factoryId;

    public Equipment(long id, String name) {
        this(id, name, -1);
    }

    public Equipment(long id, String name, long factoryId) {
        this.id = id;
        this.name = name;
        this.factoryId = factoryId;
    }
}
