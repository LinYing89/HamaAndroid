package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.database.DeviceDao;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.esptouch.EspTouchAddDevice;
import com.bairock.hamaandroid.settings.SearchActivity;
import com.bairock.iot.intelDev.communication.MessageAnalysiser;
import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.DeviceAssistent;
import com.bairock.iot.intelDev.device.LinkType;
import com.bairock.iot.intelDev.device.SetDevModelTask;

/**
 *
 * Created by Administrator on 2017/8/29.
 */

public class MyMessageAnalysiser extends MessageAnalysiser {


    @Override
    public void receivedMsg(String msg) {
        //TcpLogActivity.addRec(msg);
    }

    @Override
    public void deviceFeedback(Device device, String msg) {
        device.setLinkType(LinkType.NET);
		PadClient.getIns().sendIfSync("$" + msg);
        updateDevice(device);
    }

    public void updateDevice(Device device){
        if(device.getCtrlModel() != CtrlModel.LOCAL){
            device.setCtrlModel(CtrlModel.LOCAL);
        }
//        if (null != SearchActivity.handler && SetDevModelTask.setting
//                && SearchActivity.setDevModelThread.deviceModelHelper != null
//                && SearchActivity.setDevModelThread.deviceModelHelper.getDevToSet() == device
//                && SearchActivity.setDevModelThread.deviceModelHelper.getCtrlModel() == CtrlModel.LOCAL) {
//            SearchActivity.handler.obtainMessage(SearchActivity.handler.CTRL_MODEL_PROGRESS, 3).sendToTarget();
//        }
    }

    @Override
    public void unKnowDev(Device device, String s) {
        //新设备，未在本系统中
        //addNewDevice(device);
    }

    @Override
    public void unKnowMsg(String msg) {

    }

    @Override
    public void allMessageEnd() {

    }

    @Override
    public boolean singleMessageStart(String msg) {
        if(msg.startsWith("!")){
            if(msg.contains("#")){
                msg = msg.substring(0, msg.indexOf("#"));
            }
            String[] codings = msg.split(":");
            if(codings.length < 2){
                return false;
            }
            //Device device = DeviceAssistent.createDeviceByCoding(codings[1]);
            Device device = HamaApp.DEV_GROUP.findDeviceWithCoding(codings[1]);
            if(!(device instanceof Coordinator)){
                return false;
            }

            Coordinator coordinator = (Coordinator)device;
            if(!coordinator.isConfigingChildDevice()){
                return false;
            }
            for(int i = 2; i< codings.length; i++){
                String coding = codings[i];
                Device device1 = coordinator.findDevByCoding(coding);
                if(null == device1){
                    device1 = DeviceAssistent.createDeviceByCoding(coding);
                    if(device1 != null){
                        HamaApp.DEV_GROUP.createDefaultDeviceName(device1);
                        coordinator.addChildDev(device1);
                        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
                        deviceDao.add(device1);
                    }
                }
            }
            if(null != SearchActivity.handler){
                SearchActivity.handler.obtainMessage(SearchActivity.handler.DEV_ADD_CHILD).sendToTarget();
            }
            return false;
        }
        return true;
    }

    @Override
    public void singleMessageEnd(Device device, String msg) {

    }

    @Override
    public void configDevice(Device device, String s) {
        addNewDevice(device);
    }

    @Override
    public void configDeviceCtrlModel(Device device, String s) {
        if (null != SearchActivity.handler && SetDevModelTask.setting
                && SearchActivity.setDevModelThread.deviceModelHelper != null
                && SearchActivity.setDevModelThread.deviceModelHelper.getDevToSet() == device
                && SearchActivity.setDevModelThread.deviceModelHelper.getCtrlModel() == CtrlModel.LOCAL) {
            SearchActivity.handler.obtainMessage(SearchActivity.handler.CTRL_MODEL_PROGRESS, 3).sendToTarget();
        }
    }

    private void addNewDevice(Device device){
        if(EspTouchAddDevice.CONFIGING){
            HamaApp.DEV_GROUP.createDefaultDeviceName(device);
            EspTouchAddDevice.DEVICE = device;
        }
    }
}
