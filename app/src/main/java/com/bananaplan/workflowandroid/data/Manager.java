package com.bananaplan.workflowandroid.data;

import android.graphics.drawable.Drawable;

/**
 * Data structure for a manager
 *
 * @author Danny Lin
 * @since 2015/9/17.
 */
public class Manager extends IdData {

    private Drawable avatar;

    // TODO: 只需要傳現在這些資料就可以

    public Manager(String id, String name, long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void update(Manager manager) {
        this.name = manager.name;
        this.lastUpdatedTime = manager.lastUpdatedTime;
    }
}
