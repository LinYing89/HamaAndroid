package com.bairock.hamaandroid.settings;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage;
import com.bairock.iot.intelDev.user.DevGroup;
import java.util.ArrayList;
import java.util.List;

public class DeviceLinkageSettingActivity extends AppCompatActivity {

    public static DeviceLinkage DEVICE_LINKAGE;

    private Spinner spinnerTargetDev;
    //小于
    private EditText etxtValue1;
    private Spinner spinnerAction1;
    private TextView txtTargetDevName1;
    //大于
    private EditText etxtValue2;
    private Spinner spinnerAction2;
    private TextView txtTargetDevName2;
    private Button btnSave;
    private Button btnCancel;

    private List<Device> listTargetDev = new ArrayList<>();
    private Device targetDevice;
    private int switchModel = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_linkage_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //只有协调器系统下的设备才可设置
        if(!(DEVICE_LINKAGE.getSourceDevice().findSuperParent() instanceof Coordinator)){
            Toast.makeText(this, "非协调器系统", Toast.LENGTH_SHORT).show();
            finish();
        }

        Coordinator coordinator = (Coordinator)(DEVICE_LINKAGE.getSourceDevice().findSuperParent());
        listTargetDev = DevGroup.findListIStateDev(coordinator.getListDev(), true);
        if(listTargetDev.isEmpty()){
            Toast.makeText(this, "无可控设备", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(DEVICE_LINKAGE.getTargetDevice() != null){
            targetDevice = DEVICE_LINKAGE.getTargetDevice();
        }else{
            targetDevice = listTargetDev.get(0);
        }

        findViews();
        setListener();
        setTargetDevSpinner();
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        DEVICE_LINKAGE = null;
    }

    private void findViews(){
        spinnerTargetDev = findViewById(R.id.spinnerTargetDev);
        //小于
        etxtValue1 = findViewById(R.id.etxtValue1);
        spinnerAction1 = findViewById(R.id.spinnerAction1);
        txtTargetDevName1 = findViewById(R.id.txtTargetDevName1);
        //大于
        etxtValue2 = findViewById(R.id.etxtValue2);
        spinnerAction2 = findViewById(R.id.spinnerAction2);
        txtTargetDevName2 = findViewById(R.id.txtTargetDevName2);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setListener(){
        spinnerTargetDev.setOnItemClickListener((adapterView, view, i, l) -> {
            targetDevice = listTargetDev.get(i);
            txtTargetDevName1.setText(targetDevice.getName());
            txtTargetDevName2.setText(targetDevice.getName());
        });
        spinnerAction1.setOnItemClickListener((adapterView, view, i, l) -> {
            if(i == 0){
                switchModel = 2;
                spinnerAction2.setSelection(1);
            }else{
                switchModel = 1;
                spinnerAction2.setSelection(0);
            }
        });
        spinnerAction2.setOnItemClickListener((adapterView, view, i, l) -> {
            if(i == 0){
                switchModel = 1;
                spinnerAction1.setSelection(1);
            }else{
                switchModel = 2;
                spinnerAction1.setSelection(0);
            }
        });
        btnSave.setOnClickListener(view -> {
            DEVICE_LINKAGE.setTargetDevice(targetDevice);
            DEVICE_LINKAGE.setSwitchModel(switchModel);
            DEVICE_LINKAGE.setValue1(Float.valueOf(etxtValue1.getText().toString()));
            DEVICE_LINKAGE.setValue2(Float.valueOf(etxtValue2.getText().toString()));
            if(DEVICE_LINKAGE.getSourceDevice().findSuperParent().isNormal()){
                HamaApp.sendOrder(DEVICE_LINKAGE.getSourceDevice().findSuperParent(), DEVICE_LINKAGE.createSetOrder(), true);
            }else{
                Toast.makeText(this, "协调器状态异常", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
        btnCancel.setOnClickListener(view -> { finish(); });
    }

    private void init(){
        spinnerTargetDev.setSelection(listTargetDev.indexOf(targetDevice));
        if(DEVICE_LINKAGE.getSwitchModel() == 1){
            spinnerAction1.setSelection(0);
            spinnerAction2.setSelection(1);
        }else{
            spinnerAction1.setSelection(1);
            spinnerAction2.setSelection(0);
        }
        etxtValue1.setText(String.valueOf(DEVICE_LINKAGE.getValue1()));
        etxtValue2.setText(String.valueOf(DEVICE_LINKAGE.getValue2()));
    }

    private void setTargetDevSpinner(){
        List<String> listDeviceName = new ArrayList<>();
        for (Device device : listTargetDev) {
            listDeviceName.add(device.getName());
        }
        spinnerTargetDev.setAdapter(new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, listDeviceName));

    }
}
