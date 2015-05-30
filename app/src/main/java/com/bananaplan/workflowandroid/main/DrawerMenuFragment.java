package com.bananaplan.workflowandroid.main;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;


/**
 *
 *
 * @author Danny Lin
 * @since 2015.05.30
 */
public class DrawerMenuFragment extends Fragment {

    public DrawerMenuFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer_menu, container, false);
    }

}
