package com.bananaplan.workflowandroid.utility;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Equipment;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.download.DownloadFileFromURLCommand;
import com.bananaplan.workflowandroid.detail.task.DetailedTaskActivity;
import com.bananaplan.workflowandroid.detail.warning.DetailedWarningActivity;
import com.bananaplan.workflowandroid.detail.worker.DetailedWorkerActivity;
import com.bananaplan.workflowandroid.overview.caseoverview.CaseOverviewFragment;
import com.bananaplan.workflowandroid.overview.equipmentoverview.EquipmentOverviewFragment;
import com.bananaplan.workflowandroid.overview.workeroverview.WorkerOverviewFragment;
import com.bananaplan.workflowandroid.utility.data.BarChartData;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.TaskWarning;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.XYChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ben on 2015/7/25.
 */
public class Utils {

    public static final String DATE_FORMAT_YMD = "yyyy/MM/dd";
    public static final String DATE_FORMAT_MD = "MM/dd";
    public static final String DATE_FORMAT_YMD_HM_AMPM = "yyyy/MM/dd hh:mm aa";
    public static final String DATE_FORMAT_HM_AMPM = "hh:mm aa";


    public static View genBarChart(final Activity activity, final BarChartData data) {
        if (data == null || data.getData() == null) return null;
        Resources resources = activity.getResources();
        final String[] axis_x_string = resources.getStringArray(R.array.week);
        final XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setTextTypeface(null, Typeface.NORMAL);

        renderer.setShowGrid(true);
        renderer.setGridColor(resources.getColor(R.color.gray3));

        renderer.setLabelsColor(Color.BLACK);
        renderer.setAxesColor(resources.getColor(R.color.gray1));
        renderer.setBarSpacing(0.5);

        renderer.setXTitle("");
        renderer.setYTitle("");
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setLabelsTextSize(activity.getResources().getDimensionPixelSize(R.dimen.ov_statistics_axis_text_size));
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setXLabelsPadding(resources.getDimension(R.dimen.ov_statistics_x_axis_padding));
        renderer.setYLabelsPadding(resources.getDimension(R.dimen.ov_statistics_x_axis_padding));
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.CENTER);
        renderer.setXLabelsAngle(0);
        renderer.setShowTickMarks(false);

        renderer.setXLabels(0);
        renderer.setYAxisMin(0);

        for (int i = 0; i < data.getData().length; i++) {
            final XYSeries series = new XYSeries("");
            dataSet.addSeries(series);
            XYSeriesRenderer yRenderer = new XYSeriesRenderer();
            renderer.addSeriesRenderer(yRenderer);
            yRenderer.setColor(resources.getColor(data.sColorId[i]));
            series.add(0, 0);
            renderer.addXTextLabel(0, "");
            for (int j = 0; j < data.getData()[i].length; j++) {
                renderer.addXTextLabel(j + 1, axis_x_string[j]);
                series.add(j + 1, ((float) data.getData()[i][j]) / 3600000);
            }
            series.add(data.getData()[i].length + 1, 0);
            renderer.addXTextLabel(data.getData()[i].length + 1, "");
        }

