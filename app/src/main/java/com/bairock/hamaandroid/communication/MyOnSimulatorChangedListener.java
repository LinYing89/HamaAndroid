package com.bairock.hamaandroid.communication;

import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

public class MyOnSimulatorChangedListener implements CollectProperty.OnSimulatorChangedListener {
    @Override
    public void onSimulatorChanged(DevCollect devCollect, Float aFloat) {
//        if (null != RecyclerAdapterCollect.handler) {
//            RecyclerAdapterCollect.handler.obtainMessage(RecyclerAdapterCollect.SIMULATOR, devCollect).sendToTarget();
//        }
    }
}
