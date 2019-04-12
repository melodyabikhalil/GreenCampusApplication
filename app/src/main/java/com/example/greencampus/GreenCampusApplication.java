package com.example.greencampus;

import android.app.Application;

public class GreenCampusApplication extends Application {

    private static GreenCampusApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static GreenCampusApplication getContext() {
        return mContext;
    }
}
