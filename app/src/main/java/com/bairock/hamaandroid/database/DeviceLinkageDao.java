package com.bairock.hamaandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage;

import java.util.ArrayList;
import java.util.List;

import static com.bairock.hamaandroid.database.SdDbHelper.arrayOf;

public class DeviceLinkageDao {

    private static DeviceLinkageDao deviceLinkageDao;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DeviceLinkageDao get(Context context) {
        if(null == deviceLinkageDao){
            deviceLinkageDao = new DeviceLinkageDao(context);
        }
        return deviceLinkageDao;
    }

    private DeviceLinkageDao(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private ContentValues getContentValues(DeviceLinkage deviceLinkage) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabDeviceLinkage.Cols.ID, deviceLinkage.getId());
        values.put(DbSb.TabDeviceLinkage.Cols.SWITCH_MODEL, deviceLinkage.getSwitchModel());
        values.put(DbSb.TabDeviceLinkage.Cols.VALUE1, deviceLinkage.getValue1());
        values.put(DbSb.TabDeviceLinkage.Cols.VALUE2, deviceLinkage.getValue2());
        values.put(DbSb.TabDeviceLinkage.Cols.SOURCE_DEVICE_ID, deviceLinkage.getSourceDevice().getId());
        if(deviceLinkage.getTargetDevice() != null) {
            values.put(DbSb.TabDeviceLinkage.Cols.TARGET_DEV_ID, deviceLinkage.getTargetDevice().getId());
        }
        return values;
    }

    public void add(DeviceLinkage deviceLinkage) {
        ContentValues values = getContentValues(deviceLinkage);
        mDatabase.insert(DbSb.TabDeviceLinkage.NAME, null, values);
    }

    public void delete(DeviceLinkage deviceLinkage) {
        mDatabase.delete(DbSb.TabDeviceLinkage.NAME, DbSb.TabDeviceLinkage.Cols.ID + "=?", arrayOf(deviceLinkage.getId()));
    }

    public List<DeviceLinkage> find(Device device, List<Device> listDevice) {
        return find(DbSb.TabDeviceLinkage.Cols.SOURCE_DEVICE_ID + " = ?", arrayOf(device.getId()), listDevice);
    }

    public List<DeviceLinkage> find(String whereClause, String[] whereArgs, List<Device> listDevice) {
        List<DeviceLinkage> listDeviceLinkage = new ArrayList<>();
        DeviceLinkageWrapper cursor = query(whereClause, whereArgs, listDevice);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                DeviceLinkage device = cursor.getDeviceLinkage();
                listDeviceLinkage.add(device);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return listDeviceLinkage;
    }

    public void update(DeviceLinkage deviceLinkage) {
        ContentValues values = getContentValues(deviceLinkage);
        mDatabase.update(DbSb.TabDeviceLinkage.NAME, values,
                "id = ?",
                arrayOf(deviceLinkage.getId()));
    }

    private DeviceLinkageWrapper query(String whereClause, String[] whereArgs, List<Device> listDevice) {
        Cursor cursor = mDatabase.query(
                DbSb.TabDeviceLinkage.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        );// groupBy
        return new DeviceLinkageWrapper(cursor, listDevice);
    }

    public void clean() {
        mDatabase.execSQL("delete from " + DbSb.TabDeviceLinkage.NAME);
    }
}