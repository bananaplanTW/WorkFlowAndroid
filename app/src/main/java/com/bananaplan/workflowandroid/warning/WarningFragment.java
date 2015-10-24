package com.bananaplan.workflowandroid.warning;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class WarningFragment extends Fragment implements TextWatcher,
        AdapterView.OnItemSelectedListener{

    private Spinner mVendorsSpinner;
    private EditText mEtCaseSearch;
    private ListView mCaseListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_warning, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        initData();
    }

    private void setupViews() {
        mVendorsSpinner = (Spinner) getActivity().findViewById(R.id.ov_leftpane_spinner);
        mEtCaseSearch = (EditText) getActivity().findViewById(R.id.ov_leftpane_search_edittext);
        mCaseListView = (ListView) getActivity().findViewById(R.id.ov_leftpane_listview);

        mEtCaseSearch.addTextChangedListener(this);
        mVendorsSpinner.setOnItemSelectedListener(this);
    }

    private void initData() {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // filter data
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // to nothing
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // to nothing
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // update content
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // to nothing
    }
}
