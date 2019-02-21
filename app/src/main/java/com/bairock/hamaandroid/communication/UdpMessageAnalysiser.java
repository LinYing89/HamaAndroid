package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.app.LogUtils;
import com.bairock.hamaandroid.esptouch.EspTouchAddDevice;
import com.bairock.hamaandroid.settings.UdpLogActivity;
import com.bairock.iot.intelDev.communication.MessageAnalysiser;
import com.bairock.iot.intelDev.communication.UdpServer;
import com.bairock.iot.intelDev.device.Device;

/**
 *
 * Created by 44489 on 2018/1/11.
 */

public class UdpMessageAnalysiser extends MessageAnalysiser {

    @Override
    public void receivedMsg(String msg) {
        super.receivedMsg(msg);
        if(LogUtils.APP_DBG) {
            UdpLogActivity.addRec(msg);
        }
    }

    @Override
    public void deviceHandleBefore(Device device, String s) {

    }

    @Override
    public void deviceHandleAfter(Device device, String s) {

    }

    @Override
    public boolean singleMessageStart(String msg) {
        //如果不在添加设备，udp不处理信息
        return EspTouchAddDevice.CONFIGING;
    }

    @Override
    public void unKnowDev(Device device, String s) {

    }

    @Override
    public void unKnowMsg(String s) {

    }

    @Override
    public void allMessageEnd() {

    }

    @Override
    public void singleMessageEnd(Device device, String s) {

    }

    @Override
    public void configDevice(Device device, String s) {
        if(null == EspTouchAddDevice.DEVICE) {
            EspTouchAddDevice.DEVICE = device;
        }else {
            EspTouchAddDevice.RECEIVED_OK_COUNT++;
            UdpServer.getIns().send("$" + s);
        }
    }
}
