package com.bananaplan.workflowandroid.utility;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TabHost;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;

import java.util.HashMap;

/**
 * Created by Ben on 2015/8/14.
 */
public class TabManager implements TabHost.OnTabChangeListener {
    private final ActionBarActivity mActivity;
    private final TabHost mTabHost;
    private final int mContainerId;
    private final HashMap<String, TabInfo> mTabs = new HashMap<>();
    TabInfo mLastTab;
    private Fragment mParentFragment;

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private OvTabFragmentBase fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public TabManager(ActionBarActivity activity, Fragment parent, TabHost tabHost, int containerId) {
        mActivity = activity;
        mTabHost = tabHost;
        mContainerId = containerId;
        mParentFragment = parent;
        mTabHost.setOnTabChangedListener(this);
    }

    public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(mActivity));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);

        Fragment frag = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        info.fragment = (OvTabFragmentBase) frag;
        if (info.fragment != null && !info.fragment.isDetached()) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.detach(info.fragment);
            ft.commit();
        }

        mTabs.put(tag, info);
        mTabHost.addTab(tabSpec);
    }

    @Override
    public void onTabChanged(String tabId) {
        TabInfo newTab = mTabs.get(tabId);

        if (mLastTab != newTab) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }

            if (newTab != null) {
                newTab.fragment = (OvTabFragmentBase) Fragment.instantiate(mActivity,
                        newTab.clss.getName(), newTab.args);
                ft.add(mContainerId, newTab.fragment, newTab.tag);
                if (newTab.fragment == null) {
                    ft.detach(mLastTab.fragment);
                } else {
                    mActivity.getFragmentManager().popBackStack();
                    ft.replace(mContainerId, newTab.fragment);
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            mActivity.getFragmentManager().executePendingTransactions();
        }
    }

    public void selectItem(Object item) {
        if (mLastTab == null) return;
        mLastTab.fragment.selectItem(item);
    }
}
