package com.bairock.hamaandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bairock.iot.intelDev.device.remoter.Remoter;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;

import java.util.ArrayList;
import java.util.List;

import static com.bairock.hamaandroid.database.SdDbHelper.arrayOf;

public class RemoterKeyDao {

    private static RemoterKeyDao remoterKeyDao;
    private SQLiteDatabase mDatabase;

    public static RemoterKeyDao get(Context context) {
        if (null == remoterKeyDao) {
            remoterKeyDao = new RemoterKeyDao(context);
        }
        return remoterKeyDao;
    }

    private RemoterKeyDao(Context context) {
        Context mContext = context.getApplicationContext();
        mDatabase = new SdDbHelper(mContext).getWritableDatabase();
    }

    private ContentValues getContentValues(RemoterKey remoterKey) {
        ContentValues values = new ContentValues();
        values.put(DbSb.TabRemoterKey.Cols.ID, remoterKey.getId());
        values.put(DbSb.TabRemoterKey.Cols.REMOTE_ID, remoterKey.getRemoter().getId());
        values.put(DbSb.TabRemoterKey.Cols.NAME, remoterKey.getName());
        values.put(DbSb.TabRemoterKey.Cols.NUMBER, remoterKey.getNumber());
        values.put(DbSb.TabRemoterKey.Cols.LOCATION_X, remoterKey.getLocationX());
        values.put(DbSb.TabRemoterKey.Cols.LOCATION_Y, remoterKey.getLocationY());
        return values;
    }

    public void add(RemoterKey remoterKey) {
        ContentValues values = getContentValues(remoterKey);
        mDatabase.insert(DbSb.TabRemoterKey.NAME, null, values);
    }

    public void delete(RemoterKey remoterKey) {
        mDatabase.delete(DbSb.TabRemoterKey.NAME, DbSb.TabRemoterKey.Cols.ID + "=?", arrayOf(remoterKey.getId()));
    }

    public List<RemoterKey> find(Remoter remoter) {
        return find(DbSb.TabRemoterKey.Cols.REMOTE_ID + " = ?", arrayOf(remoter.getId()));
    }

    public List<RemoterKey> find(String whereClause, String[] whereArgs) {
        List<RemoterKey> listDevice = new ArrayList<>();
        RemoterKeyWrapper cursor = query(whereClause, whereArgs);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                RemoterKey device = cursor.getRemoterKey();
                listDevice.add(device);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return listDevice;
    }

    public void update(RemoterKey remoterKey) {
        ContentValues values = getContentValues(remoterKey);
        mDatabase.update(DbSb.TabRemoterKey.NAME, values,
                "id = ?",
                arrayOf(remoterKey.getId()));
    }

    private RemoterKeyWrapper query(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSb.TabRemoterKey.NAME, // having
                null // orderBy
                , // Columns - null selects all columns
                whereClause,
                whereArgs, null, null, null
        );// groupBy
        return new RemoterKeyWrapper(cursor);
    }

    public void clean() {
        mDatabase.execSQL("delete from " + DbSb.TabRemoterKey.NAME);
    }
}