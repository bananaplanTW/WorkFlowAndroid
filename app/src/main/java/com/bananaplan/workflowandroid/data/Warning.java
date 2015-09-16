package com.bananaplan.workflowandroid.data;

/**
 * @author Danny Lin
 * @since 2015/7/30.
 */
public class Warning extends IdData {

    public enum WarningStatus {
        SOLVED, UNSOLVED
    }

    public WarningStatus status = WarningStatus.UNSOLVED;
    public long taskItemId;
    public long handle;
    public String description;

    public Warning(String name, WarningStatus status) {
        this.name = name;
        this.status = status;
    }
}
