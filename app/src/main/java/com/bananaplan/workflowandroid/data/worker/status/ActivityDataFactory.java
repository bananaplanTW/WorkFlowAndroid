package com.bananaplan.workflowandroid.data.worker.status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by daz on 10/9/15.
 */
public class ActivityDataFactory {
    public static BaseData genData (JSONObject recordJSON) throws JSONException {
        String type = recordJSON.getString("type");
        switch (type) {
            case "checkIn":
            case "checkOut":
            case "becomeWIP":
            case "becomePause":
            case "becomeResume":
            case "becomeOverwork":
            case "becomeStop":
            case "becomePending":
            case "becomeOff":
                // [TODO] should have record builder
                HistoryData attendance = (HistoryData) DataFactory.genData(recordJSON.getString("receiverId"), BaseData.TYPE.HISTORY);
                attendance.tag = type;
                attendance.time = new Date(recordJSON.getLong("createdAt"));
                return attendance;
            case "dispatchTask":
            case "startTask":
            case "suspendTask":
            case "completeTask":
            case "unloadTask":
            case "passReviewTask":
            case "failReviewTask":
            case "createTaskException":
            case "completeTaskException":
                HistoryData task = (HistoryData) DataFactory.genData(recordJSON.getString("receiverId"), BaseData.TYPE.HISTORY);
                task.tag = type;
                task.time = new Date(recordJSON.getLong("createdAt"));
                task.description = recordJSON.getString("taskName");
                return task;
            default:
                return null;
        }
    }
}
