package com.bananaplan.workflowandroid.data.loading;

/**
 * Created by daz on 10/23/15.
 */
public class UpdatableScheduledExecution {
    public interface OnFinishCountingListener {
        void onFinishCounting();
    }

    private OnFinishCountingListener mOnFinishCountingListener;
    private int mPeriod;
    private Thread mThread;

    public UpdatableScheduledExecution (int period, OnFinishCountingListener onFinishCountingListener) {
        mPeriod = period;
        mOnFinishCountingListener = onFinishCountingListener;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                        if (mPeriod < 0) {
                            break;
                        }
                        mPeriod -= 100;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mOnFinishCountingListener.onFinishCounting();
            }
        });
    }

    public void execute() {
        if (mThread != null) {
            mThread.start();
        }
    }

    public void updatePeriod (int period) {
        mPeriod = period;
    }
}
