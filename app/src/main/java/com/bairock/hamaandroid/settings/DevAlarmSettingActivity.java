package com.bairock.hamaandroid.settings;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.database.AlarmTriggerDao;
import com.bairock.hamaandroid.database.DeviceDao;
import com.bairock.iot.intelDev.device.alarm.DevAlarm;

public class DevAlarmSettingActivity extends AppCompatActivity {

    public static DevAlarm DEVICE = null;

    private TextView txtCoding;
    private EditText etxtName;
    private EditText etxtAlias;
    private Switch switchAlarm;
    private EditText etxtMessage;
    private Button btnSave;
    private Button btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_alarm_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListener();
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DEVICE = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void findViews(){
        txtCoding = findViewById(R.id.txtCoding);
        etxtName = findViewById(R.id.etxtName);
        etxtAlias = findViewById(R.id.etxtAlias);
        switchAlarm = findViewById(R.id.switchAlarm);
        etxtMessage = findViewById(R.id.etxtMessage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    public void setListener(){
        btnSave.setOnClickListener( (view) -> {
            DEVICE.setName(etxtName.getText().toString());
            DEVICE.setAlias(etxtAlias.getText().toString());
            DeviceDao.get(this).update(DEVICE);

            DEVICE.getTrigger().setEnable(switchAlarm.isChecked());
            DEVICE.getTrigger().setMessage(etxtMessage.getText().toString());
            AlarmTriggerDao.get(this).update(DEVICE.getTrigger());

            finish();
        });
        btnCancel.setOnClickListener(view -> {
            finish();
        });
    }

    public void init(){
        txtCoding.setText(DEVICE.getLongCoding());
        etxtName.setText(DEVICE.getName());
        etxtAlias.setText(DEVICE.getAlias());
        etxtMessage.setText(DEVICE.getTrigger().getMessage());
        switchAlarm.setChecked(DEVICE.getTrigger().isEnable());
    }
}
