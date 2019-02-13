package com.bairock.hamaandroid.settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.communication.PadClient;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.esptouch.EspWifiAdminSimple;

import java.util.List;

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;

public class SettingsActivity2 extends AppCompatPreferenceActivity{
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        supportActionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override public boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || AboutPreferenceFragment.class.getName().equals(fragmentName)
                || NetPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(Config.ins().keyDevShowStyle));
            bindPreferenceSummaryToValue(findPreference(Config.ins().keyDevNameShowStyle));
            bindPreferenceSummaryToValue(findPreference(Config.ins().keyCtrlRing));
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NetPreferenceFragment extends PreferenceFragment {
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_net);
            setHasOptionsMenu(true);

            Preference routeName = findPreference(Config.ins().keyRouteName);
            EspWifiAdminSimple mWifiAdmin = new EspWifiAdminSimple(this.getActivity());
            String ssid = mWifiAdmin.getWifiConnectedSsid();
            routeName.setSummary(ssid);

            bindPreferenceSummaryToValue(findPreference(Config.keyServerName));
            bindPreferenceSummaryToValue(findPreference(Config.ins().keyRoutePsd));
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutPreferenceFragment extends PreferenceFragment {
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_about);
            setHasOptionsMenu(true);

            Preference prefVersion = findPreference("prefVersion");
            prefVersion.setSummary(MainActivity.VERSION_NAME);
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String stringValue = o.toString();

            if (preference instanceof ListPreference) {
                int index = ((ListPreference) preference).findIndexOfValue(stringValue);

                if(index >= 0){
                    preference.setSummary(((ListPreference) preference).getEntries()[index]);
                }else{
                    preference.setSummary(null);
                }

            } else {
                //if(stringValue == "true")
                preference.setSummary(stringValue);
            }
            switch (preference.getKey()){
                case Config.keyServerName :
                    if(Config.ins().getServerName().equals(stringValue)) {
                        Config.ins().setServerName(stringValue);
                        new AlertDialog.Builder(preference.getContext())
                                .setMessage("请退出账号重新登录!")
                                .setPositiveButton(MainActivity.strEnsure, null).show();
                        MainActivity.IS_ADMIN = true;
                        PadClient.getIns().closeHandler();
                        Config.ins().setNeedLogin(preference.getContext(), true);
                    }
                    break;
                case Config.keyRoutePsd:
                    Config.ins().setRoutePsd(stringValue);
                    break;
                case Config.keyDevShowStyle:
                    Config.ins().setDevShowStyle(stringValue);
                    break;
                case Config.keyDevNameShowStyle:
                    Config.ins().setDevNameShowStyle(stringValue);
                    break;
                case Config.keyCtrlRing:
                    Config.ins().setCtrlRing(Boolean.parseBoolean(stringValue));
                    break;
            }
            return true;
        }
    };

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        Object value;
        if(preference instanceof SwitchPreference){
            value = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getBoolean(preference.getKey(), true);
        }else if(preference instanceof ListPreference){
            value = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), "0");
        }else{
            value = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), "");
        }
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,value);
    }
}
