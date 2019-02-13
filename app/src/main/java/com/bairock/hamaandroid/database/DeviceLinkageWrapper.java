package com.bairock.hamaandroid.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage;
import com.bairock.iot.intelDev.user.DevGroup;

import java.util.List;

public class DeviceLinkageWrapper extends CursorWrapper {

    private List<Device> listDevice;

    public DeviceLinkageWrapper(Cursor cursor){
        super(cursor);
    }

    public DeviceLinkageWrapper(Cursor cursor, List<Device> listDevice){
        this(cursor);
        this.listDevice = listDevice;
    }

    public DeviceLinkage getDeviceLinkage() {
        String id = getString(getColumnIndex(DbSb.TabDeviceLinkage.Cols.ID));
        int switchModel = getInt(getColumnIndex(DbSb.TabDeviceLinkage.Cols.SWITCH_MODEL));
        float value1 = getFloat(getColumnIndex(DbSb.TabDeviceLinkage.Cols.VALUE1));
        float value2 = getFloat(getColumnIndex(DbSb.TabDeviceLinkage.Cols.VALUE2));
        String targetDevId = getString(getColumnIndex(DbSb.TabDeviceLinkage.Cols.TARGET_DEV_ID));

        Device targetDev = DevGroup.findDeviceByDevId(listDevice, targetDevId);

        DeviceLinkage deviceLinkage = new DeviceLinkage();
        deviceLinkage.setId(id);
        deviceLinkage.setSwitchModel(switchModel);
        deviceLinkage.setValue1(value1);
        deviceLinkage.setValue2(value2);
        deviceLinkage.setTargetDevice(targetDev);
        return deviceLinkage;
    }

}