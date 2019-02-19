package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.adapter.RecyclerAdapterChildDevice;
import com.bairock.hamaandroid.adapter.RecyclerAdapterDevice;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;

public class MyOnAliasChangedListener implements Device.OnAliasChangedListener{
    @Override
    public void onAliasChanged(Device p0, String p1) {
//        refreshUi(p0);
        updateDeviceDao(p0);
    }

    private void refreshUi(Device device) {
        if(null != RecyclerAdapterDevice.handler){
            RecyclerAdapterDevice.handler.obtainMessage(RecyclerAdapterDevice.ALIAS, device).sendToTarget();
        }

        if(null != RecyclerAdapterChildDevice.handler){
            RecyclerAdapterChildDevice.handler.obtainMessage(RecyclerAdapterChildDevice.ALIAS, device).sendToTarget();
        }
    }

    private void updateDeviceDao(Device device) {
        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        deviceDao.update(device);
    }
}