package com.bairock.hamaandroid.communication;

import android.util.Log;
import com.bairock.hamaandroid.app.AlarmMessageHelper;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.iot.intelDev.device.CtrlModel;

public class MyOnValueTriggedListener implements CollectProperty.OnValueTriggedListener{

    private void pushLocal(String title, String content){

    }

    @Override
    public void onValueTrigged(ValueTrigger valueTrigger, float v) {
        Log.e("ValueTriggedListener", valueTrigger.getMessage() + " triggered value = " + v);
        //如果服务器已连接，本地不提醒，只允许服务器推送提醒，如果服务器未连接，本地推送提醒
        if(valueTrigger.getCollectProperty().getDevCollect().getCtrlModel() == CtrlModel.LOCAL){
            String content = valueTrigger.getCollectProperty().getDevCollect().getName() + ":" + valueTrigger.getMessage()+ "(当前值:" + v + ")";
            pushLocal("提醒", content);
            AlarmMessageHelper.add(valueTrigger.getCollectProperty().getDevCollect().getName(), content);
        }
    }

    @Override
    public void onValueTriggedRelieve(ValueTrigger valueTrigger, float v) {
        AlarmMessageHelper.remove(valueTrigger.getCollectProperty().getDevCollect().getName());
    }
}