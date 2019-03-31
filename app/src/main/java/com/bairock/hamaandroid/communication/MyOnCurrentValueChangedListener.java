package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.adapter.RecyclerAdapterCollect;
import com.bairock.hamaandroid.app.ClimateFragment;
import com.bairock.hamaandroid.database.Config;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.order.DeviceOrder;
import com.bairock.iot.intelDev.order.LoginModel;
import com.bairock.iot.intelDev.order.OrderType;
import com.bairock.iot.intelDev.user.Util;

public class MyOnCurrentValueChangedListener implements CollectProperty.OnCurrentValueChangedListener {

    private static MyOnCurrentValueChangedListener ins = new MyOnCurrentValueChangedListener();

    private MyOnCurrentValueChangedListener(){}

    public static MyOnCurrentValueChangedListener getIns(){
        return ins;
    }

    @Override
    public void onCurrentValueChanged(DevCollect devCollect, Float aFloat) {
        // 客户端为本地登录,本地设备才往服务器发送状态，远程设备只接收服务器状态
        if (Config.ins().getLoginModel().equals(LoginModel.LOCAL) && devCollect.findSuperParent().getCtrlModel() == CtrlModel.LOCAL) {
            DeviceOrder devOrder = new DeviceOrder(OrderType.VALUE, devCollect.getId(), devCollect.getLongCoding(), String.valueOf(aFloat));
            String strOrder = Util.orderBaseToString(devOrder);
            PadClient.getIns().send(strOrder);
        }
        if (null != ClimateFragment.handler) {
            ClimateFragment.handler.obtainMessage(ClimateFragment.NOTIFY_ADAPTER, RecyclerAdapterCollect.VALUE, RecyclerAdapterCollect.VALUE, devCollect).sendToTarget();
        }
    }
}