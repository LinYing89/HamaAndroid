package com.bairock.hamaandroid.app;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.adapter.SectionsPagerAdapter;
import com.bairock.hamaandroid.communication.ChannelBridgeHelperHeartSendListener;
import com.bairock.hamaandroid.communication.CheckServerConnect;
import com.bairock.hamaandroid.communication.MyHttpRequest;
import com.bairock.hamaandroid.communication.MyMessageAnalysiser;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.database.SdDbHelper;
import com.bairock.hamaandroid.linkage.LinkageActivity;
import com.bairock.hamaandroid.receiver.DownloadReceiver;
import com.bairock.hamaandroid.receiver.NetworkConnectChangedReceiver;
import com.bairock.hamaandroid.settings.BridgesStateActivity;
import com.bairock.hamaandroid.settings.SearchActivity;
import com.bairock.hamaandroid.zview.MarqueeView;
import com.bairock.hamaandroid.settings.SettingsActivity2;
import com.bairock.iot.intelDev.communication.DevChannelBridge;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.communication.DevServer;
import com.bairock.iot.intelDev.communication.UdpServer;
import com.bairock.iot.intelDev.data.Result;
import com.bairock.iot.intelDev.linkage.LinkageHelper;
import com.bairock.iot.intelDev.linkage.LinkageTab;
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHelper;
import com.bairock.iot.intelDev.order.LoginModel;
import com.bairock.iot.intelDev.order.OrderType;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.IntelDevHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.ref.WeakReference;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static boolean IS_ADMIN;
    public static String VERSION_NAME = "";

    public static final int UPLOAD_FAIL = 3;
    public static final int UPLOAD_OK = 4;
    public static final int DOWNLOAD_FAIL = 5;
    public static final int DOWNLOAD_OK = 6;
    public static final int REFRESH_TITLE = 8;
    public static final int SHOW_LOGOUT_DIALOG = 9;
    public static MyHandler handler = null;

    public static final String UPDATE_ALARM_TEXT_ACTION = "com.bairock.hamadev.updateAlarm";

    public static String strEnsure;
    public static String strCancel;

    private Toolbar toolbar;
    private String title;
    private MarqueeView txtAlarmMessage;
    private ProgressDialog progressFileDialog;
    private VersionTask versionTask;
    private PackageInfo packageInfo;

    private NetworkConnectChangedReceiver networkConnectChangedReceiver;

    private BroadcastReceiver br = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(UPDATE_ALARM_TEXT_ACTION.equals(intent.getAction())){
                if(AlarmMessageHelper.listAlarmMessage.isEmpty()) {
                    txtAlarmMessage.setVisibility(View.GONE);
                }else {
                    txtAlarmMessage.setVisibility(View.VISIBLE);
                    txtAlarmMessage.startWithList(AlarmMessageHelper.listMessage);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(null == Config.ins().getLoginModel() || Config.ins().getLoginModel().isEmpty()){
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("登录过期, 请重新登录.")
                    .setPositiveButton(strEnsure,
                            (dialog, whichButton) -> {
                                Config.ins().setNeedLogin(MainActivity.this, true);
                                finish();
                            }).show();
        }
        toolbar = findViewById(R.id.toolbar);
        //toolbar.setLogo(R.mipmap.ic_logo_white);
        packageInfo = getAppVersionCode(this);
        //toolbar.setTitle(UserHelper.getUser().getName() + UserHelper.getUser().getPetName());
        if (null != packageInfo) {
            VERSION_NAME = packageInfo.versionName;
            //subTitle += " v" + VERSION_NAME;
        }
        String groupName = "";
        if(null == HamaApp.DEV_GROUP.getPetName() || HamaApp.DEV_GROUP.getPetName().isEmpty()){
            groupName = HamaApp.DEV_GROUP.getName();
        }else{
            groupName = HamaApp.DEV_GROUP.getPetName();
        }
        title = HamaApp.USER.getUserid() + "-" + groupName;
        toolbar.setTitle(title);
        //toolbar.setSubtitle(subTitle);

        txtAlarmMessage = findViewById(R.id.txtAlarmMessage);

//        AlarmMessageHelper.INSTANCE.add("烟雾探测器3", "报警3");
//        AlarmMessageHelper.INSTANCE.add("门禁3", "门禁3");
        txtAlarmMessage.startWithList(AlarmMessageHelper.listMessage);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        if(LogUtils.APP_DBG) {
            navigationView.inflateMenu(R.menu.activity_main_drawer_debug);
        }else{
            navigationView.inflateMenu(R.menu.activity_main_drawer_release);
        }
        navigationView.setNavigationItemSelectedListener(this);
        strEnsure = "确定";
        strCancel = "取消";
//        versionTask = new VersionTask(this);
//        versionTask.execute((Void) null);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        handler = new MyHandler(MainActivity.this);

        //注册网络状态变化广播
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(networkConnectChangedReceiver, filter);

        //注册报警信息更新广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_ALARM_TEXT_ACTION);
        registerReceiver(br, intentFilter);

        if (!IS_ADMIN) {
            //尝试连接服务器
            CheckServerConnect.running = true;
            IntelDevHelper.executeThread(new CheckServerConnect());
        }

        initLocalConfig();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_reset:
                //DeviceChainHelper.getIns().init();
                break;
            case R.id.action_refresh:
                //SendMsgHelper.refreshState();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        } else if (id == R.id.nav_set_chain) {
            startActivity(new Intent(MainActivity.this, LinkageActivity.class));
        } else if (id == R.id.nav_system_set) {
            startActivity(new Intent(MainActivity.this, SettingsActivity2.class));
//            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_exit) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("确定退出账号吗")
                    .setNegativeButton(strCancel, null)
                    .setPositiveButton(strEnsure,
                            (dialog, whichButton) -> {
                                Config.ins().setNeedLogin(MainActivity.this, true);
                                finish();
                            }).show();
        } else if (id == R.id.nav_log) {
            startActivity(new Intent(MainActivity.this, BridgesStateActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkConnectChangedReceiver);
        unregisterReceiver(br);
        DevServer.getIns().close();
        CheckServerConnect.running = false;
        IntelDevHelper.shutDown();
        System.exit(0);
    }

    private void downloadResult(Result<DevGroup> result) {
        DevGroup groupDownload = result.getData();
        if(null == groupDownload){
            if(null != MainActivity.handler){
                MainActivity.handler.obtainMessage(MainActivity.DOWNLOAD_FAIL).sendToTarget();
            }
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
        if(null != MainActivity.handler){
            MainActivity.handler.obtainMessage(MainActivity.DOWNLOAD_OK).sendToTarget();
        }
    }

    private void uploadResult(Result<Object> loginResult) {
        if(loginResult.getCode() == 0) {
            handler.obtainMessage(MainActivity.UPLOAD_OK).sendToTarget();
        }else {
            handler.obtainMessage(MainActivity.UPLOAD_FAIL).sendToTarget();
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            switch (msg.what) {
                case UPLOAD_FAIL:
                    Snackbar.make(theActivity.toolbar, "上传失败", Snackbar.LENGTH_SHORT).show();
                    theActivity.closeProgressDialog();
                    break;
                case UPLOAD_OK:
                    Snackbar.make(theActivity.toolbar, "上传成功", Snackbar.LENGTH_SHORT).show();
                    theActivity.closeProgressDialog();
                    break;
                case DOWNLOAD_FAIL:
                    Snackbar.make(theActivity.toolbar, "下载失败", Snackbar.LENGTH_SHORT).show();
                    theActivity.closeProgressDialog();
                    break;
                case DOWNLOAD_OK:
                    Snackbar.make(theActivity.toolbar, "下载成功", Snackbar.LENGTH_SHORT).show();
                    theActivity.closeProgressDialog();
                    break;
                case REFRESH_TITLE:
                    if (!HamaApp.NET_CONNECTED) {
                        theActivity.toolbar.setTitle(theActivity.title + "(网络未连接)");
                    } else if (!HamaApp.SERVER_CONNECTED) {
                        theActivity.toolbar.setTitle(theActivity.title + "(服务器未连接)");
                    } else {
                        theActivity.toolbar.setTitle(theActivity.title);
                    }
                    break;
                case SHOW_LOGOUT_DIALOG:
                    theActivity.showLogoutDialog();
                    break;
            }
        }
    }

    private void showLogoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage("该账号已在其他设备上本地登录!")
                .setPositiveButton("确定",
                        (dialog, whichButton) -> {
                            System.exit(0);
                        }).show();
    }

    private void showProgressDialog(String title) {
        //创建ProgressDialog对象
        progressFileDialog = new ProgressDialog(
                MainActivity.this);
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

    private static class VersionTask extends AsyncTask<Void, Void, Boolean> {
        WeakReference<MainActivity> mActivity;
        String appName = "";

        VersionTask(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                MainActivity theActivity = mActivity.get();
                if (theActivity.packageInfo != null) {
                    int version = theActivity.packageInfo.versionCode;
                    String s = MyHttpRequest.sendGet(HamaApp.getCompareVersionUrl(version), null);
                    ObjectMapper mapper = new ObjectMapper();
                    Map map = mapper.readValue(s, Map.class);
                    boolean update = Boolean.parseBoolean(map.get("update").toString());
                    appName = map.get("appName").toString();
                    return update;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            MainActivity theActivity = mActivity.get();
            theActivity.versionTask = null;
            if (success) {
                new AlertDialog.Builder(theActivity)
                        .setMessage("有新版本，是否下载更新")
                        .setNegativeButton(strCancel, null)
                        .setPositiveButton(strEnsure,
                                (dialog, whichButton) -> {
                                    //下载
                                    theActivity.intoDownloadManager(appName);
                                }).show();
            }
        }

        @Override
        protected void onCancelled() {
            MainActivity theActivity = mActivity.get();
            theActivity.versionTask = null;
        }
    }

    /**
     * 返回当前程序版本名
     */
    private PackageInfo getAppVersionCode(Context context) {
        PackageInfo pi = null;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return pi;
    }

    private void intoDownloadManager(String appName) {
        try {

            DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(HamaApp.getDownloadAppUrl(appName));
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 设置下载路径和文件名
            String downloadFileName = System.currentTimeMillis() + appName;
            DownloadReceiver.APP_NAME = downloadFileName;
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName);
            request.setDescription("智能物联网控制器新版本下载");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            // 设置为可被媒体扫描器找到
            request.allowScanningByMediaScanner();
            // 设置为可见和可管理
            request.setVisibleInDownloadsUi(true);
            assert dManager != null;
            long refernece = dManager.enqueue(request);
            // 把当前下载的ID保存起来
            Config.ins().setDownloadId(this, refernece);
        } catch (IllegalArgumentException ex) {
            Snackbar.make(toolbar, "下载器没有启用", Snackbar.LENGTH_LONG).show();
        }
    }

    private void initLocalConfig() {
        if(null != Config.ins().getLoginModel() && Config.ins().getLoginModel().equals(LoginModel.LOCAL)) {
            try {
                UdpServer.getIns().run();
                DevServer.getIns().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DevChannelBridge.analysiserName = MyMessageAnalysiser.class.getName();
            DevChannelBridgeHelper.getIns().stopSeekDeviceOnLineThread();
            DevChannelBridgeHelper.getIns().startSeekDeviceOnLineThread();
            DevChannelBridgeHelper.getIns().setOnHeartSendListener(new ChannelBridgeHelperHeartSendListener());

            LinkageTab.getIns().SetOnOrderSendListener((device, order, ctrlModel) -> {
                //Log.e("WelcomeAct", "OnOrderSendListener " + "order: " + order + " cm: " + ctrlModel);
                if(null != order && null != Config.ins().getLoginModel() && Config.ins().getLoginModel().equals(LoginModel.LOCAL)) {
                    HamaApp.sendOrder(device, order, OrderType.CTRL_DEV, false);
                }
            });

            LinkageHelper.getIns().stopCheckLinkageThread();
            LinkageHelper.getIns().startCheckLinkageThread();
            GuaguaHelper.getIns().stopCheckGuaguaThread();
            GuaguaHelper.getIns().startCheckGuaguaThread();
            GuaguaHelper.getIns().setOnOrderSendListener((guagua, s, ctrlModel) -> HamaApp.sendOrder(guagua.findSuperParent(), s, true));
        }
    }
}
