package com.bananaplan.workflowandroid.warning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.TaskWarning;

import java.util.ArrayList;

/**
 * Created by daz on 10/28/15.
 */
public class AddWarningDialog  extends AppCompatActivity implements View.OnClickListener {

    private Spinner mWarninigListSpinner;

    private ArrayList<TaskWarning> mTaskWarningListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_warning);

        initialize();
    }

    private void initialize () {
        findViews();
        setupWarningList();
    }
    private void findViews () {
        mWarninigListSpinner = (Spinner) findViewById(R.id.warning_list);
    }
    private void setupWarningList () {

        mTaskWarningListData = new ArrayList<>();
        //mTaskWarningListData.add(new TaskWarning());

    }


    @Override
    public void onClick(View view) {

    }
}
