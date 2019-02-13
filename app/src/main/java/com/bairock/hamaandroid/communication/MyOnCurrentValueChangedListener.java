package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.adapter.RecyclerAdapterCollect;
import com.bairock.hamaandroid.app.ClimateFragment;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

public class MyOnCurrentValueChangedListener implements CollectProperty.OnCurrentValueChangedListener {

    private static MyOnCurrentValueChangedListener ins = new MyOnCurrentValueChangedListener();

    private MyOnCurrentValueChangedListener(){}

    public static MyOnCurrentValueChangedListener getIns(){
        return ins;
    }

    @Override
    public void onCurrentValueChanged(DevCollect devCollect, Float aFloat) {
        if (null != ClimateFragment.handler) {
            ClimateFragment.handler.obtainMessage(ClimateFragment.NOTIFY_ADAPTER, RecyclerAdapterCollect.VALUE, RecyclerAdapterCollect.VALUE, devCollect).sendToTarget();
        }
    }
}