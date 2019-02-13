package com.bairock.hamaandroid.settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A [android.preference.PreferenceActivity] which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
public abstract class AppCompatPreferenceActivity extends PreferenceActivity {

    private AppCompatDelegate delegate;
    ActionBar supportActionBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        delegate = AppCompatDelegate.create(this, null);
        supportActionBar = delegate.getSupportActionBar();

        delegate.installViewFactory();
        delegate.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delegate.onPostCreate(savedInstanceState);
    }

    public void setSupportActionBar(Toolbar toolbar) {
        delegate.setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return delegate.getMenuInflater();
    }

    @Override
    public void setContentView(int layoutResID) {
        delegate.setContentView(layoutResID);
    }

    @Override public void setContentView(View view) {
        delegate.setContentView(view);
    }

    @Override public void setContentView(View view, ViewGroup.LayoutParams params) {
        delegate.setContentView(view, params);
    }

    @Override public void addContentView(View view, ViewGroup.LayoutParams params) {
        delegate.addContentView(view, params);
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        delegate.onPostResume();
    }

    @Override public void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        delegate.setTitle(title);
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        delegate.onConfigurationChanged(newConfig);
    }

    @Override public void onStop() {
        super.onStop();
        delegate.onStop();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        delegate.onDestroy();
    }

    @Override public void invalidateOptionsMenu() {
        delegate.invalidateOptionsMenu();
    }
}
