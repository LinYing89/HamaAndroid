package com.bairock.hamaandroid.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bairock.iot.intelDev.linkage.ChainHolder;
import com.bairock.iot.intelDev.linkage.LinkageHolder;
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHolder;
import com.bairock.iot.intelDev.linkage.loop.LoopHolder;
import com.bairock.iot.intelDev.linkage.timing.TimingHolder;

/**
 *
 * Created by Administrator on 2017/9/11.
 */

public class LinkageHolderWrapper extends CursorWrapper {

    public LinkageHolderWrapper(Cursor cursor) {
        super(cursor);
    }

    public LinkageHolder getLinkageHolder() {
        String id = getString(getColumnIndex(DbSb.TabLinkageHolder.Cols.ID));
        String type = getString(getColumnIndex(DbSb.TabLinkageHolder.Cols.LINKAGE_TYPE));
        boolean enable = getString(getColumnIndex(DbSb.TabLinkageHolder.Cols.ENABLE)).equals("1");
        LinkageHolder linkageHolder = null;
        if(type.equals("ChainHolder")){
            linkageHolder = new ChainHolder();
        }else if(type.equals("LoopHolder")){
            linkageHolder = new LoopHolder();
        }else if(type.equals("TimingHolder")){
            linkageHolder = new TimingHolder();
        }else if(type.equals("GuaguaHolder")){
            linkageHolder = new GuaguaHolder();
        }
        linkageHolder.setId(id);
        linkageHolder.setEnable(enable);
        return linkageHolder;
    }
}
