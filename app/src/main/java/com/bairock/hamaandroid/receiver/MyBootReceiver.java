package com.bairock.hamaandroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.bairock.hamaandroid.app.WelcomeActivity;
import com.bairock.hamaandroid.database.Config;

public class MyBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //如果 系统 启动的消息，则启动 APP 主页活动
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Config.ins().initBootStart(context);
            if(Config.ins().isBootStart()) {
                Intent intentMainActivity = new Intent(context, WelcomeActivity.class);
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentMainActivity);
                Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show();
            }
        }
    }
}
