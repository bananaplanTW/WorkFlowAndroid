package com.bananaplan.workflowandroid.data;

/**
 * @author Danny Lin
 * @since 2015/7/30.
 */
public class Warning {

    public enum WarningStatus {
        SOLVED, UNSOLVED
    }

    public long id;
    public String title;
    public WarningStatus status = WarningStatus.UNSOLVED;
    public long taskItemId;
    public long handle;
    public String description;

    public Warning(String title, WarningStatus status) {
        this.title = title;
        this.status = status;
    }
}
