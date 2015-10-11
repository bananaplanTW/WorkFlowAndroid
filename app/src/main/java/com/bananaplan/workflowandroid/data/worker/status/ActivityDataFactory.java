package com.bananaplan.workflowandroid.data.worker.status;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.LoadingDrawableAsyncTask;
import com.bananaplan.workflowandroid.data.loading.LoadingPhotoDataCommand;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by daz on 10/9/15.
 */
public class ActivityDataFactory {
    public static BaseData genData (JSONObject recordJSON, Context context) throws JSONException {
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
                attendance.category = BaseData.CATEGORY.WORKER;
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
                task.category = BaseData.CATEGORY.WORKER;
                task.time = new Date(recordJSON.getLong("createdAt"));
                task.description = recordJSON.getString("taskName");
                return task;
            case "comment":
                RecordData comment = (RecordData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.RECORD);
                comment.tag = type;
                comment.time = new Date(recordJSON.getLong("createdAt"));
                comment.description = recordJSON.getString("content");
                return comment;
            case "attachment":
                if (recordJSON.getString("contentType").equals("image")) {
                    PhotoData photoData = (PhotoData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.PHOTO);
                    photoData.tag = type;
                    photoData.time = new Date(recordJSON.getLong("createdAt"));
                    photoData.fileName = recordJSON.getString("name");

                    Uri.Builder thumbBuilder = Uri.parse(LoadingDataUtils.WorkingDataUrl.BASE_URL).buildUpon();
                    thumbBuilder.path(recordJSON.getString("thumbUrl"));
                    Uri thumbUri = thumbBuilder.build();
                    LoadingPhotoDataCommand loadingPhotoDataCommand = new LoadingPhotoDataCommand(context, thumbUri, photoData);
                    loadingPhotoDataCommand.execute();

                    Uri.Builder imageBuilder = Uri.parse(LoadingDataUtils.WorkingDataUrl.BASE_URL).buildUpon();
                    imageBuilder.path(recordJSON.getString("imageUrl"));
                    photoData.filePath = imageBuilder.build();
                    return photoData;
                } else if (recordJSON.getString("contentType").equals("file")) {
                    FileData fileData = (FileData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.FILE);
                    fileData.tag = type;
                    fileData.time = new Date(recordJSON.getLong("createdAt"));
                    fileData.fileName = recordJSON.getString("name");

                    Uri.Builder builder = Uri.parse(LoadingDataUtils.WorkingDataUrl.BASE_URL).buildUpon();
                    builder.path(recordJSON.getString("fileUrl"));
                    fileData.filePath = builder.build();
                    return fileData;
                }


                // Task specific activities
            case "dispatch":
                HistoryData dispatchTask = (HistoryData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.HISTORY);
                dispatchTask.tag = type;
                dispatchTask.category = BaseData.CATEGORY.TASK;
                dispatchTask.time = new Date(recordJSON.getLong("createdAt"));
                dispatchTask.description = recordJSON.getString("employeeName");
                return dispatchTask;
            case "start":
            case "pause":
            case "resume":
            case "suspend":
            case "complete":
            case "fail":
            case "pass":
                HistoryData taskStatus = (HistoryData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.HISTORY);
                taskStatus.tag = type;
                taskStatus.category = BaseData.CATEGORY.TASK;
                taskStatus.time = new Date(recordJSON.getLong("createdAt"));
                return taskStatus;
            case "create_exception":
            case "complete_exception":
                HistoryData taskException = (HistoryData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.HISTORY);
                taskException.tag = type;
                taskException.category = BaseData.CATEGORY.TASK;
                taskException.time = new Date(recordJSON.getLong("createdAt"));
                taskException.description = recordJSON.getString("exceptionName");
                return taskException;
            default:
                return null;
        }
    }
}