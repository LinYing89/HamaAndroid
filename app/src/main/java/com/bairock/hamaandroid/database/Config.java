package com.bairock.hamaandroid.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bairock.iot.intelDev.order.LoginModel;

public class Config {
    private  static Config config = new Config();
    private Config(){}

    public static Config ins(){
        return config;
    }

    public static final String keyServerName = "serverName";
    private static final String keyServerPadPort = "serverPadPort";
    private static final String keyServerDevPort = "serverDevPort";
    public static final String keyRouteName = "routeName";
    public static final String keyRoutePsd = "routePsd";
    public static final String keyDevShowStyle = "showStyle";
    public static final String keyDevNameShowStyle = "nameShowStyle";
    public static final String keyCtrlRing = "ctrlRing";
    private static final String keyNeedLogin = "needLogin";
    private static final String keyDownloadId = "downloadId";
    private static final String keyLoginModel = "loginModel";

    private String serverName = "051801.cn";
    private int serverPadPort = 10002;
    private int serverDevPort = 10003;
    private String routeName = "";
    private String routePsd = "";
    //0名称, 1别名
    private String devShowStyle = "";
    //0宫格, 1列表
    private String devNameShowStyle = "";

    private boolean ctrlRing = true;
    private boolean needLogin = true;
    private String loginModel;
    //var downloadId = ""

    private OnDevShowStyleChangedListener onDevShowStyleChangedListener = null;
    private OnDevNameShowStyleChangedListener onDevNameShowStyleChangedListener = null;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPadPort() {
        return serverPadPort;
    }

    public void setServerPadPort(int serverPadPort) {
        this.serverPadPort = serverPadPort;
    }

    public int getServerDevPort() {
        return serverDevPort;
    }

    public void setServerDevPort(int serverDevPort) {
        this.serverDevPort = serverDevPort;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRoutePsd() {
        return routePsd;
    }

    public void setRoutePsd(String routePsd) {
        this.routePsd = routePsd;
    }

    public String getDevShowStyle() {
        return devShowStyle;
    }

    public void setDevShowStyle(String devShowStyle) {
        if(!devShowStyle.equals(this.devShowStyle)) {
            this.devShowStyle = devShowStyle;
            if(null != onDevShowStyleChangedListener){
                onDevShowStyleChangedListener.onDevShowStyleChanged(devShowStyle);
            }
        }
    }

    public String getDevNameShowStyle() {
        return devNameShowStyle;
    }

    public void setDevNameShowStyle(String devNameShowStyle) {
        if(!devNameShowStyle.equals(this.devNameShowStyle)) {
            this.devNameShowStyle = devNameShowStyle;
            if(null != onDevNameShowStyleChangedListener){
                onDevNameShowStyleChangedListener.onDevNameShowStyleChanged(devShowStyle);
            }
        }
    }

    public boolean isCtrlRing() {
        return ctrlRing;
    }

    public void setCtrlRing(boolean ctrlRing) {
        this.ctrlRing = ctrlRing;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getLoginModel() {
        return loginModel;
    }

    public void setLoginModel(Context context, String loginModel) {
        this.loginModel = loginModel;
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(keyLoginModel, loginModel);
        editor.apply();
    }

    public OnDevShowStyleChangedListener getOnDevShowStyleChangedListener() {
        return onDevShowStyleChangedListener;
    }

    public void setOnDevShowStyleChangedListener(OnDevShowStyleChangedListener onDevShowStyleChangedListener) {
        this.onDevShowStyleChangedListener = onDevShowStyleChangedListener;
    }

    public OnDevNameShowStyleChangedListener getOnDevNameShowStyleChangedListener() {
        return onDevNameShowStyleChangedListener;
    }

    public void setOnDevNameShowStyleChangedListener(OnDevNameShowStyleChangedListener onDevNameShowStyleChangedListener) {
        this.onDevNameShowStyleChangedListener = onDevNameShowStyleChangedListener;
    }

    public void init(Context context){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        serverName = shared.getString(keyServerName, serverName);
        serverPadPort = shared.getInt(keyServerPadPort, serverPadPort);
        serverDevPort = shared.getInt(keyServerDevPort, serverDevPort);
        routeName = shared.getString(keyRouteName, routeName);
        routePsd = shared.getString(keyRoutePsd, routePsd);
        devShowStyle = shared.getString(keyDevShowStyle, "0");
        devNameShowStyle = shared.getString(keyDevNameShowStyle, "0");
        loginModel = shared.getString(keyLoginModel, "");
        needLogin = shared.getBoolean(keyNeedLogin, true);
        ctrlRing = shared.getBoolean(keyCtrlRing, true);
    }

    public void setServerInfo(Context context){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(keyServerPadPort, serverPadPort);
        editor.putInt(keyServerDevPort, serverDevPort);
        editor.apply();
    }

    public void setNeedLogin(Context context, boolean needLogin){
        this.needLogin = needLogin;
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(keyNeedLogin, needLogin);
        editor.apply();
    }

    public void setDownloadId(Context context, Long downloadId){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.putLong(keyDownloadId, downloadId);
        editor.apply();
    }

    public Long getDownloadId(Context context){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        return shared.getLong(keyDownloadId, 1L);
    }

    public interface OnDevShowStyleChangedListener{
        void onDevShowStyleChanged(String style);
    }

    public interface OnDevNameShowStyleChangedListener{
        void onDevNameShowStyleChanged(String name);
    }
}
