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

    private void refreshUi(Device device) {
        if (device instanceof IStateDev) {
            if (Config.ins().getDevShowStyle().equals("0")) {
                if(null != ElectricalCtrlFragment.handler){
                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.NOTIFY_ADAPTER, RecyclerAdapterElectrical3.NAME, RecyclerAdapterElectrical3.NAME, device);
                }
            } else {
                if (null != RecyclerAdapterElectricalList.handler) {
                    RecyclerAdapterElectricalList.handler.obtainMessage(RecyclerAdapterElectricalList.NAME, device).sendToTarget();
                }
            }
        }else if(device instanceof DevCollect){
            if (null != ClimateFragment.handler) {
                ClimateFragment.handler.obtainMessage(ClimateFragment.NOTIFY_ADAPTER, RecyclerAdapterCollect.NAME, RecyclerAdapterCollect.NAME, device).sendToTarget();
            }
        }
        if(null != RecyclerAdapterDevice.handler){
            RecyclerAdapterDevice.handler.obtainMessage(RecyclerAdapterDevice.NAME, device).sendToTarget();
        }

        if(null != RecyclerAdapterChildDevice.handler){
            RecyclerAdapterChildDevice.handler.obtainMessage(RecyclerAdapterChildDevice.NAME, device).sendToTarget();
        }
    }

    private void updateDeviceDao(Device device) {
        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        deviceDao.update(device);
    }

    @Override
    public void onNameChanged(MyHome myHome, String s) {
        if(myHome instanceof Device){
            refreshUi((Device) myHome);
            updateDeviceDao((Device) myHome);
        }
    }
}