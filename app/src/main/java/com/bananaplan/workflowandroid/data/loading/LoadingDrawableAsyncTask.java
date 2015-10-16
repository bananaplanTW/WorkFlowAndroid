package com.bananaplan.workflowandroid.data.loading;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import com.bananaplan.workflowandroid.main.MainApplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by daz on 10/10/15.
 */
public class LoadingDrawableAsyncTask extends AsyncTask<Void, Void, Drawable> {

    public interface OnFinishLoadingDataListener {
        void onFinishLoadingData();
        void onFailLoadingData(boolean isFailCausedByInternet);
    }

    private Context mContext;
    private Uri mUri;
    private OnFinishLoadingDataListener mOnFinishLoadingDataListener;
    private Drawable result;

    public LoadingDrawableAsyncTask(Context context, Uri uri, OnFinishLoadingDataListener onFinishLoadingDataListener) {
        mContext = context;
        mUri = uri;
        mOnFinishLoadingDataListener = onFinishLoadingDataListener;
    }


    public Drawable getResult () {
        return result;
    }

    @Override
    protected Drawable doInBackground(Void... voids) {
        if (!MainApplication.sUseTestData) {
            if (RestfulUtils.isConnectToInternet(mContext)) {
                try {
                    InputStream inputStream = null;
                    try {
                        inputStream = new URL(mUri.toString()).openStream();
                        return Drawable.createFromStream(inputStream, mUri.toString());
                    } catch (MalformedURLException e) {
                        cancel(true);
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }
                } catch (IOException e) {
                    cancel(true);
                    e.printStackTrace();
                }
            } else {
                cancel(true);
            }
        }
        return null;
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        mOnFinishLoadingDataListener.onFailLoadingData(true);
    }
    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        result = drawable;
        mOnFinishLoadingDataListener.onFinishLoadingData();
    }
}
