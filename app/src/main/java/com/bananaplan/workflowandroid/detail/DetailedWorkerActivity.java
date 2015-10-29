package com.bananaplan.workflowandroid.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.overview.TaskItemFragment;
import com.bananaplan.workflowandroid.overview.StatusFragment;
import com.bananaplan.workflowandroid.overview.workeroverview.WorkerOverviewFragment;
import com.bananaplan.workflowandroid.utility.TabManager;

public class DetailedWorkerActivity extends AppCompatActivity {

    private static final String TAG = "DetailWorkerActivity";

    public static final String EXTRA_WORKER_ID = "extra_worker_id";

    private static final class FragmentTag {
        public static final String TASK_SCHEDULE = "task_schedule";
        public static final String TASK_ITEM = "task_item";
        public static final String TASK_LOG = " task_log";
        public static final String WORKER_LOG = "worker_log";
    }

    private ActionBar mActionBar;
    private TabHost mTabHost;

    private ImageView mWorkerAvatar;
    private TextView mWorkerName;
    private TextView mWorkerJobTitle;

    private Worker mWorker;
    private TabManager mTabMgr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_worker);
        initialize(getIntent());
    }

    private void initialize(Intent intent) {
        mWorker = WorkingData.getInstance(this).getWorkerById(intent.getStringExtra(EXTRA_WORKER_ID));
        findViews();
        setupActionBar();
        setupTabs();
        setupViews();
    }

    private void findViews() {
        mTabHost = (TabHost) findViewById(R.id.detailed_worker_tab_host);
        mWorkerAvatar = (ImageView) findViewById(R.id.detailed_worker_avatar);
        mWorkerName = (TextView) findViewById(R.id.detailed_worker_name);
        mWorkerJobTitle = (TextView) findViewById(R.id.detailed_worker_jobtitle);
        mTabMgr = new TabManager(this, mTabHost, android.R.id.tabcontent);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupTabs() {
        mTabHost.setup();

        Bundle bundle1 = new Bundle();
        bundle1.putString(EXTRA_WORKER_ID, mWorker.id);
        addTab(FragmentTag.TASK_SCHEDULE, bundle1, TaskScheduleFragment.class);

//        Bundle bundle2 = new Bundle();
//        bundle2.putString(TaskItemFragment.FROM, getClass().getSimpleName());
//        addTab(FragmentTag.TASK_ITEM, bundle2, TaskItemFragment.class);

        Bundle bundle3 = new Bundle();
        bundle3.putString(StatusFragment.FROM, getClass().getSimpleName());
        addTab(FragmentTag.TASK_LOG, bundle3, StatusFragment.class);

        Bundle bundle4 = new Bundle();
        bundle4.putString(StatusFragment.FROM, WorkerOverviewFragment.class.getSimpleName());
        addTab(FragmentTag.WORKER_LOG, bundle4, StatusFragment.class);
    }

    private void addTab(String tabTag, Bundle bundle, Class<?> cls) {
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabTag)
                .setIndicator(getTabView(tabTag));
        mTabMgr.addTab(tabSpec, cls, bundle);
    }

    private View getTabView(String tabTag) {
        View view = getLayoutInflater().inflate(R.layout.tab, null);

        int titleResId;
        switch (tabTag) {
            case FragmentTag.TASK_SCHEDULE:
                titleResId = R.string.detailed_worker_task_schedule;
                break;
            case FragmentTag.TASK_ITEM:
                titleResId = R.string.detailed_worker_task_items;
                break;
            case FragmentTag.TASK_LOG:
                titleResId = R.string.detailed_worker_task_log;
                break;
            case FragmentTag.WORKER_LOG:
                titleResId = R.string.detailed_worker_worker_log;
                break;
            default:
                titleResId = -1;
                break;
        }

        String text = titleResId != -1 ? getResources().getString(titleResId) : "";
        ((TextView) view.findViewById(R.id.tab_title)).setText(text);

        return view;
    }

    private void setupViews() {
        mWorkerAvatar.setImageDrawable(mWorker.getAvator());
        mWorkerName.setText(mWorker.name);
        mWorkerJobTitle.setText(mWorker.jobTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Worker getSelectedWorker() {
        return mWorker;
    }
}
