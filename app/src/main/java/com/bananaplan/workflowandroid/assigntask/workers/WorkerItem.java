package com.bananaplan.workflowandroid.assigntask.workers;

import android.graphics.drawable.Drawable;

import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;

import java.util.ArrayList;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class WorkerItem {

    public static Drawable sDefaultAvatarDrawable;

    public long id;
    public long factoryId;
    public String name;
    public String title;
    public TaskItem currentTaskItem;
    public ArrayList<TaskItem> taskItems;
    public String address;
    public String phone;

    private Drawable avatar;


    public WorkerItem(long id, String name, String title) {
        this(id, name, title, new ArrayList<TaskItem>());
    }

    public WorkerItem(long id, String name, String title, ArrayList<TaskItem> taskItems) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.taskItems = taskItems;
    }

    public Drawable getAvator() {
        if (this.avatar != null) {
            return this.avatar;
        }
        return sDefaultAvatarDrawable;
    }

    public boolean hasCurrentTaskItem() {
        return currentTaskItem != null;
    }
}
