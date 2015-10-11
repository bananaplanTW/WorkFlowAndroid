package com.bananaplan.workflowandroid.data.loading.loadingactivities;

import org.json.JSONArray;

/**
 * Created by daz on 10/11/15.
 */
public interface ILoadingActivitiesStrategy {
    enum ActivityCategory {
        WORKER, TASK, WARNING, NONE
    };
    JSONArray load();
    ActivityCategory getCategory();
}
