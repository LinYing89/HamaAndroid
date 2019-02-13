package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;

public class MyOnSortIndexChangedListener implements Device.OnSortIndexChangedListener {
    @Override
    public void onSortIndexChanged(Device device, int i) {
        DeviceDao.get(HamaApp.HAMA_CONTEXT).update(device);
    }
}
