package com.bananaplan.workflowandroid.main;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * @author Danny Lin
 * @since 2015/10/3.
 */
public class MainApplication extends Application {

    public static boolean sUseTestData = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "ucjsGB5hkzXQYYCtzqS2ZxSTllpz5Oylcc6jaZ18", "N3XzDqdVTdQVLgp4TeZLpG4tJM37pG4wL3kuPMMN");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
