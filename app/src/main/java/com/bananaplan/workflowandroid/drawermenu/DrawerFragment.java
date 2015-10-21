package com.bananaplan.workflowandroid.drawermenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.main.LoginActivity;
import com.bananaplan.workflowandroid.main.PreloadActivity;
import com.bananaplan.workflowandroid.utility.view.DividerItemDecoration;


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

    private Button mLogoutButton;

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
        mLogoutButton = (Button) mFragmentView.findViewById(R.id.logout_button);
    }

    private void setupDrawerMenu() {
        mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mDrawerAdapter = new DrawerAdapter(mActivity, mOnClickDrawerItemListener);

        mDrawerMenu.setLayoutManager(mLinearLayoutManager);
        mDrawerMenu.addItemDecoration(new DividerItemDecoration(mActivity.getResources().
                getDrawable(R.drawable.drawer_divider), false, true));
        mDrawerMenu.setAdapter(mDrawerAdapter);

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkingData.resetAccount();
                SharedPreferences sharedPreferences = mActivity.getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);
                sharedPreferences.edit().remove(WorkingData.USER_ID).remove(WorkingData.AUTH_TOKEN).commit();

                mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                mActivity.finish();
            }
        });
    }
}
