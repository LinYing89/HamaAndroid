package com.bairock.hamaandroid.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.communication.ChannelBridgeHelperHeartSendListener;
import com.bairock.hamaandroid.communication.MyMessageAnalysiser;
import com.bairock.hamaandroid.communication.MyOnAliasChangedListener;
import com.bairock.hamaandroid.communication.MyOnBridgesChangedListener;
import com.bairock.hamaandroid.communication.MyOnCommunicationListener;
import com.bairock.hamaandroid.communication.MyOnCtrlModelChangedListener;
import com.bairock.hamaandroid.communication.MyOnDevHaveChildeOnCollectionChangedListener;
import com.bairock.hamaandroid.communication.MyOnGearChangedListener;
import com.bairock.hamaandroid.communication.MyOnSignalSourceChangedListener;
import com.bairock.hamaandroid.communication.MyOnSimulatorChangedListener;
import com.bairock.hamaandroid.communication.MyOnSortIndexChangedListener;
import com.bairock.hamaandroid.communication.MyOnStateChangedListener;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.database.DevGroupDao;
import com.bairock.hamaandroid.database.DeviceDao;
import com.bairock.hamaandroid.database.SdDbHelper;
import com.bairock.hamaandroid.database.UserDao;
import com.bairock.hamaandroid.media.Media;
import com.bairock.hamaandroid.communication.MyOnCurrentValueChangedListener;
import com.bairock.hamaandroid.communication.MyOnGearNeedToAutoListener;
import com.bairock.hamaandroid.communication.MyOnNameChangedListener;
import com.bairock.hamaandroid.communication.MyOnRemoterOrderSuccessListener;
import com.bairock.hamaandroid.communication.MyOnUnitSymbolChangedListener;
import com.bairock.hamaandroid.communication.MyOnValueTriggedListener;
import com.bairock.hamaandroid.settings.UdpLogActivity;
import com.bairock.iot.intelDev.communication.DevChannelBridge;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.communication.DevServer;
import com.bairock.iot.intelDev.communication.FindDevHelper;
import com.bairock.iot.intelDev.communication.UdpServer;
import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.DeviceAssistent;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.MainCodeHelper;
import com.bairock.iot.intelDev.device.alarm.DevAlarm;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectClimateContainer;
import com.bairock.iot.intelDev.device.devcollect.Pressure;
import com.bairock.iot.intelDev.device.remoter.RemoterContainer;
import com.bairock.iot.intelDev.linkage.LinkageHelper;
import com.bairock.iot.intelDev.linkage.LinkageTab;
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHelper;
import com.bairock.iot.intelDev.linkage.timing.WeekHelper;
import com.bairock.iot.intelDev.order.LoginModel;
import com.bairock.iot.intelDev.order.OrderType;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.User;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ToMainTask toMainTask = new ToMainTask(this);
        toMainTask.execute();
    }

    private void initMainCodeInfo(){
        Map<String, String> map = new HashMap<>();
        map.put(MainCodeHelper.XIE_TIAO_QI, "协调器");
        map.put(MainCodeHelper.GUAGUA_MOUTH, "呱呱嘴");
        map.put(MainCodeHelper.MEN_JIN, "门禁");
        map.put(MainCodeHelper.YE_WEI, "液位计");
        map.put(MainCodeHelper.COLLECTOR_SIGNAL, "信号采集器");
        map.put(MainCodeHelper.COLLECTOR_SIGNAL_CONTAINER, "多功能信号采集器");
        map.put(MainCodeHelper.COLLECTOR_CLIMATE_CONTAINER, "多功能气候采集器");
        map.put(MainCodeHelper.YAN_WU, "烟雾探测器");
        map.put(MainCodeHelper.WEN_DU, "温度");
        map.put(MainCodeHelper.SHI_DU, "湿度");
        map.put(MainCodeHelper.JIA_QUAN, "甲醛");
        map.put(MainCodeHelper.KG_1LU_2TAI, "一路开关");
        map.put(MainCodeHelper.KG_2LU_2TAI, "两路开关");
        map.put(MainCodeHelper.KG_3LU_2TAI, "三路开关");
        map.put(MainCodeHelper.KG_XLU_2TAI, "多路开关");
        map.put(MainCodeHelper.KG_3TAI, "三态开关");
        map.put(MainCodeHelper.YAO_KONG, "遥控器");
        map.put(MainCodeHelper.CHA_ZUO, "插座");
        map.put(MainCodeHelper.SMC_WU, "未知");
        map.put(MainCodeHelper.SMC_REMOTER_CHUANG_LIAN, "窗帘");
        map.put(MainCodeHelper.SMC_REMOTER_DIAN_SHI, "电视");
        map.put(MainCodeHelper.SMC_REMOTER_KONG_TIAO, "空调");
        map.put(MainCodeHelper.SMC_REMOTER_TOU_YING, "投影仪");
        map.put(MainCodeHelper.SMC_REMOTER_MU_BU, "投影幕布");
        map.put(MainCodeHelper.SMC_REMOTER_SHENG_JIANG_JIA, "升降架");
        map.put(MainCodeHelper.SMC_REMOTER_ZI_DING_YI, "自定义");
        map.put(MainCodeHelper.SMC_DENG, "灯");
        map.put(MainCodeHelper.SMC_CHUANG_HU, "窗帘");
        map.put(MainCodeHelper.SMC_FA_MEN, "阀门");
        map.put(MainCodeHelper.SMC_BING_XIANG, "冰箱");
        map.put(MainCodeHelper.SMC_XI_YI_JI, "洗衣机");
        map.put(MainCodeHelper.SMC_WEI_BO_LU, "微波炉");
        map.put(MainCodeHelper.SMC_YIN_XIANG, "音箱");
        map.put(MainCodeHelper.SMC_SHUI_LONG_TOU, "水龙头");

        MainCodeHelper.getIns().setManCodeInfo(map);
    }

    public static void initUser(){
        HamaApp.USER = SdDbHelper.getDbUser();
        if(null == HamaApp.USER){
            return;
        }
        if(HamaApp.USER.getListDevGroup().isEmpty()){
            return;
        }
        HamaApp.DEV_GROUP = HamaApp.USER.getListDevGroup().get(0);

        //本地tcp网络数据监听器名称
        //调试模式下监听网络数据
        if(LogUtils.APP_DBG) {
            FindDevHelper.getIns().setOnSendListener(new FindDevHelper.OnSendListener() {
                @Override
                public void create() {

                }

                @Override
                public void send(String s) {
                    UdpLogActivity.addSend(s);
                }
            });

            DevChannelBridgeHelper.BRIDGE_COMMUNICATION_LISTENER_NAME = MyOnCommunicationListener.class.getName();
            DevChannelBridgeHelper.getIns().setOnBridgesChangedListener(new MyOnBridgesChangedListener());
        }

        MyOnStateChangedListener onStateChangedListener = new MyOnStateChangedListener();
        MyOnGearChangedListener onGearChangedListener = new MyOnGearChangedListener();
        MyOnCtrlModelChangedListener onCtrlModelChangedListener = new MyOnCtrlModelChangedListener();
        for (Device device : HamaApp.DEV_GROUP.getListDevice()){
            //刚打开软件开始寻找设备
            FindDevHelper.getIns().findDev(device.getCoding());
            device.setDevStateId(DevStateHelper.DS_UNKNOW);
            setDeviceListener(device, onStateChangedListener, onGearChangedListener, onCtrlModelChangedListener);
        }

        DevChannelBridgeHelper.getIns().setUser(HamaApp.USER);

        //将状态设备添加到联动表，开始检查所有设备的联动和挡位状态
        List<Device> list = HamaApp.DEV_GROUP.findListIStateDev(true);
        LinkageTab.getIns().getListLinkageTabRow().clear();
        for(Device device : list){
            LinkageTab.getIns().addTabRow(device);
        }

        LinkageHelper.getIns().setChain(HamaApp.DEV_GROUP.getChainHolder());
        LinkageHelper.getIns().setLoop(HamaApp.DEV_GROUP.getLoopHolder());
        LinkageHelper.getIns().setTiming(HamaApp.DEV_GROUP.getTimingHolder());

        GuaguaHelper.getIns().setGuaguaHolder(HamaApp.DEV_GROUP.getGuaguaHolder());

        //GuaguaHelper.getIns().startCheckGuaguaThread();
    }

    public static void setDeviceListener(Device device, MyOnStateChangedListener onStateChangedListener,
                                         MyOnGearChangedListener onGearChangedListener,
                                         MyOnCtrlModelChangedListener onCtrlModelChangedListener){
        device.setCtrlModel(CtrlModel.UNKNOW);
        device.addOnStateChangedListener(onStateChangedListener);
        device.addOnGearChangedListener(onGearChangedListener);
        device.addOnCtrlModelChangedListener(onCtrlModelChangedListener);
        device.setOnSortIndexChangedListener(new MyOnSortIndexChangedListener());
        device.addOnAliasChangedListener(new MyOnAliasChangedListener());
        device.addOnNameChangedListener(new MyOnNameChangedListener());
        device.setOnGearNeedToAutoListener(new MyOnGearNeedToAutoListener());

        if(device instanceof DevHaveChild){
            DevHaveChild devHaveChild = (DevHaveChild)device;
            //协调器添加子设备集合改变监听器
            if(devHaveChild instanceof Coordinator){
                devHaveChild.addOnDeviceCollectionChangedListener(new MyOnDevHaveChildeOnCollectionChangedListener());
            }else if(devHaveChild instanceof RemoterContainer){
                devHaveChild.addOnDeviceCollectionChangedListener(new MyOnDevHaveChildeOnCollectionChangedListener());
                ((RemoterContainer) devHaveChild).setOnRemoterOrderSuccessListener(new MyOnRemoterOrderSuccessListener());
            }
            for(Device device1 : devHaveChild.getListDev()){
                setDeviceListener(device1, onStateChangedListener, onGearChangedListener, onCtrlModelChangedListener);
            }
        }
        if(device instanceof DevCollect){
            CollectProperty cp = ((DevCollect) device).getCollectProperty();
            cp.addOnCurrentValueChangedListener(MyOnCurrentValueChangedListener.getIns());
            cp.setOnSignalSourceChangedListener(new MyOnSignalSourceChangedListener());
            cp.setOnSimulatorChangedListener(new MyOnSimulatorChangedListener());
            cp.setOnUnitSymbolChangedListener(new MyOnUnitSymbolChangedListener());
            cp.setOnValueTriggedListener(new MyOnValueTriggedListener());
        }else if(device instanceof DevAlarm){
//            ((DevAlarm) device).addOnAlarmTriggedListener(MyOnAlarmTriggedListener.INSTANCE);
        }
    }

    private static void testDeviceBx(){
        User user = new User();
        user.setUserid("test123");
        user.setPassword("a123456");
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        userDao.clean();
        userDao.addUser(user);

        DevGroup devGroup = new DevGroup("1", "a123", "g1");
        devGroup.setId(UUID.randomUUID().toString());
        user.addGroup(devGroup);
        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        devGroupDao.clean();
        devGroupDao.add(devGroup);

        Device device = DeviceAssistent.createDeviceByMcId(MainCodeHelper.KG_XLU_2TAI, "0001");
        devGroup.addDevice(device);

        DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
        deviceDao.clean();
        deviceDao.add(device);

        SdDbHelper.replaceDbUser(user);
    }

    private static void testDevice(){
        User user = new User();
        user.setUserid("test123");
        user.setPassword("a123456");
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        userDao.clean();
        userDao.addUser(user);

        DevGroup devGroup = new DevGroup("1", "a123", "g1");
        devGroup.setId(UUID.randomUUID().toString());
        user.addGroup(devGroup);
        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        devGroupDao.clean();
        devGroupDao.add(devGroup);

//        Coordinator coordinator = (Coordinator)DeviceAssistent.createDeviceByMcId(MainCodeHelper.XIE_TIAO_QI, "9999");
//        DevCollectSignal devCollectSignal = (DevCollectSignal) DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL, "9999");
//        devCollectSignal.getCollectProperty().setCollectSrc(CollectSignalSource.DIGIT);
//        DevCollectSignal devCollectSignal2 = (DevCollectSignal) DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL, "9998");
//        devCollectSignal2.getCollectProperty().setCollectSrc(CollectSignalSource.ELECTRIC_CURRENT);
//        DevCollectSignal devCollectSignal3 = (DevCollectSignal) DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL, "9997");
//        devCollectSignal3.getCollectProperty().setCollectSrc(CollectSignalSource.VOLTAGE);
//        coordinator.addChildDev(devCollectSignal);
//        coordinator.addChildDev(devCollectSignal2);
//        coordinator.addChildDev(devCollectSignal3);

        Device device = DeviceAssistent.createDeviceByMcId(MainCodeHelper.KG_3LU_2TAI, "9999");
        devGroup.addDevice(device);

        Device device1 = DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_CLIMATE_CONTAINER, "9999");
        devGroup.addDevice(device1);

