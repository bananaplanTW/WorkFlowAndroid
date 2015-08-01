package com.bananaplan.workflowandroid.assigntask.tasks;

/**
 * @author Danny Lin
 * @since 2015/7/30.
 */
public class Warning {

    public enum WarningStatus {
        SOLVED, UNSOLVED
    }

    public String title;
    public WarningStatus status = WarningStatus.UNSOLVED;


    public Warning(String title, WarningStatus status) {
        this.title = title;
        this.status = status;
    }
}
