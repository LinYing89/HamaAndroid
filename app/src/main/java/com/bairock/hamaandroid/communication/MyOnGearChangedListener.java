package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.adapter.RecyclerAdapterElectrical3;
import com.bairock.hamaandroid.adapter.RecyclerAdapterElectricalList;
import com.bairock.hamaandroid.app.ElectricalCtrlFragment;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.database.DeviceDao;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.OrderHelper;
import com.bairock.iot.intelDev.order.DeviceOrder;
import com.bairock.iot.intelDev.order.OrderType;
import com.bairock.iot.intelDev.user.Util;

/**
 * 挡位改变事件
 * Created by 44489 on 2017/12/29.
 */

public class MyOnGearChangedListener implements Device.OnGearChangedListener{
    @Override
    public void onGearChanged(Device device, Gear gear) {
        //发往服务器, 服务器只有收到本地档位报文才会改变档位, 不回自动改变
        DeviceOrder ob = new DeviceOrder(OrderType.GEAR, device.getId(), device.getLongCoding(), gear.toString());
        String order = Util.orderBaseToString(ob);
        PadClient.getIns().send(order);
        refreshUi(device);
        updateDeviceDao(device);
    }

    private void refreshUi(Device device){
        if (device instanceof IStateDev) {
            if (null != ElectricalCtrlFragment.handler) {
                ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.NOTIFY_ADAPTER, RecyclerAdapterElectrical3.AUTO, RecyclerAdapterElectrical3.AUTO, device).sendToTarget();
            }
        }
    }

    private void updateDeviceDao(Device device) {
        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        deviceDao.update(device);
    }
}
