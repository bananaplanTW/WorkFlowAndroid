package com.bananaplan.workflowandroid.data.download;

import android.os.Environment;
import android.util.Log;

import com.bananaplan.workflowandroid.data.network.IGetRequestStrategy;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by daz on 10/30/15.
 */
public class DownloadFileFromURLStrategy implements IGetRequestStrategy {

    private String mUrlString;
    private String mFilePath;
    private DownloadProgressListener mDownloadProgressListener;

    public interface DownloadProgressListener {
        void updateProgress(int progress);
        void downloadCompleted();
    }

    public DownloadFileFromURLStrategy (String urlString, String filePath, DownloadProgressListener downloadProgressListener) {
        mUrlString = urlString;
        mFilePath = filePath;
        mDownloadProgressListener = downloadProgressListener;
    }

    @Override
    public JSONObject get() {
        int count;
        try {
            URL url = new URL(mUrlString);
            URLConnection connection = url.openConnection();
            connection.connect();

            int lenghtOfFile = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream
            OutputStream output = new FileOutputStream(mFilePath);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                mDownloadProgressListener.updateProgress((int) ((total * 100) / lenghtOfFile));
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            mDownloadProgressListener.downloadCompleted();

            // closing streams
            output.close();
            input.close();
        } catch (Exception e) {
            Log.d("DAZZZZ: ", e.getMessage());
        }
        return null;
    }
}
