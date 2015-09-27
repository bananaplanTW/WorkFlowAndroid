package com.bananaplan.workflowandroid.data;

/**
 * Created by logicmelody on 2015/9/22.
 */
public class Tag extends IdData {

    public Tag(String id, String name, long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void update(Tag tag) {
        this.name = tag.name;
        this.lastUpdatedTime = tag.lastUpdatedTime;
    }
}
