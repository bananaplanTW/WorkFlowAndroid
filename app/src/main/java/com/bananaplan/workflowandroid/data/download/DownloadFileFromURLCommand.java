package com.bananaplan.workflowandroid.data.download;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.network.GetRequestAsyncTask;


/**
 * Created by daz on 10/30/15.
 */
public class DownloadFileFromURLCommand implements IDownloadCommand,
        DownloadFileFromURLStrategy.DownloadProgressListener,
        GetRequestAsyncTask.OnFinishGettingDataListener {

    private GetRequestAsyncTask mGetRequestAsyncTask;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private Context mContext;
    private String mUrlString;
    private String mFilePath;
    private String mFileName;

    private int mNotificationId = 0;


    public DownloadFileFromURLCommand(Context context, String urlString, String fileName) {
        mContext = context;
        mUrlString = urlString;
        mFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName;
        mFileName = fileName;
    }

    @Override
    public void execute() {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse("content://" + mFilePath));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mNotificationId = (int) System.currentTimeMillis();
        String downloadFile = String.format(mContext.getString(R.string.download_file), mFileName);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(downloadFile)
                .setContentText(mContext.getString(R.string.downloading))
                .setAutoCancel(true)
                .setTicker(downloadFile)
                .setContentIntent(pendingIntent);

        DownloadFileFromURLStrategy downloadFileFromURLStrategy = new DownloadFileFromURLStrategy(mUrlString, mFilePath, this);
        mGetRequestAsyncTask = new GetRequestAsyncTask(mContext, downloadFileFromURLStrategy, this);
        mGetRequestAsyncTask.execute();

        Toast.makeText(mContext, downloadFile, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFinishGettingData() {

    }
    @Override
    public void onFailGettingData(boolean isFailCausedByInternet) {

    }


    @Override
    public void updateProgress(int progress) {
        mBuilder.setContentText(mContext.getString(R.string.downloading) + ": " + progress + "%");
        mBuilder.setProgress(100, progress, false);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }
    @Override
    public void downloadCompleted() {
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText(mContext.getString(R.string.download_completed)).setProgress(0, 0, false)
                .setTicker(mContext.getString(R.string.download_completed));

        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }
}
