package com.bairock.hamaandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.LinkageHolder;
import com.bairock.iot.intelDev.linkage.SubChain;
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;
import com.bairock.iot.intelDev.linkage.timing.Timing;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.User;

import java.util.List;
import static com.bairock.hamaandroid.database.DbSb.*;

public class SdDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 4;
    private static final String DATABASE_NAME = "sd_db.db";

    public SdDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建user表
        db.execSQL("create table " + TabUser.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TabUser.Cols.EMAIL + ", " +
                TabUser.Cols.USER_ID + ", " +
                TabUser.Cols.NAME + ", " +
                TabUser.Cols.PET_NAME + ", " +
                TabUser.Cols.PSD + ", " +
                TabUser.Cols.REGISTER_TIME + ", " +
                TabUser.Cols.TEL +
                ")"
        );
        //创建devGroup表
        db.execSQL("create table " + TabDevGroup.NAME + "(" +
                TabDevGroup.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabDevGroup.Cols.NAME + ", " +
                TabDevGroup.Cols.PET_NAME + ", " +
                TabDevGroup.Cols.PSD + ", " +
                TabDevGroup.Cols.USER_ID +
                ")"
        );
        //创建device表
        db.execSQL("create table " + TabDevice.NAME + "(" +
                TabDevice.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabDevice.Cols.DEVICE_TYPE + ", " +
                TabDevice.Cols.ALIAS + ", " +
                TabDevice.Cols.CTRL_MODEL + ", " +
                TabDevice.Cols.VISIBILITY + ", " +
                TabDevice.Cols.DELETED + ", " +
                TabDevice.Cols.DEV_CATEGORY + ", " +
                TabDevice.Cols.DEV_STATE_ID + ", " +
                TabDevice.Cols.GEAR + ", " +
                TabDevice.Cols.MAIN_CODE_ID + ", " +
                TabDevice.Cols.NAME + ", " +
                TabDevice.Cols.PLACE + ", " +
                TabDevice.Cols.SN + ", " +
                TabDevice.Cols.SORT_INDEX + ", " +
                TabDevice.Cols.SUB_CODE + ", " +
                TabDevice.Cols.PANID + ", " +
                TabDevice.Cols.DEV_GROUP_ID + ", " +
                TabDevice.Cols.PARENT_ID +
                ")"
        );
        //创建collect property表
        db.execSQL("create table " + TabCollectProperty.NAME + "(" +
                TabCollectProperty.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabCollectProperty.Cols.CREST_VALUE + ", " +
                TabCollectProperty.Cols.CREST_REFER_VALUE + ", " +
                TabCollectProperty.Cols.CURRENT_VALUE + ", " +
                TabCollectProperty.Cols.LEAST_VALUE + ", " +
                TabCollectProperty.Cols.LEAST_REFER_VALUE + ", " +
                TabCollectProperty.Cols.PERCENT + ", " +
                TabCollectProperty.Cols.SIGNAL_SRC + ", " +
                TabCollectProperty.Cols.UNIT_SYMBOL + ", " +
                TabCollectProperty.Cols.CALIBRATION_VALUE + ", " +
                TabCollectProperty.Cols.FORMULA + ", " +
                TabCollectProperty.Cols.DEV_COLLECT_ID +
                ")"
        );

        //创建value trigger表
        db.execSQL("create table " + TabValueTrigger.NAME + "(" +
                TabValueTrigger.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabValueTrigger.Cols.NAME + ", " +
                TabValueTrigger.Cols.ENABLE + ", " +
                TabValueTrigger.Cols.TRIGGER_VALUE + ", " +
                TabValueTrigger.Cols.COMPARE_SYMBOL + ", " +
                TabValueTrigger.Cols.MESSAGE + ", " +
                TabValueTrigger.Cols.COLLECT_PROPERTY_ID +
                ")"
        );

        //创建alarm trigger表
        db.execSQL("create table " + TabAlarmTrigger.NAME + "(" +
                TabAlarmTrigger.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabAlarmTrigger.Cols.ENABLE + ", " +
                TabAlarmTrigger.Cols.MESSAGE + ", " +
                TabAlarmTrigger.Cols.DEV_ALARM_ID +
                ")"
        );

        //创建remote key表
        db.execSQL("create table " + TabRemoterKey.NAME + "(" +
                TabRemoterKey.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabRemoterKey.Cols.REMOTE_ID + ", " +
                TabRemoterKey.Cols.NAME + ", " +
                TabRemoterKey.Cols.NUMBER + ", " +
                TabRemoterKey.Cols.LOCATION_X + ", " +
                TabRemoterKey.Cols.LOCATION_Y +
                ")"
        );

        //创建deviceLinkage表
        db.execSQL("create table " + TabDeviceLinkage.NAME + "(" +
                TabDeviceLinkage.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabDeviceLinkage.Cols.SWITCH_MODEL + ", " +
                TabDeviceLinkage.Cols.VALUE1 + ", " +
                TabDeviceLinkage.Cols.VALUE2 + ", " +
                TabDeviceLinkage.Cols.SOURCE_DEVICE_ID + ", " +
                TabDeviceLinkage.Cols.TARGET_DEV_ID +
                ")"
        );

        //创建linkage holder表
        db.execSQL("create table " + TabLinkageHolder.NAME + "(" +
                TabLinkageHolder.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkageHolder.Cols.DEVGROUP_ID + ", " +
                TabLinkageHolder.Cols.LINKAGE_TYPE + ", " +
                TabLinkageHolder.Cols.ENABLE +
                ")"
        );
        //创建linkage 子连锁数据表
        db.execSQL("create table " + TabLinkage.NAME + "(" +
                TabLinkage.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkage.Cols.LINKAGE_TYPE + ", " +
                TabLinkage.Cols.DELETED + ", " +
                TabLinkage.Cols.ENABLE + ", " +
                TabLinkage.Cols.NAME + ", " +
                TabLinkage.Cols.TRIGGERED + ", " +
                TabLinkage.Cols.LOOP_COUNT + ", " +
                TabLinkage.Cols.LINKAGE_HOLDER_ID +
                ")"
        );
        //创建linkage condition 连锁条件数据表
        db.execSQL("create table " + TabLinkageCondition.NAME + "(" +
                TabLinkageCondition.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLinkageCondition.Cols.COMPARE_SYMBOL + ", " +
                TabLinkageCondition.Cols.COMPARE_VALUE + ", " +
                TabLinkageCondition.Cols.DELETED + ", " +
                TabLinkageCondition.Cols.LOGIC + ", " +
                TabLinkageCondition.Cols.TRIGGER_STYLE + ", " +
                TabLinkageCondition.Cols.DEV_ID + ", " +
                TabLinkageCondition.Cols.SUBCHAIN_ID +
                ")"
        );
        //创建effect 连锁影响数据表
        db.execSQL("create table " + TabEffect.NAME + "(" +
                TabEffect.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabEffect.Cols.DELETED + ", " +
                TabEffect.Cols.DS_ID + ", " +
                TabEffect.Cols.EFFECT_CONTENT + ", " +
                TabEffect.Cols.EFFECT_COUNT + ", " +
                TabEffect.Cols.DEV_ID + ", " +
                TabEffect.Cols.LINKAGE_ID +
                ")"
        );
        //创建my time 时分秒数据表
        db.execSQL("create table " + TabMyTime.NAME + "(" +
                TabMyTime.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabMyTime.Cols.HOUR + ", " +
                TabMyTime.Cols.MINUTE + ", " +
                TabMyTime.Cols.TYPE + ", " +
                TabMyTime.Cols.TIMER_ID + ", " +
                TabMyTime.Cols.SECOND +
                ")"
        );
        //创建week helper 星期助手数据表
        db.execSQL("create table " + TabWeekHelper.NAME + "(" +
                TabWeekHelper.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabWeekHelper.Cols.ZTIMER_ID + ", " +
                TabWeekHelper.Cols.SUN + ", " +
                TabWeekHelper.Cols.MON + ", " +
                TabWeekHelper.Cols.TUES + ", " +
                TabWeekHelper.Cols.WED + ", " +
                TabWeekHelper.Cols.THUR + ", " +
                TabWeekHelper.Cols.FRI + ", " +
                TabWeekHelper.Cols.SAT +
                ")"
        );
        //创建ztimer 子定时数据表
        db.execSQL("create table " + TabZTimer.NAME + "(" +
                TabZTimer.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabZTimer.Cols.DELETED + ", " +
                TabZTimer.Cols.ENABLE + ", " +
                TabZTimer.Cols.TIMING_ID +
                ")"
        );
        //创建loop duration 循环区间，开区间，关区间数据表
        db.execSQL("create table " + TabLoopDuration.NAME + "(" +
                TabLoopDuration.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabLoopDuration.Cols.DELETED + ", " +
                TabLoopDuration.Cols.ZLOOP_ID +
                ")"
        );

        //创建alarm message数据表
        db.execSQL("create table " + TabAlarmMessage.NAME + "(" +
                TabAlarmMessage.Cols.ID + " PRIMARY KEY NOT NULL, " +
                TabAlarmMessage.Cols.NAME + ", " +
                TabAlarmMessage.Cols.MESSAGE + ", " +
                TabAlarmMessage.Cols.TIME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 3:
                    updateTo4(db);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateTo4(SQLiteDatabase db){
        String sql = "alter table " + TabUser.NAME + " add " + TabUser.Cols.USER_ID;
        db.execSQL(sql);
    }

    private static void cleanDb(){
        CollectPropertyDao.get(HamaApp.HAMA_CONTEXT).clean();
        ValueTriggerDao.get(HamaApp.HAMA_CONTEXT).clean();
        AlarmTriggerDao.get(HamaApp.HAMA_CONTEXT).clean();
        RemoterKeyDao.get(HamaApp.HAMA_CONTEXT).clean();
        DevGroupDao.get(HamaApp.HAMA_CONTEXT).clean();
        DeviceDao.get(HamaApp.HAMA_CONTEXT).clean();
        EffectDao.get(HamaApp.HAMA_CONTEXT).clean();
        LinkageConditionDao.get(HamaApp.HAMA_CONTEXT).clean();
        LinkageDao.get(HamaApp.HAMA_CONTEXT).clean();
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).clean();
        LoopDurationDao.get(HamaApp.HAMA_CONTEXT).clean();
        MyTimeDao.get(HamaApp.HAMA_CONTEXT).clean();
        UserDao.get(HamaApp.HAMA_CONTEXT).clean();
        WeekHelperDao.get(HamaApp.HAMA_CONTEXT).clean();
        ZTimerDao.get(HamaApp.HAMA_CONTEXT).clean();
        DeviceLinkageDao.get(HamaApp.HAMA_CONTEXT).clean();
    }

    public static void replaceDbUser(User user){
        cleanDb();
        DevGroup devGroup = user.getListDevGroup().get(0);
        UserDao.get(HamaApp.HAMA_CONTEXT).addUser(user);
        DevGroupDao.get(HamaApp.HAMA_CONTEXT).add(devGroup);
        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        for(Device device : devGroup.getListDevice()){
            deviceDao.add(device);
        }

        LinkageHolderDao linkageHolderDao = LinkageHolderDao.get(HamaApp.HAMA_CONTEXT);
        for(LinkageHolder linkageHolder : devGroup.getListLinkageHolder()){
            linkageHolderDao.add(linkageHolder);
        }

        LinkageDao linkageDao = LinkageDao.get(HamaApp.HAMA_CONTEXT);
        LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(HamaApp.HAMA_CONTEXT);
        EffectDao effectDao = EffectDao.get(HamaApp.HAMA_CONTEXT);

        for(LinkageHolder linkageHolder : devGroup.getListLinkageHolder()){
            for(Linkage linkage : linkageHolder.getListLinkage()){
                linkageDao.add(linkage, linkageHolder.getId());
                for(Effect effect : linkage.getListEffect()){
                    effectDao.add(effect, linkage.getId());
                }
                if(linkage instanceof SubChain){
                    SubChain subChain = (SubChain)linkage;
                    for(LinkageCondition linkageCondition : subChain.getListCondition()){
                        linkageConditionDao.add(linkageCondition, subChain.getId());
                    }
                    if(linkage instanceof ZLoop){
                        ZLoop loop = (ZLoop)linkage;
                        LoopDurationDao loopDurationDao = LoopDurationDao.get(HamaApp.HAMA_CONTEXT);
                        MyTimeDao myTimeDao = MyTimeDao.get(HamaApp.HAMA_CONTEXT);
                        for(LoopDuration loopDuration : loop.getListLoopDuration()) {
                            loopDurationDao.add(loopDuration, loop.getId());
                            myTimeDao.add(loopDuration.getOnKeepTime(), loopDuration.getId());
                            myTimeDao.add(loopDuration.getOffKeepTime(), loopDuration.getId());
                        }
                    }
                }else if(linkage instanceof Timing){
                    Timing timing = (Timing)linkage;
                    ZTimerDao zTimerDao = ZTimerDao.get(HamaApp.HAMA_CONTEXT);
                    MyTimeDao myTimeDao = MyTimeDao.get(HamaApp.HAMA_CONTEXT);
                    WeekHelperDao weekHelperDao = WeekHelperDao.get(HamaApp.HAMA_CONTEXT);
                    for(ZTimer zTimer : timing.getListZTimer()){
                        zTimerDao.add(zTimer, zTimer.getId());
                        myTimeDao.add(zTimer.getOnTime(), zTimer.getId());
                        myTimeDao.add(zTimer.getOffTime(), zTimer.getId());
                        weekHelperDao.add(zTimer.getWeekHelper());
                    }
                }
            }
        }
    }

    public static User getDbUser(){
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        User user =  userDao.getUser();
        if(null == user){
            return null;
        }

        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        DevGroup group = devGroupDao.find();
        if(null == group){
            return null;
        }
        user.addGroup(group);

        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        List<Device> listDevice = deviceDao.findIncludeDeleted();

        DeviceLinkageDao deviceLinkageDao = DeviceLinkageDao.get(HamaApp.HAMA_CONTEXT);
        for (Device device : listDevice){
            List<DeviceLinkage> listDeviceLinkage = deviceLinkageDao.find(device, listDevice);
            device.setListDeviceLinkage(listDeviceLinkage);
            group.addDevice(device);
        }

        LinkageConditionWrapper.devGroup = group;
        EffectWrapper.devGroup = group;
        //连锁
        LinkageHolderDao linkageHolderDao = LinkageHolderDao.get(HamaApp.HAMA_CONTEXT);
        group.setListLinkageHolder(linkageHolderDao.findByDevGroupId(group.getId()));

        LinkageDao linkageDao = LinkageDao.get(HamaApp.HAMA_CONTEXT);
        for(LinkageHolder linkageHolder : group.getListLinkageHolder()){
            List<Linkage> listLinkage = linkageDao.findChainByLinkageHolderId(linkageHolder.getId());
            linkageHolder.setListLinkage(listLinkage);
        }
        return user;
    }

    public static String[] arrayOf(String str){
        return new String[]{str};
    }
}
