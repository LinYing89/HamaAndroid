package com.bairock.hamaandroid.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.device.alarm.AlarmTrigger;

public class AlarmTriggerWrapper extends CursorWrapper {
    public AlarmTriggerWrapper(Cursor cursor) {
        super(cursor);
    }

    public AlarmTrigger getAlarmTrigger() {
        String id = getString(getColumnIndex(DbSb.TabAlarmTrigger.Cols.ID));
        boolean enable = getString(getColumnIndex(DbSb.TabAlarmTrigger.Cols.ENABLE)).equals("1");
        String message = getString(getColumnIndex(DbSb.TabAlarmTrigger.Cols.MESSAGE));

        AlarmTrigger valueTrigger = new AlarmTrigger();
        valueTrigger.setId(id);
        valueTrigger.setEnable(enable);
        valueTrigger.setMessage(message);
        return valueTrigger;
    }
}
