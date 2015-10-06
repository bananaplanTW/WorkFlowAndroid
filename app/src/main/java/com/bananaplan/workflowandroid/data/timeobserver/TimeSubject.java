package com.bananaplan.workflowandroid.data.timeobserver;

/**
 * @author Danny Lin
 * @since 2015/10/6.
 */
public interface TimeSubject {
    void registerTimeObserver(TimeObserver o);
    void removeTimeObserver(TimeObserver o);
    void notifyTimeObservers();
}
