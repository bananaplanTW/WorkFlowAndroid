package com.bananaplan.workflowandroid.warning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Manager;
import com.bananaplan.workflowandroid.data.Warning;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.data.TextSpinnerAdapter;

import java.util.ArrayList;

/**
 * Created by daz on 10/28/15.
 */
public class AddWarningDialog  extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner mWarningListSpinner;
    private Spinner mManagerListSpinner;

    private ArrayList<Warning> mWarningListData;
    private TextSpinnerAdapter<Warning> mWarningListAdapter;

    private ArrayList<Manager> mManagerListData;
    private TextSpinnerAdapter<Manager> mManagerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_warning);

        initialize();
    }

    private void initialize () {
        findViews();
        setupWarningList();
        setupMangerList();
    }
    private void findViews () {
        mWarningListSpinner = (Spinner) findViewById(R.id.warning_list);
        mManagerListSpinner = (Spinner) findViewById(R.id.manager_list);
    }
    private void setupWarningList () {

        mWarningListData = new ArrayList<>();
        mWarningListData.add(new Warning("1234", "過切"));
        mWarningListData.add(new Warning("12334", "過銷"));
        mWarningListData.add(new Warning("12344", "過同"));

        mWarningListAdapter = new TextSpinnerAdapter<>(getApplicationContext(), R.layout.text_spinner_item, mWarningListData);
        mWarningListSpinner.setAdapter(mWarningListAdapter);

        mWarningListSpinner.setOnItemSelectedListener(this);
    }
    private void setupMangerList () {

        mManagerListData = WorkingData.getInstance(getApplicationContext()).getManagers();

        mManagerListAdapter = new TextSpinnerAdapter<>(getApplicationContext(), R.layout.text_spinner_item, mManagerListData);
        mManagerListSpinner.setAdapter(mManagerListAdapter);

        mManagerListSpinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onClick(View view) {

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("DAZZZZ", "onitemseelcted: " + adapterView.getId());
        switch (adapterView.getId()) {
            case R.id.warning_list:
                Log.d("DAZZZZ", "onitemseelcted: " + mWarningListData.get(i).name);
                break;
            case R.id.manager_list:
                Log.d("DAZZZZ", "onitemseelcted: " + mManagerListData.get(i).name);
                break;
        }



    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
