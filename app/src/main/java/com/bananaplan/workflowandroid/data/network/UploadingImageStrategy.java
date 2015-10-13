package com.bananaplan.workflowandroid.data.network;

import android.os.AsyncTask;
import android.os.RecoverySystem;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.data.loading.URLUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by daz on 10/13/15.
 */
public class UploadingImageStrategy implements IPostRequestStrategy {

    private String mFilePath;
    private String mWorkerId;

    public UploadingImageStrategy(String filePath, String workerId) {
        mFilePath = filePath;
        mWorkerId = workerId;
    }

    @Override
    public String upload() {
        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.DEBUG_BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_IMAGE_ACTIVITY, null);
        String responseJSONString = RestfulUtils.restfulPostRequest(urlString, null);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new RecoverySystem.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            File sourceFile = new File(filePath);

            // Adding file data to http body
            entity.addPart("image", new FileBody(sourceFile));

            // Extra parameters if you want to pass to server
            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;
    }
}
