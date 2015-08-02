package com.bananaplan.workflowandroid.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.AssignTaskFragment;
import com.bananaplan.workflowandroid.caseoverview.CaseOverviewFragment;
import com.bananaplan.workflowandroid.workeroverview.WorkerOverviewFragment;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 */
public class UIController {

    private ActionBarActivity mMainActivity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentManager mFragmentManager;
    private DrawerMenuFragment mDrawerMenuFragment;
    private AssignTaskFragment mAssignTaskFragment;

    private static final int MENU_ITEM_CASE_OVERVIEW_FRAGMENT = 10000;
    private static final int MENU_ITEM_WORKER_OVERVIEW_FRAGMENT = MENU_ITEM_CASE_OVERVIEW_FRAGMENT + 1;

    private static final class FragmentTag {
        public static final String TAG_DRAWER_MENU_FRAGMENT = "tag_drawer_menu_fragment";
        public static final String TAG_TASK_ASSIGN_FRAGMENT = "tag_task_assign_fragment";
        public static final String TAG_CASE_OVERVIEW_FRAGMENT = "tag_case_overview_fragment";
        public static final String TAG_WORKER_OVERVIEW_FRAGMENT = "tag_worker_overview_fragment";
    }

    public UIController(ActionBarActivity activity) {
        mMainActivity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        initialize();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            // Normal menu items put here
            switch (item.getItemId()) {
                case MENU_ITEM_CASE_OVERVIEW_FRAGMENT:
                    return openCaseOverViewFragment();
                case MENU_ITEM_WORKER_OVERVIEW_FRAGMENT:
                    return openWorkerOverViewFragment();
                default:
                    break;
            }
            return false;
        }
    }

    private boolean openWorkerOverViewFragment() {
        if (mFragmentManager == null) return false;
        FragmentTransaction fragTransaction = mFragmentManager.beginTransaction();
        if (fragTransaction == null) return false;
        WorkerOverviewFragment frag = new WorkerOverviewFragment();
        fragTransaction.replace(R.id.content_container, frag, FragmentTag.TAG_WORKER_OVERVIEW_FRAGMENT).addToBackStack(null);
        fragTransaction.commit();
        return true;
    }

    private boolean openCaseOverViewFragment() {
        if (mFragmentManager == null) return false;
        FragmentTransaction fraTransaction = mFragmentManager.beginTransaction();
        if (fraTransaction == null) return false;
        CaseOverviewFragment frag = new CaseOverviewFragment();
        fraTransaction.replace(R.id.content_container, frag, FragmentTag.TAG_CASE_OVERVIEW_FRAGMENT).addToBackStack(null);
        fraTransaction.commit();
        return true;
    }

    public void onPostCreate(Bundle savedInstanceState) {
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initialize() {
        mFragmentManager = mMainActivity.getSupportFragmentManager();
        findViews();
        initActionbar();
        initDrawer();
        initFragments();
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mDrawerLayout = (DrawerLayout) mMainActivity.findViewById(R.id.drawer_layout);
    }

    private void initActionbar() {
        mMainActivity.setSupportActionBar(mToolbar);
        mActionBar = mMainActivity.getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
    }

    private void initDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(mMainActivity, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initFragments() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mDrawerMenuFragment = (DrawerMenuFragment) mFragmentManager.findFragmentByTag(FragmentTag.TAG_DRAWER_MENU_FRAGMENT);
        if (mDrawerMenuFragment == null) {
            mDrawerMenuFragment = new DrawerMenuFragment();
            fragmentTransaction.add(R.id.drawer_menu_container, mDrawerMenuFragment, FragmentTag.TAG_DRAWER_MENU_FRAGMENT);
        }

        mAssignTaskFragment = (AssignTaskFragment) mFragmentManager.findFragmentByTag(FragmentTag.TAG_TASK_ASSIGN_FRAGMENT);
        if (mAssignTaskFragment == null) {
            mAssignTaskFragment = new AssignTaskFragment();
            fragmentTransaction.add(R.id.content_container, mAssignTaskFragment, FragmentTag.TAG_TASK_ASSIGN_FRAGMENT);
        }

        fragmentTransaction.commit();
    }

    public void onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_CASE_OVERVIEW_FRAGMENT, 0, "CaseOverView Fragment");
        menu.add(0, MENU_ITEM_WORKER_OVERVIEW_FRAGMENT, 0, "WorkerOverView Fragment");
    }
}
