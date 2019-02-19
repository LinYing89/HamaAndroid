package com.bairock.hamaandroid.settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.ClimateFragment;
import com.bairock.hamaandroid.app.ElectricalCtrlFragment;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.app.WelcomeActivity;
import com.bairock.hamaandroid.communication.PadClient;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.database.SdDbHelper;
import com.bairock.hamaandroid.esptouch.EspWifiAdminSimple;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.data.Result;
import com.bairock.iot.intelDev.http.HttpDownloadTask;
import com.bairock.iot.intelDev.http.HttpUploadTask;
import com.bairock.iot.intelDev.user.DevGroup;

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
        private ProgressDialog progressFileDialog;

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(Config.keyDevShowStyle));
            bindPreferenceSummaryToValue(findPreference(Config.keyDevNameShowStyle));
            bindPreferenceSummaryToValue(findPreference(Config.keyCtrlRing));

            Preference preferenceUpload = findPreference("upload");
            Preference preferenceDownload = findPreference("download");
            if(MainActivity.IS_ADMIN) {
                preferenceUpload.setEnabled(false);
                preferenceUpload.setSummary("未登录, 不可上传");
                preferenceDownload.setEnabled(false);
                preferenceDownload.setSummary("未登录, 不可下载");
            }
            preferenceUpload.setOnPreferenceClickListener(preference -> {
                Log.e("SettingsAct", "upload");
                updownloadPrepare(0);
                return false;
            });
            preferenceDownload.setOnPreferenceClickListener(preference -> {
                Log.e("SettingsAct", "download");
                updownloadPrepare(1);
                return false;
            });
        }
        private void updownloadPrepare(int which){
            String title;
            if(which == 0){
                title = "上传";
            }else{
                title = "下载";
            }
            new AlertDialog.Builder(getActivity())
                    .setMessage("确定" + title + "吗")
                    .setNegativeButton(MainActivity.strCancel, null)
                    .setPositiveButton(MainActivity.strEnsure,
                            (dialog, whichButton) -> {
                                showProgressDialog("正在" + title);
                                if(which == 0){
                                    HttpUploadTask task = new HttpUploadTask(HamaApp.USER, Config.ins().getServerName());
                                    task.setOnExecutedListener(this::uploadResult);
                                    task.start();
                                }else{
                                    HttpDownloadTask task = new HttpDownloadTask(Config.ins().getServerName(), HamaApp.USER.getName(), HamaApp.DEV_GROUP.getName());
                                    task.setOnExecutedListener(this::downloadResult);
                                    task.start();
                                }
                            }).show();
        }

        private void showProgressDialog(String title) {
            //创建ProgressDialog对象
            progressFileDialog = new ProgressDialog(
                    getActivity());
            //设置进度条风格，风格为圆形，旋转的
            progressFileDialog.setProgressStyle(
                    ProgressDialog.STYLE_SPINNER);
            //设置ProgressDialog 标题
            progressFileDialog.setTitle(title);
            //设置ProgressDialog 提示信息
            progressFileDialog.setMessage("请稍等");
            //设置ProgressDialog 标题图标
            progressFileDialog.setIcon(android.R.drawable.btn_star);
            //设置ProgressDialog 的进度条是否不明确
            progressFileDialog.setIndeterminate(false);
            //设置ProgressDialog 是否可以按退回按键取消
            progressFileDialog.setCancelable(false);
            //设置取消按钮
//        progressFileDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",
//                (dialog, which) -> {
//                    progressFileDialog.dismiss();
//                    //webFileBase.close();
//                });
            // 让ProgressDialog显示
            progressFileDialog.show();
        }

        private void closeProgressDialog() {
            if (null != progressFileDialog && progressFileDialog.isShowing()) {
                progressFileDialog.dismiss();
            }
        }

        private void downloadResult(Result<DevGroup> result) {
            closeProgressDialog();
            DevGroup groupDownload = result.getData();
            if(null == groupDownload){
                showToase("下载失败:" + result.getMsg());
                return;
            }
            HamaApp.USER.getListDevGroup().clear();
            HamaApp.USER.addGroup(groupDownload);

            SdDbHelper.replaceDbUser(HamaApp.USER);
            DevChannelBridgeHelper.getIns().stopSeekDeviceOnLineThread();
            WelcomeActivity.initUser();

            if(null != ElectricalCtrlFragment.handler){
                ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget();
            }
            if(null != ClimateFragment.handler){
                ClimateFragment.handler.obtainMessage(ClimateFragment.REFRESH_DEVICE).sendToTarget();
            }
            showToase("下载成功");
        }

        private void uploadResult(Result<Object> loginResult) {
            closeProgressDialog();
            if(loginResult.getCode() == 0) {
                showToase("上传成功");
            }else {
                showToase("上传失败:" + loginResult.getMsg());
            }
        }

        private void showToase(String text){
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            });
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
                    if(!Config.ins().getServerName().equals(stringValue)) {
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
