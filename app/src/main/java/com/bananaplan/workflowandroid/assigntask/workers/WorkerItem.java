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

    // TODO: Delete
    public static final class WorkingStatus {
        public static final int NORMAL = 0;
        public static final int DELAY = 1;
    }

    public long id;
    public String task; // TODO: Delete
    public String name;
    public String title;
    public Drawable avatar;
    public long factoryId;
    public ArrayList<TaskItem> taskItems;
    public TaskItem currentTaskItem;


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

    // TODO: Delete
    public int getStatus() {
        return WorkingStatus.NORMAL;
    }

    // TODO: Delete
    public String getTime() {
        return "6:33:11";
    }
}
