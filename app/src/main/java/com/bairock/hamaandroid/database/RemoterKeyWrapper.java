package com.bairock.hamaandroid.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.device.remoter.RemoterKey;

public class RemoterKeyWrapper extends CursorWrapper {

    public RemoterKeyWrapper(Cursor cursor) {
        super(cursor);
    }

    public RemoterKey getRemoterKey() {
        String id = getString(getColumnIndex(DbSb.TabRemoterKey.Cols.ID));
        String name = getString(getColumnIndex(DbSb.TabRemoterKey.Cols.NAME));
        String number = getString(getColumnIndex(DbSb.TabRemoterKey.Cols.NUMBER));
        int locationX = getInt(getColumnIndex(DbSb.TabRemoterKey.Cols.LOCATION_X));
        int locationY = getInt(getColumnIndex(DbSb.TabRemoterKey.Cols.LOCATION_Y));

        RemoterKey remoterKey = new RemoterKey();
        remoterKey.setId(id);
        remoterKey.setName(name);
        remoterKey.setNumber(number);
        remoterKey.setLocationX(locationX);
        remoterKey.setLocationY(locationY);
        return remoterKey;
    }
}