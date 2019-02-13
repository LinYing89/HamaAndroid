package com.bairock.hamaandroid.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.database.DevGroupDao;
import com.bairock.hamaandroid.database.LinkageHolderDao;
import com.bairock.hamaandroid.database.SdDbHelper;
import com.bairock.hamaandroid.database.UserDao;
import com.bairock.iot.intelDev.communication.UdpServer;
import com.bairock.iot.intelDev.data.DevGroupLoginResult;
import com.bairock.iot.intelDev.data.Result;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.alarm.DevAlarm;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger;
import com.bairock.iot.intelDev.device.remoter.Remoter;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;
import com.bairock.iot.intelDev.http.LoginTask;
import com.bairock.iot.intelDev.linkage.ChainHolder;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.LinkageHolder;
import com.bairock.iot.intelDev.linkage.SubChain;
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage;
import com.bairock.iot.intelDev.linkage.guagua.GuaguaHolder;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;
import com.bairock.iot.intelDev.linkage.loop.LoopHolder;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;
import com.bairock.iot.intelDev.linkage.timing.MyTime;
import com.bairock.iot.intelDev.linkage.timing.Timing;
import com.bairock.iot.intelDev.linkage.timing.TimingHolder;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.User;
import java.util.Date;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private EditText etUserName;
    private EditText etGroupName;
    private EditText etGroupPsd;

    private Button btnLogin;
    private Button btnLoginOffline;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        setListener();
    }

    private void findViews(){

        etUserName = findViewById(R.id.etUserName);
        etGroupName = findViewById(R.id.etGroupName);
        etGroupPsd = findViewById(R.id.etGroupPsd);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginOffline = findViewById(R.id.btnLoginOffline);

        String userName = "";
        if(null != HamaApp.USER){
            userName = HamaApp.USER.getName();
        }
        etUserName.setText(userName);

        String groupName = "";
        if(null != HamaApp.DEV_GROUP){
            groupName = HamaApp.DEV_GROUP.getName();
        }
        etGroupName.setText(groupName);
    }

    private void setListener(){
        btnLogin.setOnClickListener(onClickListener);
        btnLoginOffline.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()){
            case R.id.btnLogin:
                showProgress(true);
                String userName = etUserName.getText().toString();
                String groupName = etGroupName.getText().toString();
                String groupPsd = etGroupPsd.getText().toString();
                String url = String.format("http://%s/group/client/devGroupLogin/%s/%s/%s", Config.ins().getServerName(), userName, groupName, groupPsd);
                //开启线程登录
                LoginTask loginTask = new LoginTask(url);
                loginTask.setOnExecutedListener(this::loginResult);
                loginTask.start();
                break;
            case R.id.btnLoginOffline:
                User user = new User();
                user.setId(1L);
                user.setName("admin");
                user.setPsd("admin123");
                user.setPetName("admin");
                user.setRegisterTime(new Date());
                user.setTel("888");
                DevGroup group = new DevGroup();
                group.setId(UUID.randomUUID().toString());
                group.setName("g1");
                group.setPsd("a123");
                group.setPetName("g1");
                user.addGroup(group);

                if(HamaApp.USER == null) {
                    HamaApp.USER = user;
                    UserDao userDao = UserDao.get(LoginActivity.this);
                    userDao.addUser(user);
                }else{
                    HamaApp.USER.setName(user.getName());
                    HamaApp.USER.setPsd(user.getPsd());
                    HamaApp.USER.setPetName(user.getPetName());
                    HamaApp.USER.setRegisterTime(user.getRegisterTime());
                    HamaApp.USER.setTel(user.getTel());
                    UserDao userDao = UserDao.get(LoginActivity.this);
                    userDao.updateUser(HamaApp.USER);
                }

                if(null == HamaApp.DEV_GROUP) {
                    HamaApp.DEV_GROUP = group;
                    DevGroupDao devGroupDao = DevGroupDao.get(LoginActivity.this);
                    devGroupDao.add(group);

                }else{
                    HamaApp.DEV_GROUP.setName(group.getName());
                    HamaApp.DEV_GROUP.setPsd(group.getPsd());
                    HamaApp.DEV_GROUP.setPetName(user.getPetName());
                    DevGroupDao devGroupDao = DevGroupDao.get(LoginActivity.this);
                    devGroupDao.update(HamaApp.DEV_GROUP);
                }
                if(HamaApp.DEV_GROUP.getListLinkageHolder().isEmpty()){
                    initLinkageHolder();
                }
                MainActivity.IS_ADMIN = true;
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                break;
        }
    };

    private void showProgress(final boolean show) {
        if(show){
            if(null == progressDialog){
                progressDialog = new ProgressDialog(LoginActivity.this);
            }
            progressDialog.setMessage("正在登陆，请稍等...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }else{
            if(null != progressDialog){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    private void loginResult(Result<DevGroupLoginResult> result){
        runOnUiThread(() -> {
            showProgress(false);

            if(result.getCode() != 0) {
                Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
            }else {
                Config.ins().setServerPadPort(result.getData().getPadPort());
                Config.ins().setServerDevPort(result.getData().getDevPort());

                Config.ins().setServerInfo(getApplicationContext());

                String petName = result.getData().getDevGroupPetName();

                boolean add = null == HamaApp.USER;
                if(add){
                    HamaApp.USER = new User();
                    HamaApp.USER.setId(1L);
                    HamaApp.DEV_GROUP = new DevGroup();
                    HamaApp.DEV_GROUP.setId(UUID.randomUUID().toString());
                    HamaApp.USER.addGroup(HamaApp.DEV_GROUP);
                    UdpServer.getIns().setUser(HamaApp.USER);
                }
                String oldUserName = HamaApp.USER.getName();
                String oldGroupName = HamaApp.DEV_GROUP.getName();
                String oldPsd = HamaApp.DEV_GROUP.getPsd();
                HamaApp.USER.setName(etUserName.getText().toString());
                HamaApp.DEV_GROUP.setName(etGroupName.getText().toString());
                HamaApp.DEV_GROUP.setPsd(etGroupPsd.getText().toString());
                HamaApp.DEV_GROUP.setPetName(petName);
                UserDao userDao = UserDao.get(LoginActivity.this);
                DevGroupDao devGroupDao = DevGroupDao.get(LoginActivity.this);
                if(add){
                    userDao.addUser(HamaApp.USER);
                    devGroupDao.add(HamaApp.DEV_GROUP);
                    initLinkageHolder();

                }else {
                    userDao.updateUser(HamaApp.USER);
                    devGroupDao.update(HamaApp.DEV_GROUP);
                    if(!HamaApp.USER.getName().equals(oldUserName) || !HamaApp.DEV_GROUP.getName().equals(oldGroupName)
                            || !HamaApp.DEV_GROUP.getPsd().equals(oldPsd)){
                        //重新生成设备id
                        refreshDbId();
                    }
                }

                if(HamaApp.DEV_GROUP.getListLinkageHolder().isEmpty()){
                    initLinkageHolder();
                }

                //MainActivity.FIRST_LOGIN = true;
                MainActivity.IS_ADMIN = false;
                Config.ins().setNeedLogin(getApplicationContext(), false);
            }
        });
    }

    private void initLinkageHolder(){
        //初始化四个连锁根目录
        ChainHolder chainHolder = new ChainHolder();
        chainHolder.setId(UUID.randomUUID().toString());
        HamaApp.DEV_GROUP.setChainHolder(chainHolder);
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).add(chainHolder);
        TimingHolder timingHolder = new TimingHolder();
        timingHolder.setId(UUID.randomUUID().toString());
        HamaApp.DEV_GROUP.setTimingHolder(timingHolder);
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).add(timingHolder);
        LoopHolder loopHolder = new LoopHolder();
        loopHolder.setId(UUID.randomUUID().toString());
        HamaApp.DEV_GROUP.setLoopHolder(loopHolder);
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).add(loopHolder);
        GuaguaHolder guaguaHolder = new GuaguaHolder();
        guaguaHolder.setId(UUID.randomUUID().toString());
        HamaApp.DEV_GROUP.setGuaguaHolder(guaguaHolder);
        LinkageHolderDao.get(HamaApp.HAMA_CONTEXT).add(guaguaHolder);
    }

    private void refreshDbId(){
        for(Device device : HamaApp.DEV_GROUP.getListDevice()){
            setDeviceId(device);
        }
        for(LinkageHolder linkageHolder : HamaApp.DEV_GROUP.getListLinkageHolder()) {
            linkageHolder.setId(UUID.randomUUID().toString());
            for (Linkage linkage : linkageHolder.getListLinkage()) {
                linkage.setId(UUID.randomUUID().toString());
                for(Effect effect : linkage.getListEffect()){
                    effect.setId(UUID.randomUUID().toString());
                }
                if(linkage instanceof SubChain){
                    for(LinkageCondition linkageCondition : ((SubChain) linkage).getListCondition()){
                        linkageCondition.setId(UUID.randomUUID().toString());
                    }
                    if(linkage instanceof ZLoop){
                        for(LoopDuration loopDuration : ((ZLoop) linkage).getListLoopDuration()){
                            loopDuration.setId(UUID.randomUUID().toString());
                            for(MyTime myTime : loopDuration.getListTimes()){
                                myTime.setId(UUID.randomUUID().toString());
                            }
                        }
                    }
                }else if(linkage instanceof Timing){
                    for(ZTimer zTimer : ((Timing) linkage).getListZTimer()){
                        zTimer.setId(UUID.randomUUID().toString());
                        for(MyTime myTime : zTimer.getListTimes()){
                            myTime.setId(UUID.randomUUID().toString());
                        }
                        zTimer.getWeekHelper().setId(UUID.randomUUID().toString());
                    }
                }
            }
        }
        SdDbHelper.replaceDbUser(HamaApp.USER);
    }

    private void setDeviceId(Device device){
        device.setId(UUID.randomUUID().toString());
        if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild) device).getListDev()){
                setDeviceId(device1);
            }
        }
        for(DeviceLinkage deviceLinkage : device.getListDeviceLinkage()){
            deviceLinkage.setId(UUID.randomUUID().toString());
        }
        if(device instanceof DevCollect){
            CollectProperty collectProperty = ((DevCollect) device).getCollectProperty();
            collectProperty.setId(UUID.randomUUID().toString());
            for(ValueTrigger trigger : collectProperty.getListValueTrigger()){
                trigger.setId(UUID.randomUUID().toString());
            }
        }else if(device instanceof Remoter){
            for(RemoterKey remoterKey : ((Remoter)device).getListRemoterKey()){
                remoterKey.setId(UUID.randomUUID().toString());
            }
        }else if(device instanceof DevAlarm){
            ((DevAlarm)device).getTrigger().setId(UUID.randomUUID().toString());
        }
    }
}
