package com.bananaplan.workflowandroid.data.download;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

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

    public DownloadFileFromURLCommand(Context context, String urlString, String fileName) {
        mContext = context;
        mUrlString = urlString;
        mFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName;
    }


    @Override
    public void execute() {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(mContext.getString(R.string.download_file))
                .setContentText(mContext.getString(R.string.downloading))
                .setSmallIcon(R.drawable.black_arrow_down);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);

        resultIntent.setData(Uri.parse("content://" + mFilePath));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(pendingIntent);

        DownloadFileFromURLStrategy downloadFileFromURLStrategy = new DownloadFileFromURLStrategy(mUrlString, mFilePath, this);
        mGetRequestAsyncTask = new GetRequestAsyncTask(mContext, downloadFileFromURLStrategy, this);
        mGetRequestAsyncTask.execute();
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
        mNotificationManager.notify(1, mBuilder.build());
    }
    @Override
    public void downloadCompleted() {
        mBuilder.setContentText(mContext.getString(R.string.download_completed)).setProgress(0, 0, false);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
