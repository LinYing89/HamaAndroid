package com.bairock.hamaandroid.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.bairock.iot.intelDev.device.CompareSymbol;
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger;

public class ValueTriggerWrapper extends CursorWrapper {

    public ValueTriggerWrapper(Cursor cursor) {
        super(cursor);
    }

    public ValueTrigger getValueTrigger() {
        String id = getString(getColumnIndex(DbSb.TabValueTrigger.Cols.ID));
        String name = getString(getColumnIndex(DbSb.TabValueTrigger.Cols.NAME));
        boolean enable = getString(getColumnIndex(DbSb.TabValueTrigger.Cols.ENABLE)).equals("1");
        float triggerValue = getFloat(getColumnIndex(DbSb.TabValueTrigger.Cols.TRIGGER_VALUE));
        CompareSymbol compareSymbol = CompareSymbol.valueOf(getString(getColumnIndex(DbSb.TabValueTrigger.Cols.COMPARE_SYMBOL)));
        String message = getString(getColumnIndex(DbSb.TabValueTrigger.Cols.MESSAGE));

        ValueTrigger valueTrigger = new ValueTrigger();
        valueTrigger.setId(id);
        valueTrigger.setName(name);
        valueTrigger.setEnable(enable);
        valueTrigger.setTriggerValue(triggerValue);
        valueTrigger.setCompareSymbol(compareSymbol);
        valueTrigger.setMessage(message);
        return valueTrigger;
    }
}