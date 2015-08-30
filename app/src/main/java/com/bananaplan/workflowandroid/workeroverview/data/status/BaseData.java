package com.bananaplan.workflowandroid.workeroverview.data.status;

import android.graphics.drawable.Drawable;

import java.util.Date;

/**
 * Created by Ben on 2015/8/29.
 */
public class BaseData {

    public enum TYPE {
        ALL, RECORD, FILE, PHOTO, HISTORY
    }

    public long id;
    public long worker;
    public Drawable avatar;
    public Date time;
    public TYPE type;

    public BaseData() {}

    public BaseData(TYPE type) {
        this.type = type;
    }
}
