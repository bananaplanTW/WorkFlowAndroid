package com.bananaplan.workflowandroid.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.addcase.AddCaseFragment;
import com.bananaplan.workflowandroid.addequipment.AddEquipmentFragment;
import com.bananaplan.workflowandroid.addworker.AddWorkerFragment;
import com.bananaplan.workflowandroid.assigntask.AssignTaskFragment;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataTask;
import com.bananaplan.workflowandroid.drawermenu.DrawerFragment;
import com.bananaplan.workflowandroid.overview.caseoverview.CaseOverviewFragment;
import com.bananaplan.workflowandroid.drawermenu.OnClickDrawerItemListener;
import com.bananaplan.workflowandroid.overview.equipmentoverview.EquipmentOverviewFragment;
import com.bananaplan.workflowandroid.info.MainInfoFragment;
import com.bananaplan.workflowandroid.overview.workeroverview.WorkerOverviewFragment;
import com.bananaplan.workflowandroid.warning.WarningFragment;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 *
 */
public class UIController implements OnClickDrawerItemListener, LoadingDataTask.OnFinishLoadingDataListener {

    public static final class FragmentTag {
        public static final String DRAWER_MENU_FRAGMENT = "drawer_menu_fragment";
        public static final String INFO_FRAGMENT = "info_fragment";
        public static final String ASSIGN_TASK_FRAGMENT = "assign_task_fragment";
        public static final String CASE_OVERVIEW_FRAGMENT = "case_overview_fragment";
        public static final String ADD_CASE_FRAGMENT = "add_case_fragment";
        public static final String WORKER_OVERVIEW_FRAGMENT = "worker_overview_fragment";
        public static final String ADD_WORKER_FRAGMENT = "add_worker_fragment";
        public static final String EQUIPMENT_OVERVIEW_FRAGMENT = "equipment_overview_fragment";
        public static final String ADD_EQUIPMENT_FRAGMENT = "add_equipment_fragment";
        public static final String WARNING_FRAGMENT = "warning_fragment";
    }

    private AppCompatActivity mMainActivity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private Menu mOptionMenu;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentManager mFragmentManager;
    private DrawerFragment mDrawerFragment;
    private Fragment mCurrentFragment;

    private int mDrawerItemId = R.id.drawer_info;

    public UIController(AppCompatActivity activity) {
        mMainActivity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        initialize();
    }

    public void onStart() {
        WorkingData.getInstance(mMainActivity).registerMinuteReceiver(mMainActivity);
    }

