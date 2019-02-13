package com.bairock.hamaandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.device.alarm.AlarmTrigger;
import com.bairock.iot.intelDev.device.alarm.DevAlarm;

public class AlarmTriggerDao {

    private static AlarmTriggerDao alarmTriggerDao;
    private Context context;
    private SQLiteDatabase mDatabase;

    private AlarmTriggerDao(Context context){
        this.context = context;
        mDatabase = new SdDbHelper(context).getWritableDatabase();
    }

    public static AlarmTriggerDao get(Context context){
        if(null == alarmTriggerDao){
            alarmTriggerDao = new AlarmTriggerDao(context);
        }
        return alarmTriggerDao;
    }

    private ContentValues getContentValues(AlarmTrigger alarmTrigger){
        ContentValues values = new ContentValues();
        values.put(DbSb.TabAlarmTrigger.Cols.ID, alarmTrigger.getId());
        values.put(DbSb.TabAlarmTrigger.Cols.ENABLE, alarmTrigger.isEnable());
        values.put(DbSb.TabAlarmTrigger.Cols.MESSAGE, alarmTrigger.getMessage());
        values.put(DbSb.TabAlarmTrigger.Cols.DEV_ALARM_ID, alarmTrigger.getDevAlarm().getId());
        return values;
    }

    public void add(AlarmTrigger alarmTrigger) {
        ContentValues values = getContentValues(alarmTrigger);
        mDatabase.insert(DbSb.TabAlarmTrigger.NAME, null, values);
    }

    public void delete(AlarmTrigger alarmTrigger) {
        mDatabase.delete(DbSb.TabAlarmTrigger.NAME, DbSb.TabAlarmTrigger.Cols.ID + "=?", new String[]{alarmTrigger.getId()});
    }

    public AlarmTrigger find(DevAlarm devAlarm) {
        return find(DbSb.TabAlarmTrigger.Cols.DEV_ALARM_ID + " = ?", new String[]{devAlarm.getId()});
    }

    public AlarmTrigger find(String whereClause, String[] whereArgs) {
        AlarmTrigger alarmTrigger = null;
        AlarmTriggerWrapper cursor = query(whereClause, whereArgs);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                alarmTrigger = cursor.getAlarmTrigger();
            }
        } finally {
            cursor.close();
        }
        return alarmTrigger;
    }

    public void update(AlarmTrigger alarmTrigger) {
        ContentValues values = getContentValues(alarmTrigger);
        mDatabase.update(DbSb.TabAlarmTrigger.NAME, values,
                "id = ?",
                new String[]{alarmTrigger.getId()});
    }

    private AlarmTriggerWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabAlarmTrigger.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        );// groupBy
        return new AlarmTriggerWrapper(cursor);
    }

    public void clean() {
        mDatabase.execSQL("delete from " + DbSb.TabAlarmTrigger.NAME);
    }
}
