package com.bananaplan.workflowandroid.info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.main.MainActivity;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class InfoFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().findViewById(R.id.menu_vg).setOnClickListener(this);
        getActivity().findViewById(R.id.navi_menu).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_vg:
            case R.id.navi_menu:
                openNavigationMenu();
                break;
        }
    }

    private void openNavigationMenu() {
        ((MainActivity) getActivity()).openNavigationDrawer();
    }
}
