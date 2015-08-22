package com.bananaplan.workflowandroid.drawermenu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;


/**
 * Menu in navigation drawer
 *
 * @author Danny Lin
 * @since 2015.05.30
 */
public class DrawerFragment extends Fragment {

    private static final String TAG = "DrawerMenuFragment";

    private Activity mActivity;
    private View mFragmentView;

    private OnClickDrawerItemListener mOnClickDrawerItemListener;

    private RecyclerView mDrawerMenu;
    private DrawerAdapter mDrawerAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer_menu, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    public void setOnClickDrawerItemListener(OnClickDrawerItemListener listener) {
        mOnClickDrawerItemListener = listener;
    }

    private void initialize() {
        findViews();
        setupDrawerMenu();
    }

    private void findViews() {
        mFragmentView = getView();
        mDrawerMenu = (RecyclerView) mFragmentView.findViewById(R.id.drawer_menu_listview);
    }

    private void setupDrawerMenu() {
        mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mDrawerAdapter = new DrawerAdapter(mActivity, mOnClickDrawerItemListener);

        mDrawerMenu.setLayoutManager(mLinearLayoutManager);
        mDrawerMenu.addItemDecoration(new DividerItemDecoration(mActivity.getResources().
                                                                getDrawable(R.drawable.drawer_divider), false, true));
        mDrawerMenu.setAdapter(mDrawerAdapter);
    }
}
