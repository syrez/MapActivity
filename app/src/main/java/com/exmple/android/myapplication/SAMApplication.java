package com.exmple.android.myapplication;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by k3vin on 12-03-17.
 */

public class SAMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Cnnnection au serveur
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(
                new Parse.Configuration.Builder(getApplicationContext())
                        .applicationId(getString(R.string.appId))
                        .clientKey(getString(R.string.clientKey))
                        .server("https://parseapi.back4app.com/").build()
        );

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }

}
