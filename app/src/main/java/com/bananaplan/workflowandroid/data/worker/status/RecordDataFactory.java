package com.bananaplan.workflowandroid.data.worker.status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by daz on 10/9/15.
 */
public class RecordDataFactory {
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
                RecordData record1 = (RecordData) DataFactory.genData(recordJSON.getString("receiverId"), BaseData.TYPE.RECORD);
                record1.tag = type;
                record1.time = new Date(recordJSON.getLong("createdAt"));
                record1.reporter = recordJSON.getString("ownerId");
                return record1;
            case "dispatchTask":
            case "startTask":
            case "suspendTask":
            case "completeTask":
            case "unloadTask":
            case "passReviewTask":
            case "failReviewTask":
            case "createTaskException":
            case "completeTaskException":
                RecordData record2 = (RecordData) DataFactory.genData(recordJSON.getString("receiverId"), BaseData.TYPE.RECORD);
                record2.tag = type;
                record2.time = new Date(recordJSON.getLong("createdAt"));
                record2.reporter = recordJSON.getString("ownerId");
                record2.description = recordJSON.getString("taskName");
                return record2;
            default:
                return null;
        }
    }
}
