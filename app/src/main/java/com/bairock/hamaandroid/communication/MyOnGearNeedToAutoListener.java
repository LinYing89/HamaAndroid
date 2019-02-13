package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.adapter.RecyclerAdapterElectrical3;
import com.bairock.hamaandroid.adapter.RecyclerAdapterElectricalList;
import com.bairock.hamaandroid.app.ElectricalCtrlFragment;
import com.bairock.iot.intelDev.device.Device;

public class MyOnGearNeedToAutoListener implements Device.OnGearNeedToAutoListener {

    @Override
    public void onGearNeedToAuto(Device device, boolean b) {
        if(null != ElectricalCtrlFragment.handler){
            ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.NOTIFY_ADAPTER, RecyclerAdapterElectrical3.GEAR_NEED_TO_AUTO, RecyclerAdapterElectrical3.GEAR_NEED_TO_AUTO, device).sendToTarget();
        }
        RecyclerAdapterElectricalList.handler.obtainMessage(RecyclerAdapterElectricalList.GEAR_NEED_TO_AUTO, device).sendToTarget();
    }
}