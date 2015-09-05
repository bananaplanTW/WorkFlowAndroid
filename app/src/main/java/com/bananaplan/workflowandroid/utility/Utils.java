package com.bananaplan.workflowandroid.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.tasks.Warning;
import com.bananaplan.workflowandroid.overview.caseoverview.CaseOverviewFragment;
import com.bananaplan.workflowandroid.overview.equipmentoverview.EquipmentOverviewFragment;
import com.bananaplan.workflowandroid.overview.workeroverview.WorkerOverviewFragment;
import com.bananaplan.workflowandroid.utility.data.BarChartData;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.XYChart;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ben on 2015/7/25.
 */
public class Utils {

    public static final String DATE_FORMAT_YMD = "yyyy/MM/dd";
    public static final String DATE_FORMAT_MD = "MM/dd";
    public static final String DATE_FORMAT_YMD_HM_AMPM = "yyyy/MM/dd hh:mm aa";

    public static String getTaskItemStatusString(final Context context, final TaskItem item) {
        String r = "";
        Resources resources = context.getResources();
        switch (item.status) {
            case IN_SCHEDULE:
                r = resources.getString(R.string.task_progress_in_schedule);
                break;
            case NOT_START:
                r = resources.getString(R.string.task_progress_not_start);
                break;
            case PAUSE:
                r = resources.getString(R.string.task_progress_pause);
                break;
            case WORKING:
                r = resources.getString(R.string.task_progress_working);
                break;
            case FINISH:
                if (item.finishDate != null) {
                    r = timestamp2Date(item.finishDate, Utils.DATE_FORMAT_MD) + " ";
                }
                r += resources.getString(R.string.task_progress_finish);
                break;
            default:
                break;
        }
        return r;
    }

    public static View genBarChart(final Activity activity, final BarChartData data) {
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
        renderer.setXLabelsPadding(resources.getDimension(R.dimen.case_overview_statistics_x_axis_padding));
        renderer.setYLabelsPadding(resources.getDimension(R.dimen.case_overview_statistics_x_axis_padding));
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
                series.add(j + 1, data.getData()[i][j]);
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
                if (data.from.equals(CaseOverviewFragment.class.getSimpleName()) || data.from.equals(EquipmentOverviewFragment.class.getSimpleName())) {
                    ((TextView) popupView.findViewById(R.id.date)).setText(data.getDates()[idx]);
                    if (data.getData()[0][idx] != 0) {
                        ((TextView) popupView.findViewById(R.id.working_time)).setText(activity.getResources().getString(R.string.statistics_popup_time_finish) + " " + data.getData()[0][idx] + " : 00");
                    } else {
                        popupView.findViewById(R.id.vg_working_time).setVisibility(View.GONE);
                    }
                    popupView.findViewById(R.id.vg_overtime).setVisibility(View.GONE);
                    popupView.findViewById(R.id.vg_idle).setVisibility(View.GONE);
                } else if (data.from.equals(WorkerOverviewFragment.class.getSimpleName())) {
                    ((TextView) popupView.findViewById(R.id.date)).setText(data.getDates()[idx]);
                    if (data.getData()[0][idx] != 0) {
                        ((TextView) popupView.findViewById(R.id.working_time)).setText(activity.getResources().getString(R.string.statistics_popup_time_work) + " " + data.getData()[0][idx] + " : 00");
                    } else {
                        popupView.findViewById(R.id.vg_working_time).setVisibility(View.GONE);
                    }
                    if (data.getData()[1][idx] > 0) {
                        ((TextView) popupView.findViewById(R.id.overtime)).setText(activity.getResources().getString(R.string.statistics_popup_time_overtime) + " " + data.getData()[1][idx] + " : 00");
                    } else {
                        popupView.findViewById(R.id.vg_overtime).setVisibility(View.GONE);
                    }
                    if (data.getData()[2][idx] > 0) {
                        ((TextView) popupView.findViewById(R.id.idle_time)).setText(activity.getResources().getString(R.string.statistics_popup_time_idle) + " " + data.getData()[2][idx] + " : 00");
                    } else {
                        popupView.findViewById(R.id.vg_idle).setVisibility(View.GONE);
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

    public static void setTaskItemWarningTextView(final Activity activity, final TaskItem item,
                                                  final TextView v, boolean hasClickListener) {
        String displayTxt = "";
        int txtColor;
        Drawable background;
        int unSolvedCount = item.getUnSolvedWarningCount();
        int solvedCount = item.warningList.size() - item.getUnSolvedWarningCount();
        if (unSolvedCount > 1) {
            displayTxt = activity.getResources().getString(R.string.overview_display_warning_txt, unSolvedCount);
        } else if (solvedCount > 1) {
            displayTxt = activity.getResources().getString(R.string.overview_display_warning_txt, solvedCount);
        } else {
            Warning tmp = null;
            for (Warning warning : item.warningList) {
                if (tmp == null) {
                    tmp = warning;
                } else {
                    if (tmp.status == Warning.WarningStatus.SOLVED && warning.status == Warning.WarningStatus.UNSOLVED) {
                        tmp = warning;
                    }
                }
                displayTxt = tmp.title;
            }
        }
        if (unSolvedCount > 0) {
            txtColor = Color.WHITE;
            background = activity.getResources().getDrawable(R.drawable.border_textview_bg_red, null);
        } else {
            txtColor = activity.getResources().getColor(R.color.gray1);
            background = activity.getResources().getDrawable(R.drawable.border_textview_bg_gray, null);
        }
        v.setTextColor(txtColor);
        v.setText(displayTxt);
        v.setBackground(background);
        if (hasClickListener && item.warningList.size() > 1) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = activity.getLayoutInflater().inflate(R.layout.warning_list_container_layout, null);
                    LinearLayout root = (LinearLayout) view.findViewById(R.id.warning_list_container);
                    for (Warning warning : item.warningList) {
                        TextView tv = (TextView) activity.getLayoutInflater().inflate(R.layout.warning_textview_layout, null);
                        setTaskItemWarningTextView(activity, warning, tv);
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

    public static void setTaskItemWarningTextView(final Activity activity, final Warning warning, final TextView v) {
        v.setText(warning.title);
        if (warning.status == Warning.WarningStatus.UNSOLVED) {
            v.setBackground(activity.getResources().getDrawable(R.drawable.border_textview_bg_red, null));
            v.setTextColor(Color.WHITE);
        } else {
            v.setBackground(activity.getResources().getDrawable(R.drawable.border_textview_bg_gray, null));
            v.setTextColor(activity.getResources().getColor(R.color.gray1));
        }
    }
}