        renderer.setZoomEnabled(false);
        renderer.setZoomEnabled(false, false);
        renderer.setClickEnabled(true);
        renderer.setPanEnabled(false);
        renderer.setShowLegend(false);
        try {
            renderer.setDrawGridAfterBar(true);
            renderer.setHighLightRectEnabled(true);
            renderer.setHighLightRectStrokeWidth(activity.getResources().getDimensionPixelOffset(R.dimen.ov_statistics_highlight_stroke_width));
            renderer.setHighLightRectStrokeColor(activity.getResources().getColor(R.color.gray3));
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
        View view = ChartFactory.getBarChartView(activity, dataSet, renderer, BarChart.Type.DEFAULT);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(v instanceof GraphicalView)) return;
                final GraphicalView view = (GraphicalView) v;
                SeriesSelection selection = view.getCurrentSeriesAndPoint();
                try {
                    view.onClick(selection);
                } catch (NoSuchMethodError e) {
                    e.printStackTrace();
                }
                if (selection == null) return;
                int idx = selection.getPointIndex() - 1;
                View popupView = activity.getLayoutInflater().inflate(R.layout.bar_chart_popup, null);
                if (data.from.equals(CaseOverviewFragment.class.getSimpleName()) ||
                        data.from.equals(EquipmentOverviewFragment.class.getSimpleName())) {
                    ((TextView) popupView.findViewById(R.id.date)).setText(data.getDate()[idx]);
                    if (data.getData()[0][idx] >= 0) {
                        int hour = (int) data.getData()[0][idx] / 3600000;
                        int min = (int) ((data.getData()[0][idx] % 3600000)) / 60000;
                        ((TextView) popupView.findViewById(R.id.working_time)).setText(
                                activity.getResources().getString(R.string.statistics_popup_time_finish) + " " + (hour < 10 ? ("0" + hour) : hour) + " : " + (min < 10 ? ("0" + min) : min));
                    } else {
                        popupView.findViewById(R.id.vg_working_time).setVisibility(View.GONE);
                    }
                    popupView.findViewById(R.id.vg_overtime).setVisibility(View.GONE);
                } else if (data.from.equals(WorkerOverviewFragment.class.getSimpleName())) {
                    ((TextView) popupView.findViewById(R.id.date)).setText(data.getDate()[idx]);
                    if (data.getData()[0][idx] >= 0) {
                        int hour = (int) data.getData()[0][idx] / 3600000;
                        int min = (int) ((data.getData()[0][idx] % 3600000)) / 60000;
                        ((TextView) popupView.findViewById(R.id.working_time)).setText(
                                activity.getResources().getString(R.string.statistics_popup_time_work) + " " + (hour < 10 ? ("0" + hour) : hour) + " : " + (min < 10 ? ("0" + min) : min));
                    } else {
                        popupView.findViewById(R.id.vg_working_time).setVisibility(View.GONE);
                    }
                    if (data.getData()[1][idx] >= 0) {
                        int hour = (int) data.getData()[1][idx] / 3600000;
                        int min = (int) ((data.getData()[1][idx] % 3600000)) / 60000;
                        ((TextView) popupView.findViewById(R.id.overtime)).setText(
                                activity.getResources().getString(R.string.statistics_popup_time_overtime) + " " + (hour < 10 ? ("0" + hour) : hour) + " : " + (min < 10 ? ("0" + min) : min));
                    } else {
                        popupView.findViewById(R.id.vg_overtime).setVisibility(View.GONE);
                    }
                }
                final PopupWindow popup = new PopupWindow(popupView,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                popup.setBackgroundDrawable(new BitmapDrawable());
                popup.setOutsideTouchable(true);
                popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        try {
                            view.onClick(null);
                        } catch (NoSuchMethodError e) {
                            e.printStackTrace();
                        }
                    }
                });
                /* calculate popup position */
                popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                int[] dateChooserLocations = new int[2];
                (activity.findViewById(R.id.ov_statistics_week_chooser)).getLocationOnScreen(dateChooserLocations);
                int[] statisticsViewLocations = new int[2];
                v.getLocationOnScreen(statisticsViewLocations);
                int[] barChartLocations = new int[2];
                v.getLocationOnScreen(barChartLocations);
                int x_pos = barChartLocations[0] + (int) ((XYChart) view.getChart()).getXAxisPos((double) (idx + 1)) - popupView.getMeasuredWidth() / 2;
                int y_base = dateChooserLocations[1] + (activity.findViewById(R.id.ov_statistics_week_chooser)).getMeasuredHeight();
                int y_pos = (statisticsViewLocations[1] - y_base) / 2 + y_base - popupView.getMeasuredHeight() / 2;
                popup.showAtLocation(v, Gravity.NO_GRAVITY, x_pos, y_pos);
            }
        });
        return view;
    }

    public static String timestamp2Date(Date date, String format) {
        if (date == null) return "";

        String r;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);

        cal.setTimeInMillis(date.getTime());
        r = DateFormat.format(format, cal).toString();

        return r;
    }

    public static String milliSeconds2MinsSecs(long millis) {
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return Integer.toString(hours) + " : " + Integer.toString(minutes);
    }

    public static void setTaskItemWarningTextView(final Activity activity, final Task item,
                                                  final TextView v, boolean hasClickListener) {
        if (item.taskWarnings.size() == 0) {
            v.setVisibility(View.GONE);
            return;
        }

        v.setVisibility(View.VISIBLE);

        String displayTxt = "";
        int txtColor;
        Drawable background;
        int unSolvedCount = item.getUnSolvedWarningCount();
        int solvedCount = item.taskWarnings.size() - item.getUnSolvedWarningCount();
        if (unSolvedCount > 1) {
            displayTxt = activity.getResources().getString(R.string.overview_display_warning_txt, unSolvedCount);
        } else if (solvedCount > 1) {
            displayTxt = activity.getResources().getString(R.string.overview_display_warning_txt, solvedCount);
        } else {
            TaskWarning tmp = null;
            for (TaskWarning taskWarning : item.taskWarnings) {
                if (tmp == null) {
                    tmp = taskWarning;
                } else {
                    if (tmp.status == TaskWarning.Status.CLOSED && taskWarning.status == TaskWarning.Status.OPENED) {
                        tmp = taskWarning;
                    }
                }
                displayTxt = tmp.name;
            }
        }
        if (unSolvedCount > 0) {
            txtColor = activity.getResources().getColor(R.color.red);
            background = activity.getResources().getDrawable(R.drawable.border_textview_bg_red, null);
        } else {
            txtColor = activity.getResources().getColor(R.color.gray1);
            background = activity.getResources().getDrawable(R.drawable.border_textview_bg_gray, null);
        }
        v.setTextColor(txtColor);
        v.setText(displayTxt);
        v.setBackground(background);
        if (hasClickListener && item.taskWarnings.size() > 1) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = activity.getLayoutInflater().inflate(R.layout.warning_list_container_layout, null);
                    LinearLayout root = (LinearLayout) view.findViewById(R.id.warning_list_container);
                    for (TaskWarning taskWarning : item.taskWarnings) {
                        TextView tv = (TextView) activity.getLayoutInflater().inflate(R.layout.warning_textview_layout, null);
                        setTaskItemWarningTextView(activity, taskWarning, tv);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = (int) activity.getResources().getDimension(R.dimen.ov_warning_margin_horizontal);
                        params.rightMargin = (int) activity.getResources().getDimension(R.dimen.ov_warning_margin_horizontal);
                        root.addView(tv, params);
                    }
                    final PopupWindow popup = new PopupWindow(view,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    popup.setBackgroundDrawable(new BitmapDrawable());
                    popup.setOutsideTouchable(true);
                    int[] locations = new int[2];
                    v.getLocationOnScreen(locations);
                    view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    popup.showAtLocation(v, Gravity.NO_GRAVITY, locations[0] + v.getMeasuredWidth() - view.getMeasuredWidth(), locations[1] - view.getMeasuredHeight());
                }
            });
        }
    }

    public static void setTaskItemWarningTextView(final Activity activity, final TaskWarning taskWarning, final TextView v) {
        v.setText(taskWarning.name);
        if (taskWarning.status == TaskWarning.Status.OPENED) {
            v.setBackground(activity.getResources().getDrawable(R.drawable.border_textview_bg_red, null));
            v.setTextColor(activity.getResources().getColor(R.color.red));
        } else {
            v.setBackground(activity.getResources().getDrawable(R.drawable.border_textview_bg_gray, null));
            v.setTextColor(activity.getResources().getColor(R.color.gray1));
        }
    }

    public static String pad(int c) {
        StringBuilder s = new StringBuilder();
        int absC = Math.abs(c);

        if (c < 0) {
            s.append("-");
        }

        if (absC >= 10) {
            s.append(String.valueOf(absC));
        } else {
            s.append("0").append(String.valueOf(absC));
        }

        return s.toString();
    }

    /**
     * Convert time(hours and minutes) to milliseconds format.
     *
     * @param hours
     * @param minutes
     * @return Equal time in milliseconds
     */
    public static long timeToMilliseconds(int hours, int minutes) {
        return TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes);
    }

    /**
     * Convert time from milliseconds to hh:mm format.
     *
     * @param milliseconds
     * @return Equal time in xx hours xx minutes
     */
    public static int[] millisecondsToTime(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hoursInMinutes = TimeUnit.HOURS.toMinutes(hours);

        return new int[]{(int) hours, (int) (minutes - hoursInMinutes)};
    }

    /**
     * Convert time from milliseconds to hh:mm format.
     *
     * @param milliseconds
     * @return Equal time in hh:mm
     */
    public static String millisecondsToTimeString(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hoursInMinutes = TimeUnit.HOURS.toMinutes(hours);

        return pad((int) hours) + ":" + pad((int) (minutes - hoursInMinutes));
    }

    /**
     * Convert time from milliseconds to mm/dd/yyyy format.
     *
     * @param context
     * @param milliseconds
     * @return Equal time in mm/dd/yyyy
     */
    public static String millisecondsToDate(Context context, long milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);

        return String.format(context.getString(R.string.date_format_string),
                pad(c.get(Calendar.MONTH) + 1),
                pad(c.get(Calendar.DAY_OF_MONTH)),
                pad(c.get(Calendar.YEAR)));
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            } else if (isDownloadedDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                return getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadedDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isSameId(String id1, String id2) {
        return id1.equals(id2);
    }

    public static void replaceProgressBarWhenLoadingFinished(Context context, View mainView, ProgressBar progressBar) {
        Animation fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

        mainView.setAnimation(fadeIn);
        progressBar.setAnimation(fadeOut);

        mainView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private static final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};

    public static boolean isImage(String filePath) {
        File file = new File(filePath);

        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap scaleBitmap(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) return null;
        int targetW = (int) context.getResources().getDimension(R.dimen.photo_thumbnail_max_width);
        int targetH = (int) context.getResources().getDimension(R.dimen.photo_thumbnail_max_height);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor < 1 ? 1 : scaleFactor;
        return BitmapFactory.decodeFile(filePath, bmOptions);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ?
                        (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public static String getWorkerWipEquipmentName(Context context, Worker worker) {
        String result = context.getString(R.string.no_equipment);

        if (worker.hasWipTask()) {
            Equipment wipEquipment = WorkingData.getInstance(context).getEquipmentById(worker.getWipTask().equipmentId);

            if (wipEquipment != null) {
                result = wipEquipment.name;
            }
        }

        return result;
    }

    public static void downloadFile(Context context, String urlString, String fileName) {
        DownloadFileFromURLCommand downloadFileFromURLCommand =
                new DownloadFileFromURLCommand(context, urlString, fileName);
        downloadFileFromURLCommand.execute();
    }

    /**
     * Use this method to show toast in non-UI thread
     *
     * @param context
     * @param text
     */
    public static void showToastInNonUiThread(final Context context, final String text) {
        new Handler().post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Set the style of TextView according to the status of a task
     *
     * @param context
     * @param status
     * @param task
     */
    public static void setTaskStatusForTextView(Context context, TextView status, Task task) {
        status.setText(Task.getTaskStatusString(context, task));

        switch (task.status) {
            case IN_REVIEW:
                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
                status.setBackgroundResource(R.drawable.task_card_status_in_review_background);
                break;

            case WIP:
                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
                status.setBackgroundResource(R.drawable.task_card_status_wip_background);
                break;

            case PENDING:
                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
                status.setBackgroundResource(R.drawable.task_card_status_pending_background);
                break;

            case UNCLAIMED:
                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
                status.setBackgroundResource(R.drawable.task_card_status_unclaimed_background);
                break;

            case WARNING:
                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
                status.setBackgroundResource(R.drawable.task_card_status_warning_background);
                break;

            case DONE:
                status.setTextColor(context.getResources().getColor(R.color.task_card_status_done_text_color));
                status.setBackground(null);
                break;
        }
    }

    public static final void showDetailedTaskActivity(Context context, String taskId) {
        Intent intent = new Intent(context, DetailedTaskActivity.class);
        intent.putExtra(DetailedTaskActivity.EXTRA_TASK_ID, taskId);
        context.startActivity(intent);
    }

    public static final void showDetailedWarningActivity(Context context, String warningId) {
        Intent intent = new Intent(context, DetailedWarningActivity.class);
        intent.putExtra(DetailedWarningActivity.EXTRA_WARNING_ID, warningId);
        context.startActivity(intent);
    }

    public static final void showDetailedWorkerActivity(Context context, String workerId) {
        Intent intent = new Intent(context, DetailedWorkerActivity.class);
        intent.putExtra(DetailedWorkerActivity.EXTRA_WORKER_ID, workerId);
        context.startActivity(intent);
    }
}