    public void onStop() {
        WorkingData.getInstance(mMainActivity).unregisterMinuteReceiver(mMainActivity);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mMainActivity.getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionMenu = menu;

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(!MainApplication.sUseTestData);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;

        } else {
            switch(item.getItemId()) {
                case R.id.action_refresh:
                    setRefreshMenuItemState(true);
                    new LoadingDataTask(mMainActivity, this).execute();

                    return true;

                default:
                    return false;
            }
        }
    }

    private void setRefreshMenuItemState(boolean isRefresh) {
        if (mOptionMenu == null) return;

        MenuItem refreshItem = mOptionMenu.findItem(R.id.action_refresh);

        if (isRefresh) {
            ImageView refresh = (ImageView) LayoutInflater.from(mMainActivity).inflate(R.layout.refresh_action_view, null);
            Animation rotation = AnimationUtils.loadAnimation(mMainActivity, R.anim.refresh_rotate);

            rotation.setRepeatCount(Animation.INFINITE);
            refresh.startAnimation(rotation);

            refreshItem.setActionView(refresh);
        } else {
            refreshItem.getActionView().clearAnimation();
            refreshItem.setActionView(null);
        }
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
                replaceContent();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initFragments() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mDrawerFragment = (DrawerFragment) mFragmentManager.findFragmentByTag(FragmentTag.DRAWER_MENU_FRAGMENT);
        if (mDrawerFragment == null) {
            mDrawerFragment = new DrawerFragment();
            fragmentTransaction.add(R.id.drawer_menu_container, mDrawerFragment, FragmentTag.DRAWER_MENU_FRAGMENT);
        }
        mDrawerFragment.setOnClickDrawerItemListener(this);

        // Default fragment when launch app
        MainInfoFragment mainInfoFragment = (MainInfoFragment) mFragmentManager.findFragmentByTag(FragmentTag.INFO_FRAGMENT);
        if (mainInfoFragment == null) {
            mainInfoFragment = new MainInfoFragment();
            fragmentTransaction.add(R.id.content_container, mainInfoFragment, FragmentTag.INFO_FRAGMENT);
        }

        mCurrentFragment = mainInfoFragment;
        fragmentTransaction.commit();
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onClickDrawerItem(int id) {
        mDrawerItemId = id;
        closeDrawer();
    }

    private void replaceContent() {
        switch (mDrawerItemId) {
            case R.id.drawer_setting_button:
                //Toast.makeText(mMainActivity, "Setting", Toast.LENGTH_SHORT).show();
                break;

            case R.id.drawer_info:
                if (mCurrentFragment instanceof MainInfoFragment) break;
                replaceTo(MainInfoFragment.class, FragmentTag.INFO_FRAGMENT, false);
                break;

            case R.id.drawer_assign_task:
                if (mCurrentFragment instanceof AssignTaskFragment) break;
                replaceTo(AssignTaskFragment.class, FragmentTag.ASSIGN_TASK_FRAGMENT, false);
                break;

            case R.id.drawer_case_overview:
                if (mCurrentFragment instanceof CaseOverviewFragment) break;
                replaceTo(CaseOverviewFragment.class, FragmentTag.CASE_OVERVIEW_FRAGMENT, false);
                break;

            case R.id.drawer_add_case:
                if (mCurrentFragment instanceof AddCaseFragment) break;
                replaceTo(AddCaseFragment.class, FragmentTag.ADD_CASE_FRAGMENT, false);
                break;

            case R.id.drawer_worker_overview:
                if (mCurrentFragment instanceof WorkerOverviewFragment) break;
                replaceTo(WorkerOverviewFragment.class, FragmentTag.WORKER_OVERVIEW_FRAGMENT, false);
                break;

            case R.id.drawer_add_worker:
                if (mCurrentFragment instanceof AddWorkerFragment) break;
                replaceTo(AddWorkerFragment.class, FragmentTag.ADD_WORKER_FRAGMENT, false);
                break;

            case R.id.drawer_equipment_overview:
                if (mCurrentFragment instanceof EquipmentOverviewFragment) break;
                replaceTo(EquipmentOverviewFragment.class, FragmentTag.EQUIPMENT_OVERVIEW_FRAGMENT, false);
                break;

            case R.id.drawer_add_equipment:
                if (mCurrentFragment instanceof AddEquipmentFragment) break;
                replaceTo(AddEquipmentFragment.class, FragmentTag.ADD_EQUIPMENT_FRAGMENT, false);
                break;

            case R.id.drawer_warning:
                if (mCurrentFragment instanceof WarningFragment) break;
                replaceTo(WarningFragment.class, FragmentTag.WARNING_FRAGMENT, false);
                break;
        }
    }

    private void replaceTo(Class<?> fragmentClass, String fragmentTag, boolean useAnimation) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (useAnimation) {
            fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        }

        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                fragmentTransaction.replace(R.id.content_container, fragment, fragmentTag);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        mCurrentFragment = fragment;
        fragmentTransaction.commit();
    }

    @Override
    public void onFinishLoadingData() {
        setRefreshMenuItemState(false);
        WorkingData.getInstance(mMainActivity).updateData();
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {
        setRefreshMenuItemState(false);
        Toast.makeText(mMainActivity,
                mMainActivity.getString(R.string.no_internet_connection_information), Toast.LENGTH_SHORT).show();
    }
}
