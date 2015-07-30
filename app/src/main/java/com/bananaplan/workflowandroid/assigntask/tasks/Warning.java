package com.bananaplan.workflowandroid.assigntask.tasks;

/**
 * Created by logicmelody on 2015/7/30.
 */
public class Warning {

    public enum Status {
        SOLVED, UNSOLVED
    };

    public long id;
    public String title;
    public Status status = Status.UNSOLVED;


    public Warning(long id, String title) {

    }
}
