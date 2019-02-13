package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.adapter.RecyclerAdapterCollect;
import com.bairock.hamaandroid.app.ClimateFragment;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

public class MyOnUnitSymbolChangedListener implements CollectProperty.OnUnitSymbolChangedListener{

    @Override
    public void onUnitSymbolChanged(DevCollect devCollect, String s) {
        if (null != ClimateFragment.handler) {
            ClimateFragment.handler.obtainMessage(ClimateFragment.NOTIFY_ADAPTER, RecyclerAdapterCollect.SYMBOL, RecyclerAdapterCollect.SYMBOL, devCollect).sendToTarget();
        }
    }
}