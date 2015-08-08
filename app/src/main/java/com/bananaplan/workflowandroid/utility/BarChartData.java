package com.bananaplan.workflowandroid.utility;

import android.content.Context;

import com.bananaplan.workflowandroid.R;

/**
 * Created by Ben on 2015/8/8.
 */
public class BarChartData {
    private int[][] mData;
    private int[] mColorId;

    public BarChartData() {
    }

    public int[][] getData() {
        return mData;
    }

    public int[] getColorId() {
        return mColorId;
    }

    public void genRandomData(final Context context, int dataCount) {
        mData = new int[dataCount][7];
        mColorId = new int[dataCount];
        for (int i = 0; i < dataCount; i++) {
            for (int j = 0; j < 7; j++) {
                mData[i][j] = (int) (Math.random() * 24 + 1);
            }
        }
        for (int i = 0; i < mColorId.length; i++) {
            if (i == 1) {
                mColorId[i] = R.color.statistics_bar_color2;
            } else if (i == 2) {
                mColorId[i] = R.color.statistics_bar_color3;
            } else {
                mColorId[i] = R.color.statistics_bar_color1;
            }
        }
    }

    public int getWorkingHours() {
        if (mData == null || mData.length < 1) return 0;
        int total = 0;
        for (int i = 0; i < 7; i++) {
            total += mData[0][i];
        }
        return total;
    }

    public int getOvertimeHours() {
        if (mData == null || mData.length < 2) return 0;
        int total = 0;
        for (int i = 0; i < 7; i++) {
            total += mData[1][i];
        }
        return total;
    }

    public int getIdleHours() {
        if (mData == null || mData.length < 3) return 0;
        int total = 0;
        for (int i = 0; i < 7; i++) {
            total += mData[2][i];
        }
        return total;
    }
}
