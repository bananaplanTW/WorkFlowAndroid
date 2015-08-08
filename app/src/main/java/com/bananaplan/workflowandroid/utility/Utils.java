package com.bananaplan.workflowandroid.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.View;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Ben on 2015/7/25.
 */
public class Utils {

    public static String getTaskItemStatusString(final Context context, final TaskItem.Status status) {
        String r = "";
        Resources resources = context.getResources();
        switch (status) {
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
                r = resources.getString(R.string.task_progress_finish);
                break;
            default:
                break;
        }
        return r;
    }

    public static View genBarChart(final Activity activity, BarChartData data) {
        Resources resources = activity.getResources();
        final String[] axis_x_string = resources.getStringArray(R.array.week);
        final XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setTextTypeface(null, Typeface.NORMAL);

        renderer.setShowGrid(true);
        renderer.setGridColor(resources.getColor(R.color.case_overview_statistics_grid_color));

        renderer.setLabelsColor(Color.BLACK);
        renderer.setAxesColor(resources.getColor(R.color.case_overview_statistics_axis_color));
        renderer.setBarSpacing(0.5);

        renderer.setXTitle("");
        renderer.setYTitle("");
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setXLabelsPadding(resources.getDimension(R.dimen.case_overview_statistics_x_axis_padding));
        renderer.setYLabelsPadding(resources.getDimension(R.dimen.case_overview_statistics_x_axis_padding));
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.CENTER);
        renderer.setXLabelsAngle(0);

        renderer.setXLabels(0);
        renderer.setYAxisMin(0);

        for (int i = 0; i < data.getData().length; i++) {
            final XYSeries series = new XYSeries("");
            dataset.addSeries(series);
            XYSeriesRenderer yRenderer = new XYSeriesRenderer();
            renderer.addSeriesRenderer(yRenderer);
            yRenderer.setColor(resources.getColor(data.getColorId()[i]));
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
        View view = ChartFactory.getBarChartView(activity, dataset, renderer, BarChart.Type.DEFAULT);
        return view;
    }

    public static String timestamp2Date(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("MM/dd/yyyy", cal).toString();
        return date;
    }
}
