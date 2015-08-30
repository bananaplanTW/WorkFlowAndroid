package com.bananaplan.workflowandroid.workeroverview.data.status;

import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * Created by Ben on 2015/8/29.
 */
public class PhotoData extends BaseData {
    public long uploader;
    public Drawable photo;
    public String fileName;
    public Uri filePath;

    public PhotoData(BaseData.TYPE type) {
        super(type);
    }
}
