package com.bairock.hamaandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger;

import java.util.ArrayList;
import java.util.List;

import static com.bairock.hamaandroid.database.SdDbHelper.arrayOf;

public class ValueTriggerDao {

    private static ValueTriggerDao valueTriggerDao;
    private Context context;
    private SQLiteDatabase mDatabase;

    private ValueTriggerDao(Context context){
        this.context = context;
        mDatabase = new SdDbHelper(context).getWritableDatabase();
    }

    public static ValueTriggerDao get(Context context){
        if (null == valueTriggerDao) {
            valueTriggerDao = new ValueTriggerDao(context);
        }
        return valueTriggerDao;
    }

    private ContentValues getContentValues(ValueTrigger valueTrigger) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabValueTrigger.Cols.ID, valueTrigger.getId());
        values.put(DbSb.TabValueTrigger.Cols.NAME, valueTrigger.getName());
        values.put(DbSb.TabValueTrigger.Cols.ENABLE, valueTrigger.isEnable());
        values.put(DbSb.TabValueTrigger.Cols.TRIGGER_VALUE, valueTrigger.getTriggerValue());
        values.put(DbSb.TabValueTrigger.Cols.COMPARE_SYMBOL, valueTrigger.getCompareSymbol().toString());
        values.put(DbSb.TabValueTrigger.Cols.MESSAGE, valueTrigger.getMessage());
        values.put(DbSb.TabValueTrigger.Cols.COLLECT_PROPERTY_ID, valueTrigger.getCollectProperty().getId());
        return values;
    }

    public void add(ValueTrigger valueTrigger) {
        ContentValues values = getContentValues(valueTrigger);
        mDatabase.insert(DbSb.TabValueTrigger.NAME, null, values);
    }

    public void delete(ValueTrigger valueTrigger) {
        mDatabase.delete(DbSb.TabValueTrigger.NAME, DbSb.TabValueTrigger.Cols.ID + "=?", arrayOf(valueTrigger.getId()));
    }

    public List<ValueTrigger> find(CollectProperty collectProperty) {
        return find(DbSb.TabValueTrigger.Cols.COLLECT_PROPERTY_ID + " = ?", arrayOf(collectProperty.getId()));
    }

    public List<ValueTrigger> find(String whereClause, String[] whereArgs) {
        List<ValueTrigger> listValueTrigger = new ArrayList<>();
        ValueTriggerWrapper cursor = query(whereClause, whereArgs);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ValueTrigger device = cursor.getValueTrigger();
                listValueTrigger.add(device);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return listValueTrigger;
    }

    public void update(ValueTrigger valueTrigger) {
        ContentValues values = getContentValues(valueTrigger);
        mDatabase.update(DbSb.TabValueTrigger.NAME, values,
                "id = ?",
                arrayOf(valueTrigger.getId()));
    }

    private ValueTriggerWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabValueTrigger.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        );// groupBy
        return new ValueTriggerWrapper(cursor);
    }

    public void clean() {
        mDatabase.execSQL("delete from " + DbSb.TabValueTrigger.NAME);
    }
}