//        DevCollectSignalContainer devCollectSignalContainer = (DevCollectSignalContainer)DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL_CONTAINER, "9996");
//        devGroup.addDevice(devCollectSignalContainer);

//        GuaguaMouth guaguaMouth = (GuaguaMouth)DeviceAssistent.createDeviceByMcId(MainCodeHelper.GUAGUA_MOUTH, "9999");
//        devGroup.addDevice(guaguaMouth);
//        DevCollectSignal devCollectSignal4 = (DevCollectSignal)DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_SIGNAL, "9996");
//        devCollectSignal4.getCollectProperty().setCollectSrc(CollectSignalSource.SWITCH);
//        devGroup.addDevice(devCollectSignal4);

        SdDbHelper.replaceDbUser(user);
    }

    private static void testCoordinator(){
        User user = new User();
        user.setUserid("test123");
        user.setPassword("a123456");
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        userDao.clean();
        userDao.addUser(user);

        DevGroup devGroup = new DevGroup("1", "a123", "g1");
        devGroup.setId(UUID.randomUUID().toString());
        user.addGroup(devGroup);
        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        devGroupDao.clean();
        devGroupDao.add(devGroup);

        Coordinator coordinator = (Coordinator)DeviceAssistent.createDeviceByMcId(MainCodeHelper.XIE_TIAO_QI, "9999", devGroup);
        Pressure pressure = (Pressure)DeviceAssistent.createDeviceByMcId(MainCodeHelper.YE_WEI, "9999", devGroup);

        DevCollectClimateContainer climateContainer = (DevCollectClimateContainer)DeviceAssistent.createDeviceByMcId(MainCodeHelper.COLLECTOR_CLIMATE_CONTAINER, "9999", devGroup);

        DevAlarm devAlarm = (DevAlarm) DeviceAssistent.createDeviceByMcId(MainCodeHelper.YAN_WU, "9999", devGroup);
        DevAlarm devMenJin = (DevAlarm) DeviceAssistent.createDeviceByMcId(MainCodeHelper.MEN_JIN, "9999", devGroup);

        coordinator.addChildDev(pressure);
        coordinator.addChildDev(climateContainer);
        coordinator.addChildDev(devAlarm);
        coordinator.addChildDev(devMenJin);

        devGroup.addDevice(coordinator);

        SdDbHelper.replaceDbUser(user);
    }

    private static void testRemoterContainer(){
        User user = new User();
        user.setUserid("test123");
        user.setPassword("a123456");
        UserDao userDao = UserDao.get(HamaApp.HAMA_CONTEXT);
        userDao.clean();
        userDao.addUser(user);

        DevGroup devGroup = new DevGroup("1", "a123", "g1");
        devGroup.setId(UUID.randomUUID().toString());
        user.addGroup(devGroup);
        DevGroupDao devGroupDao = DevGroupDao.get(HamaApp.HAMA_CONTEXT);
        devGroupDao.clean();
        devGroupDao.add(devGroup);

        RemoterContainer remoterContainer = (RemoterContainer)DeviceAssistent.createDeviceByMcId(MainCodeHelper.YAO_KONG, "9999");

        devGroup.addDevice(remoterContainer);

        SdDbHelper.replaceDbUser(user);
    }

    private static class ToMainTask extends AsyncTask<Void, Void, Boolean> {

        WeakReference<WelcomeActivity> mActivity;

        ToMainTask(WelcomeActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            //try {

            Media.get().init(mActivity.get());
            Config.ins().init(mActivity.get());

            //星期信息设为中文
            WeekHelper.ARRAY_WEEKS = new String[]{"日","一","二","三","四","五","六",};
            //设备主编码描述设为中文
            mActivity.get().initMainCodeInfo();

            //获取屏幕宽高
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mActivity.get().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Constant.displayWidth = displayMetrics.widthPixels;
            Constant.displayHeight = displayMetrics.heightPixels;

            //没有可搜索设备时单机测试用
//                testDevice();
//                testDeviceBx();
//                testCoordinator();
//                testRemoterContainer();
            initUser();

            //设置宫格/列表切换监听
            Config.ins().setOnDevNameShowStyleChangedListener(name -> {
                if(null != ElectricalCtrlFragment.handler){
                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.CHANGE_SHOW_NAME_STYLE).sendToTarget();
                }
                if(null != ClimateFragment.handler){
                    ClimateFragment.handler.obtainMessage(ClimateFragment.CHANGE_SHOW_NAME_STYLE).sendToTarget();
                }
            });
            Config.ins().setOnDevShowStyleChangedListener(style -> {
                if(null != ElectricalCtrlFragment.handler){
                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.CHANGE_LAYOUT_MANAGER).sendToTarget();
                }
                if(null != ClimateFragment.handler){
                    ClimateFragment.handler.obtainMessage(ClimateFragment.CHANGE_LAYOUT_MANAGER).sendToTarget();
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            WelcomeActivity theActivity = mActivity.get();
            if (success) {
                if(Config.ins().isNeedLogin() || HamaApp.USER == null || HamaApp.USER.getUserid() == null || HamaApp.DEV_GROUP == null) {
                    theActivity.startActivity(new Intent(theActivity, LoginActivity.class));
                }else{
                    MainActivity.IS_ADMIN = false;
//                    MainActivity.IS_ADMIN = HamaApp.USER.getName().equals("admin");
                    theActivity.startActivity(new Intent(theActivity, MainActivity.class));
                }
                theActivity.finish();
            }else {
                theActivity.finish();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
