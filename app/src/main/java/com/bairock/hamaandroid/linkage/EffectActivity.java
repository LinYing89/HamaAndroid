package com.bairock.hamaandroid.linkage;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.IValueDevice;
import com.bairock.iot.intelDev.linkage.Effect;

import java.util.ArrayList;
import java.util.List;

public class EffectActivity extends AppCompatActivity {

    public static final int ADD_EFFECT = 5;
    public static final int UPDATE_EFFECT = 6;

    public static boolean ADD = false;
    public static Effect effect;
    public static Handler handler;

    private TableRow tabrowTriggerValueSpinner;
    private TableRow tabrowTriggerValueEdit;
    private Spinner spinnerDevice;
    private Spinner spinnerValue;
    private EditText editValue;
    private Button btnSave;
    private Button btnCancel;

    private List<Device> listDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setSpinners();
        if(ADD){
            effect = new Effect();
        }
        init();
        setListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findViews(){
        tabrowTriggerValueSpinner =  findViewById(R.id.tabrowTriggerValueSpinner);
        tabrowTriggerValueEdit = findViewById(R.id.tabrowTriggerValueEdit);
        spinnerValue = findViewById(R.id.spinnerValue);
        spinnerDevice = findViewById(R.id.spinnerDevices);
        editValue = findViewById(R.id.etxtValue);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setSpinners(){
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(
                this,android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.array_event_state));
        spinnerValue .setAdapter(adapter3);
    }

    private void setListener(){
        spinnerDevice.setOnItemSelectedListener(deviceOnItemSelectedListener);
        spinnerValue.setOnItemSelectedListener(valueOnItemSelectedListener);
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    private void init(){
        listDevice = new ArrayList<>();
        listDevice.addAll(HamaApp.DEV_GROUP.findListIStateDev(true));
        listDevice.addAll(HamaApp.DEV_GROUP.findListDevParam(true));
        List<String> listDeviceName = new ArrayList<>();
        for(Device device : listDevice){
            listDeviceName.add(device.getName());
        }
        spinnerDevice.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,listDeviceName));

        int iDevice = listDevice.indexOf(effect.getDevice());
        iDevice = iDevice == -1 ? 0 : iDevice;
        spinnerDevice.setSelection(iDevice);

        if(effect.getDevice() instanceof IStateDev){
            showElectricalStyle();
        }else{
            showClimateStyle();
        }
    }

    private void showElectricalStyle(){
        tabrowTriggerValueSpinner.setVisibility(View.VISIBLE);
        tabrowTriggerValueEdit.setVisibility(View.GONE);
        if(effect.getDsId().equals(DevStateHelper.DS_GUAN)){
            spinnerValue.setSelection(0);
        }else{
            spinnerValue.setSelection(1);
        }
    }

    private void showClimateStyle(){
        tabrowTriggerValueSpinner.setVisibility(View.GONE);
        tabrowTriggerValueEdit.setVisibility(View.VISIBLE);

        editValue.setText(effect.getEffectContent());
    }

    /**
     * 设备选择事件，ADD/OR
     */
    private AdapterView.OnItemSelectedListener deviceOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(null == effect){
                return;
            }
            Device device = listDevice.get(position);
            effect.setDevice(device);
            if(device instanceof IStateDev){
                showElectricalStyle();
            }else{
                showClimateStyle();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 值选择事件，ADD/OR
     */
    private AdapterView.OnItemSelectedListener valueOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(null == effect || effect.getDevice() == null){
                return;
            }
            if(position == 0) {
                effect.setDsId(DevStateHelper.DS_GUAN);
            }else{
                effect.setDsId(DevStateHelper.DS_KAI);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_save:
                    if(effect.getDevice() != null){
                        try {
                            if(effect.getDevice() instanceof IValueDevice) {
                                effect.setEffectContent(editValue.getText().toString());
                            }
                            if(ADD){
                                if(null != handler){
                                    handler.obtainMessage(ADD_EFFECT, effect).sendToTarget();
                                }
                            }else{
                                if(null != handler){
                                    handler.obtainMessage(UPDATE_EFFECT, effect).sendToTarget();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        finish();
                    }else{
                        Snackbar.make(btnSave, "设备不能为空", Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_cancel:
                    finish();
                    break;
            }
        }
    };
}
