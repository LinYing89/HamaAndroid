package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.adapter.*;
import com.bairock.hamaandroid.app.ClimateFragment;
import com.bairock.hamaandroid.app.ElectricalCtrlFragment;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.user.MyHome;

public class MyOnNameChangedListener implements MyHome.OnNameChangedListener {

    private void updateDeviceDao(Device device) {
        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        deviceDao.update(device);
    }

    @Override
    public void onNameChanged(MyHome myHome, String s) {
        if(myHome instanceof Device){
//            refreshUi((Device) myHome);
            updateDeviceDao((Device) myHome);
        }
    }
}