package com.bananaplan.workflowandroid.workeroverview.data;

import com.bananaplan.workflowandroid.workeroverview.data.BaseData.TYPE;

/**
 * Created by Ben on 2015/8/29.
 */
public class DataFactory {
    public static BaseData genData(long worker, TYPE type) {
        BaseData data = null;
        switch (type) {
            case RECORD:
                data = new RecordData(type);
                break;
            case FILE:
                data = new FileData(type);
                break;
            case PHOTO:
                data = new PhotoData(type);
                break;
            case HISTORY:
                data = new HistoryData(type);
                break;
            default:
                throw new IllegalArgumentException("DataFactory.genData type = " + type);
        }
        return data;
    }
}
