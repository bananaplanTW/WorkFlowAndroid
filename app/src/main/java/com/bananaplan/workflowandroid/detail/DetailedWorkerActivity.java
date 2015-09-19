package com.bananaplan.workflowandroid.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;

public class DetailedWorkerActivity extends AppCompatActivity {

    private static final String TAG = "DetailWorkerActivity";

    public static final String EXTRA_WORKER_ID = "extra_worker_id";

    private ActionBar mActionBar;

    private ImageView mWorkerAvatar;
    private TextView mWorkerName;
    private TextView mWorkerJobtitle;

    private Worker mWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_worker);
        initialize(getIntent());
    }

    private void initialize(Intent intent) {
        mWorker = WorkingData.getInstance(this).getWorkerItemById(intent.getStringExtra(EXTRA_WORKER_ID));
        findViews();
        setupActionBar();
        setupViews();
    }

    private void findViews() {
        mWorkerAvatar = (ImageView) findViewById(R.id.detailed_worker_avatar);
        mWorkerName = (TextView) findViewById(R.id.detailed_worker_name);
        mWorkerJobtitle = (TextView) findViewById(R.id.detailed_worker_jobtitle);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_detailed_worker_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        mWorkerAvatar.setImageDrawable(mWorker.getAvator());
        mWorkerName.setText(mWorker.name);
        mWorkerJobtitle.setText(mWorker.jobTitle);
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
}
