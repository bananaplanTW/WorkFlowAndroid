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


    public Manager(String name) {
        this.name = name;
    }

    public Manager(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
