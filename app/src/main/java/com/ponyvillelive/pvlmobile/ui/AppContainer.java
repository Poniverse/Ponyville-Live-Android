package com.ponyvillelive.pvlmobile.ui;

import android.app.Activity;
import android.view.ViewGroup;

import com.ponyvillelive.pvlmobile.PvlApp;

/**
 * An indirection which allows controlling the root container used for each activity.
 * */
public interface AppContainer {
    /**
     * The root {@link android.view.ViewGroup} into which the activity should place its contents.
     * */
    ViewGroup get(Activity activity, PvlApp app);

    /**
     * An {@link AppContainer} which returns the normal activity content view.
     * */
    AppContainer DEFAULT = new AppContainer() {
        @Override
        public ViewGroup get(Activity activity, PvlApp app) {
            return (ViewGroup) activity.findViewById(android.R.id.content);
        }
    };
}
