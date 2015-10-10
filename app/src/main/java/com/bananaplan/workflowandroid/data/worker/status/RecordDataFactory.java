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
                RecordData record = (RecordData) DataFactory.genData(recordJSON.getString("receiverId"), BaseData.TYPE.RECORD);
                record.tag = type;
                record.time = new Date(recordJSON.getLong("createdAt"));
                record.reporter = recordJSON.getString("ownerId");
                return record;
            default:
                return null;
        }
    }
}
