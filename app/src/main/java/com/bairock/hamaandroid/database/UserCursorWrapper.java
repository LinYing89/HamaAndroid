package com.bairock.hamaandroid.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.user.User;

import java.util.Date;

/**
 *
 * Created by Administrator on 2017/8/8.
 */

public class UserCursorWrapper extends CursorWrapper {

    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        Long id = getLong(getColumnIndex(DbSb.TabUser.Cols.ID));
        String email = getString(getColumnIndex(DbSb.TabUser.Cols.EMAIL));
        String userid = getString(getColumnIndex(DbSb.TabUser.Cols.USER_ID));
        String name = getString(getColumnIndex(DbSb.TabUser.Cols.NAME));
        String petName = getString(getColumnIndex(DbSb.TabUser.Cols.PET_NAME));
        String psd = getString(getColumnIndex(DbSb.TabUser.Cols.PSD));
        Long date = getLong(getColumnIndex(DbSb.TabUser.Cols.REGISTER_TIME));
        String tel = getString(getColumnIndex(DbSb.TabUser.Cols.TEL));
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setUserid(userid);
        user.setUsername(name);
        user.setPetname(petName);
        user.setPassword(psd);
        user.setRegisterTime(new Date(date));
        user.setTel(tel);
        return user;
    }
}
