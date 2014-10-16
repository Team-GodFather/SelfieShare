package com.godfather.selfieshare.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import com.godfather.selfieshare.R;
import com.godfather.selfieshare.data.QueryExecutor;
import com.godfather.selfieshare.fragments.TabsPagerAdapter;

import java.util.ArrayList;

public class HomeActivity extends FragmentActivity implements ActionBar.TabListener {
    private QueryExecutor queryExecutor;
    private ProgressDialog connectionProgressDialog;

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = this.getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        ArrayList<ActionBar.Tab> tabs = new ArrayList<ActionBar.Tab>();
        tabs.add(actionBar.newTab().setText("Home").setIcon(R.drawable.ic_action_computer));
        tabs.add(actionBar.newTab().setText("NearBy").setIcon(R.drawable.ic_action_group));
        tabs.add(actionBar.newTab().setText("Requested").setIcon(R.drawable.ic_action_send_now));
        tabs.add(actionBar.newTab().setText("Received").setIcon(R.drawable.ic_action_new_picture));
        tabs.add(actionBar.newTab().setText("Settings").setIcon(R.drawable.ic_action_settings));

        for (ActionBar.Tab tab : tabs) {
            actionBar.addTab(tab.setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        this.queryExecutor = QueryExecutor.getInstance();
        this.connectionProgressDialog = new ProgressDialog(this);
        this.connectionProgressDialog.setMessage("Logging out...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        queryExecutor.logoutUser();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
